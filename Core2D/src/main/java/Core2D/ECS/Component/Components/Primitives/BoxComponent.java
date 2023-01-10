package Core2D.ECS.Component.Components.Primitives;

import Core2D.DataClasses.LineData;
import Core2D.ShaderUtils.BufferLayout;
import Core2D.ShaderUtils.VertexArray;
import Core2D.ShaderUtils.VertexAttribute;
import Core2D.ShaderUtils.VertexBuffer;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class BoxComponent extends PrimitiveComponent
{
    private Vector2f size = new Vector2f(100.0f, 100.0f);

    private float[] data = new float[] {
            // первая точка
            0.0f, 0.0f,

            // вторая точка
            0.0f, 0.0f,

            // третья точка
            0.0f, 0.0f,

            // четвертая точка
            0.0f, 0.0f
    };

    private Vector2f offset = new Vector2f();

    private float linesWidth = 0.0f;

    private Vector4f color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    public BoxComponent()
    {
        linesData = new LineData[]{
                new LineData(new Vector2f(), new Vector2f(-size.x / 2.0f, -size.y / 2.0f), new Vector2f(-size.x / 2.0f, size.y / 2.0f)),
                new LineData(new Vector2f(), new Vector2f(-size.x / 2.0f, size.y / 2.0f), new Vector2f(size.x / 2.0f, size.y / 2.0f)),
                new LineData(new Vector2f(), new Vector2f(size.x / 2.0f, size.y / 2.0f), new Vector2f(size.x / 2.0f, -size.y / 2.0f)),
                new LineData(new Vector2f(), new Vector2f(size.x / 2.0f, -size.y / 2.0f), new Vector2f(-size.x / 2.0f, -size.y / 2.0f))
        };
    }

    @Override
    public void init() {
        super.init();

        loadVAO();
    }

    @Override
    public void destroy() {
        if(vertexArray != null) {
            vertexArray.destroy();
            vertexArray = null;
        }
    }


    private void loadVAO() {
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

    public VertexArray getVertexArrayObject() { return vertexArray; }

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

    public Vector2f getSize() { return size; }
    public void setSize(Vector2f size)
    {
        this.size = size;

        linesData[0].getVertices()[0].set(-size.x / 2.0f, -size.y / 2.0f);
        linesData[0].getVertices()[1].set(-size.x / 2.0f, size.y / 2.0f);

        linesData[1].getVertices()[0].set(-size.x / 2.0f, size.y / 2.0f);
        linesData[1].getVertices()[1].set(size.x / 2.0f, size.y / 2.0f);

        linesData[2].getVertices()[0].set(size.x / 2.0f, size.y / 2.0f);
        linesData[2].getVertices()[1].set(size.x / 2.0f, -size.y / 2.0f);

        linesData[3].getVertices()[0].set(size.x / 2.0f, -size.y / 2.0f);
        linesData[3].getVertices()[1].set(-size.x / 2.0f, -size.y / 2.0f);
    }
    public void setWidth(float width)
    {
        this.size.x = width;

        setSize(size);
    }
    public void setHeight(float height)
    {
        this.size.y = height;

        setSize(size);
    }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color)
    {
        this.color = color;

        for(LineData lineData : linesData) {
            lineData.color.set(color);
        }
    }
}
