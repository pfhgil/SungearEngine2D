package Core2D.ECS.Component.Components.Primitives;

import Core2D.DataClasses.LineData;
import Core2D.ShaderUtils.*;
import org.joml.Vector2f;

import javax.sound.sampled.Line;

public class LineComponent extends PrimitiveComponent
{
    private float[] data = new float[] {
            // первая точка
            0.0f, 0.0f,

            // вторая точка
            0.0f, 0.0f
    };

    public LineComponent()
    {
        linesData = new LineData[] {
                new LineData(new Vector2f(), new Vector2f(), new Vector2f(0.0f, 100.0f))
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
}
