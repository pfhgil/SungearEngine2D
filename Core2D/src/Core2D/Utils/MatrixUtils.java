package Core2D.Utils;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

public class MatrixUtils
{
    public static FloatBuffer getMatrix4fAsFloatBuffer(Matrix4f matrix4f)
    {
        FloatBuffer floatBuffer = FloatBuffer.allocate(16);
        matrix4f.get(floatBuffer);

        return floatBuffer;
    }

    public static float[] matrixToArray(Matrix4f matrix)
    {
        float[] arr = new float[16];
        arr = matrix.get(arr);

        return arr;
    }

    public static float[] matrixToArray(Matrix3f matrix)
    {
        float[] arr = new float[9];
        arr = matrix.get(arr);

        return arr;
    }
}
