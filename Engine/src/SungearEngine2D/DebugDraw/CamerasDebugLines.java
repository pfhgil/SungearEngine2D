package SungearEngine2D.DebugDraw;

import Core2D.ECS.Camera.CameraComponent;
import Core2D.ECS.Mesh.MeshComponent;
import Core2D.ECS.Primitives.BoxComponent;
import Core2D.ECS.Transform.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Layering.Layering;
import Core2D.Utils.MatrixUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.Resources;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class CamerasDebugLines
{
    private static Entity inspectorCamera2DBox = Entity.createAsBox();
    private static Entity mainCamera2DBox = Entity.createAsBox();

    private static Entity inspectorCamera2DIcon = Entity.createAsObject();
    private static Entity mainCamera2DIcon = Entity.createAsObject();

    public static void init()
    {
        inspectorCamera2DIcon.getComponent(MeshComponent.class).texture2DData = Resources.Textures.Icons.cameraIcon96;
        Vector2f cameraSize = new Vector2f(Resources.Textures.Icons.cameraIcon96.getWidth(),
                Resources.Textures.Icons.cameraIcon96.getHeight());
        inspectorCamera2DIcon.getComponent(TransformComponent.class).scale.set(new Vector3f(cameraSize.x / 100.0f, cameraSize.y / 100.0f, 1f));
        inspectorCamera2DIcon.setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.75f));

        mainCamera2DIcon.getComponent(MeshComponent.class).texture2DData = Resources.Textures.Icons.cameraIcon96;
        mainCamera2DIcon.getComponent(TransformComponent.class).scale.set(new Vector3f(cameraSize.x / 100.0f, cameraSize.y / 100.0f, 1f));
        mainCamera2DIcon.setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.75f));

        inspectorCamera2DBox.getComponent(BoxComponent.class).color.set(new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
        mainCamera2DBox.getComponent(BoxComponent.class).color.set(new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));
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
            CameraComponent cameraComponent = camera2D.getComponent(CameraComponent.class);
            if(cameraTransformComponent != null && cameraComponent != null) {
                inspectorCamera2DIcon.active = true;
                inspectorCamera2DBox.active = true;

                Vector3f camera2DRealPosition = MatrixUtils.getPosition(cameraTransformComponent.modelMatrix);
                Vector3f camera2DRealRotation = MatrixUtils.getEulerRotation(cameraTransformComponent.modelMatrix);
                Vector3f camera2DRealScale = MatrixUtils.getScale(cameraTransformComponent.modelMatrix);

                inspectorCamera2DIcon.getComponent(TransformComponent.class).position.set(camera2DRealPosition.x, camera2DRealPosition.y, camera2DRealPosition.z + Layering.Z_INDEX_P2);
                inspectorCamera2DIcon.getComponent(TransformComponent.class).rotation.set(camera2DRealRotation);

                TransformComponent boxTransformComponent = inspectorCamera2DBox.getComponent(TransformComponent.class);
                BoxComponent boxComponent = inspectorCamera2DBox.getComponent(BoxComponent.class);

                boxTransformComponent.position.set(camera2DRealPosition.x, camera2DRealPosition.y, camera2DRealPosition.z + Layering.Z_INDEX_P2);
                boxTransformComponent.rotation.set(camera2DRealRotation);
                boxComponent.size.x = cameraComponent.viewportSize.x * (1.0f / camera2DRealScale.x);
                boxComponent.size.y = cameraComponent.viewportSize.y * (1.0f / camera2DRealScale.y);
            }
        }
        if (currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
            Entity camera2D = currentSceneManager.getCurrentScene2D().getSceneMainCamera2D();

            TransformComponent cameraTransformComponent = camera2D.getComponent(TransformComponent.class);
            CameraComponent cameraComponent = camera2D.getComponent(CameraComponent.class);
            if(cameraTransformComponent != null && cameraComponent != null) {
                mainCamera2DIcon.active = true;
                mainCamera2DBox.active = true;

                Vector3f camera2DRealPosition = MatrixUtils.getPosition(cameraTransformComponent.modelMatrix);
                Vector3f camera2DRealRotation = MatrixUtils.getEulerRotation(cameraTransformComponent.modelMatrix);
                Vector3f camera2DRealScale = MatrixUtils.getScale(cameraTransformComponent.modelMatrix);

                mainCamera2DIcon.getComponent(TransformComponent.class).position.set(camera2DRealPosition.x, camera2DRealPosition.y, camera2DRealPosition.z + Layering.Z_INDEX_P2);
                mainCamera2DIcon.getComponent(TransformComponent.class).rotation.set(camera2DRealRotation);

                TransformComponent boxTransformComponent = mainCamera2DBox.getComponent(TransformComponent.class);
                BoxComponent boxComponent = mainCamera2DBox.getComponent(BoxComponent.class);

                boxTransformComponent.position.set(camera2DRealPosition.x, camera2DRealPosition.y, camera2DRealPosition.z + Layering.Z_INDEX_P2);
                boxTransformComponent.rotation.set(camera2DRealRotation);
                boxComponent.size.x = cameraComponent.viewportSize.x * (1.0f / camera2DRealScale.x);
                boxComponent.size.y = cameraComponent.viewportSize.y * (1.0f / camera2DRealScale.y);
            }
        }

        inspectorCamera2DIcon.update();
        inspectorCamera2DBox.update();
        mainCamera2DIcon.update();
        mainCamera2DBox.update();

        /*
        ECSWorld.getCurrentECSWorld().transformationsSystem.updateTransformComponent(inspectorCamera2DIcon.getComponent(TransformComponent.class));
        ECSWorld.getCurrentECSWorld().transformationsSystem.updateTransformComponent(inspectorCamera2DBox.getComponent(TransformComponent.class));
        ECSWorld.getCurrentECSWorld().transformationsSystem.updateTransformComponent(mainCamera2DIcon.getComponent(TransformComponent.class));
        ECSWorld.getCurrentECSWorld().transformationsSystem.updateTransformComponent(mainCamera2DBox.getComponent(TransformComponent.class));

         */
        /*
        inspectorCamera2DIcon.getComponent(TransformComponent.class).getTransform().updateModelMatrix();
        inspectorCamera2DBox.getComponent(TransformComponent.class).getTransform().updateModelMatrix();
        mainCamera2DIcon.getComponent(TransformComponent.class).getTransform().updateModelMatrix();
        mainCamera2DBox.getComponent(TransformComponent.class).getTransform().updateModelMatrix();

         */

        Graphics.getMainRenderer().render(inspectorCamera2DIcon, Main.getMainCamera2DComponent());
        Graphics.getMainRenderer().render(inspectorCamera2DBox, Main.getMainCamera2DComponent());
        Graphics.getMainRenderer().render(mainCamera2DIcon, Main.getMainCamera2DComponent());
        Graphics.getMainRenderer().render(mainCamera2DBox, Main.getMainCamera2DComponent());
    }
}
