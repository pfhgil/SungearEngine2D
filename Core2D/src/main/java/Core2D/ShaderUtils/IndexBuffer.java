package Core2D.ShaderUtils;

import Core2D.Graphics.OpenGL;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

import static org.lwjgl.opengl.GL15C.*;

public class IndexBuffer implements AutoCloseable
{
    // id буфера
    private int handler;
    // данные в буфере
    private short[] data;
    // использование буфера
    private int usage;
    // слой
    private BufferLayout layout;

    public IndexBuffer(short[] data)
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

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
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
    public short[] getData() { return data; }

    public int getUsage() { return usage; }

    @Override
    public void close() throws Exception {

    }
}
