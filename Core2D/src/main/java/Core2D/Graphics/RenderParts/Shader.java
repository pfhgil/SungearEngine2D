package Core2D.Graphics.RenderParts;

import Core2D.DataClasses.ShaderData;
import Core2D.ECS.Component;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Log.Log;
import Core2D.Utils.ComponentHandler;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.Serializable;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL46C.*;

public class Shader implements Serializable
{
    public static class ShaderUniform
    {
        private String name = "";
        private int size;
        private int type;

        public Object value;

        public ComponentHandler attachedComponentHandler = new ComponentHandler();

        private transient Component attachedComponent;

        public ShaderUniform() { }

        public ShaderUniform(String name, int size, int type)
        {
            this.name = name;
            this.size = size;
            this.type = type;
        }

        public void init()
        {
            attachedComponent = attachedComponentHandler.getComponent();
            //Systems.out.println("found component: " + attachedComponent);
        }

        public void setDefaultValue()
        {
            value = switch (type) {
                case GL_FLOAT -> 0.0f;
                case GL_FLOAT_VEC2 -> new Vector2f();
                case GL_FLOAT_VEC3 -> new Vector3f();
                case GL_FLOAT_VEC4 -> new Vector4f();

                case GL_DOUBLE -> 0.0d;
                case GL_DOUBLE_VEC2 -> new Vector2d();
                case GL_DOUBLE_VEC3 -> new Vector3d();
                case GL_DOUBLE_VEC4 -> new Vector4d();

                case GL_INT, GL_UNSIGNED_INT, GL_SAMPLER_1D, GL_SAMPLER_2D, GL_SAMPLER_3D -> 0;
                case GL_INT_VEC2, GL_UNSIGNED_INT_VEC2 -> new Vector2i();
                case GL_INT_VEC3, GL_UNSIGNED_INT_VEC3 -> new Vector3i();
                case GL_INT_VEC4, GL_UNSIGNED_INT_VEC4 -> new Vector4i();

                case GL_BOOL -> false;

                case GL_FLOAT_MAT2 -> new Matrix2f();
                case GL_FLOAT_MAT3 -> new Matrix3f();
                case GL_FLOAT_MAT4 -> new Matrix4f();
                case GL_FLOAT_MAT3x2 -> new Matrix3x2f();
                case GL_FLOAT_MAT4x3 -> new Matrix4x3f();

                case GL_DOUBLE_MAT2 -> new Matrix2d();
                case GL_DOUBLE_MAT3 -> new Matrix3d();
                case GL_DOUBLE_MAT4 -> new Matrix4d();
                case GL_DOUBLE_MAT3x2 -> new Matrix3x2d();
                case GL_DOUBLE_MAT4x3 -> new Matrix4x3d();

                default -> 0;
            };
        }

        public <T> T get(Class<T> cls)
        {
            if(value == null) {
                setDefaultValue();
            }
            if(cls.isAssignableFrom(value.getClass())) {
                return cls.cast(value);
            }

            return null;
        }

        public void attachToComponent(Component component)
        {
            if(component != null) {
                attachedComponentHandler.setComponentToHandle(component);
                attachedComponent = component;
            }
        }

        // applies uniform value to shader
        // сначала нужно забиндить шейдер
        public void apply(int programHandler)
        {
            /*
            if(attachedComponent instanceof ShaderUniformFloatComponent shaderUniformFloatComponent) {
                value = shaderUniformFloatComponent.uniformValue;
            } else if(attachedComponent instanceof TextureComponent textureComponent) {
                value = textureComponent.getTexture().getFormattedTextureBlock();

                textureComponent.getTexture().bind();
            }

            ShaderUtils.setUniform(
                    programHandler,
                    name,
                    value
            );

             */
        }

        public String getName() { return name; }

        public int getSize() { return size; }

        public int getType() { return type; }

        public Component getAttachedComponent() { return attachedComponent; }

        public void resetAttachedComponent()
        {
            attachedComponentHandler.reset();
            attachedComponent = null;
        }
    }

    public static class ShaderDefine
    {
        public String name = "";
        public int value = -1;

        public ShaderDefine() { }

        public ShaderDefine(String name, int value)
        {
            this.name = name;
            this.value = value;
        }
    }

    private transient int programHandler;
    private transient HashMap<Integer, Integer> shaderPartsHandlers = new HashMap<>();

    public String path = "";

    private transient ShaderData shaderData;

    private transient boolean compiled = false;

    private List<ShaderUniform> shaderUniforms = new ArrayList<>();

    private List<ShaderDefine> shaderDefines = new ArrayList<>();

    public Shader()
    {
        addShaderDefine(new ShaderDefine("FLIP_TEXTURES_Y", 1));
    }

    public Shader(ShaderData shaderData)
    {
        addShaderDefine(new ShaderDefine("FLIP_TEXTURES_Y", 1));
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
        shader.shaderData.code = sourceCode;

        shader.compile(shader.shaderData);

        return shader;
    }

    public boolean createShaderPart(int shaderType, String shaderSourceCode)
    {
        int shaderPartHandler = OpenGL.glCall((params) -> glCreateShader(shaderType), Integer.class);
        shaderPartsHandlers.put(shaderType, shaderPartHandler);

        // добавляем все дефайны
        StringBuilder shaderDefinesStringBuilder = new StringBuilder("#define ").append(shaderType == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT").append("\n");

        for(ShaderDefine shaderDefine : shaderDefines) {
            shaderDefinesStringBuilder.append("#define ")
                    .append(shaderDefine.name)
                    .append(" ")
                    .append(shaderDefine.value)
                    .append("\n");
        }

        //Log.CurrentSession.println("defines: " + shaderDefinesStringBuilder, Log.MessageType.SUCCESS);

        // сделать легкое изменения версии шейдера
        OpenGL.glCall((params) -> glShaderSource(shaderPartHandler, "#version 400\n" + shaderDefinesStringBuilder + shaderSourceCode));

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

        // перезагрузка юниформ
        int activeUniformsNum = OpenGL.glCall((params) -> glGetProgrami(programHandler, GL_ACTIVE_UNIFORMS), Integer.class);
        List<String> actualUniformsNames = new ArrayList<>();

        // нахождение юниформ, которых нет и их создание
        for(int i = 0; i < activeUniformsNum; i++) {
            IntBuffer sizeBuffer = BufferUtils.createIntBuffer(1);
            IntBuffer typeBuffer = BufferUtils.createIntBuffer(1);

            final int finalI = i;

            String name = OpenGL.glCall((params) -> glGetActiveUniform(programHandler, finalI, sizeBuffer, typeBuffer), String.class);
            actualUniformsNames.add(name);

            if(shaderUniforms.stream().noneMatch(uniform -> uniform.name.equals(name))) {
                ShaderUniform shaderUniform = new ShaderUniform(name, sizeBuffer.get(0), typeBuffer.get(0));
                shaderUniform.setDefaultValue();
                shaderUniforms.add(shaderUniform);
            } else {
                ShaderUniform shaderUniform = shaderUniforms.stream().filter(shader -> shader.name.equals(name)).findFirst().get();
                shaderUniform.size = sizeBuffer.get(0);

                if(shaderUniform.type != typeBuffer.get(0)) {
                    shaderUniform.type = typeBuffer.get(0);
                    shaderUniform.resetAttachedComponent();
                    shaderUniform.setDefaultValue();

                    // Log.CurrentSession.println("setted name: " + shaderUniform.name + ", setted val: " + shaderUniform.value + ", type: " + shaderUniform.type + ", instanceof: " + shaderUniform.value.getClass(), Log.MessageType.ERROR);
                }
            }

            sizeBuffer.clear();
            typeBuffer.clear();
        }

        shaderUniforms.removeIf(shaderUniform -> actualUniformsNames.stream().noneMatch(uniformName -> uniformName.equals(shaderUniform.name)));
        //shaderUniforms.removeIf(shaderUniform -> actualUniformsNames.values().stream().noneMatch(uniformType -> uniformType == shaderUniform.type));

        return compiled;
    }

    public boolean compile(ShaderData shaderData)
    {
        destroy();

        this.shaderData = shaderData;
        path = shaderData.getRelativePath();

        compiled = true;

        createShaderPart(GL_VERTEX_SHADER, shaderData.code);
        createShaderPart(GL_FRAGMENT_SHADER, shaderData.code);

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
        //if(OpenGL.glCall((params) -> glIsProgram(programHandler), Boolean.class)) {
            // удаление шейдера
            OpenGL.glCall((params) -> glDeleteProgram(programHandler));
        //}

        //shaderUniforms.clear();
    }

    public void initUniforms()
    {
        for(ShaderUniform shaderUniform : shaderUniforms) {
            shaderUniform.init();
        }
    }

    public void fixUniforms()
    {
        for(ShaderUniform shaderUniform : shaderUniforms) {
            if(shaderUniform.value instanceof Double d) {
                if (shaderUniform.type == GL_FLOAT) {
                    shaderUniform.value = d.floatValue();
                } else {
                    shaderUniform.value = d.intValue();
                }
            }
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

        shaderUniforms.addAll(shader.shaderUniforms);
        shaderPartsHandlers.putAll(shader.shaderPartsHandlers);

        for(ShaderDefine shaderDefine : shader.shaderDefines) {
            addShaderDefine(shaderDefine);
        }
        //shaderDefines.addAll(shader.shaderDefines);

        path = shader.path;
        shaderData = shader.shaderData;
    }

    public void bind()
    {
        if(glIsProgram(programHandler)) {
            OpenGL.glCall((params) -> glUseProgram(programHandler));

            for(ShaderUniform shaderUniform : shaderUniforms) {
                shaderUniform.apply(programHandler);
            }
        }
    }

    public ShaderDefine getShaderDefine(String name)
    {
        return shaderDefines.stream().filter(shaderDefine -> shaderDefine.name.equals(name)).findFirst().orElse(null);
    }

    public void addShaderDefine(ShaderDefine shaderDefine)
    {
        ShaderDefine foundDefine = getShaderDefine(shaderDefine.name);

        if(foundDefine != null) return;

        shaderDefines.add(shaderDefine);
    }

    public int getShaderPartHandler(int shaderTypePart) { return shaderPartsHandlers.get(shaderTypePart); }

    public int getProgramHandler() { return programHandler; }

    public HashMap<Integer, Integer> getShaderPartsHandlers() { return shaderPartsHandlers; }

    public List<ShaderUniform> getShaderUniforms() { return shaderUniforms; }

    public List<ShaderDefine> getShaderDefines() { return shaderDefines; }

    public ShaderData getShaderData() { return shaderData; }

    public boolean isCompiled() { return compiled; }
}
