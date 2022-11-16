package Core2D.Shader;

import Core2D.Log.Log;
import Core2D.Utils.FileUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20C.*;

public class Shader implements Serializable
{
    private transient int programHandler;
    private transient HashMap<Integer, Integer> shadersHandlers = new HashMap<>();

    public static Shader loadShader(String path) { return createShader(FileUtils.readAllFile(path)); }

    public static Shader loadShader(InputStream inputStream)  { return createShader(FileUtils.readAllFile(inputStream)); }

    public static Shader createShader(String sourceCode)
    {
        if(Thread.currentThread().getName().equals("main")) {
            Shader shader = new Shader();

            shader.createShaderPart(GL_VERTEX_SHADER, sourceCode);
            shader.createShaderPart(GL_FRAGMENT_SHADER, sourceCode);

            shader.createProgram();

            return shader;
        }

        return null;
    }

    public void createShaderPart(int shaderType, String shaderSourceCode)
    {
        int shaderPartHandler = glCreateShader(shaderType);
        shadersHandlers.put(shaderType, shaderPartHandler);

        String shaderDefine = shaderType == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT";
        glShaderSource(shaderPartHandler, "#define " + shaderDefine + "\n" + shaderSourceCode);

        glCompileShader(shaderPartHandler);

        int compileStatus = glGetShaderi(shaderPartHandler, GL_COMPILE_STATUS);
        if(compileStatus == 0) {
            // получение максимальной длины ошибки
            int maxErrorStringLength = 0;
            maxErrorStringLength = glGetShaderi(shaderPartHandler, GL_INFO_LOG_LENGTH);

            // получение строки ошибки
            String errorString = "";
            errorString = glGetShaderInfoLog(shaderPartHandler, maxErrorStringLength);

            this.destroyShaderPart(shaderType);

            Log.CurrentSession.println("Error while creating and compiling shader. Shader type is: " + shaderPartTypeToString(shaderType) + ". Error is: " + errorString, Log.MessageType.ERROR);
        }
    }

    public void createProgram()
    {
        if(Thread.currentThread().getName().equals("main")) {
            programHandler = glCreateProgram();

            for(int shaderHandler : shadersHandlers.values()) {
                glAttachShader(programHandler, shaderHandler);
            }

            // соединяю шейдеры в одну программу
            glLinkProgram(programHandler);

            int linkStatus = glGetProgrami(programHandler, GL_LINK_STATUS);

            // если статус соединения шейдров = 0 (не соединены), то выводить ошибку и удалять программу и шейдеры из памяти
            if (linkStatus == 0) {
                // получение максимальной длины ошибки
                int maxErrorStringLength = 0;
                maxErrorStringLength = glGetProgrami(programHandler, GL_INFO_LOG_LENGTH);

                // получение строки ошибки
                String errorString = "";
                errorString = glGetProgramInfoLog(programHandler, maxErrorStringLength);

                // удаление программы
                destroy();

                Log.CurrentSession.println("Error while creating and linking program. Error is: " + errorString, Log.MessageType.ERROR);
            }
        }
    }

    public Shader() { }

    public void destroyShaderPart(int shaderPartType)
    {
        if(Thread.currentThread().getName().equals("main")) {
            // удаление шейдера
            glDeleteShader(shadersHandlers.get(shaderPartType));
            shadersHandlers.remove(shaderPartType);
        }
    }

    public void destroy()
    {
        if(Thread.currentThread().getName().equals("main")) {
            // удаление шейдера
            glDeleteProgram(programHandler);

            for(int shaderPartType : shadersHandlers.keySet()) {
                destroyShaderPart(shaderPartType);
            }
        }
    }

    public static String shaderPartTypeToString(int shaderType)
    {
        return shaderType == GL_VERTEX_SHADER ? "GL_VERTEX_SHADER" : "GL_FRAGMENT_SHADER";
    }

    public void set(Shader shader)
    {
    }

    public void bind()
    {
        if(Thread.currentThread().getName().equals("main")) {
            glUseProgram(programHandler);
        }
    }
    public void unBind()
    {
        if(Thread.currentThread().getName().equals("main")) {
            glUseProgram(0);
        }
    }

    public int getShaderPartHandler(int shaderTypePart) { return shadersHandlers.get(shaderTypePart); }

    public int getProgramHandler() { return programHandler; }

    public HashMap<Integer, Integer> getShadersHandlers() { return shadersHandlers; }
}
