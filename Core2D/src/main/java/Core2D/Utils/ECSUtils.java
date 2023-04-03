package Core2D.Utils;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.Graphics.RenderParts.Shader;

public class ECSUtils
{
    public static <T extends Component> T copyComponent(T component)
    {
        if(component instanceof TransformComponent transformComponent) {
            TransformComponent copy = new TransformComponent();

            copy.position.set(transformComponent.position);
            copy.rotation.set(transformComponent.rotation);
            copy.scale.set(transformComponent.scale);

            copy.center.set(transformComponent.center);
        } else if(component instanceof CameraComponent cameraComponent) {
            CameraComponent copy = new CameraComponent();

            copy.followTransformTranslation = cameraComponent.followTransformTranslation;;
            copy.followTransformRotation = cameraComponent.followTransformRotation;
            copy.followTransformScale = cameraComponent.followTransformScale;

            copy.position.set(cameraComponent.position);
            copy.rotation.set(cameraComponent.rotation);
            copy.scale.set(cameraComponent.scale);

            copy.zoom = cameraComponent.zoom;

            copy.viewportSize.set(cameraComponent.viewportSize);

            copy.viewMode = cameraComponent.viewMode;
            copy.FOV = cameraComponent.FOV;
            copy.nearPlane = cameraComponent.nearPlane;
            copy.farPlane = cameraComponent.farPlane;

            copy.sceneMainCamera = cameraComponent.sceneMainCamera;

            copy.render = cameraComponent.render;
        }

        return null;
    }

    public static Shader setNewShader(Shader newShader, Shader oldShader)
    {
        oldShader.destroy();
        return newShader;
    }
}
