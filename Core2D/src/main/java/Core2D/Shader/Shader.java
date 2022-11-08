package Core2D.Shader;

import Core2D.Log.Log;

import java.io.Serializable;

import static org.lwjgl.opengl.GL20C.*;

public class Shader implements Serializable
{
    // id шейдера
    private transient int shaderHandler;
    // тип шейдера (может быть GL_VERTEX_SHADER или GL_FRAGMENT_SHADER)
    private transient int shaderType;

    public Shader(String shaderCode, int _shaderType)
    {
        if(Thread.currentThread().getName().equals("main")) {
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
            if (compileStatus == 0) {
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
        }
    }

    public void destroy()
    {
        if(Thread.currentThread().getName().equals("main")) {
            // удаление шейдера
            glDeleteShader(shaderHandler);
        }
    }

    public String shaderTypeToString(int shaderType)
    {
        return shaderType == GL_VERTEX_SHADER ? "GL_VERTEX_SHADER" : "GL_FRAGMENT_SHADER";
    }

    public void set(Shader shader)
    {
        this.shaderHandler = shader.getHandler();
        this.shaderType = shader.getType();
    }

    // геттеры и сеттеры
    public int getHandler() { return shaderHandler; }
    public void setHandler(int shaderHandler) { this.shaderHandler = shaderHandler; }

    public int getType() { return shaderType; }
    public void setType(int shaderType) { this.shaderType = shaderType; }
}
