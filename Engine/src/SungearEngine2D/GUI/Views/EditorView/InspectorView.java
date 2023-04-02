package SungearEngine2D.GUI.Views.EditorView;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Entity;
import Core2D.Input.PC.Keyboard;
import Core2D.Layering.Layer;
import Core2D.Project.ProjectsManager;
import Core2D.Scripting.Script;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Tag;
import SungearEngine2D.GUI.ImGuiUtils;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Scripting.Compiler;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
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
            ImGui.image(Resources.Textures.Icons.object2DFileIcon.getHandler(), 27.0f, 27.0f);

            ImGui.sameLine();

            inspectingEntity.name = ImGuiUtils.inputText("", inspectingEntity.name, "EntityName");

            ImGui.text("Layer");
            ImGui.sameLine();
            ImGui.pushID("LayersCombo");
            {
                if (ImGuiUtils.imCallWBorder(func -> ImGui.beginCombo("", inspectingEntity.layerName))) {
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
                                    ImGuiUtils.imCallWBorder(func -> ImGui.inputText("", newName));
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
                                        if (currentEditingID == i) {
                                            ImGui.setNextItemOpen(true, ImGuiCond.Once);
                                        }
                                        boolean opened = ImGui.collapsingHeader("Layer \"" + currentLayer.getName() + "\"");

                                        if(opened) {
                                            ImGui.text("ID: " + currentLayer.getID());

                                            int flags = currentLayer.getName().equals("default") ? ImGuiInputTextFlags.ReadOnly : ImGuiInputTextFlags.None;

                                            currentLayer.setName(ImGuiUtils.inputText("", currentLayer.getName(), "Layer_" + i + "_InputText", flags));

                                            if(ImGui.isItemClicked()) {
                                                currentEditingID = i;
                                            }

                                            if(ImGui.isItemDeactivatedAfterEdit()) {
                                                currentEditingID = -1;
                                            }

                                            if(!currentLayer.getName().equals("default")) {
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
                if (ImGuiUtils.imCallWBorder(func -> ImGui.beginCombo("", inspectingEntity.tag.getName()))) {
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
                                    ImGuiUtils.imCallWBorder(func -> ImGui.inputText("", newName));
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
                                        if (currentEditingID == i) {
                                            ImGui.setNextItemOpen(true, ImGuiCond.Once);
                                        }
                                        boolean opened = ImGui.collapsingHeader("Tag \"" + currentTag.getName() + "\"");

                                        if(opened) {
                                            int flags = currentTag.getName().equals("default") ? ImGuiInputTextFlags.ReadOnly : ImGuiInputTextFlags.None;

                                            currentTag.setName(ImGuiUtils.inputText("", currentTag.getName(), "Tag_" + i + "_InputText", flags));

                                            if(ImGui.isItemClicked()) {
                                                currentEditingID = i;
                                            }

                                            if(ImGui.isItemDeactivatedAfterEdit()) {
                                                currentEditingID = -1;
                                            }

                                            if(!currentTag.getName().equals("default")) {
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
            if (ImGuiUtils.imCallWBorder(func -> ImGui.colorEdit4("Color", col))) {
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
                            javaFile,
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
                    } // FIXME
                    /*else if(script.getScriptClass().getSuperclass().isAssignableFrom(Systems.class)) {
                        ScriptableSystem scriptableSystem = new ScriptableSystem();
                        scriptableSystem.script = script;
                        inspectingObject2D.addSystem(scriptableSystem);
                    } */
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
