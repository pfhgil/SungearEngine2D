package SungearEngine2D.GUI.Views.EditorView;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.ScriptableSystem;
import Core2D.Input.PC.Keyboard;
import Core2D.Layering.Layer;
import Core2D.Project.ProjectsManager;
import Core2D.Scripting.Script;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Scripting.Compiler;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImString;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class InspectorView extends View
{
    // текущий просматриваемый объект
    private Object currentInspectingObject;

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

    private int childDockspace;

    public InspectorView()
    {
        init();
    }

    @Override
    public void init()
    {
        dialogWindow = new DialogWindow("Add layer", "Cancel", "Add");
        childDockspace = ImGui.getID("ComponentsAndSystemsDockspace");
    }

    public void draw()
    {
        ImGui.begin("Inspector", ImGuiWindowFlags.NoMove);
        {
            if(currentInspectingObject != null) {
                if(currentInspectingObject instanceof Entity) {
                    Entity inspectingGameObject = (Entity) currentInspectingObject;
                    inspectObject2D(inspectingGameObject);
                }
            }

            update();
        }
        ImGui.end();
    }

    private void inspectObject2D(Entity inspectingEntity)
    {
        setCurrentInspectingObject(inspectingEntity);
        if(inspectingEntity != null && !inspectingEntity.isShouldDestroy()) {
            ImGui.image(Resources.Textures.Icons.object2DFileIcon.getTextureHandler(), 27.0f, 27.0f);

            ImGui.sameLine();
            ImGui.pushID("Object2DName");
            {
                ImString name = new ImString(inspectingEntity.name, 256);
                if (ImGui.inputText("", name)) {
                    inspectingEntity.name = name.get();
                }
            }
            ImGui.popID();

            ImGui.text("Layer");
            ImGui.sameLine();
            ImGui.pushID("LayersCombo");
            {
                if (ImGui.beginCombo("", inspectingEntity.layerName)) {
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
                            inspectingEntity.setLayer(currentSceneManager.getCurrentScene2D().getLayering().getLayers().get(i));
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
                                    currentSceneManager.getCurrentScene2D().getLayering().addLayer(new Layer(currentSceneManager.getCurrentScene2D().getLayering().getLayersMaxID() + 1, newName.get()));
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

            ImGui.text("Tag   ");
            ImGui.sameLine();
            ImGui.pushID("TagsCombo");
            {
                if (ImGui.beginCombo("", inspectingEntity.tag.getName())) {
                    List<Tag> tags = currentSceneManager.getCurrentScene2D().getTags();
                    for (int i = 0; i < tags.size(); i++) {
                        if (ImGui.selectable(tags.get(i).getName())) {
                            inspectingEntity.tag.set(tags.get(i));
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
                    inspectingEntity.getColor().x,
                            inspectingEntity.getColor().y,
                            inspectingEntity.getColor().z,
                            inspectingEntity.getColor().w  };
            if (ImGui.colorEdit4("Color", col)) {
                inspectingEntity.setColor(new Vector4f(col));
            }

            ImVec2 windowSize = ImGui.getWindowSize();

            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0F);

            ImGui.beginChild("ComponentSystemsChild", windowSize.x, windowSize.y, false);
            ImGui.popStyleVar(3);
            ImGui.dockSpace(childDockspace);
            ImGui.endChild();

            drawAction();
        }
    }

    public void compileAndAddScript(File javaFile, Entity inspectingObject2D)
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
                    String baseName = FilenameUtils.getBaseName(javaFile.getName());

                    Script script = new Script();
                    script.loadClass(ProjectsManager.getCurrentProject().getScriptsPath(), javaFile.getPath(), baseName);
                    script.path = relativePath;

                    if(script.getScriptClass().getSuperclass().isAssignableFrom(Component.class)) {
                        ScriptComponent scriptComponent = new ScriptComponent();
                        scriptComponent.script = script;
                        inspectingObject2D.addComponent(scriptComponent);
                    } else if(script.getScriptClass().getSuperclass().isAssignableFrom(System.class)) {
                        ScriptableSystem scriptableSystem = new ScriptableSystem();
                        scriptableSystem.script = script;
                        inspectingObject2D.addSystem(scriptableSystem);
                    }
                }
            }
        });
    }

    private void drawAction()
    {
        if(action.equals("addLayer") || action.equals("editLayers") ||
                action.equals("addTag") || action.equals("editTags")) {
            dialogWindow.draw();
        }
    }

    public void setCurrentInspectingObject(Object currentInspectingObject) { this.currentInspectingObject = currentInspectingObject; }

    public Object getCurrentInspectingObject() { return currentInspectingObject; }
}
