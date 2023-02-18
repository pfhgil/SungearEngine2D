package Core2D.ECS.Component.Components.Primitives;

import Core2D.AssetManager.AssetManager;
import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.OpenGL.BufferLayout;
import Core2D.Graphics.OpenGL.VertexArray;
import Core2D.Graphics.OpenGL.VertexAttribute;
import Core2D.Graphics.OpenGL.VertexBuffer;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class PrimitiveComponent extends Component
{
    protected transient float[] data = new float[0];

    public transient Shader shader;

    public transient VertexArray vertexArray;

    protected LineData[] linesData;

    public boolean scaleWithEntity = false;

    protected Vector2f offset = new Vector2f();

    protected Vector4f color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    protected float linesWidth = 3.0f;

    @Override
    public void set(Component component)
    {
        if(component instanceof PrimitiveComponent primitiveComponent) {
            destroy();

            linesData = new LineData[primitiveComponent.linesData.length];

            for(int i = 0; i < linesData.length; i++) {
                linesData[i] = new LineData(primitiveComponent.linesData[i]);
            }

            scaleWithEntity = primitiveComponent.scaleWithEntity;

            offset.set(primitiveComponent.offset);

            color.set(primitiveComponent.color);

            linesWidth = primitiveComponent.linesWidth;
        }
    }

    @Override
    public void init()
    {
        destroy();

        shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/primitives/line2D/shader.glsl"));

        loadVAO();
    }

    protected void loadVAO() {
        if(vertexArray != null) {
            vertexArray.destroy();
            vertexArray = null;
        }

        vertexArray = new VertexArray();
        VertexBuffer vertexBuffer = new VertexBuffer(data);

        // создаю описание аттрибутов в шейдерной программе
        BufferLayout attributesLayout = new BufferLayout(
                new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
        );

        vertexBuffer.setLayout(attributesLayout);
        vertexArray.putVBO(vertexBuffer, false);

        // отвязываю vao
        vertexArray.unBind();
    }

    @Override
    public void destroy()
    {
        if(vertexArray != null) {
            vertexArray.destroy();
            vertexArray = null;
        }

        if(shader != null) {
            shader.destroy();
            shader = null;
        }
    }

    public LineData[] getLinesData() { return linesData; }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color)
    {
        this.color = color;

        for(LineData lineData : linesData) {
            lineData.color.set(color);
        }
    }

    public Vector2f getOffset() { return offset; }
    public void setOffset(Vector2f offset)
    {
        this.offset = offset;

        for(LineData lineData : linesData) {
            lineData.offset.set(offset);
        }
    }

    public float getLinesWidth() { return linesWidth; }
    public void setLinesWidth(float linesWidth)
    {
        this.linesWidth = linesWidth;

        for(LineData lineData : linesData) {
            lineData.lineWidth = linesWidth;
        }
    }
}
