package SungearEngine2D.GUI.Views.EditorView;

import Core2D.AssetManager.AssetManager;
import Core2D.Audio.Audio;
import Core2D.Component.Component;
import Core2D.Component.Components.*;
import Core2D.Component.NonRemovable;
import Core2D.GameObject.GameObject;
import Core2D.GameObject.RenderParts.Texture2D;
import Core2D.Input.PC.Keyboard;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.SceneManager;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import SungearEngine2D.GUI.ImGuiUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Main.EngineSettings;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.ResourcesUtils;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.internal.ImGuiContext;
import imgui.internal.ImRect;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.apache.commons.io.FilenameUtils;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;
import static org.lwjgl.opengl.GL11.GL_DST_ALPHA;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL14.*;

public class InspectorView extends View
{
    // текущий просматриваемый объект
    private Object currentInspectingObject;

    private boolean isEditing = false;

    // текущее действие
    private String action = "";

    private DialogWindow dialogWindow;

    // имя нового чего-то
    private ImString newName = new ImString();

    // имя текущего изменяемого чего-то
    private ImString currentEditingName = new ImString();
    // имя текущего чего-то
    private ImString currentName = new ImString();
    // id изменяемого чего-то
    private int currentEditingID = -1;

    // нужно ли показывать выпадающее меню
    private boolean showPopupWindow = false;
    // наведена ли мышь на какую-нибудь из кнопок в впадающем меню
    private boolean someButtonInPopupWindowHovered = false;

    // текущий изменяемый компонент
    private Component currentEditingComponent;
    private int currentEditingComponentID = -1;

    private int currentHoveredComponentID = -1;

    // текущий перемещаемый в это окно файл
    public File droppingFile;

    public InspectorView()
    {
        init();
    }

    @Override
    public void init()
    {
        dialogWindow = new DialogWindow("Add layer", "Cancel", "Add");
    }

    public void draw()
    {
        ImGui.begin("Inspector", ImGuiWindowFlags.NoMove);
        {
            if(currentInspectingObject != null) {
                if(currentInspectingObject instanceof GameObject) {
                    GameObject inspectingGameObject = (GameObject) currentInspectingObject;
                    inspectObject2D(inspectingGameObject);
                }
            }

            update();
        }
        ImGui.end();
    }

    private void inspectObject2D(GameObject inspectingObject2D)
    {
        setCurrentInspectingObject(inspectingObject2D);
        if(inspectingObject2D != null && !inspectingObject2D.isShouldDestroy()) {
            if (showPopupWindow) {
                ImGui.openPopup("Component actions");
                if (ImGui.beginPopupContextWindow("Component actions", ImGuiMouseButton.Left)) {
                    if (!(currentEditingComponent instanceof NonRemovable)) {
                        boolean deleteClicked = ImGui.menuItem("Remove");
                        someButtonInPopupWindowHovered = ImGui.isItemHovered();
                        if (deleteClicked) {
                            try {
                                inspectingObject2D.removeComponent(currentEditingComponent.getClass());
                            } catch (Exception e) {
                                ImGui.endPopup();
                            }
                            showPopupWindow = false;
                        }
                    }

                    ImGui.endPopup();
                }
            }

            ImGui.image(Resources.Textures.Icons.object2DFileIcon.getTextureHandler(), 27.0f, 27.0f);

            ImGui.sameLine();
            ImGui.pushID("Object2DName");
            {
                ImString name = new ImString(inspectingObject2D.name, 256);
                if (ImGui.inputText("", name)) {
                    inspectingObject2D.name = name.get();
                    isEditing = true;
                }
            }
            ImGui.popID();

            ImGui.text("Layer");
            ImGui.sameLine();
            ImGui.pushID("LayersCombo");
            {
                if (ImGui.beginCombo("", inspectingObject2D.layerName)) {
                    List<Layer> layers = currentSceneManager.getCurrentScene2D().getLayering().getLayers();
                    for (int i = 0; i < layers.size(); i++) {
                        boolean selected = ImGui.selectable(layers.get(i).getID() + ".  " + layers.get(i).getName());

                        if (ImGui.beginDragDropSource()) {
                            ImGui.text(layers.get(i).getName());
                            ImGui.setDragDropPayload("Layer", layers.get(i));
                            ImGui.endDragDropSource();
                        }

                        if (ImGui.beginDragDropTarget()) {
                            Object droppedObject = ImGui.acceptDragDropPayload("Layer");

                            if (droppedObject != null) {
                                Layer droppedLayer = (Layer) droppedObject;

                                // меняю слои местами и сортирую
                                int thisLayerID = layers.get(i).getID();
                                layers.get(i).setID(droppedLayer.getID());
                                droppedLayer.setID(thisLayerID);

                                currentSceneManager.getCurrentScene2D().getLayering().sort();
                            }

                            ImGui.endDragDropTarget();
                        }

                        if (selected) {
                            inspectingObject2D.setLayer(currentSceneManager.getCurrentScene2D().getLayering().getLayers().get(i));
                        }
                    }
                    layers = null;

                    ImGui.separator();

                    if (ImGui.selectable("Add layer...")) {
                        action = "addLayer";
                        dialogWindow.setWindowName("Add layer");
                        dialogWindow.setButtonsNum(2);
                        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                            @Override
                            public void onDraw() {
                                ImGui.text("Layer name");
                                ImGui.sameLine();
                                ImGui.pushID("NewLayerNameInputText");
                                {
                                    ImGui.inputText("", newName);
                                    if (!ImGui.isItemActive() && Keyboard.keyReleased(GLFW.GLFW_KEY_ENTER)) {
                                        onRightButtonClicked();
                                    }
                                }
                                ImGui.popID();
                            }

                            @Override
                            public void onMiddleButtonClicked() {

                            }

                            @Override
                            public void onLeftButtonClicked() {
                                dialogWindow.setActive(false);
                            }

                            @Override
                            public void onRightButtonClicked() {
                                if (!newName.get().equals("")) {
                                    currentSceneManager.getCurrentScene2D().getLayering().addLayer(new Layer(currentSceneManager.getCurrentScene2D().getLayering().getLayers().size(), newName.get()));
                                    dialogWindow.setActive(false);
                                    newName.set("", true);
                                }
                            }
                        });
                        dialogWindow.setActive(true);
                    }

                    if (ImGui.selectable("Edit layers...")) {
                        action = "editLayers";
                        dialogWindow.setWindowName("Edit layers");
                        dialogWindow.setButtonsNum(1);
                        dialogWindow.setMiddleButtonText("OK");
                        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                            @Override
                            public void onDraw() {
                                ImGui.beginChild("EditLayersChildWindow", dialogWindow.getCurrentWindowSize().x - 17, dialogWindow.getCurrentWindowSize().y - 75, true);
                                {
                                    for (int i = 0; i < currentSceneManager.getCurrentScene2D().getLayering().getLayers().size(); i++) {
                                        Layer currentLayer = currentSceneManager.getCurrentScene2D().getLayering().getLayers().get(i);
                                        if (currentLayer.getName().equals(currentEditingName.get())) {
                                            ImGui.setNextItemOpen(true, ImGuiCond.Once);
                                        }
                                        boolean opened = ImGui.collapsingHeader("Layer \"" + currentLayer.getName() + "\"");

                                        if (opened) {
                                            ImGui.text("ID: " + currentLayer.getID());

                                            if (!currentLayer.getName().equals("default")) {
                                                ImGui.pushID("Layer_" + currentLayer.getName() + "_InputText");
                                                {
                                                    if (currentEditingID != i) {
                                                        currentName.set(currentLayer.getName(), true);
                                                        ImGui.inputText("", currentName, ImGuiInputTextFlags.ReadOnly);
                                                        if (ImGui.isItemClicked()) {
                                                            currentEditingID = i;
                                                            currentEditingName.set(currentLayer.getName(), true);
                                                        }
                                                    } else {
                                                        ImGui.inputText("", currentEditingName);
                                                        if (ImGui.isItemDeactivatedAfterEdit()) {
                                                            currentLayer.setName(currentEditingName.get());
                                                            currentEditingID = -1;
                                                        }
                                                    }
                                                }
                                                ImGui.popID();

                                                if (ImGui.button("Remove")) {
                                                    currentSceneManager.getCurrentScene2D().getLayering().deleteLayer(currentLayer);
                                                }
                                            }
                                        }
                                    }
                                }
                                ImGui.endChild();
                            }

                            @Override
                            public void onMiddleButtonClicked() {
                                dialogWindow.setActive(false);
                                currentEditingName.set("", true);
                            }

                            @Override
                            public void onLeftButtonClicked() {

                            }

                            @Override
                            public void onRightButtonClicked() {

                            }
                        });
                        dialogWindow.setActive(true);
                    }

                    ImGui.endCombo();
                }
            }
            ImGui.popID();

            ImGui.text("   Tag");
            ImGui.sameLine();
            ImGui.pushID("TagsCombo");
            {
                if (ImGui.beginCombo("", inspectingObject2D.tag.getName())) {
                    List<Tag> tags = currentSceneManager.getCurrentScene2D().getTags();
                    for (int i = 0; i < tags.size(); i++) {
                        if (ImGui.selectable(tags.get(i).getName())) {
                            inspectingObject2D.tag.set(tags.get(i));
                        }
                    }

                    ImGui.separator();

                    if (ImGui.selectable("Add tag...")) {
                        action = "addTag";
                        dialogWindow.setWindowName("Add tag");
                        dialogWindow.setButtonsNum(2);
                        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                            @Override
                            public void onDraw() {
                                ImGui.text("Tag name");
                                ImGui.sameLine();
                                ImGui.pushID("NewTagNameInputText");
                                {
                                    ImGui.inputText("", newName);
                                    if (!ImGui.isItemActive() && Keyboard.keyReleased(GLFW.GLFW_KEY_ENTER)) {
                                        onRightButtonClicked();
                                    }
                                }
                                ImGui.popID();
                            }

                            @Override
                            public void onMiddleButtonClicked() {

                            }

                            @Override
                            public void onLeftButtonClicked() {
                                dialogWindow.setActive(false);
                            }

                            @Override
                            public void onRightButtonClicked() {
                                if (!newName.get().equals("")) {
                                    currentSceneManager.getCurrentScene2D().addTag(new Tag(newName.get()));
                                    dialogWindow.setActive(false);
                                    newName.set("", true);
                                }
                            }
                        });
                        dialogWindow.setActive(true);
                    }

                    if (ImGui.selectable("Edit tags...")) {
                        action = "editTags";
                        dialogWindow.setWindowName("Edit tags");
                        dialogWindow.setButtonsNum(1);
                        dialogWindow.setMiddleButtonText("OK");
                        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                            @Override
                            public void onDraw() {
                                ImGui.beginChild("EditTagsChildWindow", dialogWindow.getCurrentWindowSize().x - 17, dialogWindow.getCurrentWindowSize().y - 75, true);
                                {
                                    for (int i = 0; i < currentSceneManager.getCurrentScene2D().getTags().size(); i++) {
                                        Tag currentTag = currentSceneManager.getCurrentScene2D().getTags().get(i);
                                        if (currentTag.getName().equals(currentEditingName.get())) {
                                            ImGui.setNextItemOpen(true, ImGuiCond.Once);
                                        }
                                        boolean opened = ImGui.collapsingHeader("Tag \"" + currentTag.getName() + "\"");

                                        if (opened) {
                                            if (!currentTag.getName().equals("default")) {
                                                ImGui.pushID("Tag_" + currentTag.getName() + "_InputText");
                                                {
                                                    if (currentEditingID != i) {
                                                        currentName.set(currentTag.getName(), true);
                                                        ImGui.inputText("", currentName, ImGuiInputTextFlags.ReadOnly);
                                                        if (ImGui.isItemClicked()) {
                                                            currentEditingID = i;
                                                            currentEditingName.set(currentTag.getName(), true);
                                                        }
                                                    } else {
                                                        ImGui.inputText("", currentEditingName);
                                                        if (ImGui.isItemDeactivatedAfterEdit()) {
                                                            currentTag.setName(currentEditingName.get());
                                                            currentEditingID = -1;
                                                        }
                                                    }
                                                }
                                                ImGui.popID();

                                                if (ImGui.button("Remove")) {
                                                    currentSceneManager.getCurrentScene2D().deleteTag(currentTag);
                                                }
                                            }
                                        }
                                    }
                                }
                                ImGui.endChild();
                            }

                            @Override
                            public void onMiddleButtonClicked() {
                                dialogWindow.setActive(false);
                                currentEditingName.set("", true);
                            }

                            @Override
                            public void onLeftButtonClicked() {

                            }

                            @Override
                            public void onRightButtonClicked() {

                            }
                        });
                        dialogWindow.setActive(true);
                    }

                    ImGui.endCombo();
                }
            }
            ImGui.popID();

            ImGui.separator();

            float[] col = new float[] {
                    inspectingObject2D.getColor().x,
                            inspectingObject2D.getColor().y,
                            inspectingObject2D.getColor().z,
                            inspectingObject2D.getColor().w  };
            if (ImGui.colorEdit4("Color", col)) {
                inspectingObject2D.setColor(new Vector4f(col));
                isEditing = true;
            }

            ImVec4 windowBg = ImGui.getStyle().getColor(ImGuiCol.WindowBg);
            ImGui.pushStyleColor(ImGuiCol.Header, windowBg.x, windowBg.y, windowBg.z, windowBg.w);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, windowBg.x, windowBg.y, windowBg.z, windowBg.w);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, windowBg.x, windowBg.y, windowBg.z, windowBg.w);

            boolean someItemHovered = false;

            for (int i = 0; i < inspectingObject2D.getComponents().size(); i++) {
                Component currentComponent = inspectingObject2D.getComponents().get(i);
                String componentName = currentComponent.getClass().getSimpleName();

                ImGui.pushID(componentName + i);
                boolean opened = false;
                if(!componentName.equals("ScriptComponent")) {
                    opened = ImGui.collapsingHeader(componentName + ". ID: " + currentComponent.componentID);
                } else {
                    opened = ImGui.collapsingHeader(((ScriptComponent) currentComponent).getScript().getName() + " (" + componentName + ")" + ". ID: " + inspectingObject2D.getComponents().get(i).componentID);
                }
                if(ImGui.beginDragDropSource()) {
                    ImGui.setDragDropPayload("Component", currentComponent);
                    ImGui.text(componentName);
                    ImGui.endDragDropSource();
                }
                ImGui.popID();
                ImGui.sameLine();

                ImVec2 headerSize = ImGui.getItemRectSize();

                ImGui.setCursorPosX(headerSize.x - headerSize.y);
                if (currentHoveredComponentID == i) {
                    ImGui.image(Resources.Textures.Icons.threeDotsIcon.getTextureHandler(), headerSize.y, headerSize.y, 0, 0, 1, 1, 0.6f, 0.6f, 0.6f, 1.0f);
                } else {
                    ImGui.image(Resources.Textures.Icons.threeDotsIcon.getTextureHandler(), headerSize.y, headerSize.y);
                }

                ImVec2 minRect = ImGui.getItemRectMin();
                ImVec2 maxRect = ImGui.getItemRectMax();

                if (ImGui.isMouseHoveringRect(minRect.x, minRect.y, maxRect.x, maxRect.y)) {
                    currentHoveredComponentID = i;
                    someItemHovered = true;
                }

                if (ImGui.isMouseHoveringRect(minRect.x, minRect.y, maxRect.x, maxRect.y) &&
                        ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                    showPopupWindow = true;
                    currentEditingComponent = currentComponent;
                    currentEditingComponentID = i;
                } else if (!ImGui.isMouseHoveringRect(minRect.x, minRect.y, maxRect.x, maxRect.y) &&
                        (ImGui.isMouseClicked(ImGuiMouseButton.Left) || ImGui.isMouseClicked(ImGuiMouseButton.Right)) &&
                        currentEditingComponentID == i &&
                        !someButtonInPopupWindowHovered) {
                    showPopupWindow = false;
                    currentEditingComponentID = -1;
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
                                isEditing = true;
                            }
                            if (ImGui.dragFloat("Rotation", rotation)) {
                                transformComponent.getTransform().setRotation(rotation[0]);
                                isEditing = true;
                            }
                            if (ImGui.dragFloat2("Scale", scale, 0.01f)) {
                                transformComponent.getTransform().setScale(new Vector2f(scale));
                                isEditing = true;
                            }
                            if (ImGui.dragFloat2("Centre", centre, 0.01f)) {
                                transformComponent.getTransform().setCentre(new Vector2f(centre));
                                isEditing = true;
                            }
                        }
                        case "MeshRendererComponent" -> {
                            MeshRendererComponent meshRendererComponent = (MeshRendererComponent) currentComponent;
                            ImString textureName = new ImString(new File(meshRendererComponent.texture.path).getName());
                            ImGui.inputText("Path", textureName, ImGuiInputTextFlags.ReadOnly);
                            if (ViewsManager.getResourcesView().getCurrentMovingFile() != null && ResourcesUtils.isFileImage(ViewsManager.getResourcesView().getCurrentMovingFile())) {
                                if (ImGui.beginDragDropTarget()) {
                                    Object imageFile = ImGui.acceptDragDropPayload("File");
                                    if (imageFile != null) {
                                        textureName.set(ViewsManager.getResourcesView().getCurrentMovingFile().getName(), true);
                                        String relativePath = FileUtils.getRelativePath(
                                                new File(ViewsManager.getResourcesView().getCurrentMovingFile().getPath()),
                                                new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                        meshRendererComponent.texture.set(
                                                new Texture2D(AssetManager.getInstance().getTexture2DData(relativePath))
                                        );
                                        meshRendererComponent.texture.path = relativePath;
                                        ViewsManager.getResourcesView().setCurrentMovingFile(null);
                                    }

                                    ImGui.endDragDropTarget();
                                }
                            }

                            /*
                            if (ImGui.beginCombo("Blend source factor", Texture2D.blendFactorToString(textureComponent.texture.blendSourceFactor))) {
                                for(int factor : Texture2D.getAllBlendFactors()) {
                                    if (ImGui.selectable(Texture2D.blendFactorToString(factor))) {
                                        textureComponent.texture.blendSourceFactor = factor;
                                    }
                                }
                                ImGui.endCombo();
                            }

                            if (ImGui.beginCombo("Blend destination factor", Texture2D.blendFactorToString(textureComponent.texture.blendDestinationFactor))) {
                                for(int factor : Texture2D.getAllBlendFactors()) {
                                    if (ImGui.selectable(Texture2D.blendFactorToString(factor))) {
                                        textureComponent.texture.blendDestinationFactor = factor;
                                    }
                                }
                                ImGui.endCombo();
                            }

                             */
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

                                //System.out.println("x: " + rigidbody2DComponent.getRigidbody2D().getBody().getTransform().position.x + ", " + rigidbody2DComponent.getRigidbody2D().getBody().getTransform().position.y);

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

                            float[] offset = new float[]{boxCollider2DComponent.getBoxCollider2D().getOffset().x, boxCollider2DComponent.getBoxCollider2D().getOffset().y};
                            ImGui.pushID("BoxCollider2DOffsetDragFloat_" + i);
                            {
                                if (ImGui.dragFloat2("Offset", offset)) {
                                    boxCollider2DComponent.getBoxCollider2D().setOffset(new Vector2f(offset[0], offset[1]));
                                }
                            }
                            ImGui.popID();

                            float[] scale = new float[]{boxCollider2DComponent.getBoxCollider2D().getScale().x, boxCollider2DComponent.getBoxCollider2D().getScale().y};
                            ImGui.pushID("BoxCollider2DScaleDragFloat_" + i);
                            {
                                if (ImGui.dragFloat2("Scale", scale, 0.01f)) {
                                    boxCollider2DComponent.getBoxCollider2D().setScale(new Vector2f(scale[0], scale[1]));
                                }
                            }
                            ImGui.popID();
                        }
                        case "CircleCollider2DComponent" -> {
                            CircleCollider2DComponent circleCollider2DComponent = (CircleCollider2DComponent) currentComponent;

                            float[] offset = new float[]{circleCollider2DComponent.getCircleCollider2D().getOffset().x, circleCollider2DComponent.getCircleCollider2D().getOffset().y};
                            ImGui.pushID("CircleCollider2DOffsetDragFloat_" + i);
                            {
                                if (ImGui.dragFloat2("Offset", offset)) {
                                    circleCollider2DComponent.getCircleCollider2D().setOffset(new Vector2f(offset[0], offset[1]));
                                }
                            }
                            ImGui.popID();

                            float[] radius = new float[]{circleCollider2DComponent.getCircleCollider2D().getRadius()};
                            ImGui.pushID("CircleCollider2DRadiusDragFloat_" + i);
                            {
                                if (ImGui.dragFloat("Radius", radius, 0.01f)) {
                                    circleCollider2DComponent.getCircleCollider2D().setRadius(radius[0]);
                                }
                            }
                            ImGui.popID();
                        }
                        case "ScriptComponent" -> {
                            ScriptComponent scriptComponent = (ScriptComponent) currentComponent;

                            // System.out.println(scriptComponent.getScript().getScriptClass());

                            List<Field> inspectorViewFields = scriptComponent.getScript().getInspectorViewFields();
                            if (inspectorViewFields.size() != 0) {
                                for (Field field : inspectorViewFields) {
                                    Class<?> cs = field.getType();

                                    if (cs.isAssignableFrom(float.class)) {
                                        float[] floats = new float[]{(float) scriptComponent.getScript().getFieldValue(field)};
                                        ImGui.pushID(field.getName() + "_" + i);
                                        if (ImGui.dragFloat(field.getName(), floats)) {
                                            scriptComponent.getScript().setFieldValue(field, floats[0]);
                                        }
                                        ImGui.popID();
                                    } else if (cs.isAssignableFrom(String.class)) {
                                        ImString string = new ImString((String) scriptComponent.getScript().getFieldValue(field), 128);

                                        ImGui.pushID(field.getName() + "_" + i);
                                        ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.CallbackAlways);
                                        ImGui.popID();

                                        scriptComponent.getScript().setFieldValue(field, string.get());
                                    } else if (cs.isAssignableFrom(GameObject.class)) {
                                        ImString string = new ImString(cs.getSimpleName());
                                        if (scriptComponent.getScript().getFieldValue(field) != null) {
                                            string.set(((GameObject) scriptComponent.getScript().getFieldValue(field)).name, true);
                                        }

                                        ImGui.pushID(field.getName() + "_" + i);
                                        if(scriptComponent.getScript().getFieldValue(field) != null) {
                                            ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                        } else {
                                            ImGui.pushStyleColor(ImGuiCol.Text, 0.65f, 0.65f, 0.65f, 1.0f);
                                            ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                            ImGui.popStyleColor(1);
                                        }
                                        ImGui.popID();

                                        if (ImGui.beginDragDropTarget()) {
                                            Object droppedObject = ImGui.acceptDragDropPayload("SceneGameObject");
                                            if (droppedObject instanceof GameObject gameObject) {
                                                scriptComponent.getScript().setFieldValue(field, gameObject);
                                            }
                                            ImGui.endDragDropTarget();
                                        }
                                    } else if(cs.getSuperclass().isAssignableFrom(Component.class)) {
                                        ImString string = new ImString(cs.getSimpleName());
                                        if (scriptComponent.getScript().getFieldValue(field) != null) {
                                            string.set(((Component) scriptComponent.getScript().getFieldValue(field)).getClass().getSimpleName(), true);
                                        }

                                        ImGui.pushID(field.getName() + "_" + i);
                                        if(scriptComponent.getScript().getFieldValue(field) != null) {
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
                                                scriptComponent.getScript().setFieldValue(field, droppedObject);
                                            }
                                            ImGui.endDragDropTarget();
                                        }
                                        //if(cs.getClass().equals())
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

                            /*
                            float[] referenceDistance = new float[] { audioComponent.getAudio().getReferenceDistance() };
                            ImGui.pushID("AudioReferenceDistanceDragFloat_" + i);
                            if(ImGui.dragFloat("Reference distance", referenceDistance)) {
                                float res = Math.max(0, referenceDistance[0]);
                                audioComponent.getAudio().setReferenceDistance(res);
                            }
                            ImGui.popID();

                            float[] rolloffFactor = new float[] { audioComponent.getAudio().getRolloffFactor() };
                            ImGui.pushID("AudioRolloffFactorDragFloat_" + i);
                            if(ImGui.dragFloat("Rolloff factor", rolloffFactor, 0.05f)) {
                                float res = Math.max(0, rolloffFactor[0]);
                                audioComponent.getAudio().setRolloffFactor(res);
                            }
                            ImGui.popID();

                             */

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

            if (!someItemHovered) {
                currentHoveredComponentID = -1;
            }

            ImGui.popStyleColor(3);

            ImVec2 textSize = new ImVec2();
            ImGui.calcTextSize(textSize, "Add component");
            ImVec2 windowSize = ImGui.getWindowSize();
            ImGui.setCursorPos(windowSize.x / 2.0f - textSize.x / 2.0f, ImGui.getCursorPosY());
            if (ImGui.button("Add component")) {
                action = "addObject2DComponent";
            }

            if(droppingFile != null) {
                if (ImGui.beginDragDropTarget()) {
                    String extension = FilenameUtils.getExtension(droppingFile.getName());
                    if (extension.equals("java")) {
                        Object droppedFile = ImGui.acceptDragDropPayload("File");
                        if (droppedFile instanceof File javaFile) {
                            compileAndAddScriptComponent(javaFile, inspectingObject2D);
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

                            inspectingObject2D.addComponent(audioComponent);
                        }
                    }
                    ImGui.endDragDropTarget();
                }
            }

            drawAction();
        }
    }

    private void compileAndAddScriptComponent(File javaFile, GameObject inspectingObject2D)
    {
        ViewsManager.getBottomMenuView().addTaskToList(new StoppableTask("Compiling script " + javaFile.getName() + "... ", 1.0f, 0.0f) {
            @Override
            public void run()
            {
                boolean compiled = Compiler.compileScript(javaFile.getPath());

                if(compiled) {
                    String relativePath = FileUtils.getRelativePath(
                            new File(javaFile.getPath()),
                            new File(ProjectsManager.getCurrentProject().getProjectPath())
                    );
                    ScriptComponent scriptComponent = new ScriptComponent();
                    scriptComponent.getScript().loadClass(javaFile.getParent(), FilenameUtils.getBaseName(javaFile.getName()));
                    scriptComponent.getScript().path = relativePath;

                    inspectingObject2D.addComponent(scriptComponent);
                }
            }
        });
    }

    private void drawAction()
    {
        if(action.equals("addObject2DComponent")) {
            ImGui.newLine();

            ImVec2 windowSize = ImGui.getWindowSize();
            ImGui.setCursorPos(windowSize.x / 2.0f - 105.0f / 2.0f, ImGui.getCursorPosY());
            ImGui.setNextItemWidth(120.0f);

            ImGui.pushID("Components");
            {
                if(ImGui.beginListBox("")) {

                    try {
                        if(ImGui.selectable("TransformComponent")) {
                            ((GameObject) currentInspectingObject).addComponent(new TransformComponent());
                            action = "";
                        }
                        if (ImGui.selectable("MeshRendererComponent")) {
                            ((GameObject) currentInspectingObject).addComponent(new MeshRendererComponent());
                            action = "";
                        }
                        if (ImGui.selectable("Rigidbody2DComponent")) {
                            Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
                            ((GameObject) currentInspectingObject).addComponent(rigidbody2DComponent);
                            rigidbody2DComponent.getRigidbody2D().setType(BodyType.STATIC);
                            action = "";
                        }
                        if (ImGui.selectable("BoxCollider2DComponent")) {
                            ((GameObject) currentInspectingObject).addComponent(new BoxCollider2DComponent());
                            action = "";
                        }
                        if (ImGui.selectable("CircleCollider2DComponent")) {
                            ((GameObject) currentInspectingObject).addComponent(new CircleCollider2DComponent());
                            action = "";
                        }
                        if(ImGui.selectable("AudioComponent")) {
                            ((GameObject) currentInspectingObject).addComponent(new AudioComponent());
                            action = "";
                        }
                        if(ImGui.selectable("Camera2DComponent")) {
                            ((GameObject) currentInspectingObject).addComponent(new Camera2DComponent());
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
        } else if(action.equals("addLayer") || action.equals("editLayers") ||
                action.equals("addTag") || action.equals("editTags")) {
            dialogWindow.draw();
        }
    }

    public void setCurrentInspectingObject(Object currentInspectingObject) { this.currentInspectingObject = currentInspectingObject; }

    public Object getCurrentInspectingObject() { return currentInspectingObject; }

    public boolean isEditing() { return isEditing; }
    public void setEditing(boolean editing) { isEditing = editing; }
}
