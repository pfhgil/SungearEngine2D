package Core2D.Audio;

import Core2D.CamerasManager.CamerasManager;
import Core2D.Component.Components.TransformComponent;
import Core2D.Utils.MatrixUtils;
import org.joml.Vector2f;
import org.lwjgl.openal.AL10;

public class AudioListener
{
    private static Vector2f position = new Vector2f();

    public static void update()
    {
        if(CamerasManager.mainCamera2D != null) {
            TransformComponent cameraTransformComponent = CamerasManager.mainCamera2D.getComponent(TransformComponent.class);
            if(cameraTransformComponent == null) return;
            Vector2f pos = MatrixUtils.getPosition(cameraTransformComponent.getTransform().getResultModelMatrix()).negate();

            if(Float.isNaN(pos.x) || Float.isInfinite(pos.x)) {
                pos.x = 0.0f;
            }
            if(Float.isNaN(pos.y) || Float.isInfinite(pos.y)) {
                pos.y = 0.0f;
            }

            position.set(pos);
            OpenAL.alCall((params) -> AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, 0.0f));
        }
    }

    public static Vector2f getPosition() { return position; }
}
