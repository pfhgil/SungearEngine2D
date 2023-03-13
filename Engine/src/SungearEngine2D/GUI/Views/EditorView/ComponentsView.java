package SungearEngine2D.GUI.Views.EditorView;

import Core2D.AssetManager.AssetManager;
import Core2D.Audio.OpenAL;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.Audio.AudioState;
import Core2D.ECS.Component.Components.Audio.AudioType;
import Core2D.ECS.Component.Components.Physics.BoxCollider2DComponent;
import Core2D.ECS.Component.Components.Physics.CircleCollider2DComponent;
import Core2D.ECS.Component.Components.Physics.Rigidbody2DComponent;
import Core2D.ECS.Component.Components.Primitives.BoxComponent;
import Core2D.ECS.Component.Components.Primitives.CircleComponent;
import Core2D.ECS.Component.Components.Primitives.LineComponent;
import Core2D.ECS.Component.Components.Shader.TextureComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.NonRemovable;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scripting.Script;
import Core2D.Utils.ComponentHandler;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.ImGuiUtils;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Resources;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.apache.commons.io.FilenameUtils;
import org.jbox2d.dynamics.BodyType;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL46C;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class ComponentsView extends View
{
    private boolean showPopupWindow = false;

    private boolean showAddPPPopupWindow = false;

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

            if(i == 0) {
                ImVec2 cursorPos = ImGui.getCursorPos();
                ImGui.setCursorPos(cursorPos.x, cursorPos.y + 5.0f);
            }

            boolean opened = false;
            if(!componentName.equals("ScriptComponent")) {
                opened = ImGui.collapsingHeader(componentName + ". ID: " + currentComponent.ID);
            } else {
                opened = ImGui.collapsingHeader(((ScriptComponent) currentComponent).script.getName() + " (" + componentName + ")" + ". ID: " + inspectingEntity.getComponents().get(i).ID);
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
                                transformComponent.position.x,
                                transformComponent.position.y
                        };
                        float[] rotation = new float[]{
                                transformComponent.rotation
                        };
                        float[] scale = new float[]{
                                transformComponent.scale.x,
                                transformComponent.scale.y
                        };
                        float[] centre = new float[]{
                                transformComponent.centre.x,
                                transformComponent.centre.y
                        };

                        if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Position", pos))) {
                            transformComponent.position.set(new Vector2f(pos));
                        }
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Rotation", rotation))) {
                            transformComponent.rotation = rotation[0];
                        }
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Scale", scale, 0.01f))) {
                            transformComponent.scale.set(new Vector2f(scale));
                        }
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Centre", centre, 0.01f))) {
                            transformComponent.centre.set(new Vector2f(centre));
                        }
                    }
                    case "MeshComponent" -> {
                        MeshComponent meshComponent = (MeshComponent) currentComponent;

                        ImString textureName = new ImString(new File(meshComponent.getTexture().path).getName());

                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText("Texture", textureName, ImGuiInputTextFlags.ReadOnly));

                        if (ImGui.beginDragDropTarget()) {
                            Object imageFile = ImGui.acceptDragDropPayload("File");
                            if (imageFile instanceof File file) {
                                textureName.set(file.getName(), true);
                                String relativePath = FileUtils.getRelativePath(
                                        file,
                                        new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                meshComponent.setTexture(new Texture2D(AssetManager.getInstance().getTexture2DData(relativePath)));
                                meshComponent.getTexture().path = relativePath;
                            }

                            ImGui.endDragDropTarget();
                        }

                        ImString shaderName = new ImString(new File(meshComponent.getShader().path).getName());

                        boolean[] shaderEditButtonPressed = new boolean[1];
                        Object[] droppedObject = new Object[1];
                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.inputTextWithRightButton("Shader", shaderName, ImGuiInputTextFlags.ReadOnly,
                                Resources.Textures.Icons.editIcon24.getTextureHandler(), shaderEditButtonPressed, "Edit shader", true, droppedObject, "File"));

                        if (droppedObject[0] instanceof File file) {
                            shaderName.set(file.getName(), true);
                            String relativePath = FileUtils.getRelativePath(
                                    file,
                                    new File(ProjectsManager.getCurrentProject().getProjectPath()));
                            meshComponent.setShader(new Shader(AssetManager.getInstance().getShaderData(relativePath)));
                            //meshComponent.getShader().path = relativePath;
                        }

                        if(shaderEditButtonPressed[0]) {
                            ViewsManager.getShadersEditorView().addEditingShader(
                                    new ShadersEditorView.ShaderEditorWindow(meshComponent.getShader(), new ComponentHandler(meshComponent.entity.getLayer().getID(), meshComponent.entity.ID, meshComponent.ID))
                            );
                        }
                    }
                    case "TextureComponent" -> {
                        TextureComponent textureComponent = (TextureComponent) currentComponent;

                        ImString textureName = new ImString(new File(textureComponent.getTexture().path).getName());

                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText("Texture", textureName, ImGuiInputTextFlags.ReadOnly));

                        if (ImGui.beginDragDropTarget()) {
                            Object imageFile = ImGui.acceptDragDropPayload("File");
                            if (imageFile instanceof File file) {
                                textureName.set(file.getName(), true);
                                String relativePath = FileUtils.getRelativePath(
                                        file,
                                        new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                textureComponent.setTexture(new Texture2D(AssetManager.getInstance().getTexture2DData(relativePath)));
                                textureComponent.getTexture().path = relativePath;
                            }

                            ImGui.endDragDropTarget();
                        }

                        String[] allTextureBlocks = new String[32];
                        for(int k = 0; k < allTextureBlocks.length; k++) {
                            allTextureBlocks[k] = "" + k;
                        }

                        ImInt chosenTextureBlock = new ImInt(textureComponent.getTexture().getFormattedTextureBlock());

                        ImGui.pushID("TextureBlockCombo_" + i);
                        ImGui.combo("Texture block", chosenTextureBlock, allTextureBlocks);
                        ImGui.popID();

                        textureComponent.getTexture().setTextureBlock(GL46C.GL_TEXTURE0 + chosenTextureBlock.get());
                    }
                    case "Rigidbody2DComponent" -> {
                        Rigidbody2DComponent rigidbody2DComponent = (Rigidbody2DComponent) currentComponent;

                        ImGui.pushID("Rigidbody2DType");
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.beginCombo("Type", rigidbody2DComponent.getRigidbody2D().typeToString()))) {

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
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Density", density, 0.01f))) {
                            rigidbody2DComponent.getRigidbody2D().setDensity(density[0]);
                        }
                        float[] restitution = new float[] { rigidbody2DComponent.getRigidbody2D().getRestitution() };
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Restitution", restitution, 0.01f))) {
                            rigidbody2DComponent.getRigidbody2D().setRestitution(restitution[0]);
                        }
                        float[] friction = new float[] { rigidbody2DComponent.getRigidbody2D().getFriction() };
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Friction", friction, 0.01f))) {
                            rigidbody2DComponent.getRigidbody2D().setFriction(friction[0]);
                        }
                        ImGui.separator();
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Sensor", rigidbody2DComponent.getRigidbody2D().isSensor()))) {
                            rigidbody2DComponent.getRigidbody2D().setSensor(!rigidbody2DComponent.getRigidbody2D().isSensor());
                        }
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Fixed rotation", rigidbody2DComponent.getRigidbody2D().isFixedRotation()))) {
                            rigidbody2DComponent.getRigidbody2D().setFixedRotation(!rigidbody2DComponent.getRigidbody2D().isFixedRotation());
                        }
                        ImGui.separator();
                    }
                    case "BoxCollider2DComponent" -> {
                        BoxCollider2DComponent boxCollider2DComponent = (BoxCollider2DComponent) currentComponent;

                        ImGui.pushID("BoxCollider2DOffsetDragFloat_" + i);
                        {
                            float[] offset = new float[] { boxCollider2DComponent.getBoxCollider2D().getOffset().x, boxCollider2DComponent.getBoxCollider2D().getOffset().y };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Offset", offset))) {
                                boxCollider2DComponent.getBoxCollider2D().setOffset(new Vector2f(offset[0], offset[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxCollider2DScaleDragFloat_" + i);
                        {
                            float[] scale = new float[] { boxCollider2DComponent.getBoxCollider2D().getScale().x, boxCollider2DComponent.getBoxCollider2D().getScale().y };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Scale", scale, 0.01f))) {
                                boxCollider2DComponent.getBoxCollider2D().setScale(new Vector2f(scale[0], scale[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxCollider2DRotationDragFloat_" + i);
                        {
                            float[] angle = new float[] { boxCollider2DComponent.getBoxCollider2D().getAngle() };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Rotation", angle))) {
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
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Offset", offset))) {
                                circleCollider2DComponent.getCircleCollider2D().setOffset(new Vector2f(offset[0], offset[1]));
                            }
                        }
                        ImGui.popID();

                        float[] radius = new float[] { circleCollider2DComponent.getCircleCollider2D().getRadius() };
                        ImGui.pushID("CircleCollider2DRadiusDragFloat_" + i);
                        {
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Radius", radius, 0.01f))) {
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
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Offset", offset))) {
                                lineComponent.getLinesData()[0].offset.set(offset[0], offset[1]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineStartDragFloat_" + i);
                        {
                            float[] start = new float[] { lineComponent.getLinesData()[0].getVertices()[0].x, lineComponent.getLinesData()[0].getVertices()[0].y };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Start", start))) {
                                lineComponent.getLinesData()[0].getVertices()[0].set(start[0], start[1]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineEndDragFloat_" + i);
                        {
                            float[] end = new float[] { lineComponent.getLinesData()[0].getVertices()[1].x, lineComponent.getLinesData()[0].getVertices()[1].y };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("End", end))) {
                                lineComponent.getLinesData()[0].getVertices()[1].set(end[0], end[1]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineColorDragFloat_" + i);
                        {
                            float[] color = new float[] { lineComponent.getLinesData()[0].color.x, lineComponent.getLinesData()[0].color.y,
                                    lineComponent.getLinesData()[0].color.z, lineComponent.getLinesData()[0].color.w };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.colorEdit4("Color", color))) {
                                lineComponent.getLinesData()[0].color.set(color[0], color[1], color[2], color[3]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineWidthDragFloat_" + i);
                        {
                            float[] width = new float[] { lineComponent.getLinesData()[0].lineWidth };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Width", width, 0.1f, 1.0f, 20.0f))) {
                                width[0] = Math.clamp(1, 20, width[0]);
                                lineComponent.getLinesData()[0].lineWidth = width[0];
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("LineScaleWithEntityCheckbox_" + i);
                        {
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Scale with entity", lineComponent.scaleWithEntity))) {
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
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Offset", offset))) {
                                boxComponent.setOffset(new Vector2f(offset[0], offset[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxSizeDragFloat_" + i);
                        {
                            float[] size = new float[] { boxComponent.getSize().x, boxComponent.getSize().y };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Size", size))) {
                                boxComponent.setSize(new Vector2f(size[0], size[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxLinesWidthDragFloat_" + i);
                        {
                            float[] lineWidth = new float[] { boxComponent.getLinesWidth() };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Lines width", lineWidth, 0.1f, 1.0f, 20.0f))) {
                                lineWidth[0] = Math.clamp(1, 20, lineWidth[0]);
                                boxComponent.setLinesWidth(lineWidth[0]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxColorDragFloat_" + i);
                        {
                            float[] color = new float[] { boxComponent.getColor().x, boxComponent.getColor().y,
                                    boxComponent.getColor().z, boxComponent.getColor().w };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.colorEdit4("Color", color))) {
                                boxComponent.setColor(new Vector4f(color[0], color[1], color[2], color[3]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("BoxScaleWithEntityCheckbox_" + i);
                        {
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Scale with entity", boxComponent.scaleWithEntity))) {
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
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Offset", offset))) {
                                circleComponent.setOffset(new Vector2f(offset[0], offset[1]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleRadiusDragFloat_" + i);
                        {
                            float[] radius = new float[] { circleComponent.getRadius() };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Radius", radius, 0.1f))) {
                                circleComponent.setRadius(radius[0]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleLinesWidthDragFloat_" + i);
                        {
                            float[] lineWidth = new float[] { circleComponent.getLinesWidth() };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Lines width", lineWidth, 0.1f, 1.0f, 20.0f))) {
                                lineWidth[0] = Math.clamp(1.0f, 20.0f, lineWidth[0]);
                                circleComponent.setLinesWidth(lineWidth[0]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleColorDragFloat_" + i);
                        {
                            float[] color = new float[] { circleComponent.getColor().x, circleComponent.getColor().y,
                                    circleComponent.getColor().z, circleComponent.getColor().w };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.colorEdit4("Color", color))) {
                                circleComponent.setColor(new Vector4f(color[0], color[1], color[2], color[3]));
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleAngleIncrementDragInt_" + i);
                        {
                            int[] angleIncrement = new int[] { circleComponent.getAngleIncrement() };
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragInt("Angle increment", angleIncrement, 1, 1, 360))) {
                                angleIncrement[0] = Math.clamp(1, 360, angleIncrement[0]);
                                circleComponent.setAngleIncrement(angleIncrement[0]);
                            }
                        }
                        ImGui.popID();

                        ImGui.pushID("CircleScaleWithEntityCheckbox_" + i);
                        {
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Scale with entity", circleComponent.scaleWithEntity))) {
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
                                    if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat(field.getName(), floats))) {
                                        Script.setFieldValue(scriptClsInstance, field, floats[0]);
                                    }
                                    ImGui.popID();
                                } else if (cs.isAssignableFrom(String.class)) {
                                    ImString string = new ImString((String) Script.getFieldValue(scriptClsInstance, field), 128);

                                    ImGui.pushID(field.getName() + "_" + i);
                                    ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly));
                                    ImGui.popID();

                                    Script.setFieldValue(scriptClsInstance, field, string.get());
                                } else if (cs.isAssignableFrom(Entity.class)) {
                                    ImString string = new ImString(cs.getSimpleName());
                                    if (Script.getFieldValue(scriptClsInstance, field) != null) {
                                        string.set(((Entity) Script.getFieldValue(scriptClsInstance, field)).name, true);
                                    }

                                    ImGui.pushID(field.getName() + "_" + i);
                                    if(Script.getFieldValue(scriptClsInstance, field) != null) {
                                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly));
                                    } else {
                                        ImGui.pushStyleColor(ImGuiCol.Text, 0.65f, 0.65f, 0.65f, 1.0f);
                                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly));
                                        ImGui.popStyleColor();
                                    }
                                    ImGui.popID();

                                    if (ImGui.beginDragDropTarget()) {
                                        Object droppedObject = ImGui.acceptDragDropPayload("Entity");
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
                                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly));
                                    } else {
                                        ImGui.pushStyleColor(ImGuiCol.Text, 0.65f, 0.65f, 0.65f, 1.0f);
                                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly));
                                        ImGui.popStyleColor();
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

                        ImString audioName = new ImString(audioComponent.path);

                        ImGui.text("Source ID: " + audioComponent.sourceHandler);

                        ImGui.pushID("AudioPath_" + i);
                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText("Path", audioName, ImGuiInputTextFlags.ReadOnly));
                        ImGui.popID();

                        if (ImGui.beginDragDropTarget()) {
                            Object audioFile = ImGui.acceptDragDropPayload("File");
                            if (audioFile instanceof File file) {
                                audioName.set(file.getName(), true);
                                String relativePath = FileUtils.getRelativePath(
                                        new File(file.getPath()),
                                        new File(ProjectsManager.getCurrentProject().getProjectPath()));

                                ECSWorld.getCurrentECSWorld().audioSystem.setAudioComponentData(audioComponent, AssetManager.getInstance().getAudioData(relativePath));
                                //audioComponent.audio.loadAndSetup(ViewsManager.getResourcesView().getCurrentMovingFile().getPath());
                                //audioComponent.audio.path = relativePath;
                                audioComponent.path = relativePath;
                            }
                            ImGui.endDragDropTarget();
                        }

                        ImGui.pushID("AudioType_" + i);
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.beginCombo("Type", audioComponent.type.toString()))) {
                            ImGui.pushID("AudioTypeSelectable0_" + i);
                            if (ImGui.selectable("Background")) {
                                audioComponent.type = AudioType.BACKGROUND;
                            }
                            ImGui.popID();

                            ImGui.pushID("AudioTypeSelectable1_" + i);
                            if (ImGui.selectable("Worldspace")) {
                                audioComponent.type =AudioType.WORLDSPACE;
                            }
                            ImGui.popID();

                            //System.out.println("x: " + rigidbody2DComponent.getRigidbody2D().getBody().getTransform().position.x + ", " + rigidbody2DComponent.getRigidbody2D().getBody().getTransform().position.y);

                            ImGui.endCombo();
                        }
                        ImGui.popID();

                        float[] maxDistance = new float[] { audioComponent.maxDistance };
                        ImGui.pushID("AudioMaxDistanceDragFloat_" + i);
                        if(ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Max distance", maxDistance))) {
                            audioComponent.maxDistance = Math.max(0, maxDistance[0]);
                        }
                        ImGui.popID();

                        float[] volumePercent = new float[] { audioComponent.volumePercent };
                        ImGui.pushID("AudioVolumePercentDragFloat_" + i);
                        if(ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Volume percent", volumePercent))) {
                            audioComponent.volumePercent = Math.max(0, Math.min(volumePercent[0], 100.0f));
                        }
                        ImGui.popID();

                        ImGui.pushID("AudioIsCyclicCheckbox_" + i);
                        if(ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Cyclic", audioComponent.cyclic))) {
                            audioComponent.cyclic = !audioComponent.cyclic;
                        }
                        ImGui.popID();

                        if(!OpenAL.alCall(params -> AL10.alIsSource(audioComponent.sourceHandler), Boolean.class)) continue;

                        ImGui.newLine();
                        //ImGui.progressBar((float) audioComponent.audio.getCurrentSecond() / (audioComponent.audio.audioInfo.getAudioLength() / 1000f), 120.0f, 5.0f, "");
                        float[] second = { audioComponent.currentSecond };
                        //System.out.println("cur: " + second[0]+ ", len: " + audioComponent.audio.audioInfo.getAudioLength() / 1000f);

                        if(ImGuiUtils.sliderFloat(String.format("%.1f", audioComponent.currentSecond),
                                second,
                                0f,
                                audioComponent.audioLengthInSeconds,
                                "AudioCurrentSecondSliderFloat_" + i,
                                "",
                                String.format("%.1f", audioComponent.audioLengthInSeconds))) {
                            audioComponent.currentSecond = second[0];
                        }

                        AudioState lastAudioState = audioComponent.state;

                        Vector4f playButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                        if(lastAudioState == AudioState.PLAYING) {
                            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                            playButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                        }
                        ImGui.pushID("AudioPlayButton_" + i);
                        if(ImGui.imageButton(Resources.Textures.Icons.playButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, playButtonColor.x, playButtonColor.y, playButtonColor.z, playButtonColor.w)) {
                            if(audioComponent.state == AudioState.PLAYING) {
                                audioComponent.state = AudioState.STOPPED;
                            } else {
                                audioComponent.state = AudioState.PLAYING;
                                //audioComponent.audio.play();
                            }
                        }
                        ImGui.popID();
                        if(lastAudioState == AudioState.PLAYING) {
                            ImGui.popStyleColor(3);
                        }

                        Vector4f pauseButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                        if(lastAudioState == AudioState.PAUSED) {
                            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                            pauseButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                        }
                        ImGui.sameLine();
                        ImGui.pushID("AudioPauseButton_" + i);
                        if(ImGui.imageButton(Resources.Textures.Icons.pauseButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, pauseButtonColor.x, pauseButtonColor.y, pauseButtonColor.z, pauseButtonColor.w)) {
                            if (audioComponent.state == AudioState.PAUSED) {
                                audioComponent.state = AudioState.PLAYING;
                            } else {
                                audioComponent.state = AudioState.PAUSED;
                            }
                        }
                        ImGui.popID();
                        if(lastAudioState == AudioState.PAUSED) {
                            ImGui.popStyleColor(3);
                        }

                        ImGui.sameLine();

                        ImGui.pushID("AudioStopButton_" + i);
                        if(ImGui.imageButton(Resources.Textures.Icons.stopButtonIcon.getTextureHandler(), 8, 10)) {
                            audioComponent.state = AudioState.STOPPED;
                        }
                        ImGui.popID();
                    }
                    case "Camera2DComponent" -> {
                        Camera2DComponent camera2DComponent = (Camera2DComponent) currentComponent;

                        ImGui.pushID("Scene2DMainCamera_" + i);
                        if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Scene2D main camera", camera2DComponent.scene2DMainCamera2D))) {
                            ECSWorld.getCurrentECSWorld().componentsInitializerSystem.setScene2DMainCamera2D(camera2DComponent, !camera2DComponent.scene2DMainCamera2D);
                            //camera2DComponent.setScene2DMainCamera2D(!camera2DComponent.isScene2DMainCamera2D);
                        }
                        ImGui.popID();

                        ImGui.separator();

                        ImGui.pushID("CameraTransformations_" + i);
                        if (ImGui.treeNode("Transformations")) {
                            ImGui.pushID("CameraFollowTranslation_" + i);
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Follow translation", camera2DComponent.followTranslation))) {
                                camera2DComponent.followTranslation = !camera2DComponent.followTranslation;
                            }
                            ImGui.popID();

                            ImGui.pushID("CameraFollowRotation_" + i);
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Follow rotation", camera2DComponent.followRotation))) {
                                camera2DComponent.followRotation = !camera2DComponent.followRotation;
                            }
                            ImGui.popID();

                            ImGui.pushID("CameraFollowScale_" + i);
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Follow scale", camera2DComponent.followScale))) {
                                camera2DComponent.followScale = !camera2DComponent.followScale;
                            }
                            ImGui.popID();

                            ImGui.newLine();

                            float[] pos = new float[]{
                                    camera2DComponent.position.x,
                                    camera2DComponent.position.y
                            };
                            float[] rotation = new float[]{
                                    camera2DComponent.rotation
                            };
                            float[] scale = new float[]{
                                    camera2DComponent.scale.x,
                                    camera2DComponent.scale.y
                            };

                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Position", pos))) {
                                camera2DComponent.position.set(new Vector2f(pos));
                            }
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat("Rotation", rotation))) {
                                camera2DComponent.rotation = rotation[0];
                            }
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.dragFloat2("Scale", scale, 0.01f))) {
                                camera2DComponent.scale.set(new Vector2f(scale));
                            }

                            ImGui.treePop();
                        }
                        ImGui.popID();

                        ImGui.pushID("CameraRendering_" + i);
                        if (ImGui.treeNode("Rendering")) {

                            ImGui.pushID("CameraRender_" + i);
                            if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Render", camera2DComponent.render))) {
                                camera2DComponent.render = !camera2DComponent.render;
                            }
                            ImGui.popID();


                            int k = 0;

                            ImGui.pushID("CameraPP_" + i);
                            if (ImGui.treeNode("Postprocessing")) {
                                if (ImGuiUtils.arrowButton("Add PP layer...", "AddPPLayerButton_" + i, true)) {
                                    ImGui.pushID("PPLayersToAddListBox_" + i);
                                    ImGui.sameLine();
                                    if (ImGuiUtils.imCallWBorder(func -> ImGui.beginListBox("", 150.0f, 75.0f))) {
                                        for (Layer layer : currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                                            if (!ECSWorld.getCurrentECSWorld().camerasManagerSystem.isPostprocessingLayerExists(camera2DComponent, layer)) {
                                                ImGui.pushID("PPLayerToAdd_" + k + "_" + i);
                                                if (ImGui.selectable(layer.getName())) {
                                                    camera2DComponent.postprocessingLayers.add(new PostprocessingLayer(layer));
                                                    ImGuiUtils.setArrowButtonRetention("AddPPLayerButton_" + i, false);
                                                }
                                                ImGui.popID();
                                            }
                                            k++;
                                        }
                                        ImGui.endListBox();
                                    }
                                    ImGui.popID();
                                }

                                k = 0;
                                Iterator<PostprocessingLayer> ppCamLayersIterator = camera2DComponent.postprocessingLayers.listIterator();
                                while (ppCamLayersIterator.hasNext()) {
                                    PostprocessingLayer ppLayer = ppCamLayersIterator.next();
                                    ImGui.pushID("PPLayer_" + k + "_" + i);
                                    if (ImGui.treeNode("Postprocessing layer \"" + (ppLayer.getEntitiesLayerToRender() != null ? ppLayer.getEntitiesLayerToRender().getName() + "\"" : "unknown\""))) {

                                        ImGui.pushID("CameraPPLayerRender_" + k + "_" + i);
                                        if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Render", ppLayer.render))) {
                                            ppLayer.render = !ppLayer.render;
                                        }
                                        ImGui.popID();

                                        ImGui.pushID("CameraPPLayerOverlay_" + k + "_" + i);
                                        if (ImGuiUtils.imCallWBorder(func -> ImGui.checkbox("Overlay", ppLayer.overlay))) {
                                            ppLayer.overlay = !ppLayer.overlay;
                                        }
                                        ImGui.popID();

                                        ImGui.newLine();

                                        ImString shaderName = new ImString(new File(ppLayer.getShader().path).getName());

                                        boolean[] shaderEditButtonPressed = new boolean[1];
                                        Object[] droppedObject = new Object[1];
                                        ImGuiUtils.imCallWBorder(func -> ImGuiUtils.inputTextWithRightButton("Shader", shaderName, ImGuiInputTextFlags.ReadOnly,
                                                Resources.Textures.Icons.editIcon24.getTextureHandler(), shaderEditButtonPressed, "Edit shader", true, droppedObject, "File"));

                                        if (droppedObject[0] instanceof File file) {
                                            shaderName.set(file.getName(), true);
                                            String relativePath = FileUtils.getRelativePath(
                                                    new File(file.getPath()),
                                                    new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                            ppLayer.setShader(new Shader(AssetManager.getInstance().getShaderData(relativePath)));
                                            //ppLayer.getShader().path = relativePath;
                                        }

                                        if (shaderEditButtonPressed[0]) {
                                            ShadersEditorView.ShaderEditorWindow newShaderEditorWindow = new ShadersEditorView.ShaderEditorWindow(
                                                    ppLayer.getShader(),
                                                    new ComponentHandler(camera2DComponent.entity.getLayer().getID(),
                                                            camera2DComponent.entity.ID, camera2DComponent.ID));
                                            newShaderEditorWindow.ppLayerName = ppLayer.getEntitiesLayerToRenderName();
                                            ViewsManager.getShadersEditorView().addEditingShader(newShaderEditorWindow);
                                        }


                                        if (ImGui.button("View...")) {
                                            if (!ViewsManager.isFBOViewExists("PPLayerView_" + i + "_" + k)) {
                                                ViewsManager.getFBOViews().add(new GameView("PPLayer \"" + ppLayer.getEntitiesLayerToRenderName() + "\"", "PPLayerView_" + i + "_" + k,
                                                        ppLayer.getFrameBuffer().getTextureHandler(), true, ppLayer, new ComponentHandler(camera2DComponent.entity.getLayer().getID(), camera2DComponent.entity.ID, camera2DComponent.ID)));
                                            } else {
                                                GameView gameView = ViewsManager.getFBOView("PPLayerView_" + i + "_" + k);
                                                gameView.setPostprocessingLayer(ppLayer, new ComponentHandler(camera2DComponent.entity.getLayer().getID(), camera2DComponent.entity.ID, camera2DComponent.ID));
                                            }
                                        }

                                        ImGui.newLine();

                                        ImGui.pushID("RemovePPLayer" + k + "_" + i);
                                        if (ImGui.button("Remove")) {
                                            ppLayer.destroy();
                                            ppCamLayersIterator.remove();
                                        }
                                        ImGui.popID();

                                        if (camera2DComponent.postprocessingLayers.size() > 1 && k != camera2DComponent.postprocessingLayers.size() - 1) {
                                            ImGui.separator();
                                        }

                                        ImGui.treePop();
                                    }
                                    ImGui.popID();

                                    k++;
                                }
                                ImGui.treePop();
                            }
                            ImGui.popID();

                            ImGui.treePop();
                        }
                        ImGui.popID();
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
                    if(droppedFile instanceof File file) {
                        String relativePath = FileUtils.getRelativePath(
                                file,
                                new File(ProjectsManager.getCurrentProject().getProjectPath())
                        );
                        AudioComponent audioComponent = ECSWorld.getCurrentECSWorld().audioSystem.createAudioComponent(AssetManager.getInstance().getAudioData(relativePath));
                        audioComponent.path = relativePath;

                        //Log.CurrentSession.println("is source: " + audioComponent.sourceHandler, Log.MessageType.SUCCESS);
                        //audioComponent.audio.path = relativePath;

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
                        if(ImGui.selectable("ProgramTimeComponent")) {
                            inspectingEntity.addComponent(new ProgramTimeComponent());
                            action = "";
                        }
                        if(ImGui.selectable("TextureComponent")) {
                            inspectingEntity.addComponent(new TextureComponent());
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
