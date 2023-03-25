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


    public static Vector3f getPosition(Matrix4f matrix4f)
    {
        Vector3f tmp = new Vector3f();
        matrix4f.getTranslation(tmp);
        return tmp;
    }

    public static Vector3f getEulerRotation(Matrix4f matrix4f)
    {
        Vector3f eulerRotation = new Vector3f();
        matrix4f.getEulerAnglesZYX(eulerRotation);

        eulerRotation.x = (float) Math.toDegrees(eulerRotation.x);
        eulerRotation.y = (float) Math.toDegrees(eulerRotation.y);
        eulerRotation.z = (float) Math.toDegrees(eulerRotation.z);

        return eulerRotation;
    }

    public static Vector3f getScale(Matrix4f matrix4f)
    {
        Vector3f tmp = new Vector3f();
        matrix4f.getScale(tmp);
        return tmp;
    }

    public static Matrix4f getTranslationMatrix(Matrix4f from)
    {
        Vector3f position = getPosition(from);
        return new Matrix4f().translate(position);
    }

    public static Matrix4f getRotationMatrix(Matrix4f from)
    {
        Vector3f rotation = getEulerRotation(from);

        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateX((float) Math.toRadians(rotation.x));
        rotationQ.rotateY((float) Math.toRadians(rotation.y));
        rotationQ.rotateZ((float) Math.toRadians(rotation.z));

        return new Matrix4f().rotate(rotationQ);
    }

    public static Matrix4f getScaleMatrix(Matrix4f from)
    {
        Vector3f scale = getScale(from);
        return new Matrix4f().scale(scale);
    }
}
