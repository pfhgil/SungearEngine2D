package Core2D.ShaderUtils;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_INT;
import static org.lwjgl.opengl.GL20C.GL_BOOL;

public class VertexAttribute
{
    public static class ShaderDataType
    {
        // поддерживаемые типы данных в шейдере
        public static final int SHADER_DATA_TYPE_NONE = 0;

        public static final int SHADER_DATA_TYPE_T_INT = 1;
        public static final int SHADER_DATA_TYPE_T_INT2 = 2;
        public static final int SHADER_DATA_TYPE_T_INT3 = 3;
        public static final int SHADER_DATA_TYPE_T_INT4 = 4;

        public static final int SHADER_DATA_TYPE_T_FLOAT = 5;
        public static final int SHADER_DATA_TYPE_T_FLOAT2 = 6;
        public static final int SHADER_DATA_TYPE_T_FLOAT3 = 7;
        public static final int SHADER_DATA_TYPE_T_FLOAT4 = 8;

        public static final int SHADER_DATA_TYPE_T_MAT2 = 9;
        public static final int SHADER_DATA_TYPE_T_MAT3 = 10;
        public static final int SHADER_DATA_TYPE_T_MAT4 = 11;

        public static final int SHADER_DATA_TYPE_T_BOOLEAN = 12;

        private int type;

        public ShaderDataType(int type)
        {
            this.type = type;
        }

        // геттеры и сеттеры
        public int getType() { return type; }
        public void setType(int type) { this.type = type; }
    }

    // возращает тип _type в байтах
    public static int shaderDataTypeInByte(int type)
    {
        switch(type) {
            case ShaderDataType.SHADER_DATA_TYPE_T_INT: return 4;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT2: return 4 * 2;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT3: return 4 * 3;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT4: return 4 * 4;

            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT: return 4;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2: return 4 * 2;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3: return 4 * 3;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT4: return 4 * 4;

            case ShaderDataType.SHADER_DATA_TYPE_T_MAT2: return 4 * 2 * 2;
            case ShaderDataType.SHADER_DATA_TYPE_T_MAT3: return 4 * 3 * 3;
            case ShaderDataType.SHADER_DATA_TYPE_T_MAT4: return 4 * 4 * 4;

            case ShaderDataType.SHADER_DATA_TYPE_T_BOOLEAN: return 4;
        }

        System.out.println("Error: unknown shader data type!");
        return 0;
    }

    // конвертация ShaderDataType в OpenGL тип данных
    public static int convertShaderDataTypeToOpenGLDataType(int type)
    {
        switch(type)
        {
            case ShaderDataType.SHADER_DATA_TYPE_T_INT: return GL_INT;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT2: return GL_INT;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT3: return GL_INT;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT4: return GL_INT;

            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT: return GL_FLOAT;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2: return GL_FLOAT;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3: return GL_FLOAT;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT4: return GL_FLOAT;
            case ShaderDataType.SHADER_DATA_TYPE_T_MAT2: return GL_FLOAT;
            case ShaderDataType.SHADER_DATA_TYPE_T_MAT3: return GL_FLOAT;
            case ShaderDataType.SHADER_DATA_TYPE_T_MAT4: return GL_FLOAT;

            case ShaderDataType.SHADER_DATA_TYPE_T_BOOLEAN: return GL_BOOL;
        }

        if(type == 0)
        {
            System.out.println("Can't convert to OpenGL type, because input data is unknown!");
            Runtime.getRuntime().exit(-1);
        }

        return 0;
    }

        // имя аттрибута
    private String name;
    // тип данных аттрибута
    private int shaderDataType;
    // конвертированный тип данных (в OpenGL тип)
    private int convertedShaderDataType;
    // смещение
    private int offset;
    // размер
    private int size;
    // нормализованный
    private boolean normalized;
    // id аттрибута
    private int ID;

    // конструктор
    public VertexAttribute(int ID, String name, int shaderDataType, boolean normalized)
    {
        this.ID = ID;

        this.name = name;
        this.shaderDataType = shaderDataType;
        convertedShaderDataType = convertShaderDataTypeToOpenGLDataType(shaderDataType);
        this.normalized = normalized;

        offset = 0;
        size = shaderDataTypeInByte(shaderDataType);
    }
    // конструктор
    public VertexAttribute(int ID, String name, int shaderDataType)
    {
        this(ID, name, shaderDataType, false);
    }

    // получить размер аттрибута
    public int getElementAttributeSize()
    {
        switch(shaderDataType) {
            case ShaderDataType.SHADER_DATA_TYPE_T_INT: return 1;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT2: return 2;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT3: return 3;
            case ShaderDataType.SHADER_DATA_TYPE_T_INT4: return 4;

            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT: return 1;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2: return 2;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3: return 3;
            case ShaderDataType.SHADER_DATA_TYPE_T_FLOAT4: return 4;

            case ShaderDataType.SHADER_DATA_TYPE_T_MAT2: return 2 * 2;
            case ShaderDataType.SHADER_DATA_TYPE_T_MAT3: return 3 * 3;
            case ShaderDataType.SHADER_DATA_TYPE_T_MAT4: return 4 * 4;

            case ShaderDataType.SHADER_DATA_TYPE_T_BOOLEAN: return 1;
        }

        System.out.println("Error: unknown attribute size!");
        return 0;
    }

    public void destroy()
    {
        name = null;
    }

    // геттеры и сеттеры

    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public int getConvertedShaderDataType() { return convertedShaderDataType; }
    public void setConvertedShaderDataType(int convertedShaderDataType) { this.convertedShaderDataType = convertedShaderDataType; }

    public boolean isNormalized() { return normalized; }
    public void setNormalized(boolean normalized) { this.normalized = normalized; }
}
