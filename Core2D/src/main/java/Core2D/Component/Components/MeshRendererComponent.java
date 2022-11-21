package Core2D.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.Component.Component;
import Core2D.GameObject.RenderParts.Material2D;
import Core2D.GameObject.RenderParts.Shader;
import Core2D.ShaderUtils.*;
import Core2D.GameObject.RenderParts.Texture2D;
import Core2D.Utils.PositionsQuad;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11.*;

public class MeshRendererComponent extends Component {

    public Texture2D texture = new Texture2D();

    public Shader shader;

    public Material2D material2D;

    // VAO четырехугольника (VAO - Vertex Array Object. Хранит в себе указатели на VBO, IBO и т.д.)
    public transient VertexArrayObject vertexArrayObject;
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

    @Override
    public void init() {
        shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/object2D/shader.glsl"));

        gameObject.color.set(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

        loadVAO();
    }

    @Override
    public void deltaUpdate(float time) {
    }

    @Override
    public void update()
    {
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
        if(transformComponent == null) return;
        if(gameObject.isShouldDestroy()) return;
        // использую VAO, текстуру и шейдер
        vertexArrayObject.bind();
        texture.bind();
        if(material2D != null && material2D.material2DData != null) {
            glBlendFunc(material2D.material2DData.blendSourceFactor, material2D.material2DData.blendDestinationFactor);
        }
        shader.bind();

        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "mvpMatrix",
                transformComponent.getMvpMatrix()
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "color",
                gameObject.getColor()
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "drawMode",
                textureDrawMode
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "sampler",
                texture.getFormattedTextureBlock()
        ); //FIXME: сделать нормальный метод для того что бы задовать сразу несколько юниформ

        // нарисовать два треугольника
        glDrawElements(GL11C.GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0);

        // прекращаю использование шейдера, текстуры и VAO
        shader.unBind();
        texture.unBind();
        vertexArrayObject.unBind();
    }

    @Override
    public void destroy() {
        if(vertexArrayObject != null) {
            vertexArrayObject.destroy();
            vertexArrayObject = null;
        }
    }

    private void loadVAO() {
        if (Thread.currentThread().getName().equals("main")) {
            vertexArrayObject = new VertexArrayObject();
            // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
            VertexBufferObject vertexBufferObject = new VertexBufferObject(data);
            // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
            IndexBufferObject indexBufferObject = new IndexBufferObject(indices);

            // создаю описание аттрибутов в шейдерной программе
            BufferLayout attributesLayout = new BufferLayout(
                    new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2),
                    new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
            );

            vertexBufferObject.setLayout(attributesLayout);
            vertexArrayObject.putVBO(vertexBufferObject, false);
            vertexArrayObject.putIBO(indexBufferObject);

            indices = null;

            // отвязываю vao
            vertexArrayObject.unBind();


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

            if (vertexArrayObject != null) {
                VertexBufferObject vbo = vertexArrayObject.getVBOs().get(0);
                vertexArrayObject.updateVBO(vbo, data);
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

    public VertexArrayObject getVertexArrayObject() { return vertexArrayObject; }
}
