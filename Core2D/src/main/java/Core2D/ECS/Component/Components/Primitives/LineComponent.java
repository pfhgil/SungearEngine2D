package Core2D.ECS.Component.Components.Primitives;

import Core2D.AssetManager.AssetManager;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.ShaderUtils.*;

public class LineComponent extends PrimitiveComponent
{
    private float[] data = new float[] {
            // первая точка
            0.0f, 0.0f,

            // вторая точка
            0.0f, 0.0f
    };

    private short[] indices = new short[] {
            0, 1
    };

    public float lineWidth = 1.0f;

    @Override
    public void init() {
        //entity.color.set(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/primitives/line2D/shader.glsl"));

        loadVAO();
    }

    @Override
    public void destroy() {
        if(vertexArray != null) {
            vertexArray.destroy();
            vertexArray = null;
        }
    }

    private void loadVAO()
    {
        vertexArray = new VertexArray();
        // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
        VertexBuffer vertexBuffer = new VertexBuffer(data);
        // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
        IndexBuffer indexBuffer = new IndexBuffer(indices);

        // создаю описание аттрибутов в шейдерной программе
        BufferLayout attributesLayout = new BufferLayout(
                new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
        );

        vertexBuffer.setLayout(attributesLayout);
        vertexArray.putVBO(vertexBuffer, false);
        vertexArray.putIBO(indexBuffer);

        indices = null;

        // отвязываю vao
        vertexArray.unBind();
    }

    public float[] getData() { return data; }

    public VertexArray getVertexArrayObject() { return vertexArray; }
}
