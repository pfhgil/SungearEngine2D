package Core2D.ShaderUtils;

import Core2D.Graphics.OpenGL;

import static org.lwjgl.opengl.GL15C.*;

public class VertexBuffer
{
    // id буфера
    private int handler;
    // данные в буфере
    private float[] data;
    // использование буфера
    private int usage;
    // слой
    private BufferLayout layout;

    public VertexBuffer(float[] data)
    {
        this.data = data;
        usage = GL_STATIC_DRAW;

        create();
        putData();
    }

    public VertexBuffer(float[] data, int usage)
    {
        this.data = data;
        this.usage = usage;

        create();
        putData();
    }
    // создание буфера
    private void create()
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
    // положить данные в буфер
    public void putData()
    {
        OpenGL.glCall((params) -> glBufferData(GL_ARRAY_BUFFER, data, usage));
    }
    // положить новые данные
    public void putNewData(float[] data)
    {
        this.data = data;

        OpenGL.glCall((params) -> glBufferSubData(GL_ARRAY_BUFFER, 0, data));
    }
    // связка
    public void bind()
    {
        OpenGL.glCall((params) -> glBindBuffer(GL_ARRAY_BUFFER, handler));
    }
    // развязывание
    public void unBind()
    {
        OpenGL.glCall((params) -> glBindBuffer(GL_ARRAY_BUFFER, 0));
    }

    // геттеры и сеттеры

    public int getHandler() { return handler; }

    public BufferLayout getLayout() { return layout; }
    public void setLayout(BufferLayout layout) { this.layout = layout; }

    public float[] getData() { return data; }

    public int getUsage() { return usage; }
}
