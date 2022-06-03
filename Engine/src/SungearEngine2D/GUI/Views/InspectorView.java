package SungearEngine2D.GUI.Views;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Component;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Component.NonRemovable;
import Core2D.Controllers.PC.Keyboard;
import Core2D.Core2D.Core2D;
import Core2D.Layering.Layer;
import Core2D.Object2D.Object2D;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.Tag;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Utils.ResourcesUtils;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class InspectorView extends View
{
    // текущий просматриваемый объект
    private Object currentInspectingObject;

    // если просматриваемый объект - object2D
    private ImString inspectingObject2DName = new ImString();

    private float[] inspectingObject2DColor = new float[4];

    private float[] inspectingObject2DPosition = new float[2];
    private float[] inspectingObject2DRotation = new float[1];
    private float[] inspectingObject2DScale = new float[2];

    private ImString inspectingObject2DTextureName = new ImString();

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

    public InspectorView()
    {
        init();
    }

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
                    Object2D inspectingObject2D = ((Object2D) currentInspectingObject);
                    inspectObject2D(inspectingObject2D);
                }
            }

            update();
        }
        ImGui.end();
    }

    private void inspectObject2D(Object2D inspectingObject2D)
    {
        if(showPopupWindow) {
            ImGui.openPopup("Component actions");
            if(ImGui.beginPopupContextWindow("Component actions", ImGuiMouseButton.Left)) {
                if(!(currentEditingComponent instanceof NonRemovable)) {
                    boolean deleteClicked = ImGui.menuItem("Delete");
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
            if(ImGui.inputText("", inspectingObject2DName)) {
                inspectingObject2D.setName(inspectingObject2DName.get());
                isEditing = true;
            }
        }
        ImGui.popID();

        ImGui.text("Layer");
        ImGui.sameLine();
        ImGui.pushID("LayersCombo");
        {
            if(ImGui.beginCombo("", inspectingObject2D.getLayer().getName())) {
                List<Layer> layers = Core2D.getSceneManager2D().getCurrentScene2D().getLayering().getLayers();
                for (int i = 0; i < layers.size(); i++) {
                    boolean selected = ImGui.selectable(layers.get(i).getId() + ".  " + layers.get(i).getName());

                    if(ImGui.beginDragDropSource()) {
                        ImGui.text(layers.get(i).getName());
                        ImGui.setDragDropPayload("Layer", layers.get(i));
                        ImGui.endDragDropSource();
                    }

                    if(ImGui.beginDragDropTarget()) {
                        Object droppedObject = ImGui.acceptDragDropPayload("Layer");

                        if(droppedObject != null) {
                            Layer droppedLayer = (Layer) droppedObject;

                            // меняю слои местами и сортирую
                            int thisLayerID = layers.get(i).getId();
                            layers.get(i).setId(droppedLayer.getId());
                            droppedLayer.setId(thisLayerID);

                            Core2D.getSceneManager2D().getCurrentScene2D().getLayering().sort();
                        }

                        ImGui.endDragDropTarget();
                    }

                    if(selected) {
                        inspectingObject2D.setLayer(Core2D.getSceneManager2D().getCurrentScene2D().getLayering().getLayers().get(i));
                    }
                }
                layers = null;

                ImGui.separator();

                if(ImGui.selectable("Add layer...")) {
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
                                if(!ImGui.isItemActive() && Keyboard.keyReleased(GLFW.GLFW_KEY_ENTER)) {
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
                            if(!newName.get().equals("")) {
                                Core2D.getSceneManager2D().getCurrentScene2D().getLayering().addLayer(new Layer(Core2D.getSceneManager2D().getCurrentScene2D().getLayering().getLayers().size(), newName.get()));
                                dialogWindow.setActive(false);
                                newName.set("", true);
                            }
                        }
                    });
                    dialogWindow.setActive(true);
                }

                if(ImGui.selectable("Edit layers...")) {
                    action = "editLayers";
                    dialogWindow.setWindowName("Edit layers");
                    dialogWindow.setButtonsNum(1);
                    dialogWindow.setMiddleButtonText("OK");
                    dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                        @Override
                        public void onDraw() {
                            ImGui.beginChild("EditLayersChildWindow", dialogWindow.getCurrentWindowSize().x - 17, dialogWindow.getCurrentWindowSize().y - 75, true);
                            {
                                for (int i = 0; i < Core2D.getSceneManager2D().getCurrentScene2D().getLayering().getLayers().size(); i++) {
                                    Layer currentLayer = Core2D.getSceneManager2D().getCurrentScene2D().getLayering().getLayers().get(i);
                                    if (currentLayer.getName().equals(currentEditingName.get())) {
                                        ImGui.setNextItemOpen(true, ImGuiCond.Once);
                                    }
                                    boolean opened = ImGui.collapsingHeader("Layer \"" + currentLayer.getName() + "\"");

                                    if (opened) {
                                        ImGui.text("ID: " + currentLayer.getId());

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

                                            if(ImGui.button("Delete")) {
                                                Core2D.getSceneManager2D().getCurrentScene2D().getLayering().deleteLayer(currentLayer);
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
            if(ImGui.beginCombo("", inspectingObject2D.getTag().getName())) {
                List<Tag> tags = Core2D.getSceneManager2D().getCurrentScene2D().getTags();
                for(int i = 0; i < tags.size(); i++) {
                    if(ImGui.selectable(tags.get(i).getName())) {
                        inspectingObject2D.setTag(tags.get(i).getName());
                    }
                }
                tags = null;

                ImGui.separator();

                if(ImGui.selectable("Add tag...")) {
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
                                if(!ImGui.isItemActive() && Keyboard.keyReleased(GLFW.GLFW_KEY_ENTER)) {
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
                            if(!newName.get().equals("")) {
                                Core2D.getSceneManager2D().getCurrentScene2D().addTag(new Tag(newName.get()));
                                dialogWindow.setActive(false);
                                newName.set("", true);
                            }
                        }
                    });
                    dialogWindow.setActive(true);
                }

                if(ImGui.selectable("Edit tags...")) {
                    action = "editTags";
                    dialogWindow.setWindowName("Edit tags");
                    dialogWindow.setButtonsNum(1);
                    dialogWindow.setMiddleButtonText("OK");
                    dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                        @Override
                        public void onDraw() {
                            ImGui.beginChild("EditTagsChildWindow", dialogWindow.getCurrentWindowSize().x - 17, dialogWindow.getCurrentWindowSize().y - 75, true);
                            {
                                for (int i = 0; i < Core2D.getSceneManager2D().getCurrentScene2D().getTags().size(); i++) {
                                    Tag currentTag = Core2D.getSceneManager2D().getCurrentScene2D().getTags().get(i);
                                    if(currentTag.getName().equals(currentEditingName.get())) {
                                        ImGui.setNextItemOpen(true, ImGuiCond.Once);
                                    }
                                    boolean opened = ImGui.collapsingHeader("Tag \"" + currentTag.getName() + "\"");

                                    if(opened) {
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

                                            if (ImGui.button("Delete")) {
                                                System.out.println("sdsd");
                                                Core2D.getSceneManager2D().getCurrentScene2D().deleteTag(currentTag);
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

        if(ImGui.colorEdit4("Color", inspectingObject2DColor)) {
            inspectingObject2D.setColor(new Vector4f(inspectingObject2DColor));
            isEditing = true;
        }

        ImVec4 windowBg = ImGui.getStyle().getColor(ImGuiCol.WindowBg);
        ImGui.pushStyleColor(ImGuiCol.Header, windowBg.x, windowBg.y, windowBg.z, windowBg.w);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, windowBg.x, windowBg.y, windowBg.z, windowBg.w);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, windowBg.x, windowBg.y, windowBg.z, windowBg.w);

        boolean someItemHovered = false;

        for(int i = 0; i < inspectingObject2D.getComponents().size(); i++) {
            String componentName = "";
            if(inspectingObject2D.getComponents().get(i) instanceof TransformComponent) {
                componentName = "Transform";
            } else if(inspectingObject2D.getComponents().get(i) instanceof TextureComponent) {
                componentName = "Texture";
            }

            boolean opened = ImGui.collapsingHeader(componentName);
            ImGui.sameLine();

            ImVec2 headerSize = ImGui.getItemRectSize();

            ImGui.setCursorPosX(headerSize.x - headerSize.y);
            if(currentHoveredComponentID == i) {
                System.out.println("dsds");
                ImGui.image(Resources.Textures.Icons.threeDotsIcon.getTextureHandler(), headerSize.y, headerSize.y, 0, 0, 1, 1,0.6f, 0.6f, 0.6f, 1.0f);
            } else {
                ImGui.image(Resources.Textures.Icons.threeDotsIcon.getTextureHandler(), headerSize.y, headerSize.y);
            }

            ImVec2 minRect = ImGui.getItemRectMin();
            ImVec2 maxRect = ImGui.getItemRectMax();

            if(ImGui.isMouseHoveringRect(minRect.x, minRect.y, maxRect.x, maxRect.y)) {
                currentHoveredComponentID = i;
                someItemHovered = true;
            }

            if(ImGui.isMouseHoveringRect(minRect.x, minRect.y, maxRect.x, maxRect.y) &&
            ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                showPopupWindow = true;
                currentEditingComponent = inspectingObject2D.getComponents().get(i);
                currentEditingComponentID = i;
            } else if(!ImGui.isMouseHoveringRect(minRect.x, minRect.y, maxRect.x, maxRect.y) &&
                    (ImGui.isMouseClicked(ImGuiMouseButton.Left) || ImGui.isMouseClicked(ImGuiMouseButton.Right)) &&
                    currentEditingComponentID == i &&
                    !someButtonInPopupWindowHovered) {
                showPopupWindow = false;
                currentEditingComponentID = -1;
            }

            if(opened) {
                if (componentName.equals("Transform")) {
                    TransformComponent transformComponent = ((TransformComponent) inspectingObject2D.getComponents().get(i));
                    if (ImGui.dragFloat2("Position", inspectingObject2DPosition)) {
                        transformComponent.getTransform().setPosition(new Vector2f(inspectingObject2DPosition));
                        isEditing = true;
                    }
                    if (ImGui.dragFloat("Rotation", inspectingObject2DRotation)) {
                        transformComponent.getTransform().setRotation(inspectingObject2DRotation[0]);
                        isEditing = true;
                    }
                    if (ImGui.dragFloat2("Scale", inspectingObject2DScale)) {
                        transformComponent.getTransform().setScale(new Vector2f(inspectingObject2DScale));
                        isEditing = true;
                    }
                    transformComponent = null;

                } else if (componentName.equals("Texture")) {
                    ImGui.inputText("Source", inspectingObject2DTextureName, ImGuiInputTextFlags.ReadOnly);

                    if (MainView.getResourcesView().getCurrentMovingFile() != null && ResourcesUtils.isFileImage(MainView.getResourcesView().getCurrentMovingFile())) {
                        if (ImGui.beginDragDropTarget()) {
                            Object imageFile = ImGui.acceptDragDropPayload("File");
                            if (imageFile != null) {
                                inspectingObject2DTextureName.set(MainView.getResourcesView().getCurrentMovingFile().getName(), true);
                                ((TextureComponent) inspectingObject2D.getComponents().get(i)).setTexture2D(new Texture2D(MainView.getResourcesView().getCurrentMovingFile().getPath()));
                                MainView.getResourcesView().setCurrentMovingFile(null);
                            }

                            ImGui.endDragDropTarget();
                        }
                    }
                }
            }

            ImGui.separator();
        }

        if(!someItemHovered) {
            currentHoveredComponentID = -1;
        }

        ImGui.popStyleColor(3);

        ImVec2 textSize = new ImVec2();
        ImGui.calcTextSize(textSize, "Add component");
        ImVec2 windowSize = ImGui.getWindowSize();
        ImGui.setCursorPos(windowSize.x / 2.0f - textSize.x / 2.0f, ImGui.getCursorPosY());
        if(ImGui.button("Add component")) {
            action = "addObject2DComponent";
        }

        drawAction();
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
                ImGui.beginListBox("");

                try {
                    if (ImGui.selectable("Texture component")) {
                        ((Object2D) currentInspectingObject).addComponent(new TextureComponent());
                        action = "";
                    }
                } catch (Exception e) {
                    action = "";

                    ImGui.endListBox();
                    ImGui.popID();

                    return;
                }

                if (ImGui.isMouseClicked(ImGuiMouseButton.Left) && !ImGui.isItemHovered()) {
                    action = "";
                }

                ImGui.endListBox();
            }
            ImGui.popID();
        } else if(action.equals("addLayer") || action.equals("editLayers") ||
                action.equals("addTag") || action.equals("editTags")) {
            dialogWindow.draw();
        }
    }

    public void setCurrentInspectingObject(Object currentInspectingObject)
    {
        this.currentInspectingObject = currentInspectingObject;

        if(currentInspectingObject instanceof Object2D) {
            Object2D inspectingObject2D = ((Object2D) currentInspectingObject);

            inspectingObject2DName.set(inspectingObject2D.getName(), true);

            inspectingObject2DColor[0] = inspectingObject2D.getColor().x;
            inspectingObject2DColor[1] = inspectingObject2D.getColor().y;
            inspectingObject2DColor[2] = inspectingObject2D.getColor().z;
            inspectingObject2DColor[3] = inspectingObject2D.getColor().w;

            TransformComponent transformComponent = inspectingObject2D.getComponent(TransformComponent.class);
            if(transformComponent != null) {
                inspectingObject2DPosition[0] = transformComponent.getTransform().getPosition().x;
                inspectingObject2DPosition[1] = transformComponent.getTransform().getPosition().y;

                inspectingObject2DRotation[0] = transformComponent.getTransform().getRotation();

                inspectingObject2DScale[0] = transformComponent.getTransform().getScale().x;
                inspectingObject2DScale[1] = transformComponent.getTransform().getScale().y;
            }

            TextureComponent textureComponent = inspectingObject2D.getComponent(TextureComponent.class);
            if(textureComponent != null) {
                inspectingObject2DTextureName.set(FilenameUtils.getName(textureComponent.getTexture2D().getSource()));
            }
        }
    }

    public boolean isEditing() { return isEditing; }
    public void setEditing(boolean editing) { isEditing = editing; }
}
