package Core2D.Utils;

import Core2D.Graphics.OpenGL.OpenGL;
import org.joml.*;

import static org.lwjgl.opengl.GL40C.*;

public class ShaderUtils
{
    public static boolean setUniform(int programHandler, String uniformName, Matrix4f val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix4fv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix3f val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix3fv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix2f val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix3fv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix3x2f val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix3fv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix4x3f val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            float[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix3fv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }



    public static boolean setUniform(int programHandler, String uniformName, Matrix4d val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            double[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix4dv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix3d val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            double[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix3dv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix2d val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            double[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix3dv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix3x2d val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            double[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix3dv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Matrix4x3d val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            double[] data = MatrixUtils.matrixToArray(val);
            OpenGL.glCall((params) -> glUniformMatrix3dv(uniformLocation, false, data));
        }

        return uniformLocation != -1;
    }



    public static boolean setUniform(int programHandler, String uniformName, float val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform1f(uniformLocation, val));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector2f val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform2f(uniformLocation, val.x, val.y));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector3f val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform3f(uniformLocation, val.x, val.y, val.z));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector4f val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform4f(uniformLocation, val.x, val.y, val.z, val.w));
        }

        return uniformLocation != -1;
    }



    public static boolean setUniform(int programHandler, String uniformName, double val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform1d(uniformLocation, val));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector2d val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform2d(uniformLocation, val.x, val.y));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector3d val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform3d(uniformLocation, val.x, val.y, val.z));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector4d val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform4d(uniformLocation, val.x, val.y, val.z, val.w));
        }

        return uniformLocation != -1;
    }



    public static boolean setUniform(int programHandler, String uniformName, int val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform1i(uniformLocation, val));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector2i val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform2i(uniformLocation, val.x, val.y));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector3i val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform3i(uniformLocation, val.x, val.y, val.z));
        }

        return uniformLocation != -1;
    }
    public static boolean setUniform(int programHandler, String uniformName, Vector4i val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            OpenGL.glCall((params) -> glUniform4i(uniformLocation, val.x, val.y, val.z, val.w));
        }

        return uniformLocation != -1;
    }



    public static boolean setUniform(int programHandler, String uniformName, boolean val)
    {
        if(!glIsProgram(programHandler)) return false;
        // получить нахождение uniform в шейдерной программе
        int uniformLocation = OpenGL.glCall((params) -> glGetUniformLocation(programHandler, uniformName), Integer.class);
        if(uniformLocation != -1) {
            if (val) OpenGL.glCall((params) -> glUniform1i(uniformLocation, 1));
            else OpenGL.glCall((params) -> glUniform1i(uniformLocation, 0));
        }

        return uniformLocation != -1;
    }

    public static boolean setUniform(int programHandler, String name, Object val)
    {
        if(val instanceof Float) {
            return setUniform(programHandler, name, (float) val);
        } else if(val instanceof Vector2f vector) {
            return setUniform(programHandler, name, vector);
        } else if(val instanceof Vector3f vector) {
            return setUniform(programHandler, name, vector);
        } else if(val instanceof Vector4f vector) {
            return setUniform(programHandler, name, vector);
        }

        else if(val instanceof Double) {
            return setUniform(programHandler, name, (double) val);
        } else if(val instanceof Vector2d vector) {
            return setUniform(programHandler, name, vector);
        } else if(val instanceof Vector3d vector) {
            return setUniform(programHandler, name, vector);
        } else if(val instanceof Vector4d vector) {
            return setUniform(programHandler, name, vector);
        }

        else if(val instanceof Integer) {
            return setUniform(programHandler, name, (int) val);
        } else if(val instanceof Vector2i vector) {
            return setUniform(programHandler, name, vector);
        } else if(val instanceof Vector3i vector) {
            return setUniform(programHandler, name, vector);
        } else if(val instanceof Vector4i vector) {
            return setUniform(programHandler, name, vector);
        }

        else if(val instanceof Matrix4f matrix) {
            return setUniform(programHandler, name, matrix);
        } else if(val instanceof Matrix3f matrix) {
            return setUniform(programHandler, name, matrix);
        } else if(val instanceof Matrix2f matrix) {
            return setUniform(programHandler, name, matrix);
        } else if(val instanceof Matrix3x2f matrix) {
            return setUniform(programHandler, name, matrix);
        } else if(val instanceof Matrix4x3f matrix) {
            return setUniform(programHandler, name, matrix);
        } else if(val instanceof Matrix3x2d matrix) {
            return setUniform(programHandler, name, matrix);
        } else if(val instanceof Matrix4x3d matrix) {
            return setUniform(programHandler, name, matrix);
        }

        return false;
    }
}
