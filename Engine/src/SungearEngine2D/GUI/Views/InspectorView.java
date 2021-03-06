package SungearEngine2D.GUI.Views;

import Core2D.Camera2D.Camera2D;
import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Component;
import Core2D.Component.Components.*;
import Core2D.Component.NonRemovable;
import Core2D.Controllers.PC.Keyboard;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Object2D.Transform;
import Core2D.Scene2D.SceneManager;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Tag;
import Core2D.Utils.WrappedObject;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Main.Settings;
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
import org.newdawn.slick.Image;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class InspectorView extends View
{
    // текущий просматриваемый объект
    private Object currentInspectingObject;

    // если просматриваемый объект - object2D
    private ImString inspectingObjectName = new ImString();

    private float[] inspectingObject2DColor = new float[4];

    private float[] inspectingObjectPosition = new float[2];
    private float[] inspectingObjectRotation = new float[1];
    private float[] inspectingObjectScale = new float[2];

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

    // текущий перемещаемый в это окно файл
    private File droppingFile;

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
                if (ImGui.inputText("", inspectingObjectName)) {
                    inspectingObject2D.setName(inspectingObjectName.get());
                    isEditing = true;
                }
            }
            ImGui.popID();

            ImGui.text("Layer");
            ImGui.sameLine();
            ImGui.pushID("LayersCombo");
            {
                if (ImGui.beginCombo("", inspectingObject2D.getLayer().getName())) {
                    List<Layer> layers = SceneManager.getCurrentScene2D().getLayering().getLayers();
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

                                SceneManager.getCurrentScene2D().getLayering().sort();
                            }

                            ImGui.endDragDropTarget();
                        }

                        if (selected) {
                            inspectingObject2D.setLayer(SceneManager.getCurrentScene2D().getLayering().getLayers().get(i));
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
                                    SceneManager.getCurrentScene2D().getLayering().addLayer(new Layer(SceneManager.getCurrentScene2D().getLayering().getLayers().size(), newName.get()));
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
                                    for (int i = 0; i < SceneManager.getCurrentScene2D().getLayering().getLayers().size(); i++) {
                                        Layer currentLayer = SceneManager.getCurrentScene2D().getLayering().getLayers().get(i);
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
                                                    SceneManager.getCurrentScene2D().getLayering().deleteLayer(currentLayer);
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
                    List<Tag> tags = SceneManager.getCurrentScene2D().getTags();
                    for (int i = 0; i < tags.size(); i++) {
                        if (ImGui.selectable(tags.get(i).getName())) {
                            inspectingObject2D.setTag(tags.get(i).getName());
                        }
                    }
                    tags = null;

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
                                    SceneManager.getCurrentScene2D().addTag(new Tag(newName.get()));
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
                                    for (int i = 0; i < SceneManager.getCurrentScene2D().getTags().size(); i++) {
                                        Tag currentTag = SceneManager.getCurrentScene2D().getTags().get(i);
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
                                                    SceneManager.getCurrentScene2D().deleteTag(currentTag);
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

            if (ImGui.colorEdit4("Color", inspectingObject2DColor)) {
                inspectingObject2D.setColor(new Vector4f(inspectingObject2DColor));
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
                    if (componentName.equals("Transform")) {
                        TransformComponent transformComponent = ((TransformComponent) inspectingObject2D.getComponents().get(i));
                        if (ImGui.dragFloat2("Position", inspectingObjectPosition)) {
                            transformComponent.getTransform().setPosition(new Vector2f(inspectingObjectPosition));
                            isEditing = true;
                        }
                        if (ImGui.dragFloat("Rotation", inspectingObjectRotation)) {
                            transformComponent.getTransform().setRotation(inspectingObjectRotation[0]);
                            isEditing = true;
                        }
                        if (ImGui.dragFloat2("Scale", inspectingObjectScale, 0.01f)) {
                            transformComponent.getTransform().setScale(new Vector2f(inspectingObjectScale));
                            isEditing = true;
                        }

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
                    } else if (componentName.equals("Rigidbody2D")) {
                        Rigidbody2DComponent rigidbody2DComponent = ((Rigidbody2DComponent) inspectingObject2D.getComponents().get(i));
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

                        float[] density = new float[]{rigidbody2DComponent.getRigidbody2D().getDensity()};
                        if (ImGui.dragFloat("Density", density, 0.01f)) {
                            rigidbody2DComponent.getRigidbody2D().setDensity(density[0]);
                        }

                        float[] restitution = new float[]{rigidbody2DComponent.getRigidbody2D().getRestitution()};
                        if (ImGui.dragFloat("Restitution", restitution, 0.01f)) {
                            rigidbody2DComponent.getRigidbody2D().setRestitution(restitution[0]);
                        }

                        float[] friction = new float[]{rigidbody2DComponent.getRigidbody2D().getFriction()};
                        if (ImGui.dragFloat("Friction", friction, 0.01f)) {
                            rigidbody2DComponent.getRigidbody2D().setFriction(friction[0]);
                        }

                        ImGui.separator();

                        if(ImGui.checkbox("Sensor", rigidbody2DComponent.getRigidbody2D().isSensor())) {
                            rigidbody2DComponent.getRigidbody2D().setSensor(!rigidbody2DComponent.getRigidbody2D().isSensor());
                        }

                        if(ImGui.checkbox("Fixed rotation", rigidbody2DComponent.getRigidbody2D().isFixedRotation())) {
                            rigidbody2DComponent.getRigidbody2D().setFixedRotation(!rigidbody2DComponent.getRigidbody2D().isFixedRotation());
                        }

                        ImGui.separator();

                        /*
                        ImGui.text("Mass data");
                        ImGui.newLine();
                        float[] mass = new float[] { rigidbody2DComponent.getRigidbody2D().getMass() };
                        if (ImGui.dragFloat("Mass", mass, 0.01f)) {
                            rigidbody2DComponent.getRigidbody2D().setMass(mass[0]);
                        }

                         */
                    } else if (componentName.equals("BoxCollider2D")) {
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
                    } else if (componentName.equals("CircleCollider2D")) {
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
                    } else if (componentName.equals("ScriptComponent")) {
                        ScriptComponent scriptComponent = (ScriptComponent) inspectingObject2D.getComponents().get(i);

                       // System.out.println(scriptComponent.getScript().getScriptClass());

                        List<Field> inspectorViewFields = scriptComponent.getScript().getInspectorViewFields();

                        if(inspectorViewFields.size() != 0) {
                            for(Field field : inspectorViewFields) {
                                Class<?> cs = field.getType();

                                if(cs.isAssignableFrom(float.class)) {
                                    float[] floats = new float[] { (float) scriptComponent.getScript().getFieldValue(field) };
                                    if(ImGui.dragFloat(field.getName(), floats)) {
                                        scriptComponent.getScript().setFieldValue(field, floats[0]);
                                    }
                                } else if(cs.isAssignableFrom(String.class)) {
                                    ImString string = new ImString((String) scriptComponent.getScript().getFieldValue(field),  128);

                                    ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.CallbackAlways);

                                    scriptComponent.getScript().setFieldValue(field, string.get());
                                } else if(cs.isAssignableFrom(Object2D.class)) {
                                    ImString string = new ImString("null");
                                    if(scriptComponent.getScript().getFieldValue(field) != null) {
                                        string.set(((Object2D) scriptComponent.getScript().getFieldValue(field)).getName(), true);
                                    }
                                    ImGui.inputText(field.getName(), string, ImGuiInputTextFlags.ReadOnly);
                                    if(ImGui.beginDragDropTarget()) {
                                        Object droppedObject = ImGui.acceptDragDropPayload("SceneObject2D");
                                        if(droppedObject != null && ((WrappedObject) droppedObject).getObject() instanceof Object2D) {
                                            Object2D object2D = (Object2D) ((WrappedObject) droppedObject).getObject();

                                            scriptComponent.getScript().setFieldValue(field, object2D);
                                        }
                                        ImGui.endDragDropTarget();
                                    }
                                }
                            }

                            //ImGui.separator();
                        }

                        /*
                        if(ImGui.button("Compile")) {
                            Compiler.compileScript(scriptComponent.getScript().getPath() + ".java");
                            scriptComponent.getScript().loadClass(new File(scriptComponent.getScript().getPath()).getParent(), FilenameUtils.getBaseName(new File(scriptComponent.getScript().getPath()).getName()));
                        }

                         */
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
                    if (FilenameUtils.getExtension(droppingFile.getName()).equals("java")) {
                        Object droppedFile = ImGui.acceptDragDropPayload("File");
                        if (droppedFile instanceof File) {
                            File javaFile = (File) droppedFile;

                            boolean compiled = Compiler.compileScript(javaFile.getPath());
                            if(compiled) {
                                ScriptComponent scriptComponent = new ScriptComponent();
                                scriptComponent.getScript().loadClass(javaFile.getParent(), FilenameUtils.getBaseName(javaFile.getName()));

                                inspectingObject2D.addComponent(scriptComponent);
                            }
                        }
                    }
                    ImGui.endDragDropTarget();
                }
            }

            drawAction();
        }
    }

    private void inspectCamera2D(Camera2D camera2D)
    {
        setCurrentInspectingObject(camera2D);
        if(camera2D != null) {
            ImGui.pushID("Camera2DName");
            {
                if (ImGui.inputText("", inspectingObjectName)) {
                    camera2D.setName(inspectingObjectName.get());
                    isEditing = true;
                }
            }
            ImGui.popID();

            ImGui.separator();

            if (ImGui.dragFloat2("Position", inspectingObjectPosition)) {
                camera2D.getTransform().setPosition(new Vector2f(inspectingObjectPosition));
                isEditing = true;
            }
            if (ImGui.dragFloat("Rotation", inspectingObjectRotation)) {
                camera2D.getTransform().setRotation(inspectingObjectRotation[0]);
                isEditing = true;
            }
            if (ImGui.dragFloat2("Scale", inspectingObjectScale, 0.01f)) {
                camera2D.getTransform().setScale(new Vector2f(inspectingObjectScale));
                isEditing = true;
            }

            ImGui.separator();

            boolean asMainCamera2D = false;
            if (SceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
                asMainCamera2D = SceneManager.getCurrentScene2D().getSceneMainCamera2D().getID() == camera2D.getID();
            }
            if (ImGui.checkbox("As main Camera2D", asMainCamera2D)) {
                if (asMainCamera2D) {
                    SceneManager.getCurrentScene2D().setSceneMainCamera2D(null);
                    if(Settings.Playmode.active) {
                        CamerasManager.setMainCamera2D(null);
                    }
                } else {
                    SceneManager.getCurrentScene2D().setSceneMainCamera2D(camera2D);
                    if(Settings.Playmode.active) {
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
                ImGui.beginListBox("");

                try {
                    if(ImGui.selectable("Texture")) {
                        ((Object2D) currentInspectingObject).addComponent(new TextureComponent());
                        action = "";
                    }
                    if(ImGui.selectable("Rigidbody2D")) {
                        Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
                        ((Object2D) currentInspectingObject).addComponent(rigidbody2DComponent);
                        rigidbody2DComponent.getRigidbody2D().setType(BodyType.STATIC);
                        action = "";
                    }
                    if(ImGui.selectable("BoxCollider2D")) {
                        ((Object2D) currentInspectingObject).addComponent(new BoxCollider2DComponent());
                        action = "";
                    }
                    if(ImGui.selectable("CircleCollider2D")) {
                        ((Object2D) currentInspectingObject).addComponent(new CircleCollider2DComponent());
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

            if(!inspectingObject2D.isShouldDestroy()) {
                inspectingObjectName.set(inspectingObject2D.getName(), true);

                inspectingObject2DColor[0] = inspectingObject2D.getColor().x;
                inspectingObject2DColor[1] = inspectingObject2D.getColor().y;
                inspectingObject2DColor[2] = inspectingObject2D.getColor().z;
                inspectingObject2DColor[3] = inspectingObject2D.getColor().w;

                TransformComponent transformComponent = inspectingObject2D.getComponent(TransformComponent.class);
                if (transformComponent != null) {
                    inspectingObjectPosition[0] = transformComponent.getTransform().getPosition().x;
                    inspectingObjectPosition[1] = transformComponent.getTransform().getPosition().y;

                    inspectingObjectRotation[0] = transformComponent.getTransform().getRotation();

                    inspectingObjectScale[0] = transformComponent.getTransform().getScale().x;
                    inspectingObjectScale[1] = transformComponent.getTransform().getScale().y;
                }

                TextureComponent textureComponent = inspectingObject2D.getComponent(TextureComponent.class);
                if (textureComponent != null) {
                    inspectingObject2DTextureName.set(FilenameUtils.getName(textureComponent.getTexture2D().getSource()));
                }
            }
        } else if(currentInspectingObject instanceof Camera2D) {
            Camera2D camera2D = (Camera2D) currentInspectingObject;

            inspectingObjectName.set(camera2D.getName(), true);

            Transform transform = camera2D.getTransform();
            if (transform != null) {
                inspectingObjectPosition[0] = transform.getPosition().x;
                inspectingObjectPosition[1] = transform.getPosition().y;

                inspectingObjectRotation[0] = transform.getRotation();

                inspectingObjectScale[0] = transform.getScale().x;
                inspectingObjectScale[1] = transform.getScale().y;
            }
        }
    }

    public Object getCurrentInspectingObject() { return currentInspectingObject; }

    public boolean isEditing() { return isEditing; }
    public void setEditing(boolean editing) { isEditing = editing; }

    public File getDroppingFile() { return droppingFile; }
    public void setDroppingFile(File droppingFile) { this.droppingFile = droppingFile; }
}
