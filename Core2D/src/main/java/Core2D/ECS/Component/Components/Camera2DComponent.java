package Core2D.ECS.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.ECS.NonDuplicated;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.ShaderUtils.*;
import Core2D.Utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;

public class Camera2DComponent extends Component implements NonDuplicated
{
    public interface Camera2DCallback
    {
        void preRender();
        void postRender();
    }

    private Vector2f viewportSize = new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);

    private transient Matrix4f projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

    private transient Matrix4f viewMatrix = new Matrix4f();

    private boolean isScene2DMainCamera2D = false;

    // промежуточный фрейм буфер без пост процессинга
    private transient FrameBuffer frameBuffer;

    // результативный фрейм буфер с пост процессингом
    private transient FrameBuffer resultFrameBuffer;

    public transient Camera2DCallback camera2DCallback;


    // post processing quad -----------------------------------------------------
    private transient short[] ppQuadIndices = new short[] { 0, 1, 2, 0, 2, 3 };

    // массив данных о вершинах
    // первые строки - позиции вершин, вторые строки - текстурные координаты
    private transient Vector2f ppQuadSize = new Vector2f(100.0f, 100.0f);
    private transient float[] ppQuadData = new float[] {
            -1, -1,
            0, 0,

            -1, 1,
            0, 1,

            1, 1,
            1, 1,

            1, -1,
            1, 0,
    };

    private transient VertexArray ppQuadVertexArray;

    private Shader postprocessingShader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/postprocessing/postprocessing_default_shader.glsl"));

    public Camera2DComponent()
    {

    }

    @Override
    public void init()
    {
        if(frameBuffer != null) {
            frameBuffer.destroy();
            frameBuffer = null;
        }
        if(resultFrameBuffer != null) {
            resultFrameBuffer.destroy();
            resultFrameBuffer = null;
        }
        loadVAO();
        setScene2DMainCamera2D(isScene2DMainCamera2D);
        frameBuffer = new FrameBuffer(Graphics.getScreenSize().x, Graphics.getScreenSize().y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);
        resultFrameBuffer = new FrameBuffer(Graphics.getScreenSize().x, Graphics.getScreenSize().y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);
    }

    private void loadVAO()
    {
        if (ppQuadVertexArray != null) {
            ppQuadVertexArray.destroy();
            ppQuadVertexArray = null;
        }

        ppQuadVertexArray = new VertexArray();
        // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
        VertexBuffer vertexBuffer = new VertexBuffer(ppQuadData);
        // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
        IndexBuffer indexBuffer = new IndexBuffer(ppQuadIndices);

        // создаю описание аттрибутов в шейдерной программе
        BufferLayout attributesLayout = new BufferLayout(
                new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2),
                new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
        );

        vertexBuffer.setLayout(attributesLayout);
        ppQuadVertexArray.putVBO(vertexBuffer, false);
        ppQuadVertexArray.putIBO(indexBuffer);

        ppQuadIndices = null;

        // отвязываю vao
        ppQuadVertexArray.unBind();
    }

    @Override
    public void update()
    {
        Vector2i windowSize = Core2D.getWindow().getSize();
        this.viewportSize.set(windowSize);
        projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

        updateViewMatrix();

        //postprocessingShader.bind();
        frameBuffer.bind();

        if(camera2DCallback != null) {
            camera2DCallback.preRender();
        }

        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().draw(this);
        }

        if(camera2DCallback != null) {
            camera2DCallback.postRender();
        }

        frameBuffer.unBind();

        resultFrameBuffer.bind();

        ppQuadVertexArray.bind();
        frameBuffer.bindTexture();
        postprocessingShader.bind();

        /*
        ShaderUtils.setUniform(
                postprocessingShader.getProgramHandler(),
                "color",
                entity.getColor()
        );

         */
        ShaderUtils.setUniform(
                postprocessingShader.getProgramHandler(),
                "sampler",
                frameBuffer.getTextureBlock() - GL_TEXTURE0
        );

        float time = (float) glfwGetTime();

        ShaderUtils.setUniform(
                postprocessingShader.getProgramHandler(),
                "time",
                time
        );

        // нарисовать два треугольника
        OpenGL.glCall((params) -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0));

        frameBuffer.unBindTexture();
        ppQuadVertexArray.unBind();

        resultFrameBuffer.unBind();
    }

    @Override
    public void destroy()
    {
        setScene2DMainCamera2D(false);
        frameBuffer.destroy();
        ppQuadVertexArray.destroy();
        resultFrameBuffer.destroy();
    }

    public void updateViewMatrix()
    {
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        if(transformComponent != null) {
            Vector2f position = MatrixUtils.getPosition(transformComponent.getTransform().getResultModelMatrix());
            float rotation = MatrixUtils.getRotation(transformComponent.getTransform().getResultModelMatrix());
            Vector2f scale = MatrixUtils.getScale(transformComponent.getTransform().getResultModelMatrix());

            viewMatrix.identity();

            viewMatrix.scale(new Vector3f(scale.x, scale.y, 1f));
            viewMatrix.rotate((float) Math.toRadians(-rotation), 0f, 0f, 1f);
            viewMatrix.translate(new Vector3f(-position.x, -position.y, 1f));
        }
    }

    public Vector2f getViewportSize() { return viewportSize; }
    @Deprecated
    public void setViewportSize(Vector2f viewportSize)
    {
        this.viewportSize = viewportSize;

        projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);
    }

    public Matrix4f getProjectionMatrix() { return projectionMatrix; }

    public Matrix4f getViewMatrix() { return viewMatrix; }

    public boolean isScene2DMainCamera2D() { return isScene2DMainCamera2D; }

    public void setScene2DMainCamera2D(boolean scene2DMainCamera2D, Scene2D scene2D)
    {
        if(scene2D != null &&
                scene2DMainCamera2D && scene2D.getSceneMainCamera2D() != null) {
            Camera2DComponent camera2DComponent = scene2D.getSceneMainCamera2D().getComponent(Camera2DComponent.class);
            camera2DComponent.isScene2DMainCamera2D = false;
            scene2D.setSceneMainCamera2D(null);
        }
        isScene2DMainCamera2D = scene2DMainCamera2D;
        if(scene2D != null) {
            scene2D.setSceneMainCamera2D(isScene2DMainCamera2D ? entity : scene2D.getSceneMainCamera2D());
        }
    }

    public void setScene2DMainCamera2D(boolean scene2DMainCamera2D)
    {
        if(SceneManager.currentSceneManager != null) {
            setScene2DMainCamera2D(scene2DMainCamera2D, SceneManager.currentSceneManager.getCurrentScene2D());
        }
    }

    public FrameBuffer getResultFrameBuffer() { return resultFrameBuffer; }

    public Shader getPostprocessingShader() { return postprocessingShader; }

    public void setPostprocessingShader(Shader postprocessingShader)
    {
        if(this.postprocessingShader != null) {
            this.postprocessingShader.destroy();
        }
        this.postprocessingShader = postprocessingShader;
    }
}
