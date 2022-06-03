package Core2D.Utils;

import org.joml.Math;
import org.joml.Vector3f;

public class ColorUtils
{
    public static Vector3f intToVector3f(int rgb)
    {
        float red = ((rgb >> 16) & 0x0ff) / 255.0f;
        float green = ((rgb >> 8) & 0x0ff) / 255.0f;
        float blue = ((rgb) & 0x0ff) / 255.0f;

        return new Vector3f(
                Math.max(0.0f, Math.min(1.0f, red)),
                Math.max(0.0f, Math.min(1.0f, green)),
                Math.max(0.0f, Math.min(1.0f, blue)));
    }
}