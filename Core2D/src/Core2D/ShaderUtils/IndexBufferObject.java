package Core2D.ShaderUtils;

import static org.lwjgl.opengl.GL15C.*;

public class IndexBufferObject
{
    // id буфера
    private int handler;
    // данные в буфере
    private short[] data;
    // использование буфера
    private int usage;
    // слой
    private BufferLayout layout;

    public IndexBufferObject(short[] data)
    {
        this.data = data;
        usage = GL_STATIC_DRAW;

        create();
        putData();

        data = null;
    }
    // создание буфера
    public void create()
    {
        handler = glGenBuffers();

        bind();
    }
    // удаление буфера
    public void destroy()
    {
        glDeleteBuffers(handler);

        data = null;

        if(layout != null) layout.destroy();
        layout = null;
    }
    // положить данные
    public void putData()
    {
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage);
    }
    // связка
    public void bind()
    {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handler);
    }
    // развязывание
    public void unBind()
    {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    // геттеры и сеттеры
    public short[] getData() { return data; }

    public int getUsage() { return usage; }
}
