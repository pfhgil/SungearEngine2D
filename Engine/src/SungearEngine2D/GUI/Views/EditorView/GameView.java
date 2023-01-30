package SungearEngine2D.GUI.Views.EditorView;

import Core2D.CamerasManager.CamerasManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.Input.PC.Mouse;
import Core2D.Scene2D.SceneManager;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.Main.GraphicsRenderer;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Main.EngineSettings;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class GameView extends View
{
    // позиция окна сцены относительно окна
    private Vector2f sceneViewWindowScreenPosition = new Vector2f();
    private Vector2f sceneViewWindowSize = new Vector2f();

    public void draw()
    {
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.65f, 0.65f, 0.65f, 1.0f);
        boolean windowOpened = ImGui.begin("Game view", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);
        if(windowOpened) {
            if(currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
                CamerasManager.mainCamera2D = currentSceneManager.getCurrentScene2D().getSceneMainCamera2D();
            }

            ImGui.beginMenuBar();
            {
                Vector4f playButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                boolean active = EngineSettings.Playmode.active;
                if(active) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                    playButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                }
                if(ImGui.imageButton(Resources.Textures.Icons.playButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, playButtonColor.x, playButtonColor.y, playButtonColor.z, playButtonColor.w)) {
                    if(currentSceneManager.getCurrentScene2D() != null) {
                        if(!EngineSettings.Playmode.active && !EngineSettings.Playmode.paused) {
                            ViewsManager.getSceneView().startPlayMode();
                        } else {
                            ViewsManager.getSceneView().stopPlayMode();
                        }
                    }
                }
                if(active) {
                    ImGui.popStyleColor(3);
                }

                Vector4f pauseButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                boolean paused = EngineSettings.Playmode.paused;
                if(paused) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                    pauseButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                }
                if(ImGui.imageButton(Resources.Textures.Icons.pauseButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, pauseButtonColor.x, pauseButtonColor.y, pauseButtonColor.z, pauseButtonColor.w)) {
                    if(currentSceneManager.getCurrentScene2D() != null) {
                        ViewsManager.getSceneView().pausePlayMode();
                    }
                }
                if(paused) {
                    ImGui.popStyleColor(3);
                }

                if(ImGui.imageButton(Resources.Textures.Icons.stopButtonIcon.getTextureHandler(), 8, 10)) {
                    if(currentSceneManager.getCurrentScene2D() != null) {
                        ViewsManager.getSceneView().stopPlayMode();
                    }
                }
                //if(ImGui.menuItem())
            }
            ImGui.endMenuBar();

            ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());

            Vector2i engineWindowSize = Core2D.getWindow().getSize();
            ImVec2 windowSize = getLargestSizeForViewport(engineWindowSize.x / (float) engineWindowSize.y);
            ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
            ImGui.setCursorPos(windowPos.x, windowPos.y);

            // нахожу позицию окна
            ImVec2 windowScreenPos = ImGui.getCursorScreenPos();
            windowScreenPos.x -= ImGui.getScrollX();
            windowScreenPos.y -= ImGui.getScrollY();

            sceneViewWindowScreenPosition.x = windowScreenPos.x;
            sceneViewWindowScreenPosition.y = (Core2D.getWindow().getSize().y - windowScreenPos.y) - windowSize.y;

            sceneViewWindowSize.x = windowSize.x;
            sceneViewWindowSize.y = windowSize.y;

            Mouse.setViewportPosition(sceneViewWindowScreenPosition);
            Mouse.setViewportSize(new Vector2f(sceneViewWindowSize.x, sceneViewWindowSize.y));

            if(currentSceneManager != null && currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
                ImGui.image(currentSceneManager.getCurrentScene2D().getSceneMainCamera2D().getComponent(Camera2DComponent.class).getResultFrameBuffer().getTextureHandler(), sceneViewWindowSize.x, sceneViewWindowSize.y);
            }

            ImGui.popStyleColor(1);
        } else {
            ImGui.popStyleColor(1);
        }
        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport(float targetAspect)
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        //windowSize.x -= ImGui.getScrollX();
        //windowSize.y -= ImGui.getScrollY();

        //Vector2i engineWindowSize = Core2D.getWindow().getSize();
        //float targetAspect = engineWindowSize.x / (float) engineWindowSize.y;

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / targetAspect;

        if(aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * targetAspect;
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize)
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }
}
