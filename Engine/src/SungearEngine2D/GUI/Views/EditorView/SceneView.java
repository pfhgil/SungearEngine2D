package SungearEngine2D.GUI.Views.EditorView;

import Core2D.AssetManager.AssetManager;
import Core2D.CamerasManager.CamerasManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Input.PC.Mouse;
import Core2D.Prefab.Prefab;
import Core2D.Project.ProjectsManager;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Main.EngineSettings;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.ResourcesUtils;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.apache.commons.io.FilenameUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.io.File;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class SceneView extends View
{
    private Vector2f targetSize = new Vector2f(0.0f, 0.0f);

    // позиция окна сцены относительно окна
    private Vector2f sceneViewWindowScreenPosition = new Vector2f();
    private Vector2f sceneViewWindowSize = new Vector2f();

    // scale камеры, установленный для одинакового размера объектов под разные экраны и вид
    private Vector2f ratioCameraScale = new Vector2f();

    // превью нового объекта на сцене
    private Entity newObject2DPreview;

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
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        boolean windowOpened = ImGui.begin("Scene2D view", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);
        if(windowOpened) {
            CamerasManager.mainCamera2D = Main.getMainCamera2D();

            ImGui.popStyleVar(1);
            ImGui.popStyleColor(1);
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
                boolean paused = EngineSettings.Playmode.paused;
                if(paused) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                    pauseButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                }
                if(ImGui.imageButton(Resources.Textures.Icons.pauseButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, pauseButtonColor.x, pauseButtonColor.y, pauseButtonColor.z, pauseButtonColor.w)) {
                    if(currentSceneManager.getCurrentScene2D() != null) {
                        pausePlayMode();
                    }
                }
                if(paused) {
                    ImGui.popStyleColor(3);
                }

                if(ImGui.imageButton(Resources.Textures.Icons.stopButtonIcon.getTextureHandler(), 8, 10)) {
                    if(currentSceneManager.getCurrentScene2D() != null) {
                        stopPlayMode();
                    }
                }
            }
            boolean hovered = ImGui.isAnyItemHovered();

            ImGui.endMenuBar();

            ViewsManager.isSomeViewFocusedExceptSceneView = !ImGui.isWindowFocused() || hovered;

            //ImGui.popStyleColor(1);

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

            // нахожу размер окна, который нужен
            ImVec2 windowSizeDest = getLargestSizeForViewport();

            // нахожу соотношение сторон
            ratioCameraScale.x = (windowSizeDest.x / windowSize.x) / (targetSize.x / Core2D.getWindow().getSize().x);
            ratioCameraScale.y = (windowSizeDest.y / windowSize.y) / (targetSize.y / Core2D.getWindow().getSize().y);

            Mouse.setViewportPosition(sceneViewWindowScreenPosition);
            Mouse.setViewportSize(new Vector2f(sceneViewWindowSize.x, sceneViewWindowSize.y));

            ImGui.image(Main.getMainCamera2DComponent().getResultFrameBuffer().getTextureHandler(), windowSize.x, windowSize.y);

            if(ImGui.beginDragDropTarget()) {
                if(ViewsManager.getResourcesView().getCurrentMovingFile() != null &&
                        (ResourcesUtils.isFileImage(ViewsManager.getResourcesView().getCurrentMovingFile()) ||
                                ResourcesUtils.isFilePrefab(ViewsManager.getResourcesView().getCurrentMovingFile())) &&
                        currentSceneManager.getCurrentScene2D() != null) {
                    // если превью не существует, то создаю его
                    if(newObject2DPreview == null) {
                        newObject2DPreview = createSceneEntity(ViewsManager.getResourcesView().getCurrentMovingFile());
                    }
                    // нахожу позицию для превью относительно мыши, чтобы он за ней следовал
                    Vector2f oglPosition = getMouseOGLPosition(Mouse.getMousePosition());
                    // ставлю превью в эту позицию
                    newObject2DPreview.getComponent(TransformComponent.class).getTransform().setPosition(oglPosition);

                    Object droppedObject = ImGui.acceptDragDropPayload("File");
                    //ImGui.setMouseCursor(ImGuiMouseCursor.ResizeAll);
                    // если мышка отжата, то есть droppedFile != null, то создаю объект на сцене по координатам мышки, а превью удаляю
                    if(droppedObject instanceof File) {
                        File file = (File) droppedObject;
                        Entity entity = createSceneEntity(file);
                        if(entity != null) {
                            entity.getComponent(TransformComponent.class).getTransform().setPosition(
                                    newObject2DPreview.getComponent(TransformComponent.class).getTransform().getPosition()
                            );
                        }
                        newObject2DPreview.destroy();
                        newObject2DPreview = null;
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
        } else {
            ImGui.popStyleVar(1);
            ImGui.popStyleColor(1);
        }
        ImGui.end();
    }

    public void startPlayMode()
    {
        if(currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
            if (Compiler.getNotCompiledScripts().size() != 0) {
                ViewsManager.getBottomMenuView().leftSideInfo = "Script " + Compiler.getNotCompiledScripts().get(Compiler.getNotCompiledScripts().size() - 1) + " was not compiled. Fix all errors before entering the playmode";
                ViewsManager.getBottomMenuView().leftSideInfoColor.set(1.0f, 0.0f, 0.0f, 1.0f);
            }
            if (currentSceneManager.getCurrentScene2D() != null && EngineSettings.Playmode.canEnterPlaymode) {
                EngineSettings.Playmode.active = true;
                EngineSettings.Playmode.paused = false;

                currentSceneManager.saveScene(currentSceneManager.getCurrentScene2D(), currentSceneManager.getCurrentScene2D().getScenePath());

                ScriptSystem.reloadAllSceneScriptsWithGlobalClassLoader();

                CamerasManager.mainCamera2D = currentSceneManager.getCurrentScene2D().getSceneMainCamera2D();
                currentSceneManager.getCurrentScene2D().setRunning(true);
                currentSceneManager.getCurrentScene2D().applyScriptsTempValues();
            }
        }
    }

    public void stopPlayMode()
    {
        // вот уже все плохо =(
        if(currentSceneManager.getCurrentScene2D() != null && EngineSettings.Playmode.active) {
            currentSceneManager.loadSceneAsCurrent(currentSceneManager.getCurrentScene2D().getScenePath());

            ScriptSystem.reloadAllSceneScriptsWithUniqueClassLoader();

            EngineSettings.Playmode.active = false;
            EngineSettings.Playmode.paused = false;

            ViewsManager.getInspectorView().setCurrentInspectingObject(null);
            currentSceneManager.getCurrentScene2D().setRunning(false);
        }
    }

    public void pausePlayMode()
    {
        if(currentSceneManager.getCurrentScene2D() != null && EngineSettings.Playmode.active) {
            System.out.println("paused!!");
            EngineSettings.Playmode.paused = !EngineSettings.Playmode.paused;
            currentSceneManager.getCurrentScene2D().setRunning(!currentSceneManager.getCurrentScene2D().isRunning());
        }
    }

    private ImVec2 getLargestSizeForViewport()
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float targetAspect = 16.0f / 9.0f;

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / targetAspect;

        if(aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * targetAspect;
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getLargestSizeForViewport(float targetAspect)
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

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
        float currentX = screenMousePosition.x - ViewsManager.getSceneView().getSceneViewWindowScreenPosition().x;
        currentX = (currentX / ViewsManager.getSceneView().getSceneViewWindowSize().x) * ViewsManager.getSceneView().getTargetSize().x;

        float currentY = screenMousePosition.y - ViewsManager.getSceneView().getSceneViewWindowScreenPosition().y;
        currentY = (currentY / ViewsManager.getSceneView().getSceneViewWindowSize().y) * ViewsManager.getSceneView().getTargetSize().y;

        return new Vector2f(currentX, currentY);
    }

    // создает объект на сцене
    private Entity createSceneEntity(File file)
    {
        String extension = FilenameUtils.getExtension(file.getName());
        if(extension.equals("png") || extension.equals("jpg")) {
            Entity newSceneEntity = Entity.createAsObject2D();

            String relativePath = FileUtils.getRelativePath(
                    new File(file.getPath()),
                    new File(ProjectsManager.getCurrentProject().getProjectPath()));
            MeshComponent meshComponent = newSceneEntity.getComponent(MeshComponent.class);
            Texture2D texture2D = new Texture2D(AssetManager.getInstance().getTexture2DData(relativePath));
            meshComponent.setTexture(texture2D);
            meshComponent.getTexture().path = relativePath;

            Vector2f oglPosition = getMouseOGLPosition(Mouse.getMousePosition());
            newSceneEntity.getComponent(TransformComponent.class).getTransform().setPosition(oglPosition);

            Vector2f newObject2DScale = new Vector2f(meshComponent.getTexture().getTexture2DData().getWidth() / 100.0f,
                    meshComponent.getTexture().getTexture2DData().getHeight() / 100.0f);
            newSceneEntity.getComponent(TransformComponent.class).getTransform().setScale(newObject2DScale);

            // дефолтный layer
            newSceneEntity.setLayer(currentSceneManager.getCurrentScene2D().getLayering().getLayer("default"));
            newSceneEntity.name = FilenameUtils.getBaseName(file.getName());

            ViewsManager.getResourcesView().setCurrentMovingFile(null);

            System.gc();
            System.out.println("added obj!");

            return newSceneEntity;
        } else if(extension.equals("sgopref")) {
            Prefab prefab = Prefab.load(file.getPath());

            ViewsManager.getResourcesView().setCurrentMovingFile(null);

            prefab.applyObjectsToScene();

            return prefab.getPrefabObject();
        }

        return null;
    }

    // получить позицию в мировом пространстве относительно viewport
    public Vector2f getMouseOGLPosition(Vector2f mousePosition)
    {
        float currentX = mousePosition.x / Graphics.getScreenSize().x * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        Matrix4f viewProjection = new Matrix4f();
        Matrix4f inverseView = new Matrix4f();
        Matrix4f inverseProjection = new Matrix4f();

        Camera2DComponent camera2DComponent = CamerasManager.mainCamera2D.getComponent(Camera2DComponent.class);
        if(camera2DComponent != null) {
            camera2DComponent.getViewMatrix().invert(inverseView);
            camera2DComponent.getProjectionMatrix().invert(inverseProjection);
        }

        inverseView.mul(inverseProjection, viewProjection);
        tmp.mul(viewProjection);

        currentX = tmp.x;

        float currentY = mousePosition.y / Graphics.getScreenSize().y * 2.0f - 1.0f;

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
