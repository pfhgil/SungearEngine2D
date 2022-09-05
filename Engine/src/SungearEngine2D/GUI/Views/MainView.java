package SungearEngine2D.GUI.Views;

import Core2D.Core2D.Core2D;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class MainView
{
    private static int mainDockspaceID;

    // находится ли какой-то вид в фокусе кроме вида сцены
    public static boolean isSomeViewFocusedExceptSceneView = false;

    private static int lastFocusedDialogWindow = -1;
    private static int currentFocusedDialogWindow = -1;

    private static InspectorView inspectorView;
    private static ProjectTreeView projectTreeView;
    private static ResourcesView resourcesView;
    private static SceneTreeView sceneTreeView;
    private static SceneView sceneView;
    private static GameView gameView;
    private static TopToolbarView topToolbarView;
    private static LogView logView;
    private static BottomMenuView bottomMenuView;
    private static ToolbarView toolbarView;

    public static void init()
    {
        mainDockspaceID = ImGui.getID("Main dockspace");

        inspectorView = new InspectorView();
        projectTreeView = new ProjectTreeView();
        resourcesView = new ResourcesView();
        sceneTreeView = new SceneTreeView();
        sceneView = new SceneView();
        gameView = new GameView();
        topToolbarView = new TopToolbarView();
        logView = new LogView();
        bottomMenuView = new BottomMenuView();
        toolbarView = new ToolbarView();
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
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0F);

        ImGui.begin("Dockspace demo", new ImBoolean(true), windowFlags);
        {
            ImGui.popStyleVar(3);

            ImGui.dockSpace(mainDockspaceID);

            topToolbarView.draw();

            sceneTreeView.draw();

            projectTreeView.draw();

            resourcesView.draw();

            inspectorView.draw();

            sceneView.draw();

            gameView.draw();

            logView.draw();

            bottomMenuView.draw();

            toolbarView.draw();
        }
        ImGui.end();
    }

    public static InspectorView getInspectorView() { return inspectorView; }

    public static ProjectTreeView getProjectTreeView() { return projectTreeView; }

    public static ResourcesView getResourcesView() { return resourcesView; }

    public static SceneTreeView getSceneTreeView() { return sceneTreeView; }

    public static SceneView getSceneView() { return sceneView; }

    public static TopToolbarView getTopToolbarView() { return topToolbarView; }

    public static BottomMenuView getBottomMenuView() {  return bottomMenuView;  }

    public static int getCurrentFocusedDialogWindow() { return currentFocusedDialogWindow; }
    public static void setCurrentFocusedDialogWindow(int currentFocusedDialogWindow)
    {
        MainView.lastFocusedDialogWindow = MainView.currentFocusedDialogWindow;
        MainView.currentFocusedDialogWindow = currentFocusedDialogWindow;
    }
}
