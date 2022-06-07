package Core2D.Primitives;

import Core2D.Camera2D.Camera2D;
import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Resources;
import Core2D.Object2D.Transform;
import Core2D.Shader.Shader;
import Core2D.Shader.ShaderProgram;
import Core2D.ShaderUtils.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;

public class Line2D extends CommonDrawableObjectsParameters
{
    // трансформации объекта
    private Transform transform;

    // model view projection matrix
    private Matrix4f mvpMatrix;

    // камера, в виде которой будет объект
    private Camera2D attachedCamera2D;

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

    private ShaderProgram shaderProgram;

    private VertexArrayObject vertexArrayObject;

    private boolean isUIElement = false;

    private float lineWidth = 1.0f;

    public Line2D(Vector2f start, Vector2f end)
    {
        data[0] = start.x;
        data[1] = start.y;

        data[2] = end.x;
        data[3] = end.y;

        Shader vertexShader = new Shader(Resources.ShadersTexts.Primitives.Line2D.vertexShaderText, GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(Resources.ShadersTexts.Primitives.Line2D.fragmentShaderText, GL_FRAGMENT_SHADER);

        // создаю шейдерную программу
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        vertexShader = null;
        fragmentShader = null;

        Vector4f col = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        setColor(col);
        col = null;

        transform = new Transform();

        loadVAO();

        if(Core2D.currentCamera2D != null) {
            setAttachedCamera2D(Core2D.currentCamera2D);
        }
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

        // отвязываю vao
        vertexArrayObject.unBind();
    }

    @Override
    public void draw()
    {
        vertexArrayObject.bind();

        // использовать шейдер
        shaderProgram.bind();

        if (attachedCamera2D != null && !isUIElement) {
            mvpMatrix = new Matrix4f(Core2D.getProjectionMatrix()).mul(attachedCamera2D.getTransform().getModelMatrix()).mul(transform.getModelMatrix());
        } else {
            mvpMatrix = new Matrix4f(Core2D.getProjectionMatrix()).mul(transform.getModelMatrix());
        }
        ShaderUtils.setUniform(
                shaderProgram.getHandler(),
                "mvpMatrix",
                mvpMatrix
        );

        glLineWidth(lineWidth);
        glDrawElements(GL_LINES, 2, GL_UNSIGNED_SHORT, 0);
        glLineWidth(1.0f);

        shaderProgram.unBind();

        vertexArrayObject.unBind();
    }

    @Override
    public void destroy()
    {
        transform.destroy();
        transform = null;

        mvpMatrix = null;

        attachedCamera2D = null;

        color = null;

        indices = null;

        data = null;

        shaderProgram.delete();
        shaderProgram = null;

        vertexArrayObject.destroy();
        vertexArrayObject = null;

        destroyLayerObject();
    }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color)
    {
        this.color = new Vector4f(color);
        color = null;

        shaderProgram.bind();

        ShaderUtils.setUniform(
                shaderProgram.getHandler(),
                "color",
                new Vector4f(this.color)
        );

        shaderProgram.unBind();
    }

    public float[] getData() { return data; }

    public ShaderProgram getShaderProgram() { return shaderProgram; }

    public Transform getTransform() { return transform; }
    public void setTransform(Transform transform)
    {
        this.transform = transform;
        transform = null;
    }

    public Matrix4f getMvpMatrix() { return mvpMatrix; }

    public Camera2D getAttachedCamera2D() { return attachedCamera2D; }
    public void setAttachedCamera2D(Camera2D attachedCamera2D)
    {
        this.attachedCamera2D = attachedCamera2D;
        attachedCamera2D = null;
    }

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
}
