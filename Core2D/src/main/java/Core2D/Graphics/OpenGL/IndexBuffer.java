package Core2D.Graphics.OpenGL;

import static org.lwjgl.opengl.GL15C.*;

public class IndexBuffer
{
    // id буфера
    private int handler;
    // данные в буфере
    private int[] data;
    // использование буфера
    private int usage;
    // слой
    private BufferLayout layout;

    public IndexBuffer(int[] data)
    {
        this.data = data;
        usage = GL_STATIC_DRAW;

        create();
        putData();
    }
    // создание буфера
    public void create()
    {
        handler = OpenGL.glCall((params) -> glGenBuffers(), Integer.class);

        bind();
    }
    // удаление буфера
    public void destroy()
    {
        OpenGL.glCall((params) -> glDeleteBuffers(handler));

        data = null;

        if(layout != null) layout.destroy();
        layout = null;
    }
    // положить данные
    public void putData()
    {
        OpenGL.glCall((params) -> glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage));
    }
    // связка
    public void bind()
    {
        OpenGL.glCall((params) -> glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handler));
    }
    // развязывание
    public void unBind()
    {
        OpenGL.glCall((params) -> glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0));
    }

    // геттеры и сеттеры
    public int[] getData() { return data; }

    public int getUsage() { return usage; }
}
