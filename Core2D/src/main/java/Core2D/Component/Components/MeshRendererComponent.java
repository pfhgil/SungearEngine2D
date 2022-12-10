package Core2D.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.Component.Component;
import Core2D.GameObject.RenderParts.Material2D;
import Core2D.GameObject.RenderParts.RenderMethod;
import Core2D.GameObject.RenderParts.Shader;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.Renderer;
import Core2D.ShaderUtils.*;
import Core2D.GameObject.RenderParts.Texture2D;
import Core2D.Utils.PositionsQuad;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11.*;

public class MeshRendererComponent extends Component {

    public Texture2D texture = new Texture2D();

    public transient Shader shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/object2D/shader.glsl"));

    public Material2D material2D;

    // VAO четырехугольника (VAO - Vertex Array Object. Хранит в себе указатели на VBO, IBO и т.д.)
    public transient VertexArray vertexArray;
    private int drawingMode = GL_TRIANGLES;
    private transient short[] indices = new short[] { 0, 1, 2, 0, 2, 3 };

    // массив данных о вершинах
    // первые строки - позиции вершин, вторые строки - текстурные координаты
    private transient Vector2f size = new Vector2f(100.0f, 100.0f);
    private transient float[] data = new float[] {
            -size.x / 2.0f, -size.y / 2.0f,
            0, 0,

            -size.x / 2.0f, size.y / 2.0f,
            0, 1,

            size.x / 2.0f, size.y / 2.0f,
            1, 1,

            size.x / 2.0f, -size.y / 2.0f,
            1, 0,
    };
    public int textureDrawMode = Texture2D.TextureDrawModes.DEFAULT;

    public MeshRendererComponent() { }

    public MeshRendererComponent(MeshRendererComponent component)
    {
        set(component);
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof MeshRendererComponent meshRendererComponent) {
            shader.set(meshRendererComponent.shader);
            texture.set(meshRendererComponent.texture);
            if(meshRendererComponent.indices != null) {
                indices = meshRendererComponent.indices;
            }
            if(meshRendererComponent.data != null) {
                data = meshRendererComponent.data;
            }
            textureDrawMode = meshRendererComponent.textureDrawMode;

            loadVAO();
        }
    }

    @Override
    public void init() {
        //gameObject.color.set(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

        loadVAO();
    }

    @Override
    public void deltaUpdate(float time) {
    }

    @Override
    public void update()
    {

    }

    @RenderMethod
    public void render()
    {
        if(gameObject.isShouldDestroy()) return;

        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
        MeshRendererComponent meshRendererComponent = gameObject.getComponent(MeshRendererComponent.class);

        if (meshRendererComponent != null) {
            if (transformComponent == null) return;
            // использую VAO, текстуру и шейдер
            meshRendererComponent.vertexArray.bind();
            meshRendererComponent.texture.bind();
            if (meshRendererComponent.material2D != null && meshRendererComponent.material2D.material2DData != null) {
                glBlendFunc(meshRendererComponent.material2D.material2DData.blendSourceFactor, meshRendererComponent.material2D.material2DData.blendDestinationFactor);
            }
            meshRendererComponent.shader.bind();

            ShaderUtils.setUniform(
                    meshRendererComponent.shader.getProgramHandler(),
                    "mvpMatrix",
                    transformComponent.getMvpMatrix()
            );
            ShaderUtils.setUniform(
                    meshRendererComponent.shader.getProgramHandler(),
                    "color",
                    gameObject.getColor()
            );
            ShaderUtils.setUniform(
                    meshRendererComponent.shader.getProgramHandler(),
                    "drawMode",
                    meshRendererComponent.textureDrawMode
            );
            ShaderUtils.setUniform(
                    meshRendererComponent.shader.getProgramHandler(),
                    "sampler",
                    meshRendererComponent.texture.getFormattedTextureBlock()
            ); //FIXME: сделать нормальный метод для того что бы задовать сразу несколько юниформ

            // нарисовать два треугольника
            glDrawElements(GL11C.GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0);

            // прекращаю использование шейдера, текстуры и VAO
            meshRendererComponent.shader.unBind();
            meshRendererComponent.texture.unBind();
            meshRendererComponent.vertexArray.unBind();
        }
    }

    @Override
    public void destroy() {
        if(vertexArray != null) {
            vertexArray.destroy();
            vertexArray = null;
        }
    }

    private void loadVAO() {
        if (Thread.currentThread().getName().equals("main")) {
            if(vertexArray != null) {
                vertexArray.destroy();
            }

            vertexArray = new VertexArray();
            // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
            VertexBuffer vertexBuffer = new VertexBuffer(data);
            // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
            IndexBuffer indexBuffer = new IndexBuffer(indices);

            // создаю описание аттрибутов в шейдерной программе
            BufferLayout attributesLayout = new BufferLayout(
                    new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2),
                    new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
            );

            vertexBuffer.setLayout(attributesLayout);
            vertexArray.putVBO(vertexBuffer, false);
            vertexArray.putIBO(indexBuffer);

            indices = null;

            // отвязываю vao
            vertexArray.unBind();


        }
    }
    public void setUV(float[] UV) {
        if (gameObject != null) {
            data[2] = UV[0];
            data[3] = UV[1];

            data[6] = UV[2];
            data[7] = UV[3];

            data[10] = UV[4];
            data[11] = UV[5];

            data[14] = UV[6];
            data[15] = UV[7];

            if (vertexArray != null) {
                VertexBuffer vbo = vertexArray.getVBOs().get(0);
                vertexArray.updateVBO(vbo, data);
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

    public float[] getData() { return data; }

    public VertexArray getVertexArrayObject() { return vertexArray; }
}
