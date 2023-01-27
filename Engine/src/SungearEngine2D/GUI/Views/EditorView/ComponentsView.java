package SungearEngine2D.GUI.Views.EditorView;

import Core2D.AssetManager.AssetManager;
import Core2D.Audio.Audio;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.Component.Components.Primitives.BoxComponent;
import Core2D.ECS.Component.Components.Primitives.CircleComponent;
import Core2D.ECS.Component.Components.Primitives.LineComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.NonRemovable;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Log.Log;
import Core2D.Physics.PhysicsWorld;
import Core2D.Project.ProjectsManager;
import Core2D.Scripting.Script;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.ImGuiUtils;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Utils.ResourcesUtils;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImString;
import org.apache.commons.io.FilenameUtils;
import org.jbox2d.dynamics.BodyType;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class ComponentsView extends View
{
    private boolean showPopupWindow = false;

    private boolean someButtonInPopupWindowHovered = false;

    private Component currentEditingComponent;

    private String action = "";

    public void draw()
    {
        Entity inspectingEntity = (Entity) ViewsManager.getInspectorView().getCurrentInspectingObject();
        if(inspectingEntity == null) return;

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

        ImGui.begin("Components", ImGuiWindowFlags.NoMove);
        ImGui.popStyleVar(3);

        if (showPopupWindow) {
            ImGui.openPopup("Component actions");
            if (ImGui.beginPopupContextWindow("Component actions", ImGuiMouseButton.Left)) {
                if (!(currentEditingComponent instanceof NonRemovable)) {
                    boolean deleteClicked = ImGui.menuItem("Remove");
                    someButtonInPopupWindowHovered = ImGui.isItemHovered();
                    if (deleteClicked) {
                        try {
                            inspectingEntity.removeComponent(currentEditingComponent);
                        } catch (Exception e) {
                            ImGui.endPopup();
                        }
                        showPopupWindow = false;
                    }
                }

                ImGui.endPopup();
            }
        }

        if ((ImGui.isMouseClicked(ImGuiMouseButton.Left) || ImGui.isMouseClicked(ImGuiMouseButton.Right)) &&
                !someButtonInPopupWindowHovered) {
            showPopupWindow = false;
        }

        for (int i = 0; i < inspectingEntity.getComponents().size(); i++) {
            Component currentComponent = inspectingEntity.getComponents().get(i);
            String componentName = currentComponent.getClass().getSimpleName();

            ImGui.pushID(componentName + i);

            boolean opened = false;
            if(!componentName.equals("ScriptComponent")) {
                opened = ImGui.collapsingHeader(componentName + ". ID: " + currentComponent.componentID);
            } else {
                opened = ImGui.collapsingHeader(((ScriptComponent) currentComponent).script.getName() + " (" + componentName + ")" + ". ID: " + inspectingEntity.getComponents().get(i).componentID);
            }
            ImVec2 minRect = ImGui.getItemRectMin();
            ImVec2 maxRect = ImGui.getItemRectMax();

            if(ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("Component", currentComponent);
                ImGui.text(componentName);
                ImGui.endDragDropSource();
            }

            ImGui.popID();

            if (ImGui.isMouseHoveringRect(minRect.x, minRect.y, maxRect.x, maxRect.y) &&
                    ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                showPopupWindow = true;
                currentEditingComponent = currentComponent;
            }

            if (opened) {
                switch (componentName) {
                    case "TransformComponent" -> {
                        TransformComponent transformComponent = ((TransformComponent) currentComponent);
                        float[] pos = new float[]{
                                transformComponent.getTransform().getPosition().x,
                                transformComponent.getTransform().getPosition().y
                        };
                        float[] rotation = new float[]{
                                transformComponent.getTransform().getRotation()
                        };
                        float[] scale = new float[]{
                                transformComponent.getTransform().getScale().x,
                                transformComponent.getTransform().getScale().y
                        };
                        float[] centre = new float[]{
                                transformComponent.getTransform().getCentre().x,
                                transformComponent.getTransform().getCentre().y
                        };

                        if (ImGui.dragFloat2("Position", pos)) {
                            transformComponent.getTransform().setPosition(new Vector2f(pos));
                        }
                        if (ImGui.dragFloat("Rotation", rotation)) {
                            transformComponent.getTransform().setRotation(rotation[0]);
                        }
                        if (ImGui.dragFloat2("Scale", scale, 0.01f)) {
                            transformComponent.getTransform().setScale(new Vector2f(scale));
                        }
                        if (ImGui.dragFloat2("Centre", centre, 0.01f)) {
                            transformComponent.getTransform().setCentre(new Vector2f(centre));
                        }
                    }
                    case "MeshComponent" -> {
                        MeshComponent meshRendererComponent = (MeshComponent) currentComponent;

                        ImString textureName = new ImString(new File(meshRendererComponent.texture.path).getName());
                        ImGui.inputText("Texture", textureName, ImGuiInputTextFlags.ReadOnly);
                        if (ViewsManager.getResourcesView().getCurrentMovingFile() != null && ResourcesUtils.isFileImage(ViewsManager.getResourcesView().getCurrentMovingFile())) {
                            if (ImGui.beginDragDropTarget()) {
                                Object imageFile = ImGui.acceptDragDropPayload("File");
                                if (imageFile != null) {
                                    textureName.set(ViewsManager.getResourcesView().getCurrentMovingFile().getName(), true);
                                    String relativePath = FileUtils.getRelativePath(
                                            new File(ViewsManager.getResourcesView().getCurrentMovingFile().getPath()),
                                            new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                    meshRendererComponent.texture = new Texture2D(AssetManager.getInstance().getTexture2DData(relativePath));
                                    meshRendererComponent.texture.path = relativePath;
                                    ViewsManager.getResourcesView().setCurrentMovingFile(null);
                                }

                                ImGui.endDragDropTarget();
                            }
                        }

                        ImString shaderName = new ImString(new File(meshRendererComponent.shader.path).getName());
                        ImGui.inputText("Shader", shaderName, ImGuiInputTextFlags.ReadOnly);
                        if (ViewsManager.getResourcesView().getCurrentMovingFile() != null && ResourcesUtils.isFileShader(ViewsManager.getResourcesView().getCurrentMovingFile())) {
                            if (ImGui.beginDragDropTarget()) {
                                Object imageFile = ImGui.acceptDragDropPayload("File");
                                if (imageFile != null) {
                                    shaderName.set(ViewsManager.getResourcesView().getCurrentMovingFile().getName(), true);
                                    String relativePath = FileUtils.getRelativePath(
                                            new File(ViewsManager.getResourcesView().getCurrentMovingFile().getPath()),
                                            new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                    meshRendererComponent.shader = new Shader(AssetManager.getInstance().getShaderData(relativePath));
                                    meshRendererComponent.shader.path = relativePath;
                                    ViewsManager.getResourcesView().setCurrentMovingFile(null);

                                    /*
                                    if(!meshRendererComponent.shader.isCompiled()) {
                                        ViewsManager.getBottomMenuView().leftSideInfo = "Shader " + meshRendererComponent.shader.path + " was not compiled. See the log for details";
                                        ViewsManager.getBottomMenuView().leftSideInfoColor.set(1.0f, 0.0f, 0.0f, 1.0f);
                                    }

                                     */
                                }

                                ImGui.endDragDropTarget();
                            }
                        }
                    }
                    case "Rigidbody2DComponent" -> {
                        Rigidbody2DComponent rigidbody2DComponent = (Rigidbody2DComponent) currentComponent;

                        ImGui.pushID("Rigidbody2DType");
                        if (ImGui.beginCombo("Type", rigidbody2DComponent.getRigidbody2D().typeToString())) {

                            if (ImGui.selectable("Dynamic")) {
                                rigidbody2DComponent.getRigidbody2D().setType(BodyType.DYNAMIC);
                            }

                            if (ImGui.selectable("Static")) {
                                rigidbody2DComponent.getRigidbody2D().setType(BodyType.STATIC);
                            }

                            if (ImGui.selectable("Kinematic")) {
                                rigidbody2DComponent.getRigidbody2D().setType(BodyType.KINEMATIC);
                            }

                            ImGui.endCombo();
                        }
                        ImGui.popID();

                        float[] density = new float[] { rigidbody2DComponent.getRigidbody2D().getDensity() };
                        if (ImGui.dragFloat("Density", density, 0.01f)) {
                            rigidbody2DComponent.getRigidbody2D().setDensity(density[0]);
                        }
                        float[] restitution = new float[] { rigidbody2DComponent.getRigidbody2D().getRestitution() };
                        if (ImGui.dragFloat("Restitution", restitution, 0.01f)) {
                            rigidbody2DComponent.getRigidbody2D().setRestitution(restitution[0]);
                        }
                        float[] friction = new float[] { rigidbody2DComponent.getRigidbody2D().getFriction() };
                        if (ImGui.dragFloat("Friction", friction, 0.01f)) {
                            rigidbody2DComponent.getRigidbody2D().setFriction(friction[0]);
                        }
                        ImGui.separator();
                        if (ImGui.checkbox("Sensor", rigidbody2DComponent.getRigidbody2D().isSensor())) {
                            rigidbody2DComponent.getRigidbody2D().setSensor(!rigidbody2DComponent.getRigidbody2D().isSensor());
                        }
                        if (ImGui.checkbox("Fixed rotation", rigidbody2DComponent.getRigidbody2D().isFixedRotation())) {
                            rigidbody2DComponent.getRigidbody2D().setFixedRotation(!rigidbody2DComponent.getRigidbody2D().isFixedRotation());
                        }
                        ImGui.separator();
                    }
                    case "BoxCollider2DComponent" -> {
                        BoxCollider2DComponent boxCollider2DComponent = (BoxCollider2DComponent) currentComponent;

                        ImGui.pushID("BoxCollider2DOffsetDragFloat_" + i);
                        {
                            float[] offset = new float[] { boxCollider2DComponent.getBoxCollider2D().getOffset().x, boxCollider2DComponent.getBoxCollider2D().getOffset().y };
                            if (ImGui.dragFloat2("Offset", offset)) {
                                boxCollider2DComponent.getBoxCollider2D().setOffset(new Vector2f(offset[0], offset[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxCollider2DScaleDragFloat_" + i);
                        {
                            float[] scale = new float[] { boxCollider2DComponent.getBoxCollider2D().getScale().x, boxCollider2DComponent.getBoxCollider2D().getScale().y };
                            if (ImGui.dragFloat2("Scale", scale, 0.01f)) {
                                boxCollider2DComponent.getBoxCollider2D().setScale(new Vector2f(scale[0], scale[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxCollider2DRotationDragFloat_" + i);
                        {
                            float[] angle = new float[] { boxCollider2DComponent.getBoxCollider2D().getAngle() };
                            if (ImGui.dragFloat("Rotation", angle)) {
                                boxCollider2DComponent.getBoxCollider2D().setAngle(angle[0]);
                            }
                        }
                        ImGui.popID();
                    }
                    case "CircleCollider2DComponent" -> {
                        CircleCollider2DComponent circleCollider2DComponent = (CircleCollider2DComponent) currentComponent;

                        ImGui.pushID("CircleCollider2DOffsetDragFloat_" + i);
                        {
                            float[] offset = new float[] { circleCollider2DComponent.getCircleCollider2D().getOffset().x, circleCollider2DComponent.getCircleCollider2D().getOffset().y };
                            if (ImGui.dragFloat2("Offset", offset)) {
                                circleCollider2DComponent.getCircleCollider2D().setOffset(new Vector2f(offset[0], offset[1]));
                            }
                        }
                        ImGui.popID();

                        float[] radius = new float[] { circleCollider2DComponent.getCircleCollider2D().getRadius() };
                        ImGui.pushID("CircleCollider2DRadiusDragFloat_" + i);
                        {
                            if (ImGui.dragFloat("Radius", radius, 0.01f)) {
                                circleCollider2DComponent.getCircleCollider2D().setRadius(radius[0]);
                            }
                        }
                        ImGui.popID();
                    }
                    case "LineComponent" -> {
                        LineComponent lineComponent = (LineComponent) currentComponent;

                        ImGui.pushID("LineOffsetDragFloat_" + i);
                        {
                            float[] offset = new float[] { lineComponent.getLinesData()[0].offset.x, lineComponent.getLinesData()[0].offset.y };
                            if (ImGui.dragFloat2("Offset", offset)) {
                                lineComponent.getLinesData()[0].offset.set(offset[0], offset[1]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineStartDragFloat_" + i);
                        {
                            float[] start = new float[] { lineComponent.getLinesData()[0].getVertices()[0].x, lineComponent.getLinesData()[0].getVertices()[0].y };
                            if (ImGui.dragFloat2("Start", start)) {
                                lineComponent.getLinesData()[0].getVertices()[0].set(start[0], start[1]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineEndDragFloat_" + i);
                        {
                            float[] end = new float[] { lineComponent.getLinesData()[0].getVertices()[1].x, lineComponent.getLinesData()[0].getVertices()[1].y };
                            if (ImGui.dragFloat2("End", end)) {
                                lineComponent.getLinesData()[0].getVertices()[1].set(end[0], end[1]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineColorDragFloat_" + i);
                        {
                            float[] color = new float[] { lineComponent.getLinesData()[0].color.x, lineComponent.getLinesData()[0].color.y,
                                    lineComponent.getLinesData()[0].color.z, lineComponent.getLinesData()[0].color.w };
                            if (ImGui.colorEdit4("Color", color)) {
                                lineComponent.getLinesData()[0].color.set(color[0], color[1], color[2], color[3]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineWidthDragFloat_" + i);
                        {
                            float[] width = new float[] { lineComponent.getLinesData()[0].lineWidth };
                            if (ImGui.dragFloat("Width", width, 0.1f, 1.0f, 20.0f)) {
                                width[0] = Math.clamp(1, 20, width[0]);
                                lineComponent.getLinesData()[0].lineWidth = width[0];
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineScaleWithEntityCheckbox_" + i);
                        {
                            if (ImGui.checkbox("Scale with entity", lineComponent.scaleWithEntity)) {
                                lineComponent.scaleWithEntity = !lineComponent.scaleWithEntity;
                            }
                        }
                        ImGui.popID();
                    }
                    case "BoxComponent" -> {
                        BoxComponent boxComponent = (BoxComponent) currentComponent;

                        ImGui.pushID("BoxOffsetDragFloat_" + i);
                        {
                            float[] offset = new float[] { boxComponent.getOffset().x, boxComponent.getOffset().y };
                            if (ImGui.dragFloat2("Offset", offset)) {
                                boxComponent.setOffset(new Vector2f(offset[0], offset[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxSizeDragFloat_" + i);
                        {
                            float[] size = new float[] { boxComponent.getSize().x, boxComponent.getSize().y };
                            if (ImGui.dragFloat2("Size", size)) {
                                boxComponent.setSize(new Vector2f(size[0], size[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxLinesWidthDragFloat_" + i);
                        {
                            float[] lineWidth = new float[] { boxComponent.getLinesWidth() };
                            if (ImGui.dragFloat("Lines width", lineWidth, 0.1f, 1.0f, 20.0f)) {
                                lineWidth[0] = Math.clamp(1, 20, lineWidth[0]);
                                boxComponent.setLinesWidth(lineWidth[0]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxColorDragFloat_" + i);
                        {
                            float[] color = new float[] { boxComponent.getColor().x, boxComponent.getColor().y,
                                    boxComponent.getColor().z, boxComponent.getColor().w };
                            if (ImGui.colorEdit4("Color", color)) {
                                boxComponent.setColor(new Vector4f(color[0], color[1], color[2], color[3]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxScaleWithEntityCheckbox_" + i);
                        {
                            if (ImGui.checkbox("Scale with entity", boxComponent.scaleWithEntity)) {
                                boxComponent.scaleWithEntity = !boxComponent.scaleWithEntity;
                            }
                        }
                        ImGui.popID();
                    }
                    case "CircleComponent" -> {
                        CircleComponent circleComponent = (CircleComponent) currentComponent;

                        ImGui.pushID("CircleOffsetDragFloat_" + i);
                        {
                            float[] offset = new float[] { circleComponent.getOffset().x, circleComponent.getOffset().y };
                            if (ImGui.dragFloat2("Offset", offset)) {
                                circleComponent.setOffset(new Vector2f(offset[0], offset[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleRadiusDragFloat_" + i);
                        {
                            float[] radius = new float[] { circleComponent.getRadius() };
                            if (ImGui.dragFloat("Radius", radius, 0.1f)) {
                                circleComponent.setRadius(radius[0]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleLinesWidthDragFloat_" + i);
                        {
                            float[] lineWidth = new float[] { circleComponent.getLinesWidth() };
                            if (ImGui.dragFloat("Lines width", lineWidth, 0.1f, 1.0f, 20.0f)) {
                                lineWidth[0] = Math.clamp(1.0f, 20.0f, lineWidth[0]);
                                circleComponent.setLinesWidth(lineWidth[0]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleColorDragFloat_" + i);
                        {
                            float[] color = new float[] { circleComponent.getColor().x, circleComponent.getColor().y,
                                    circleComponent.getColor().z, circleComponent.getColor().w };
                            if (ImGui.colorEdit4("Color", color)) {
                                circleComponent.setColor(new Vector4f(color[0], color[1], color[2], color[3]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleAngleIncrementDragInt_" + i);
                        {
                            int[] angleIncrement = new int[] { circleComponent.getAngleIncrement() };
                            if (ImGui.dragInt("Angle increment", angleIncrement, 1, 1, 360)) {
                                angleIncrement[0] = Math.clamp(1, 360, angleIncrement[0]);
                                circleComponent.setAngleIncrement(angleIncrement[0]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleScaleWithEntityCheckbox_" + i);
                        {
                            if (ImGui.checkbox("Scale with entity", circleComponent.scaleWithEntity)) {
                                circleComponent.scaleWithEntity = !circleComponent.scaleWithEntity;
                            }
                        }
                        ImGui.popID();
                    }
                    case "ScriptComponent" -> {
                        ScriptComponent scriptComponent = (ScriptComponent) currentComponent;

                        List<Field> inspectorViewFields = Script.getInspectorViewFields(scriptComponent.script.getScriptClassInstance().getClass());

                        Object scriptClsInstance = scriptComponent.script.getScriptClassInstance();
                        if (inspectorViewFields.size() != 0) {
                            for (Field field : inspectorViewFields) {
                                Class<?> cs = field.getType();

                                if (cs.isAssignableFrom(float.class)) {
                                    float[] floats = new float[]{(float) Script.getFieldValue(scriptClsInstance, field)};
                                    ImGui.pushID(field.getName() + "_" + i);
                                    if (ImGui.dragFloat(field.getName(), floats)) {
                                        Script.setFieldValue(scriptClsInstance, field, floats[0]);
                                    }
                                    ImGui.popID();
                                } else if (cs.isAssignableFrom(String.class)) {
                                    ImString string = new ImString((String) Script.getFieldValue(scriptClsInstance, field), 128);

                                    ImGui.pushID(field.getName() + "_" + i);
                                    ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.CallbackAlways);
                                    ImGui.popID();

                                    Script.setFieldValue(scriptClsInstance, field, string.get());
                                } else if (cs.isAssignableFrom(Entity.class)) {
                                    ImString string = new ImString(cs.getSimpleName());
                                    if (Script.getFieldValue(scriptClsInstance, field) != null) {
                                        string.set(((Entity) Script.getFieldValue(scriptClsInstance, field)).name, true);
                                    }

                                    ImGui.pushID(field.getName() + "_" + i);
                                    if(Script.getFieldValue(scriptClsInstance, field) != null) {
                                        ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                    } else {
                                        ImGui.pushStyleColor(ImGuiCol.Text, 0.65f, 0.65f, 0.65f, 1.0f);
                                        ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                        ImGui.popStyleColor(1);
                                    }
                                    ImGui.popID();

                                    if (ImGui.beginDragDropTarget()) {
                                        Object droppedObject = ImGui.acceptDragDropPayload("SceneGameObject");
                                        if (droppedObject instanceof Entity entity) {
                                            Script.setFieldValue(scriptClsInstance, field, entity);
                                        }
                                        ImGui.endDragDropTarget();
                                    }
                                } else if(cs.getSuperclass().isAssignableFrom(Component.class)) {
                                    ImString string = new ImString(cs.getSimpleName());
                                    if (Script.getFieldValue(scriptClsInstance, field) != null) {
                                        string.set(((Component) Script.getFieldValue(scriptClsInstance, field)).getClass().getSimpleName(), true);
                                    }

                                    ImGui.pushID(field.getName() + "_" + i);
                                    if(Script.getFieldValue(scriptClsInstance, field) != null) {
                                        ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                    } else {
                                        ImGui.pushStyleColor(ImGuiCol.Text, 0.65f, 0.65f, 0.65f, 1.0f);
                                        ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                        ImGui.popStyleColor(1);
                                    }
                                    ImGui.popID();

                                    if (ImGui.beginDragDropTarget()) {
                                        Object droppedObject = ImGui.acceptDragDropPayload("Component");
                                        if (droppedObject instanceof Component && droppedObject.getClass().isAssignableFrom(cs)) {
                                            Script.setFieldValue(scriptClsInstance, field, droppedObject);
                                        }
                                        ImGui.endDragDropTarget();
                                    }
                                }
                            }
                        }
                    }
                    case "AudioComponent" -> {
                        AudioComponent audioComponent = (AudioComponent) currentComponent;

                        ImString audioName = new ImString(new File(audioComponent.audio.path).getName());

                        ImGui.text("Source ID: " + audioComponent.audio.source);

                        ImGui.pushID("AudioPath_" + i);
                        ImGui.inputText("Path", audioName, ImGuiInputTextFlags.ReadOnly);
                        ImGui.popID();

                        if (ViewsManager.getResourcesView().getCurrentMovingFile() != null && ResourcesUtils.isFileImage(ViewsManager.getResourcesView().getCurrentMovingFile())) {
                            if (ImGui.beginDragDropTarget()) {
                                Object audioFile = ImGui.acceptDragDropPayload("File");
                                if (audioFile != null) {
                                    audioName.set(ViewsManager.getResourcesView().getCurrentMovingFile().getName(), true);
                                    String relativePath = FileUtils.getRelativePath(
                                            new File(ViewsManager.getResourcesView().getCurrentMovingFile().getPath()),
                                            new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                    audioComponent.audio.loadAndSetup(ViewsManager.getResourcesView().getCurrentMovingFile().getPath());
                                    audioComponent.audio.path = relativePath;
                                    ViewsManager.getResourcesView().setCurrentMovingFile(null);
                                }

                                ImGui.endDragDropTarget();
                            }
                        }

                        ImGui.pushID("AudioType_" + i);
                        if (ImGui.beginCombo("Type", audioComponent.audio.audioType.toString())) {
                            ImGui.pushID("AudioTypeSelectable0_" + i);
                            if (ImGui.selectable("Background")) {
                                audioComponent.audio.audioType = Audio.AudioType.BACKGROUND;
                            }
                            ImGui.popID();

                            ImGui.pushID("AudioTypeSelectable1_" + i);
                            if (ImGui.selectable("Worldspace")) {
                                audioComponent.audio.audioType = Audio.AudioType.WORLDSPACE;
                            }
                            ImGui.popID();

                            //System.out.println("x: " + rigidbody2DComponent.getRigidbody2D().getBody().getTransform().position.x + ", " + rigidbody2DComponent.getRigidbody2D().getBody().getTransform().position.y);

                            ImGui.endCombo();
                        }
                        ImGui.popID();

                        float[] maxDistance = new float[] { audioComponent.audio.getMaxDistance() };
                        ImGui.pushID("AudioMaxDistanceDragFloat_" + i);
                        if(ImGui.dragFloat("Max distance", maxDistance)) {
                            float res = Math.max(0, maxDistance[0]);
                            audioComponent.audio.setMaxDistance(res);
                        }
                        ImGui.popID();

                        float[] volumePercent = new float[] { audioComponent.audio.volumePercent };
                        ImGui.pushID("AudioVolumePercentDragFloat_" + i);
                        if(ImGui.dragFloat("Volume percent", volumePercent)) {
                            float res = Math.max(0, Math.min(volumePercent[0], 100.0f));
                            audioComponent.audio.volumePercent = res;
                        }
                        ImGui.popID();

                        ImGui.pushID("AudioIsCyclicCheckbox_" + i);
                        if(ImGui.checkbox("Cyclic", audioComponent.audio.isCyclic())) {
                            audioComponent.audio.setCyclic(!audioComponent.audio.isCyclic());
                        }
                        ImGui.popID();

                        ImGui.newLine();

                        //ImGui.progressBar((float) audioComponent.audio.getCurrentSecond() / (audioComponent.audio.audioInfo.getAudioLength() / 1000f), 120.0f, 5.0f, "");
                        float[] second = { audioComponent.audio.getCurrentSecond() };
                        //System.out.println("cur: " + second[0]+ ", len: " + audioComponent.audio.audioInfo.getAudioLength() / 1000f);

                        if(ImGuiUtils.sliderFloat(String.format("%.1f", audioComponent.audio.getCurrentSecond()),
                                second,
                                0f,
                                audioComponent.audio.audioInfo.getAudioLengthInSeconds(),
                                "AudioCurrentSecondSliderFloat_" + i,
                                "",
                                String.format("%.1f", audioComponent.audio.audioInfo.getAudioLengthInSeconds()))) {
                            audioComponent.audio.setCurrentSecond(second[0]);
                        }

                        Vector4f playButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                        boolean playing = audioComponent.audio.isPlaying();
                        if(playing) {
                            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                            playButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                        }
                        ImGui.pushID("AudioPlayButton_" + i);
                        if(ImGui.imageButton(Resources.Textures.Icons.playButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, playButtonColor.x, playButtonColor.y, playButtonColor.z, playButtonColor.w)) {
                            if(playing) {
                                audioComponent.audio.stop();
                            } else {
                                audioComponent.audio.play();
                            }
                        }
                        ImGui.popID();
                        if(playing) {
                            ImGui.popStyleColor(3);
                        }

                        Vector4f pauseButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                        boolean paused = audioComponent.audio.isPaused();
                        if(paused) {
                            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                            pauseButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                        }
                        ImGui.sameLine();
                        ImGui.pushID("AudioPauseButton_" + i);
                        if(ImGui.imageButton(Resources.Textures.Icons.pauseButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, pauseButtonColor.x, pauseButtonColor.y, pauseButtonColor.z, pauseButtonColor.w)) {
                            if(playing) {
                                if (paused) {
                                    audioComponent.audio.play();
                                } else {
                                    audioComponent.audio.pause();
                                }
                            }
                        }
                        ImGui.popID();
                        if(paused) {
                            ImGui.popStyleColor(3);
                        }

                        ImGui.sameLine();

                        ImGui.pushID("AudioStopButton_" + i);
                        if(ImGui.imageButton(Resources.Textures.Icons.stopButtonIcon.getTextureHandler(), 8, 10)) {
                            audioComponent.audio.stop();
                        }
                        ImGui.popID();
                    }
                    case "Camera2DComponent" -> {
                        Camera2DComponent camera2DComponent = (Camera2DComponent) currentComponent;
                        if(ImGui.checkbox("Scene2D main Camera2D", camera2DComponent.isScene2DMainCamera2D())) {
                            camera2DComponent.setScene2DMainCamera2D(!camera2DComponent.isScene2DMainCamera2D());
                        }
                    }
                }
            }

            ImGui.separator();
        }

        //ImGui.popStyleColor(3);

        ImVec2 textSize = new ImVec2();
        ImGui.calcTextSize(textSize, "Add component");
        ImVec2 windowSize = ImGui.getWindowSize();
        ImGui.setCursorPos(windowSize.x / 2.0f - textSize.x / 2.0f, ImGui.getCursorPosY());
        if (ImGui.button("Add component")) {
            action = "addEntityComponent";
        }

        if(ViewsManager.getResourcesView().getCurrentMovingFile() != null) {
            if (ImGui.beginDragDropTarget()) {
                String extension = FilenameUtils.getExtension(ViewsManager.getResourcesView().getCurrentMovingFile().getName());
                if (extension.equals("java")) {
                    Object droppedFile = ImGui.acceptDragDropPayload("File");
                    if (droppedFile instanceof File javaFile) {
                        ViewsManager.getInspectorView().compileAndAddScript(javaFile, inspectingEntity);
                    }
                } else if(extension.equals("wav")) {
                    Object droppedFile = ImGui.acceptDragDropPayload("File");
                    if(droppedFile instanceof File audioFile) {
                        AudioComponent audioComponent = new AudioComponent();
                        audioComponent.audio.loadAndSetup(audioFile.getPath());

                        String relativePath = FileUtils.getRelativePath(
                                new File(audioFile.getPath()),
                                new File(ProjectsManager.getCurrentProject().getProjectPath())
                        );
                        audioComponent.audio.path = relativePath;

                        inspectingEntity.addComponent(audioComponent);
                    }
                }
                ImGui.endDragDropTarget();
            }
        }

        drawAction(inspectingEntity);
        ImGui.end();
    }

    private void drawAction(Entity inspectingEntity)
    {
        if(action.equals("addEntityComponent")) {
            ImGui.newLine();

            ImVec2 windowSize = ImGui.getWindowSize();
            ImGui.setCursorPos(windowSize.x / 2.0f - 105.0f / 2.0f, ImGui.getCursorPosY());
            ImGui.setNextItemWidth(120.0f);

            ImGui.pushID("Components");
            {
                if(ImGui.beginListBox("")) {

                    try {
                        if(ImGui.selectable("TransformComponent")) {
                            inspectingEntity.addComponent(new TransformComponent());
                            action = "";
                        }
                        if (ImGui.selectable("MeshComponent")) {
                            inspectingEntity.addComponent(new MeshComponent());
                            action = "";
                        }
                        if (ImGui.selectable("Rigidbody2DComponent")) {
                            Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
                            inspectingEntity.addComponent(rigidbody2DComponent);
                            rigidbody2DComponent.getRigidbody2D().setType(BodyType.STATIC);
                            action = "";
                        }
                        if (ImGui.selectable("BoxCollider2DComponent")) {
                            inspectingEntity.addComponent(new BoxCollider2DComponent());
                            action = "";
                        }
                        if (ImGui.selectable("CircleCollider2DComponent")) {
                            inspectingEntity.addComponent(new CircleCollider2DComponent());
                            action = "";
                        }
                        if(ImGui.selectable("AudioComponent")) {
                            inspectingEntity.addComponent(new AudioComponent());
                            action = "";
                        }
                        if(ImGui.selectable("Camera2DComponent")) {
                            inspectingEntity.addComponent(new Camera2DComponent());
                            action = "";
                        }
                        if(ImGui.selectable("ParticlesSystemComponent")) {
                            inspectingEntity.addComponent(new ParticlesSystemComponent());
                            action = "";
                        }
                        if(ImGui.selectable("LineComponent")) {
                            inspectingEntity.addComponent(new LineComponent());
                            action = "";
                        }
                        if(ImGui.selectable("BoxComponent")) {
                            inspectingEntity.addComponent(new BoxComponent());
                            action = "";
                        }
                        if(ImGui.selectable("CircleComponent")) {
                            inspectingEntity.addComponent(new CircleComponent());
                            action = "";
                        }
                    } catch (Exception e) {
                        Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);

                        action = "";

                        ImGui.endListBox();
                        ImGui.popID();

                        return;
                    }

                    if (ImGui.isMouseClicked(ImGuiMouseButton.Left) && !ImGui.isAnyItemHovered()) {
                        action = "";
                    }

                    ImGui.endListBox();
                }
            }
            ImGui.popID();
        }
    }
}
