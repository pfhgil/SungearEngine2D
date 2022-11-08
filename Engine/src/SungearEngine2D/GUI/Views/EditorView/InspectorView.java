package SungearEngine2D.GUI.Views.EditorView;

import Core2D.Audio.Audio;
import Core2D.Camera2D.Camera2D;
import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Component;
import Core2D.Component.Components.*;
import Core2D.Component.NonRemovable;
import Core2D.Drawable.Object2D;
import Core2D.Input.PC.Keyboard;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Tasks.StoppableTask;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import Core2D.Utils.WrappedObject;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Main.EngineSettings;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.ResourcesUtils;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
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
                if(currentInspectingObject instanceof Object2D) {
                    Object2D inspectingObject2D = (Object2D) currentInspectingObject;
                    inspectObject2D(inspectingObject2D);
                } else if(currentInspectingObject instanceof Camera2D) {
                    Camera2D inspectingCamera2D = (Camera2D) currentInspectingObject;
                    inspectCamera2D(inspectingCamera2D);
                }
            }

            update();
        }
        ImGui.end();
    }

    private void inspectObject2D(Object2D inspectingObject2D)
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
                                inspectingObject2D.removeComponent(currentEditingComponent);
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
                ImString name = new ImString(inspectingObject2D.getName(), 256);
                if (ImGui.inputText("", name)) {
                    inspectingObject2D.setName(name.get());
                    isEditing = true;
                }
            }
            ImGui.popID();

            ImGui.text("Layer");
            ImGui.sameLine();
            ImGui.pushID("LayersCombo");
            {
                if (ImGui.beginCombo("", inspectingObject2D.getLayer().getName())) {
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
                if (ImGui.beginCombo("", inspectingObject2D.getTag().getName())) {
                    List<Tag> tags = currentSceneManager.getCurrentScene2D().getTags();
                    for (int i = 0; i < tags.size(); i++) {
                        if (ImGui.selectable(tags.get(i).getName())) {
                            inspectingObject2D.setTag(tags.get(i).getName());
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
                            inspectingObject2D.getColor().w };
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
                String componentName = "";
                if (inspectingObject2D.getComponents().get(i) instanceof TransformComponent) {
                    componentName = "Transform";
                } else if (inspectingObject2D.getComponents().get(i) instanceof TextureComponent) {
                    componentName = "Texture";
                } else if (inspectingObject2D.getComponents().get(i) instanceof Rigidbody2DComponent) {
                    componentName = "Rigidbody2D";
                } else if (inspectingObject2D.getComponents().get(i) instanceof BoxCollider2DComponent) {
                    componentName = "BoxCollider2D";
                } else if (inspectingObject2D.getComponents().get(i) instanceof CircleCollider2DComponent) {
                    componentName = "CircleCollider2D";
                } else if (inspectingObject2D.getComponents().get(i) instanceof ScriptComponent) {
                    componentName = "ScriptComponent";
                } else if (inspectingObject2D.getComponents().get(i) instanceof AudioComponent) {
                    componentName = "AudioComponent";
                }

                ImGui.pushID(componentName + i);
                boolean opened = false;
                if(!componentName.equals("ScriptComponent")) {
                    opened = ImGui.collapsingHeader(componentName);
                } else {
                    opened = ImGui.collapsingHeader(((ScriptComponent) inspectingObject2D.getComponents().get(i)).getScript().getName() + " (" + componentName + ")");
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
                    currentEditingComponent = inspectingObject2D.getComponents().get(i);
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
                        case "Transform" -> {
                            TransformComponent transformComponent = ((TransformComponent) inspectingObject2D.getComponents().get(i));
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
                        case "Texture" -> {
                            TextureComponent textureComponent = (TextureComponent) inspectingObject2D.getComponents().get(i);
                            ImString textureName = new ImString(new File(textureComponent.getTexture2D().path).getName());
                            ImGui.inputText("Path", textureName, ImGuiInputTextFlags.ReadOnly);
                            if (ViewsManager.getResourcesView().getCurrentMovingFile() != null && ResourcesUtils.isFileImage(ViewsManager.getResourcesView().getCurrentMovingFile())) {
                                if (ImGui.beginDragDropTarget()) {
                                    Object imageFile = ImGui.acceptDragDropPayload("File");
                                    if (imageFile != null) {
                                        textureName.set(ViewsManager.getResourcesView().getCurrentMovingFile().getName(), true);
                                        String relativePath = FileUtils.getRelativePath(
                                                new File(ViewsManager.getResourcesView().getCurrentMovingFile().getPath()),
                                                new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                        textureComponent.setTexture2D(new Texture2D(ViewsManager.getResourcesView().getCurrentMovingFile().getPath()));
                                        textureComponent.getTexture2D().path = relativePath;
                                        ViewsManager.getResourcesView().setCurrentMovingFile(null);
                                    }

                                    ImGui.endDragDropTarget();
                                }
                            }

                            if (ImGui.beginCombo("Blend source factor", Texture2D.blendFactorToString(textureComponent.getTexture2D().blendSourceFactor))) {
                                for(int factor : Texture2D.getAllBlendFactors()) {
                                    if (ImGui.selectable(Texture2D.blendFactorToString(factor))) {
                                        textureComponent.getTexture2D().blendSourceFactor = factor;
                                    }
                                }
                                ImGui.endCombo();
                            }

                            if (ImGui.beginCombo("Blend destination factor", Texture2D.blendFactorToString(textureComponent.getTexture2D().blendDestinationFactor))) {
                                for(int factor : Texture2D.getAllBlendFactors()) {
                                    if (ImGui.selectable(Texture2D.blendFactorToString(factor))) {
                                        textureComponent.getTexture2D().blendDestinationFactor = factor;
                                    }
                                }
                                ImGui.endCombo();
                            }
                        }
                        case "Rigidbody2D" -> {
                            Rigidbody2DComponent rigidbody2DComponent = ((Rigidbody2DComponent) inspectingObject2D.getComponents().get(i));

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
                        case "BoxCollider2D" -> {
                            BoxCollider2DComponent boxCollider2DComponent = ((BoxCollider2DComponent) inspectingObject2D.getComponents().get(i));

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
                        case "CircleCollider2D" -> {
                            CircleCollider2DComponent circleCollider2DComponent = ((CircleCollider2DComponent) inspectingObject2D.getComponents().get(i));

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
                            ScriptComponent scriptComponent = (ScriptComponent) inspectingObject2D.getComponents().get(i);

                            // System.out.println(scriptComponent.getScript().getScriptClass());

                            List<Field> inspectorViewFields = scriptComponent.getScript().getInspectorViewFields();
                            if (inspectorViewFields.size() != 0) {
                                for (Field field : inspectorViewFields) {
                                    Class<?> cs = field.getType();

                                    if (cs.isAssignableFrom(float.class)) {
                                        float[] floats = new float[]{(float) scriptComponent.getScript().getFieldValue(field)};
                                        if (ImGui.dragFloat(field.getName(), floats)) {
                                            scriptComponent.getScript().setFieldValue(field, floats[0]);
                                        }
                                    } else if (cs.isAssignableFrom(String.class)) {
                                        ImString string = new ImString((String) scriptComponent.getScript().getFieldValue(field), 128);

                                        ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.CallbackAlways);

                                        scriptComponent.getScript().setFieldValue(field, string.get());
                                    } else if (cs.isAssignableFrom(Object2D.class)) {
                                        ImString string = new ImString("null");
                                        if (scriptComponent.getScript().getFieldValue(field) != null) {
                                            string.set(((Object2D) scriptComponent.getScript().getFieldValue(field)).getName(), true);
                                        }
                                        ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                        if (ImGui.beginDragDropTarget()) {
                                            Object droppedObject = ImGui.acceptDragDropPayload("SceneWrappedObject");
                                            if (droppedObject instanceof WrappedObject && ((WrappedObject) droppedObject).getObject() instanceof Object2D) {
                                                Object2D object2D = (Object2D) ((WrappedObject) droppedObject).getObject();

                                                scriptComponent.getScript().setFieldValue(field, object2D);
                                            }
                                            ImGui.endDragDropTarget();
                                        }
                                    } else if (cs.isAssignableFrom(Camera2D.class)) {
                                        ImString string = new ImString("null");
                                        if (scriptComponent.getScript().getFieldValue(field) != null) {
                                            string.set(((Camera2D) scriptComponent.getScript().getFieldValue(field)).name, true);
                                        }
                                        ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                        if (ImGui.beginDragDropTarget()) {
                                            Object droppedObject = ImGui.acceptDragDropPayload("SceneWrappedObject");
                                            if (droppedObject instanceof Camera2D) {
                                                Camera2D camera2D = (Camera2D) droppedObject;

                                                scriptComponent.getScript().setFieldValue(field, camera2D);
                                            }
                                            ImGui.endDragDropTarget();
                                        }
                                    }
                                }
                            }
                        }
                        case "AudioComponent" -> {
                            AudioComponent audioComponent = (AudioComponent) inspectingObject2D.getComponents().get(i);

                            ImString audioName = new ImString(new File(audioComponent.getAudio().path).getName());
                            ImGui.inputText("Path", audioName, ImGuiInputTextFlags.ReadOnly);
                            if (ViewsManager.getResourcesView().getCurrentMovingFile() != null && ResourcesUtils.isFileImage(ViewsManager.getResourcesView().getCurrentMovingFile())) {
                                if (ImGui.beginDragDropTarget()) {
                                    Object audioFile = ImGui.acceptDragDropPayload("File");
                                    if (audioFile != null) {
                                        audioName.set(ViewsManager.getResourcesView().getCurrentMovingFile().getName(), true);
                                        String relativePath = FileUtils.getRelativePath(
                                                new File(ViewsManager.getResourcesView().getCurrentMovingFile().getPath()),
                                                new File(ProjectsManager.getCurrentProject().getProjectPath()));
                                        audioComponent.getAudio().loadAndSetup(ViewsManager.getResourcesView().getCurrentMovingFile().getPath());
                                        audioComponent.getAudio().path = relativePath;
                                        ViewsManager.getResourcesView().setCurrentMovingFile(null);
                                    }

                                    ImGui.endDragDropTarget();
                                }
                            }

                            ImGui.pushID("AudioType");
                            if (ImGui.beginCombo("Type", audioComponent.getAudio().audioType.toString())) {
                                if (ImGui.selectable("Background")) {
                                    audioComponent.getAudio().audioType = Audio.AudioType.BACKGROUND;
                                }

                                if (ImGui.selectable("Worldspace")) {
                                    audioComponent.getAudio().audioType = Audio.AudioType.WORLDSPACE;
                                }

                                //System.out.println("x: " + rigidbody2DComponent.getRigidbody2D().getBody().getTransform().position.x + ", " + rigidbody2DComponent.getRigidbody2D().getBody().getTransform().position.y);

                                ImGui.endCombo();
                            }
                            ImGui.popID();

                            if(ImGui.button("Start")) {
                                audioComponent.getAudio().start();
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
                            audioComponent.getAudio().loadAndSetup(audioFile.getPath());

                            String relativePath = FileUtils.getRelativePath(
                                    new File(audioFile.getPath()),
                                    new File(ProjectsManager.getCurrentProject().getProjectPath())
                            );
                            audioComponent.getAudio().path = relativePath;

                            inspectingObject2D.addComponent(audioComponent);
                        }
                    }
                    ImGui.endDragDropTarget();
                }
            }

            drawAction();
        }
    }

    private void compileAndAddScriptComponent(File javaFile, Object2D inspectingObject2D)
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

    private void inspectCamera2D(Camera2D camera2D)
    {
        setCurrentInspectingObject(camera2D);
        if(camera2D != null) {
            ImGui.pushID("Camera2DName");
            {
                ImString name = new ImString(camera2D.name, 256);
                if (ImGui.inputText("", name)) {
                    camera2D.name = name.get();
                    isEditing = true;
                }
            }
            ImGui.popID();

            ImGui.separator();

            float[] pos = new float[] {
                    camera2D.getTransform().getPosition().x,
                    camera2D.getTransform().getPosition().y
            };
            float[] rotation = new float[] {
                    camera2D.getTransform().getRotation()
            };
            float[] scale = new float[] {
                    camera2D.getTransform().getScale().x,
                    camera2D.getTransform().getScale().y
            };

            if (ImGui.dragFloat2("Position", pos)) {
                camera2D.getTransform().setPosition(new Vector2f(pos));
                isEditing = true;
            }
            if (ImGui.dragFloat("Rotation", rotation)) {
                camera2D.getTransform().setRotation(rotation[0]);
                isEditing = true;
            }
            if (ImGui.dragFloat2("Scale", scale, 0.01f)) {
                camera2D.getTransform().setScale(new Vector2f(scale));
                isEditing = true;
            }

            ImGui.separator();

            boolean asMainCamera2D = false;
            if (currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
                asMainCamera2D = currentSceneManager.getCurrentScene2D().getSceneMainCamera2D().getID() == camera2D.getID();
            }
            if (ImGui.checkbox("As main Camera2D", asMainCamera2D)) {
                if (asMainCamera2D) {
                    currentSceneManager.getCurrentScene2D().setSceneMainCamera2D(null);
                    if(EngineSettings.Playmode.active) {
                        CamerasManager.setMainCamera2D(null);
                    }
                } else {
                    currentSceneManager.getCurrentScene2D().setSceneMainCamera2D(camera2D);
                    if(EngineSettings.Playmode.active) {
                        CamerasManager.setMainCamera2D(camera2D);
                    }
                }
            }
        }
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
                        if (ImGui.selectable("Texture")) {
                            ((Object2D) currentInspectingObject).addComponent(new TextureComponent());
                            action = "";
                        }
                        if (ImGui.selectable("Rigidbody2D")) {
                            Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
                            ((Object2D) currentInspectingObject).addComponent(rigidbody2DComponent);
                            rigidbody2DComponent.getRigidbody2D().setType(BodyType.STATIC);
                            action = "";
                        }
                        if (ImGui.selectable("BoxCollider2D")) {
                            ((Object2D) currentInspectingObject).addComponent(new BoxCollider2DComponent());
                            action = "";
                        }
                        if (ImGui.selectable("CircleCollider2D")) {
                            ((Object2D) currentInspectingObject).addComponent(new CircleCollider2DComponent());
                            action = "";
                        }
                        if(ImGui.selectable("Audio")) {
                            ((Object2D) currentInspectingObject).addComponent(new AudioComponent());
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
