package SungearEngine2D.DebugDraw;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Primitives.BoxComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Transform.Transform;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.Resources;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class CamerasDebugLines
{
    private static Entity inspectorCamera2DBox = Entity.createAsBox();
    private static Entity mainCamera2DBox = Entity.createAsBox();

    private static Entity inspectorCamera2DIcon = Entity.createAsObject2D();
    private static Entity mainCamera2DIcon = Entity.createAsObject2D();

    public static void init()
    {
        inspectorCamera2DIcon.getComponent(MeshComponent.class).texture.set(Resources.Textures.Icons.cameraIcon);
        Vector2f cameraSize = new Vector2f(Resources.Textures.Icons.cameraIcon.getTexture2DData().getWidth(),
                Resources.Textures.Icons.cameraIcon.getTexture2DData().getHeight());
        inspectorCamera2DIcon.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(cameraSize.x / 100.0f, cameraSize.y / 100.0f));

        mainCamera2DIcon.getComponent(MeshComponent.class).texture.set(Resources.Textures.Icons.cameraIcon);
        mainCamera2DIcon.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(cameraSize.x / 100.0f, cameraSize.y / 100.0f));

        inspectorCamera2DBox.getComponent(BoxComponent.class).setColor(new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
        mainCamera2DBox.getComponent(BoxComponent.class).setColor(new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));

        Main.getMainCamera2D().getComponent(Camera2DComponent.class).getAdditionalEntitiesToRender().add(inspectorCamera2DIcon);
        Main.getMainCamera2D().getComponent(Camera2DComponent.class).getAdditionalEntitiesToRender().add(inspectorCamera2DBox);
        Main.getMainCamera2D().getComponent(Camera2DComponent.class).getAdditionalEntitiesToRender().add(mainCamera2DIcon);
        Main.getMainCamera2D().getComponent(Camera2DComponent.class).getAdditionalEntitiesToRender().add(mainCamera2DBox);
    }

    public static void draw() {
        inspectorCamera2DIcon.active = false;
        inspectorCamera2DBox.active = false;
        mainCamera2DIcon.active = false;
        mainCamera2DBox.active = false;

        if (currentSceneManager.getCurrentScene2D() != null
                && currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null
                && ViewsManager.getInspectorView().getCurrentInspectingObject() != null
                && ((Entity) ViewsManager.getInspectorView().getCurrentInspectingObject()).ID != currentSceneManager.getCurrentScene2D().getSceneMainCamera2D().ID) {
            Entity camera2D = (Entity) ViewsManager.getInspectorView().getCurrentInspectingObject();

            TransformComponent cameraTransformComponent = camera2D.getComponent(TransformComponent.class);
            Camera2DComponent camera2DComponent = camera2D.getComponent(Camera2DComponent.class);
            if(cameraTransformComponent != null && camera2DComponent != null) {
                inspectorCamera2DIcon.active = true;
                inspectorCamera2DBox.active = true;

                inspectorCamera2DIcon.getComponent(TransformComponent.class).getTransform().setPosition(cameraTransformComponent.getTransform().getRealPosition());

                Transform boxTransform = inspectorCamera2DBox.getComponent(TransformComponent.class).getTransform();
                BoxComponent boxComponent = inspectorCamera2DBox.getComponent(BoxComponent.class);
                Vector2f camera2DRealScale = cameraTransformComponent.getTransform().getRealScale();
                boxTransform.setPosition(cameraTransformComponent.getTransform().getRealPosition());
                boxTransform.setRotation(cameraTransformComponent.getTransform().getRealRotation());
                boxComponent.setWidth(camera2DComponent.getViewportSize().x * (1.0f / camera2DRealScale.x));
                boxComponent.setHeight(camera2DComponent.getViewportSize().y * (1.0f / camera2DRealScale.y));
            }
        }
        if (currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
            Entity camera2D = currentSceneManager.getCurrentScene2D().getSceneMainCamera2D();

            TransformComponent cameraTransformComponent = camera2D.getComponent(TransformComponent.class);
            Camera2DComponent camera2DComponent = camera2D.getComponent(Camera2DComponent.class);
            if(cameraTransformComponent != null && camera2DComponent != null) {
                mainCamera2DIcon.active = true;
                mainCamera2DBox.active = true;

                mainCamera2DIcon.getComponent(TransformComponent.class).getTransform().setPosition(cameraTransformComponent.getTransform().getRealPosition());

                Transform boxTransform = mainCamera2DBox.getComponent(TransformComponent.class).getTransform();
                BoxComponent boxComponent = mainCamera2DBox.getComponent(BoxComponent.class);
                Vector2f camera2DRealScale = cameraTransformComponent.getTransform().getRealScale();
                boxTransform.setPosition(cameraTransformComponent.getTransform().getRealPosition());
                boxTransform.setRotation(cameraTransformComponent.getTransform().getRealRotation());
                boxComponent.setWidth(camera2DComponent.getViewportSize().x * (1.0f / camera2DRealScale.x));
                boxComponent.setHeight(camera2DComponent.getViewportSize().y * (1.0f / camera2DRealScale.y));
            }
        }

        inspectorCamera2DIcon.update();
        inspectorCamera2DBox.update();
        mainCamera2DIcon.update();
        mainCamera2DBox.update();

        inspectorCamera2DIcon.getComponent(TransformComponent.class).getTransform().update(0.0f);
        inspectorCamera2DBox.getComponent(TransformComponent.class).getTransform().update(0.0f);
        mainCamera2DIcon.getComponent(TransformComponent.class).getTransform().update(0.0f);
        mainCamera2DBox.getComponent(TransformComponent.class).getTransform().update(0.0f);
    }
}
