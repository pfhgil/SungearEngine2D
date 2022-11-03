package Core2D.ShaderUtils;

import Core2D.Log.Log;
import Core2D.Utils.MatrixUtils;
import org.joml.*;
import org.lwjgl.opengl.GL32C;

public class ShaderUtils
{
    public static void setUniform(int programHandler, String uniformName, Matrix4f matrix4f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = GL32C.glGetUniformLocation(programHandler, uniformName);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(matrix4f);
            // передача в uniform матрицу
            GL32C.glUniformMatrix4fv(uniformLocation, false, data);
        } else {
            Log.CurrentSession.println("Uniform with name \"" + uniformName + "\" was not found!", Log.MessageType.ERROR);
        }
    }
    public static void setUniform(int programHandler, String uniformName, Matrix3f matrix3f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = GL32C.glGetUniformLocation(programHandler, uniformName);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(matrix3f);
            // передача в uniform матрицу
            GL32C.glUniformMatrix3fv(uniformLocation, false, data);
        } else {
            Log.CurrentSession.println("Uniform with name \"" + uniformName + "\" was not found!", Log.MessageType.ERROR);
        }
    }
    public static void setUniform(int programHandler, String uniformName, Vector2f vector2f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = GL32C.glGetUniformLocation(programHandler, uniformName);
        if(uniformLocation != -1) {
            // передача в uniform вектора
            GL32C.glUniform2f(uniformLocation, vector2f.x, vector2f.y);
        } else {
            Log.CurrentSession.println("Uniform with name \"" + uniformName + "\" was not found!", Log.MessageType.ERROR);
        }
    }
    public static void setUniform(int programHandler, String uniformName, Vector3f vector3f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = GL32C.glGetUniformLocation(programHandler, uniformName);
        if(uniformLocation != -1) {
            // передача в uniform вектора
            GL32C.glUniform3f(uniformLocation, vector3f.x, vector3f.y, vector3f.z);
        } else {
            Log.CurrentSession.println("Uniform with name \"" + uniformName + "\" was not found!", Log.MessageType.ERROR);
        }
    }
    public static void setUniform(int programHandler, String uniformName, Vector4f vector4f)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = GL32C.glGetUniformLocation(programHandler, uniformName);
        if(uniformLocation != -1) {
            // передача в uniform вектора
            GL32C.glUniform4f(uniformLocation, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
        } else {
            //Log.CurrentSession.println("Uniform with name \"" + uniformName + "\" was not found!", Log.MessageType.ERROR);
        }
    }
    public static void setUniform(int programHandler, String uniformName, int _int)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = GL32C.glGetUniformLocation(programHandler, uniformName);
        if(uniformLocation != -1) {
            // передача в uniform int
            GL32C.glUniform1i(uniformLocation, _int);
        } else {
            Log.CurrentSession.println("Uniform with name \"" + uniformName + "\" was not found!", Log.MessageType.ERROR);
        }
    }
    public static void setUniform(int programHandler, String uniformName, float _float)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = GL32C.glGetUniformLocation(programHandler, uniformName);
        if(uniformLocation != -1) {
            // передача в uniform float
            GL32C.glUniform1f(uniformLocation, _float);
        } else {
            Log.CurrentSession.println("Uniform with name \"" + uniformName + "\" was not found!", Log.MessageType.ERROR);
        }
    }
    public static void setUniform(int programHandler, String uniformName, boolean _boolean)
    {
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = GL32C.glGetUniformLocation(programHandler, uniformName);
        if(uniformLocation != -1) {
            // передача в uniform int
            if (_boolean) GL32C.glUniform1i(uniformLocation, 1);
            else GL32C.glUniform1i(uniformLocation, 0);
        } else {
            Log.CurrentSession.println("Uniform with name \"" + uniformName + "\" was not found!", Log.MessageType.ERROR);
        }
    }
}
