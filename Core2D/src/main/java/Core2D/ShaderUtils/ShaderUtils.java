package Core2D.ShaderUtils;

import Core2D.Graphics.OpenGL;
import Core2D.Log.Log;
import Core2D.Utils.MatrixUtils;
import org.joml.*;
import static org.lwjgl.opengl.GL32C.*;

public class ShaderUtils
{
    public static boolean setUniform(int programHandler, String uniformName, Matrix4f matrix4f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(matrix4f);
            // передача в uniform матрицу
            OpenGL.glCall((params) -> glUniformMatrix4fv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix3f matrix3f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(matrix3f);
            // передача в uniform матрицу
            OpenGL.glCall((params) -> glUniformMatrix3fv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector2f vector2f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            // передача в uniform вектора
            OpenGL.glCall((params) -> glUniform2f(uniformLocation, vector2f.x, vector2f.y));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector3f vector3f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            // передача в uniform вектора
            OpenGL.glCall((params) -> glUniform3f(uniformLocation, vector3f.x, vector3f.y, vector3f.z));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector4f vector4f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            // передача в uniform вектора
            OpenGL.glCall((params) -> glUniform4f(uniformLocation, vector4f.x, vector4f.y, vector4f.z, vector4f.w));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, int _int)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            // передача в uniform int
            OpenGL.glCall((params) -> glUniform1i(uniformLocation, _int));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, float _float)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            // передача в uniform float
            OpenGL.glCall((params) -> glUniform1f(uniformLocation, _float));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, boolean _boolean)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            // передача в uniform int
            if (_boolean) OpenGL.glCall((params) -> glUniform1i(uniformLocation, 1));
            else OpenGL.glCall((params) -> glUniform1i(uniformLocation, 0));
        }

        return uniformLocation != -1;
    }
}
