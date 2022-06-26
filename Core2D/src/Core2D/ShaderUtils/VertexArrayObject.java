package Core2D.ShaderUtils;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL30C.*;

public class VertexArrayObject implements AutoCloseable
{
    // array id
    private int handler;
    // vbo в vao
    private List<VertexBufferObject> VBOs = new ArrayList<>();
    // ibo в vao
    private List<IndexBufferObject> IBOs = new ArrayList<>();

    public VertexArrayObject()
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
        Iterator<VertexBufferObject> vbosIterator = VBOs.iterator();
        while (vbosIterator.hasNext()) {
            VertexBufferObject vbo = vbosIterator.next();
            vbo.destroy();
            vbo = null;
            vbosIterator.remove();
        }

        Iterator<IndexBufferObject> ibosIterator = IBOs.iterator();
        while (ibosIterator.hasNext()) {
            IndexBufferObject ibo = ibosIterator.next();
            ibo.destroy();
            ibo = null;
            ibosIterator.remove();
        }

        vbosIterator = null;
        ibosIterator = null;

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
    public void putVBO(VertexBufferObject vertexBufferObject, boolean divisor)
    {
        // добавляю все аттрибуты
        vertexBufferObject.getLayout().addAllAttributes(divisor);

        // добавялю vbo в список
        VBOs.add(vertexBufferObject);
        vertexBufferObject = null;
    }
    public void putIBO(IndexBufferObject indexBufferObject)
    {
        // добавляю IBO в список
        IBOs.add(indexBufferObject);
        indexBufferObject = null;
    }

    public void updateVBO(VertexBufferObject VBO, float[] data)
    {
        VBO.bind();
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
        VBO.unBind();
        VBO = null;
        data = null;
    }

    // геттеры и сеттеры
    public int getHandler() { return handler; }

    public List<VertexBufferObject> getVBOs() { return VBOs; }

    public List<IndexBufferObject> getIBOs() { return IBOs; }

    @Override
    public void close() throws Exception {

    }
}