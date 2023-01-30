package Core2D.Graphics.RenderParts;

import Core2D.DataClasses.ShaderData;
import Core2D.Graphics.OpenGL;
import Core2D.Log.Log;

import java.io.Serializable;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20C.*;

public class Shader implements Serializable
{
    private transient int programHandler;
    private transient HashMap<Integer, Integer> shaderPartsHandlers = new HashMap<>();

    public String path = "";

    private transient ShaderData shaderData;

    private transient boolean compiled = false;

    public long lastModified = -1;

    public Shader() { }

    public Shader(ShaderData shaderData)
    {
        set(create(shaderData));
    }

    public static Shader create(ShaderData shaderData)
    {
        Shader shader = new Shader();
        shader.compile(shaderData);
        return shader;
    }

    public static Shader create(String sourceCode)
    {
        Shader shader = new Shader();

        shader.shaderData = new ShaderData();
        shader.shaderData.setSourceCode(sourceCode);
        shader.compile(shader.shaderData);

        return shader;
    }

    public boolean createShaderPart(int shaderType, String shaderSourceCode)
    {
        int shaderPartHandler = OpenGL.glCall((params) -> glCreateShader(shaderType), Integer.class);
        shaderPartsHandlers.put(shaderType, shaderPartHandler);

        String shaderDefine = shaderType == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT";
        // сделать легкое изменения версии шейдера
        OpenGL.glCall((params) -> glShaderSource(shaderPartHandler, "#version 400\n" + "#define " + shaderDefine + "\n" + shaderSourceCode));

        OpenGL.glCall((params) -> glCompileShader(shaderPartHandler));

        int compileStatus = OpenGL.glCall((params) -> glGetShaderi(shaderPartHandler, GL_COMPILE_STATUS), Integer.class);
        if(compileStatus == 0) {
            // получение максимальной длины ошибки
            final int maxErrorStringLength = OpenGL.glCall((params) -> glGetShaderi(shaderPartHandler, GL_INFO_LOG_LENGTH), Integer.class);

            // получение строки ошибки
            String errorString = "";
            errorString = OpenGL.glCall((params) -> glGetShaderInfoLog(shaderPartHandler, maxErrorStringLength), String.class);

            this.destroyShaderPart(shaderType);

            Log.CurrentSession.println("Error while creating and compiling shader. Shader type is: " + shaderPartTypeToString(shaderType) + ". Error is: " + errorString, Log.MessageType.ERROR);
            Log.CurrentSession.println(shaderSourceCode, Log.MessageType.ERROR);

            compiled = false;
        }

        return compiled;
    }

    public boolean createProgram() {
        programHandler = OpenGL.glCall((params) -> glCreateProgram(), Integer.class);

        for (int shaderHandler : shaderPartsHandlers.values()) {
            OpenGL.glCall((params) -> glAttachShader(programHandler, shaderHandler));
        }

        // соединяю шейдеры в одну программу
        OpenGL.glCall((params) -> glLinkProgram(programHandler));

        int linkStatus = OpenGL.glCall((params) -> glGetProgrami(programHandler, GL_LINK_STATUS), Integer.class);

        // если статус соединения шейдров = 0 (не соединены), то выводить ошибку и удалять программу и шейдеры из памяти
        if (linkStatus == 0) {
            // получение максимальной длины ошибки
            final int maxErrorStringLength = OpenGL.glCall((params) -> glGetProgrami(programHandler, GL_INFO_LOG_LENGTH), Integer.class);

            // получение строки ошибки
            String errorString = "";
            errorString = OpenGL.glCall((params) -> glGetProgramInfoLog(programHandler, maxErrorStringLength), String.class);

            // удаление программы
            destroy();

            Log.CurrentSession.println("Error while creating and linking program. Error is: " + errorString, Log.MessageType.ERROR);

            compiled = false;
        }

        return compiled;
    }

    public boolean compile(ShaderData shaderData)
    {
        destroy();

        this.shaderData = shaderData;
        path = shaderData.getPath();

        compiled = true;

        createShaderPart(GL_VERTEX_SHADER, shaderData.getSourceCode());
        createShaderPart(GL_FRAGMENT_SHADER, shaderData.getSourceCode());

        createProgram();

        return compiled;
    }

    public void destroyShaderPart(int shaderPartType)
    {
        // удаление шейдера
        OpenGL.glCall((params) -> glDeleteShader(shaderPartsHandlers.get(shaderPartType)));
        shaderPartsHandlers.remove(shaderPartType);
    }

    public void destroyAllShaderParts()
    {
        for(int shaderPartType : shaderPartsHandlers.keySet()) {
            OpenGL.glCall((params) -> glDeleteShader(shaderPartsHandlers.get(shaderPartType)));
            OpenGL.glCall((params) -> glDetachShader(programHandler, shaderPartsHandlers.get(shaderPartType)));
        }
        shaderPartsHandlers.clear();
    }

    public void destroy()
    {
        destroyAllShaderParts();
        // удаление шейдера
        OpenGL.glCall((params) -> glDeleteProgram(programHandler));
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
        if(glIsProgram(programHandler)) {
            OpenGL.glCall((params) -> glUseProgram(programHandler));
        }
    }

    public int getShaderPartHandler(int shaderTypePart) { return shaderPartsHandlers.get(shaderTypePart); }

    public int getProgramHandler() { return programHandler; }

    public HashMap<Integer, Integer> getShaderPartsHandlers() { return shaderPartsHandlers; }

    public ShaderData getShaderData() { return shaderData; }

    public boolean isCompiled() { return compiled; }
}
