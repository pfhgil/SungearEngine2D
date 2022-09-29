package Core2D.Primitives;

import Core2D.AssetManager.AssetManager;
import Core2D.Camera2D.CamerasManager;
import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Log.Log;
import Core2D.Object2D.Transform;
import Core2D.Shader.ShaderProgram;
import Core2D.ShaderUtils.*;
import Core2D.Utils.ExceptionsUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Line2D extends CommonDrawableObjectsParameters implements AutoCloseable
{
    // трансформации объекта
    private Transform transform;

    // model view projection matrix
    private Matrix4f mvpMatrix;

    // цвет
    private Vector4f color;

    private float[] data = new float[] {
            // первая точка
            0.0f, 0.0f,

            // вторая точка
            0.0f, 0.0f
    };

    private short[] indices = new short[] {
            0, 1
    };

    private VertexArrayObject vertexArrayObject;

    private ShaderProgram shaderProgram;

    private boolean isUIElement = false;

    private float lineWidth = 1.0f;

    public Line2D()
    {
        shaderProgram = AssetManager.getShaderProgram("line2DProgram");

        setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

        transform = new Transform();

        loadVAO();
    }

    private void loadVAO()
    {
        vertexArrayObject = new VertexArrayObject();
        // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
        VertexBufferObject vertexBufferObject = new VertexBufferObject(data);
        // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
        IndexBufferObject indexBufferObject = new IndexBufferObject(indices);

        // создаю описание аттрибутов в шейдерной программе
        BufferLayout attributesLayout = new BufferLayout(
                new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
        );

        vertexBufferObject.setLayout(attributesLayout);
        vertexArrayObject.putVBO(vertexBufferObject, false);
        vertexArrayObject.putIBO(indexBufferObject);

        indices = null;

        // отвязываю vao
        vertexArrayObject.unBind();
    }

    @Override
    public void update()
    {
        updateMVPMatrix();
    }

    private void updateMVPMatrix()
    {
        if(CamerasManager.getMainCamera2D() != null && !isUIElement) {
            mvpMatrix = new Matrix4f(CamerasManager.getMainCamera2D().getProjectionMatrix()).mul(CamerasManager.getMainCamera2D().getTransform().getModelMatrix()).mul(transform.getModelMatrix());
        } else {
            mvpMatrix = new Matrix4f().identity().mul(transform.getModelMatrix());
        }
    }

    @Override
    public void destroy()
    {
        shouldDestroy = true;

        transform.destroy();
        transform = null;

        vertexArrayObject.destroy();
        vertexArrayObject = null;

        //destroyParams();

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color) { this.color = new Vector4f(color); }

    public float[] getData() { return data; }

    public VertexArrayObject getVertexArrayObject() { return vertexArrayObject; }

    public Transform getTransform() { return transform; }
    public void setTransform(Transform transform) { this.transform = transform; }

    public Matrix4f getMvpMatrix() { return mvpMatrix; }

    public boolean isUIElement() { return isUIElement; }
    public void setUIElement(boolean UIElement) { isUIElement = UIElement; }

    public Vector2f getStart() { return new Vector2f(data[0], data[1]); }
    public void setStart(Vector2f start)
    {
        data[0] = start.x;
        data[1] = start.y;

        VertexBufferObject vbo = vertexArrayObject.getVBOs().get(0);
        vertexArrayObject.updateVBO(vbo, data);
        vbo = null;
    }

    public Vector2f getEnd() { return new Vector2f(data[2], data[3]); }
    public void setEnd(Vector2f end)
    {
        data[2] = end.x;
        data[3] = end.y;

        VertexBufferObject vbo = vertexArrayObject.getVBOs().get(0);
        vertexArrayObject.updateVBO(vbo, data);
        vbo = null;
    }

    public float getLineWidth() { return lineWidth; }
    public void setLineWidth(float lineWidth) { this.lineWidth = lineWidth; }

    public ShaderProgram getShaderProgram() { return shaderProgram; }
    public void setShaderProgram(ShaderProgram shaderProgram) { this.shaderProgram = shaderProgram; }

    @Override
    public void close() throws Exception {

    }
}
