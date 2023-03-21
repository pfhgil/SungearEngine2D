package SungearEngine2D.GUI.Views;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.CameraComponent;
import Core2D.ECS.Component.Components.Physics.Rigidbody2DComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.System.ComponentsQuery;
import SungearEngine2D.GUI.Views.DebuggerView.DebuggerView;
import SungearEngine2D.GUI.Views.EditorView.*;
import SungearEngine2D.GUI.Views.Other.EngineSettingsView;
import SungearEngine2D.GUI.Views.Other.ProjectSettingsView;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class ViewsManager
{
    private static int mainDockspaceID;

    // находится ли какой-то вид в фокусе кроме вида сцены
    public static boolean isSceneViewFocused = false;

    private static int lastFocusedDialogWindow = -1;
    private static int currentFocusedDialogWindow = -1;

    private static InspectorView inspectorView;
    private static ComponentsView componentsView;
    private static SystemsView systemsView;

    private static ProjectTreeView projectTreeView;
    private static ResourcesView resourcesView;
    private static SceneTreeView sceneTreeView;
    private static SceneView sceneView;
    private static GameView mainCameraResultView;
    // остальные виды FBO
    private static List<GameView> FBOViews = new ArrayList<>();
    private static TopToolbarView topToolbarView;
    private static LogView logView;
    private static BottomMenuView bottomMenuView;
    private static ToolbarView toolbarView;

    private static ProjectSettingsView projectSettingsView;
    private static EngineSettingsView engineSettingsView;

    private static DebuggerView debuggerView;

    private static ShadersEditorView shadersEditorView;

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
        mainCameraResultView = new GameView("Game view", "GameView", -1, false);
        topToolbarView = new TopToolbarView();
        logView = new LogView();
        bottomMenuView = new BottomMenuView();
        toolbarView = new ToolbarView();

        projectSettingsView = new ProjectSettingsView();
        engineSettingsView = new EngineSettingsView();

        debuggerView = new DebuggerView();

        shadersEditorView = new ShadersEditorView();
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

        if(currentSceneManager != null && currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
            CameraComponent cameraComponent = currentSceneManager.getCurrentScene2D().getSceneMainCamera2D().getComponent(CameraComponent.class);
            if(cameraComponent != null) {
                mainCameraResultView.setViewTextureHandler(cameraComponent.resultFrameBuffer.getTextureHandler());
            }
        }

        ImGui.begin("Dockspace demo", new ImBoolean(true), windowFlags);
        {
            ImGui.popStyleVar(3);

            ImGui.dockSpace(mainDockspaceID);

            topToolbarView.draw();

            sceneTreeView.draw();

            projectTreeView.draw();

            resourcesView.draw();

            for(ComponentsQuery componentsQuery : ECSWorld.getCurrentECSWorld().getComponentsQueries()) {
                for(Component component : componentsQuery.getComponents()) {
                    if(component instanceof Rigidbody2DComponent rigidbody2DComponent) {
                        Vec2 linearVelocity = new Vec2(rigidbody2DComponent.getRigidbody2D().getBody().getLinearVelocity());
                        float rotation = rigidbody2DComponent.getRigidbody2D().getBody().getAngularVelocity();

                        ImGui.begin("velocitites");

                        ImGui.text("velocity: " + linearVelocity.x + ", " + linearVelocity.y + ", rotation: " + rotation);

                        ImGui.end();
                    }
                }
            }

            inspectorView.draw();
            componentsView.draw();
            systemsView.draw();

            sceneView.draw();

            mainCameraResultView.draw();
            for(int i = 0; i < FBOViews.size(); i++) {
                FBOViews.get(i).handlePostprocessingLayer();
                FBOViews.get(i).draw();
            }

            logView.draw();

            bottomMenuView.draw();

            toolbarView.draw();

            debuggerView.draw();

            shadersEditorView.draw();

            /*
            ImGui.begin("Systems");

            for(ComponentsQuery componentsQuery : ECSWorld.getCurrentECSWorld().componentsManager.getComponentsQueries()) {
                if(ImGui.collapsingHeader("Components query " + componentsQuery.entityID)) {
                    for(Component component : componentsQuery.getComponents()) {
                        ImGui.text("Component: " + component + ", entity ID: " + component.entity.ID);
                    }
                }
            }

            ImGui.end();

             */
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

    public static GameView getMainCameraResultView() { return mainCameraResultView; }

    public static List<GameView> getFBOViews() { return FBOViews; }
    public static boolean isFBOViewExists(String windowID)
    {
        return FBOViews.stream().anyMatch(fbo -> fbo.getWindowID().equals(windowID));
    }

    public static GameView getFBOView(String windowID)
    {
        Optional<GameView> fboViewOptional = FBOViews.stream().filter(fboView -> fboView.getWindowID().equals(windowID)).findFirst();

        return fboViewOptional.orElse(null);
    }

    public static TopToolbarView getTopToolbarView() { return topToolbarView; }

    public static BottomMenuView getBottomMenuView() {  return bottomMenuView;  }

    public static ProjectSettingsView getProjectSettingsView() { return projectSettingsView; }

    public static EngineSettingsView getEngineSettingsView() { return engineSettingsView; }

    public static DebuggerView getDebuggerView() { return debuggerView; }

    public static ShadersEditorView getShadersEditorView() { return shadersEditorView; }

    public static int getCurrentFocusedDialogWindow() { return currentFocusedDialogWindow; }
    public static void setCurrentFocusedDialogWindow(int currentFocusedDialogWindow)
    {
        ViewsManager.lastFocusedDialogWindow = ViewsManager.currentFocusedDialogWindow;
        ViewsManager.currentFocusedDialogWindow = currentFocusedDialogWindow;
    }

    public static int getMainDockspaceID() { return mainDockspaceID; }
}
