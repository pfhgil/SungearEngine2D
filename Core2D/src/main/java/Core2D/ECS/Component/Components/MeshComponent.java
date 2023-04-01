package Core2D.ECS.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.OpenGL.*;
import Core2D.Graphics.RenderParts.Material2D;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Utils.PositionsQuad;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class MeshComponent extends Component
{
    private Texture2D texture = new Texture2D(AssetManager.getInstance().getTexture2DData("/data/textures/white_texture.png"));

    private Shader shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/mesh/default_shader.glsl"));

    public Material2D material2D;

    // VAO четырехугольника (VAO - Vertex Array Object. Хранит в себе указатели на VBO, IBO и т.д.)
    private transient VertexArray vertexArray;
    private int drawingMode = GL_TRIANGLES;

    // массив данных о вершинах
    // первые строки - позиции вершин, вторые строки - текстурные координаты
    private transient Vector2f size = new Vector2f(100.0f, 100.0f);

    public MeshComponent() { }

    public MeshComponent(MeshComponent component)
    {
        set(component);
    }

    @Override
    public void set(Component component)
    {
        /*
        if(component instanceof MeshComponent meshComponent) {
            shader.set(meshComponent.shader);
            texture.set(meshComponent.texture);
            if(meshComponent.indices != null) {
                indices = meshComponent.indices;
            }
            if(meshComponent.data != null) {
                data = meshComponent.data;
            }

            loadVAO();
        }

         */
    }

    @Override
    public void init() {
        entity.color.set(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

        loadVAO();
    }

    @Override
    public void destroy() {
        if(vertexArray != null) {
            vertexArray.destroy();
            vertexArray = null;
        }

        if(texture != null) {
            texture.destroy();
            texture = null;
        }

        if(shader != null) {
            shader.destroy();
            shader = null;
        }
    }

    private void loadVAO()
    {
        float[] data = new float[] {
                -size.x / 2.0f, -size.y / 2.0f, 0f,
                0, 0, 0f,
                0, 0, 0f,

                -size.x / 2.0f, size.y / 2.0f, 0f,
                0, 1, 0f,
                0, 0, 0f,

                size.x / 2.0f, size.y / 2.0f, 0f,
                1, 1, 0f,
                0, 0, 0f,

                size.x / 2.0f, -size.y / 2.0f, 0f,
                1, 0, 0f,
                0, 0, 0f
        };

        int[] indices = new int[] { 0, 1, 2, 0, 2, 3 };

        if (vertexArray != null) {
            vertexArray.destroy();
            vertexArray = null;
        }

        vertexArray = new VertexArray();
        // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
        VertexBuffer vertexBuffer = new VertexBuffer(data);
        // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
        IndexBuffer indexBuffer = new IndexBuffer(indices);

        // создаю описание аттрибутов в шейдерной программе
        BufferLayout attributesLayout = new BufferLayout(
                new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3),
                new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3),
                new VertexAttribute(2, "normalPositionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT3)
        );

        vertexBuffer.setLayout(attributesLayout);
        vertexArray.putVBO(vertexBuffer, false);
        vertexArray.putIBO(indexBuffer);

        // отвязываю vao
        vertexArray.unBind();
    }
    public void setUV(float[] UV)
    {
        if (entity != null) {
            if (vertexArray != null) {
                VertexBuffer vbo = vertexArray.getVBOs().get(0);
                vbo.getData()[2] = UV[0];
                vbo.getData()[3] = UV[1];

                vbo.getData()[6] = UV[2];
                vbo.getData()[7] = UV[3];

                vbo.getData()[10] = UV[4];
                vbo.getData()[11] = UV[5];

                vbo.getData()[14] = UV[6];
                vbo.getData()[15] = UV[7];

                vertexArray.updateVBO(vbo, vbo.getData());
            }
        }
    }

    public void setUV(Vector2f bottomLeft, Vector2f upLeft, Vector2f upRight, Vector2f bottomRight){
        setUV(new float[] {bottomLeft.x, bottomLeft.y, upLeft.x, upLeft.y, upRight.x, upRight.y, bottomRight.x, bottomRight.y});
    }
    public void setUV(PositionsQuad positionsQuad) {
        Vector2f resP0 = new Vector2f(positionsQuad.getLeftBottom().x / positionsQuad.getAtlasSize().x, positionsQuad.getLeftBottom().y / positionsQuad.getAtlasSize().y);
        Vector2f resP1 = new Vector2f(positionsQuad.getLeftTop().x / positionsQuad.getAtlasSize().x, positionsQuad.getLeftTop().y / positionsQuad.getAtlasSize().y);
        Vector2f resP2 = new Vector2f(positionsQuad.getRightTop().x / positionsQuad.getAtlasSize().x, positionsQuad.getRightTop().y / positionsQuad.getAtlasSize().y);
        Vector2f resP3 = new Vector2f(positionsQuad.getRightBottom().x / positionsQuad.getAtlasSize().x, positionsQuad.getRightBottom().y / positionsQuad.getAtlasSize().y);
        setUV(resP0, resP1, resP2, resP3);
    }

    public VertexArray getVertexArrayObject() { return vertexArray; }

    public Texture2D getTexture() { return texture; }
    public void setTexture(Texture2D texture)
    {
        if(this.texture != null) {
            this.texture.destroy();
        }
        this.texture = texture;
    }

    public Shader getShader() { return shader; }
    public void setShader(Shader shader)
    {
        if(this.shader != null) {
            this.shader.destroy();
        }
        this.shader = shader;
    }
}
