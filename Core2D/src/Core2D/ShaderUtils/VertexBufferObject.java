package Core2D.ShaderUtils;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

import static org.lwjgl.opengl.GL15C.*;

public class VertexBufferObject implements AutoCloseable
{
    // id буфера
    private int handler;
    // данные в буфере
    private float[] data;
    // использование буфера
    private int usage;
    // слой
    private BufferLayout layout;

    public VertexBufferObject(float[] data)
    {
        this.data = data;
        usage = GL_STATIC_DRAW;

        create();
        putData();
    }

    public VertexBufferObject(float[] data, int usage)
    {
        this.data = data;
        this.usage = usage;

        create();
        putData();
    }
    // создание буфера
    private void create()
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

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }
    // положить данные в буфер
    public void putData()
    {
        glBufferData(GL_ARRAY_BUFFER, data, usage);
    }
    // положить новые данные
    public void putNewData(float[] data)
    {
        this.data = data;

        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
    }
    // связка
    public void bind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, handler);
    }
    // развязывание
    public void unBind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    // геттеры и сеттеры

    public int getHandler() { return handler; }

    public BufferLayout getLayout() { return layout; }
    public void setLayout(BufferLayout layout) { this.layout = layout; }

    public float[] getData() { return data; }

    public int getUsage() { return usage; }

    @Override
    public void close() throws Exception {

    }
}
