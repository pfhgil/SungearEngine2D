package Core2D.Shader;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

import java.io.Serializable;

import static org.lwjgl.opengl.GL20C.*;

public class Shader implements Serializable, AutoCloseable
{
    // id шейдера
    private transient int shaderHandler;
    // тип шейдера (может быть GL_VERTEX_SHADER или GL_FRAGMENT_SHADER)
    private transient int shaderType;

    public Shader(String shaderCode, int _shaderType)
    {
        shaderType = _shaderType;

        // создаю шейдер shaderType типа
        shaderHandler = glCreateShader(shaderType);

        // указываю текст шейдера
        glShaderSource(shaderHandler, shaderCode);
        // компилирую шейдер
        glCompileShader(shaderHandler);

        // получение статуса компиляции шейдера
        int compileStatus = 0;
        compileStatus = glGetShaderi(shaderHandler, GL_COMPILE_STATUS);

        // если статус компиляции шейдера = 0 (не скомпилирован), то выводить ошибку и удалять шейдер из памяти
        if(compileStatus == 0) {
            // получение максимальной длины ошибки
            int maxErrorStringLength = 0;
            maxErrorStringLength = glGetShaderi(shaderHandler, GL_INFO_LOG_LENGTH);

            // получение строки ошибки
            String errorString = "";
            errorString = glGetShaderInfoLog(shaderHandler, maxErrorStringLength);

            // удаление шейдера
            destroy();

            // вывод ошибки в консоль
            Log.CurrentSession.println("Error while creating and compiling shader. Core2D.Shader type is: " + shaderTypeToString(shaderType) + ". Error is: " + errorString, Log.MessageType.ERROR);
        }

        shaderCode = null;
    }

    public void destroy()
    {
        // удаление шейдера
        glDeleteShader(shaderHandler);

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public String shaderTypeToString(int shaderType)
    {
        return shaderType == GL_VERTEX_SHADER ? "GL_VERTEX_SHADER" : "GL_FRAGMENT_SHADER";
    }

    // геттеры и сеттеры
    public int getHandler() { return shaderHandler; }
    public void setHandler(int shaderHandler) { this.shaderHandler = shaderHandler; }

    public int getType() { return shaderType; }
    public void setType(int shaderType) { this.shaderType = shaderType; }

    @Override
    public void close() throws Exception {

    }
}
