package SungearEngine2D.GUI.Views;

import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Controllers.PC.Mouse;
import Core2D.Core2D.Core2D;
import Core2D.Graphics.Graphics;
import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Texture2D.Texture2D;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.DebugDraw.Grid;
import SungearEngine2D.Main.GraphicsRenderer;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Main.Settings;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.ResourcesUtils;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import org.apache.commons.io.FilenameUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.io.File;

public class SceneView extends View
{
    private Vector2f targetSize = new Vector2f(0.0f, 0.0f);

    // позиция окна сцены относительно окна
    private Vector2f sceneViewWindowScreenPosition = new Vector2f();
    private Vector2f sceneViewWindowSize = new Vector2f();

    // scale камеры, установленный для одинакового размера объектов под разные экраны и вид
    private Vector2f ratioCameraScale = new Vector2f();

    // превью нового объекта на сцене
    private Object2D newObject2DPreview;

    private float targetAspect = 16 / 9.0f;

    public SceneView()
    {
        init();
    }

    @Override
    public void init()
    {
        Vector2i size = Graphics.getScreenSize();
        targetSize.x = size.x;
        targetSize.y = size.y;
    }

    public void draw()
    {
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.65f, 0.65f, 0.65f, 1.0f);
        ImGui.begin("Scene2D view", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);
        {
            ImGui.beginMenuBar();
            {
                Vector4f playButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                boolean active = Settings.Playmode.active;
                if(active) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                    playButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                }
                if(ImGui.imageButton(Resources.Textures.Icons.playButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, playButtonColor.x, playButtonColor.y, playButtonColor.z, playButtonColor.w)) {
                    if(SceneManager.getCurrentScene2D() != null) {
                        if(!Settings.Playmode.active && !Settings.Playmode.paused) {
                            startPlayMode();
                        } else {
                            stopPlayMode();
                        }
                    }
                }
                if(active) {
                    ImGui.popStyleColor(3);
                }

                Vector4f pauseButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                boolean paused = Settings.Playmode.paused;
                if(paused) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                    pauseButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                }
                if(ImGui.imageButton(Resources.Textures.Icons.pauseButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, pauseButtonColor.x, pauseButtonColor.y, pauseButtonColor.z, pauseButtonColor.w)) {
                    if(SceneManager.getCurrentScene2D() != null) {
                        pausePlayMode();
                    }
                }
                if(paused) {
                    ImGui.popStyleColor(3);
                }

                if(ImGui.imageButton(Resources.Textures.Icons.stopButtonIcon.getTextureHandler(), 8, 10)) {
                    if(SceneManager.getCurrentScene2D() != null) {
                        stopPlayMode();
                    }
                }
                //if(ImGui.menuItem())
            }
            boolean hovered = ImGui.isAnyItemHovered();

            ImGui.endMenuBar();

            MainView.isSomeViewFocusedExceptSceneView = !ImGui.isWindowFocused() || hovered;

            ImGui.popStyleColor(1);

            if(!Settings.Playmode.active) {
                // нахожу размер свободного места
                ImVec2 windowSize = ImGui.getContentRegionAvail();

                // нахожу позицию окна
                ImVec2 windowScreenPos = ImGui.getCursorScreenPos();
                windowScreenPos.x -= ImGui.getScrollX();
                windowScreenPos.y -= ImGui.getScrollY();

                sceneViewWindowScreenPosition.x = windowScreenPos.x;
                sceneViewWindowScreenPosition.y = (Core2D.getWindow().getSize().y - windowScreenPos.y) - windowSize.y;

                sceneViewWindowSize.x = windowSize.x;
                sceneViewWindowSize.y = windowSize.y;

                //System.out.println("size: " + windowSize.x + ", " + windowSize.y);
                // нахожу размер окна, который нужен
                ImVec2 windowSizeDest = getLargestSizeForViewport();

                // нахожу соотношение сторон
                ratioCameraScale.x = (windowSizeDest.x / windowSize.x) / (targetSize.x / Core2D.getWindow().getSize().x);
                ratioCameraScale.y = (windowSizeDest.y / windowSize.y) / (targetSize.y / Core2D.getWindow().getSize().y);

                Mouse.setViewportPosition(sceneViewWindowScreenPosition);
                Mouse.setViewportSize(new Vector2f(sceneViewWindowSize.x, sceneViewWindowSize.y));

                ImGui.image(GraphicsRenderer.getRenderTarget().getTextureHandler(), windowSize.x, windowSize.y, 0, 1, 1, 0);
            } else {
                ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
                ImVec2 windowSize = getLargestSizeForViewport();
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

                Vector2i engineWindowSize = Core2D.getWindow().getSize();
                ImGui.image(GraphicsRenderer.getRenderTarget().getTextureHandler(), sceneViewWindowSize.x, sceneViewWindowSize.y, 0, 1, 1, 0);
            }

            if(ImGui.beginDragDropTarget()) {
                if(MainView.getResourcesView().getCurrentMovingFile() != null && ResourcesUtils.isFileImage(MainView.getResourcesView().getCurrentMovingFile()) && SceneManager.getCurrentScene2D() != null) {
                    // если превью не существует, то создаю его
                    if(newObject2DPreview == null) {
                        newObject2DPreview = createSceneObject2D(MainView.getResourcesView().getCurrentMovingFile());
                    }
                    // нахожу позицию для превью относительно мыши, чтобы он за ней следовал
                    Vector2f oglPosition = getMouseOGLPosition(Mouse.getMousePosition());
                    Vector2f objectPosition = new Vector2f(
                            oglPosition.x - newObject2DPreview.getComponent(TransformComponent.class).getTransform().getScale().x * 100.0f / 2.0f,
                            oglPosition.y - newObject2DPreview.getComponent(TransformComponent.class).getTransform().getScale().y * 100.0f / 2.0f);
                    // ставлю превью в эту позицию
                    newObject2DPreview.getComponent(TransformComponent.class).getTransform().setPosition(objectPosition);

                    Object droppedObject = ImGui.acceptDragDropPayload("File");
                    //ImGui.setMouseCursor(ImGuiMouseCursor.ResizeAll);
                    // если мышка отжата, то есть droppedFile != null, то создаю объект на сцене по координатам мышки, а превью удаляю
                    if(droppedObject instanceof File) {
                        File imageFile = (File) droppedObject;

                        newObject2DPreview.destroy();
                        newObject2DPreview = null;
                        Object2D newSceneObject2D = createSceneObject2D(imageFile);
                    }
                } else {
                    //System.out.println("dsds");
                    //ImGui.setMouseCursor(ImGuiMouseCursor.NotAllowed);
                }

                ImGui.endDragDropTarget();
            }

            // если мышка не находится в окне и объект превью существует, то удаляю его
            if(!isHovered() && newObject2DPreview != null) {
                newObject2DPreview.destroy();
                newObject2DPreview = null;
            }

            update();
        }
        ImGui.end();
    }

    public void startPlayMode()
    {
        if(SceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
            if (Compiler.getNotCompiledScripts().size() != 0) {
                MainView.getBottomMenuView().leftSideInfo = "Script " + Compiler.getNotCompiledScripts().get(Compiler.getNotCompiledScripts().size() - 1) + " was not compiled. Fix all errors before entering the playmode";
                MainView.getBottomMenuView().leftSideInfoColor.set(1.0f, 0.0f, 0.0f, 1.0f);
            }
            if (SceneManager.getCurrentScene2D() != null && Settings.Playmode.canEnterPlaymode) {
                Settings.Playmode.active = true;
                Settings.Playmode.paused = false;
                Grid.show = false;
                CameraController.controlCamera = false;
                SceneManager.saveScene(SceneManager.getCurrentScene2D(), SceneManager.getCurrentScene2D().getScenePath());
                CamerasManager.setMainCamera2D(SceneManager.getCurrentScene2D().getSceneMainCamera2D());
                SceneManager.getCurrentScene2D().getPhysicsWorld().simulatePhysics = true;
                SceneManager.getCurrentScene2D().getScriptSystem().runScripts = true;
            }
        }
    }

    public void stopPlayMode()
    {
        if(SceneManager.getCurrentScene2D() != null && Settings.Playmode.active) {
            Settings.Playmode.active = false;
            Settings.Playmode.paused = false;
            Grid.show = true;
            CameraController.controlCamera = true;
            SceneManager.loadScene(SceneManager.getCurrentScene2D().getScenePath());
            CamerasManager.setMainCamera2D(Main.getMainCamera2D());
            MainView.getInspectorView().setCurrentInspectingObject(null);
            SceneManager.getCurrentScene2D().getPhysicsWorld().simulatePhysics = false;
            SceneManager.getCurrentScene2D().getScriptSystem().runScripts = false;
        }
    }

    public void pausePlayMode()
    {
        if(SceneManager.getCurrentScene2D() != null && Settings.Playmode.active) {
            Settings.Playmode.paused = !Settings.Playmode.paused;
            SceneManager.getCurrentScene2D().getPhysicsWorld().simulatePhysics = !SceneManager.getCurrentScene2D().getPhysicsWorld().simulatePhysics;
            SceneManager.getCurrentScene2D().getScriptSystem().runScripts = !SceneManager.getCurrentScene2D().getScriptSystem().runScripts;
        }
    }

    private ImVec2 getLargestSizeForViewport()
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        //windowSize.x -= ImGui.getScrollX();
        //windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / targetAspect;

        if(aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * targetAspect;
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    // возвращает позицию мыши относительно экрана вида сцены
    public Vector2f getMouseRelativePosition(Vector2f screenMousePosition)
    {
        float currentX = screenMousePosition.x - MainView.getSceneView().getSceneViewWindowScreenPosition().x;
        currentX = (currentX / MainView.getSceneView().getSceneViewWindowSize().x) * MainView.getSceneView().getTargetSize().x;

        float currentY = screenMousePosition.y - MainView.getSceneView().getSceneViewWindowScreenPosition().y;
        currentY = (currentY / MainView.getSceneView().getSceneViewWindowSize().y) * MainView.getSceneView().getTargetSize().y;

        return new Vector2f(currentX, currentY);
    }

    // создает объект на сцене
    private Object2D createSceneObject2D(File imageFile)
    {
        Object2D newSceneObject2D = new Object2D();

        // TODO: сделать папку, которая будет для ресурсов (папку можно будет отметить как ресурсной) и чтобы только оттуда можно было дропать файлы (она будет использоваться для билд)
        TextureComponent textureComponent = newSceneObject2D.getComponent(TextureComponent.class);
        textureComponent.setTexture2D(new Texture2D(imageFile.getPath()));

        Vector2f newObject2DScale = new Vector2f(textureComponent.getTexture2D().getWidth() / 100.0f, textureComponent.getTexture2D().getHeight() / 100.0f);
        newSceneObject2D.getComponent(TransformComponent.class).getTransform().setScale(newObject2DScale);

        Vector2f oglPosition = getMouseOGLPosition(Mouse.getMousePosition());
        Vector2f objectPosition = new Vector2f(
                oglPosition.x - newSceneObject2D.getComponent(TransformComponent.class).getTransform().getScale().x * 100.0f / 2.0f,
                oglPosition.y - newSceneObject2D.getComponent(TransformComponent.class).getTransform().getScale().y * 100.0f / 2.0f);
        newSceneObject2D.getComponent(TransformComponent.class).getTransform().setPosition(objectPosition);

        // дефолтный layer
        newSceneObject2D.setLayer(SceneManager.getCurrentScene2D().getLayering().getLayer("default"));
        newSceneObject2D.setName(FilenameUtils.getBaseName(imageFile.getName()));

        newObject2DScale = null;
        textureComponent = null;

        MainView.getResourcesView().setCurrentMovingFile(null);

        return newSceneObject2D;
    }

    // получить позицию в мировом пространстве относительно viewport
    public Vector2f getMouseOGLPosition(Vector2f mousePosition)
    {
        /*
        Vector2f originalMousePosition = new Vector2f(
                mousePosition.x / Graphics.getScreenSize().x * sceneViewWindowSize.x + sceneViewWindowScreenPosition.x,
                mousePosition.y / Graphics.getScreenSize().y * sceneViewWindowSize.y + sceneViewWindowScreenPosition.y
        );

         */

        //float currentX = originalMousePosition.x - sceneViewWindowScreenPosition.x;
        //currentX = (currentX / sceneViewWindowSize.x) * 2.0f - 1.0f;
        float currentX = mousePosition.x / Graphics.getScreenSize().x * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        Matrix4f viewProjection = new Matrix4f();
        Matrix4f inverseView = new Matrix4f();
        Matrix4f inverseProjection = new Matrix4f();

        Main.getMainCamera2D().getTransform().getModelMatrix().invert(inverseView);
        Core2D.getProjectionMatrix().invert(inverseProjection);

        inverseView.mul(inverseProjection, viewProjection);
        tmp.mul(viewProjection);

        currentX = tmp.x;

        //float currentY = originalMousePosition.y - sceneViewWindowScreenPosition.y;
        //currentY = ((currentY / sceneViewWindowSize.y) * 2.0f - 1.0f);
        float currentY = mousePosition.y / Graphics.getScreenSize().y * 2.0f - 1.0f;;

        tmp = new Vector4f(0, currentY, 0, 1);
        tmp.mul(viewProjection);

        currentY = tmp.y;

        return new Vector2f(currentX, currentY);
    }

    public Vector2f getRatioCameraScale() { return ratioCameraScale; }

    public Vector2f getSceneViewWindowScreenPosition() { return sceneViewWindowScreenPosition; }

    public Vector2f getSceneViewWindowSize() { return sceneViewWindowSize; }

    public Vector2f getTargetSize() { return targetSize; }
}
