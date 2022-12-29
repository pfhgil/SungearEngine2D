package SungearEngine2D.GUI.Views;

import Core2D.Core2D.Core2D;
import SungearEngine2D.GUI.Views.DebuggerView.DebuggerView;
import SungearEngine2D.GUI.Views.EditorView.*;
import SungearEngine2D.GUI.Views.Other.EngineSettingsView;
import SungearEngine2D.GUI.Views.Other.ProjectSettingsView;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class ViewsManager
{
    private static int mainDockspaceID;

    // находится ли какой-то вид в фокусе кроме вида сцены
    public static boolean isSomeViewFocusedExceptSceneView = false;

    private static int lastFocusedDialogWindow = -1;
    private static int currentFocusedDialogWindow = -1;

    private static InspectorView inspectorView;
    private static ComponentsView componentsView;
    private static SystemsView systemsView;

    private static ProjectTreeView projectTreeView;
    private static ResourcesView resourcesView;
    private static SceneTreeView sceneTreeView;
    private static SceneView sceneView;
    private static GameView gameView;
    private static TopToolbarView topToolbarView;
    private static LogView logView;
    private static BottomMenuView bottomMenuView;
    private static ToolbarView toolbarView;

    private static ProjectSettingsView projectSettingsView;
    private static EngineSettingsView engineSettingsView;

    private static DebuggerView debuggerView;

    public static void init()
    {
        mainDockspaceID = ImGui.getID("Main dockspace");

        inspectorView = new InspectorView();
        componentsView = new ComponentsView();
        systemsView = new SystemsView();

        projectTreeView = new ProjectTreeView();
        resourcesView = new ResourcesView();
        sceneTreeView = new SceneTreeView();
        sceneView = new SceneView();
        gameView = new GameView();
        topToolbarView = new TopToolbarView();
        logView = new LogView();
        bottomMenuView = new BottomMenuView();
        toolbarView = new ToolbarView();

        projectSettingsView = new ProjectSettingsView();
        engineSettingsView = new EngineSettingsView();

        debuggerView = new DebuggerView();
    }

    public static void draw()
    {
        int windowFlags = ImGuiWindowFlags.MenuBar |
                ImGuiWindowFlags.NoDocking |
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus |
                ImGuiWindowFlags.NoNavFocus;

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

        ImGui.begin("Dockspace demo", new ImBoolean(true), windowFlags);
        {
            ImGui.popStyleVar(3);

            ImGui.dockSpace(mainDockspaceID);

            topToolbarView.draw();

            sceneTreeView.draw();

            projectTreeView.draw();

            resourcesView.draw();

            inspectorView.draw();
            componentsView.draw();
            systemsView.draw();

            sceneView.draw();

            gameView.draw();

            logView.draw();

            bottomMenuView.draw();

            toolbarView.draw();

            debuggerView.draw();
        }
        ImGui.end();

        if(projectSettingsView.active) {
            projectSettingsView.draw();
        }

        if(engineSettingsView.active) {
            engineSettingsView.draw();
        }
    }

    public static InspectorView getInspectorView() { return inspectorView; }

    public static ProjectTreeView getProjectTreeView() { return projectTreeView; }

    public static ResourcesView getResourcesView() { return resourcesView; }

    public static SceneTreeView getSceneTreeView() { return sceneTreeView; }

    public static SceneView getSceneView() { return sceneView; }

    public static TopToolbarView getTopToolbarView() { return topToolbarView; }

    public static BottomMenuView getBottomMenuView() {  return bottomMenuView;  }

    public static ProjectSettingsView getProjectSettingsView() { return projectSettingsView; }

    public static EngineSettingsView getEngineSettingsView() { return engineSettingsView; }

    public static DebuggerView getDebuggerView() { return debuggerView; }

    public static int getCurrentFocusedDialogWindow() { return currentFocusedDialogWindow; }
    public static void setCurrentFocusedDialogWindow(int currentFocusedDialogWindow)
    {
        ViewsManager.lastFocusedDialogWindow = ViewsManager.currentFocusedDialogWindow;
        ViewsManager.currentFocusedDialogWindow = currentFocusedDialogWindow;
    }
}
