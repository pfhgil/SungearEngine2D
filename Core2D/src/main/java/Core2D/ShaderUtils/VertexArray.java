package Core2D.ShaderUtils;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL30C.*;

public class VertexArray implements AutoCloseable
{
    // array id
    private int handler;
    // vbo в vao
    private List<VertexBuffer> VBOs = new ArrayList<>();
    // ibo в vao
    private List<IndexBuffer> IBOs = new ArrayList<>();

    public VertexArray()
    {
        create();
    }
    // создание vao
    private void create()
    {
        handler = glGenVertexArrays();

        bind();
    }
    // удаление vao
    public void destroy()
    {
        Iterator<VertexBuffer> vbosIterator = VBOs.iterator();
        while (vbosIterator.hasNext()) {
            VertexBuffer vbo = vbosIterator.next();
            vbo.destroy();
            vbo = null;
            vbosIterator.remove();
        }

        Iterator<IndexBuffer> ibosIterator = IBOs.iterator();
        while (ibosIterator.hasNext()) {
            IndexBuffer ibo = ibosIterator.next();
            ibo.destroy();
            ibo = null;
            ibosIterator.remove();
        }

        VBOs = null;
        IBOs = null;

        // удаление vao
        glDeleteVertexArrays(handler);

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    // связка
    public void bind()
    {
        glBindVertexArray(handler);
    }
    // развязывание
    public void unBind()
    {
        glBindVertexArray(0);
    }
    public void putVBO(VertexBuffer vertexBuffer, boolean divisor)
    {
        // добавляю все аттрибуты
        vertexBuffer.getLayout().addAllAttributes(divisor);

        // добавялю vbo в список
        VBOs.add(vertexBuffer);
    }
    public void putIBO(IndexBuffer indexBuffer)
    {
        // добавляю IBO в список
        IBOs.add(indexBuffer);
    }

    public void updateVBO(VertexBuffer VBO, float[] data)
    {
        VBO.bind();
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
        VBO.unBind();
    }

    // геттеры и сеттеры
    public int getHandler() { return handler; }

    public List<VertexBuffer> getVBOs() { return VBOs; }

    public List<IndexBuffer> getIBOs() { return IBOs; }

    @Override
    public void close() throws Exception {

    }
}