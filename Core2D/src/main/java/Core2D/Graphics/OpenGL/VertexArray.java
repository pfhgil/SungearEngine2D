package Core2D.Graphics.OpenGL;

import Core2D.Utils.Debugger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL30C.*;

public class VertexArray
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
        handler = OpenGL.glCall((params) -> glGenVertexArrays(), Integer.class);

        bind();
    }
    // удаление vao
    public void destroy()
    {
        Iterator<VertexBuffer> vbosIterator = VBOs.iterator();
        while (vbosIterator.hasNext()) {
            VertexBuffer vbo = vbosIterator.next();
            vbo.destroy();
            vbosIterator.remove();
        }

        Iterator<IndexBuffer> ibosIterator = IBOs.iterator();
        while (ibosIterator.hasNext()) {
            IndexBuffer ibo = ibosIterator.next();
            ibo.destroy();
            ibosIterator.remove();
        }

        // удаление vao
        OpenGL.glCall((params) -> glDeleteVertexArrays(handler));

        VBOs = null;
        IBOs = null;
    }

    // связка
    public void bind()
    {
        OpenGL.glCall((params) -> glBindVertexArray(handler));

        Debugger.VAOBindCallsNum++;
    }
    // развязывание
    public void unBind()
    {
        OpenGL.glCall((params) -> glBindVertexArray(0));
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
        OpenGL.glCall((params) -> glBufferSubData(GL_ARRAY_BUFFER, 0, data));
        VBO.unBind();
    }

    // геттеры и сеттеры
    public int getHandler() { return handler; }

    public List<VertexBuffer> getVBOs() { return VBOs; }

    public List<IndexBuffer> getIBOs() { return IBOs; }
}