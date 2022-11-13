package Core2D.Audio;

import Core2D.Camera2D.CamerasManager;
import Core2D.Utils.MatrixUtils;
import org.joml.Vector2f;
import org.lwjgl.openal.AL10;

public class AudioListener
{
    private static Vector2f position = new Vector2f();

    public static void update()
    {
        if(CamerasManager.getMainCamera2D() != null) {
            Vector2f pos = MatrixUtils.getPosition(CamerasManager.getMainCamera2D().getTransform().getResultModelMatrix()).negate();

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
