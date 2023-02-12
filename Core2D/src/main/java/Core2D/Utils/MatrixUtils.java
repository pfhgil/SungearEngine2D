package Core2D.Utils;

import org.joml.*;

import java.lang.Math;
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

    public static float[] matrixToArray(Matrix2f matrix)
    {
        float[] arr = new float[4];
        arr = matrix.get(arr);

        return arr;
    }

    public static float[] matrixToArray(Matrix3x2f matrix)
    {
        float[] arr = new float[6];
        arr = matrix.get(arr);

        return arr;
    }

    public static float[] matrixToArray(Matrix4x3f matrix)
    {
        float[] arr = new float[12];
        arr = matrix.get(arr);

        return arr;
    }


    public static double[] matrixToArray(Matrix4d matrix)
    {
        double[] arr = new double[16];
        arr = matrix.get(arr);

        return arr;
    }

    public static double[] matrixToArray(Matrix3d matrix)
    {
        double[] arr = new double[9];
        arr = matrix.get(arr);

        return arr;
    }

    public static double[] matrixToArray(Matrix2d matrix)
    {
        double[] arr = new double[4];
        arr = matrix.get(arr);

        return arr;
    }

    public static double[] matrixToArray(Matrix3x2d matrix)
    {
        double[] arr = new double[6];
        arr = matrix.get(arr);

        return arr;
    }

    public static double[] matrixToArray(Matrix4x3d matrix)
    {
        double[] arr = new double[12];
        arr = matrix.get(arr);

        return arr;
    }


    public static Vector2f getPosition(Matrix4f matrix4f)
    {
        Vector3f tmp = new Vector3f();
        matrix4f.getTranslation(tmp);
        return new Vector2f(tmp.x, tmp.y);
    }

    public static float getRotation(Matrix4f matrix4f)
    {
        Vector3f eulerRotation = new Vector3f();
        matrix4f.getEulerAnglesZYX(eulerRotation);
        return (float) Math.toDegrees(eulerRotation.z);
    }

    public static Vector2f getScale(Matrix4f matrix4f)
    {
        Vector3f tmp = new Vector3f();
        matrix4f.getScale(tmp);
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Matrix4f getTranslationMatrix(Matrix4f from)
    {
        Vector2f position = getPosition(from);
        return new Matrix4f().translate(new Vector3f(position.x, position.y, 0.0f));
    }

    public static Matrix4f getRotationMatrix(Matrix4f from)
    {
        float rotation = getRotation(from);

        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateX((float) Math.toRadians(0));
        rotationQ.rotateY((float) Math.toRadians(0));
        rotationQ.rotateZ((float) Math.toRadians(rotation));

        return new Matrix4f().rotateAround(rotationQ, 0, 0, 0.0f);
    }

    public static Matrix4f getScaleMatrix(Matrix4f from)
    {
        Vector2f scale = getScale(from);
        return new Matrix4f().scale(new Vector3f(scale.x, scale.y, 1));
    }
}
