package Core2D.GameObject.RenderParts;

import Core2D.DataClasses.ShaderData;
import Core2D.Log.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import static org.lwjgl.opengl.GL20C.*;

public class Shader implements Serializable
{
    private transient int programHandler;
    private transient HashMap<Integer, Integer> shaderPartsHandlers = new HashMap<>();

    public String path = "";

    private ShaderData shaderData;

    public Shader() { }

    public Shader(ShaderData shaderData)
    {
        set(create(shaderData.getSourceCode()));
    }

    public static Shader create(ShaderData shaderData)
    {
        Shader shader = create(shaderData.getSourceCode());
        shader.shaderData = shaderData;
        return shader;
    }

    public static Shader create(String sourceCode)
    {
        Shader shader = new Shader();

        if(Thread.currentThread().getName().equals("main")) {
            shader.createShaderPart(GL_VERTEX_SHADER, sourceCode);
            shader.createShaderPart(GL_FRAGMENT_SHADER, sourceCode);

            shader.createProgram();
        }

        return shader;
    }

    public void createShaderPart(int shaderType, String shaderSourceCode)
    {
        int shaderPartHandler = glCreateShader(shaderType);
        shaderPartsHandlers.put(shaderType, shaderPartHandler);

        String shaderDefine = shaderType == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT";
        // сделать легкое изменения версии шейдера
        glShaderSource(shaderPartHandler, "#version 330 core\n" + "#define " + shaderDefine + "\n" + shaderSourceCode);

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
            Log.CurrentSession.println(shaderSourceCode, Log.MessageType.ERROR);
        }
    }

    public void createProgram() {
        if (Thread.currentThread().getName().equals("main")) {
            programHandler = glCreateProgram();

            for (int shaderHandler : shaderPartsHandlers.values()) {
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

    public void destroyShaderPart(int shaderPartType)
    {
        if(Thread.currentThread().getName().equals("main")) {
            // удаление шейдера
            glDeleteShader(shaderPartsHandlers.get(shaderPartType));
            shaderPartsHandlers.remove(shaderPartType);
        }
    }

    public void destroyAllShaderParts()
    {
        if(Thread.currentThread().getName().equals("main")) {
            for(int shaderPartType : shaderPartsHandlers.keySet()) {
                glDeleteShader(shaderPartsHandlers.get(shaderPartType));
            }
            shaderPartsHandlers.clear();
        }
    }

    public void destroy()
    {
        if(Thread.currentThread().getName().equals("main")) {
            // удаление шейдера
            glDeleteProgram(programHandler);

            destroyAllShaderParts();
        }
    }

    public static String shaderPartTypeToString(int shaderType)
    {
        return shaderType == GL_VERTEX_SHADER ? "GL_VERTEX_SHADER" : "GL_FRAGMENT_SHADER";
    }

    public void set(Shader shader)
    {
        destroy();

        programHandler = shader.programHandler;
        shaderPartsHandlers.putAll(shader.shaderPartsHandlers);
        path = shader.path;
        shaderData = shader.shaderData;
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

    public int getShaderPartHandler(int shaderTypePart) { return shaderPartsHandlers.get(shaderTypePart); }

    public int getProgramHandler() { return programHandler; }

    public HashMap<Integer, Integer> getShaderPartsHandlers() { return shaderPartsHandlers; }

    public ShaderData getShaderData() { return shaderData; }
}
