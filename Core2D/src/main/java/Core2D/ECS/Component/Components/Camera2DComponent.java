package Core2D.ECS.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.ShaderUtils.*;
import Core2D.Utils.MatrixUtils;
import org.joml.*;
import org.lwjgl.glfw.GLFW;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL46C.*;

public class Camera2DComponent extends Component
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

    private Shader postprocessingDefaultShader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/postprocessing/postprocessing_default_shader.glsl"));

    private List<PostprocessingLayer> postprocessingLayers = new ArrayList<>();

    // to delete ------------------
    /*
    private transient Random random = new Random();

    private transient int totalFrames = 0;

    private transient Vector3f cameraPosition = new Vector3f(-3.9999986f, 2.9999995f, 0.0f);
    private transient Vector2f lightPos = new Vector2f(0.0f, 0.0f);
    private transient Vector2f lastMousePosition = new Vector2f();
    private transient Vector2f mousePosition = new Vector2f();

     */
    // -----------------------------

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
        Vector2i screenSize = Graphics.getScreenSize();
        frameBuffer = new FrameBuffer(screenSize.x, screenSize.y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);
        resultFrameBuffer = new FrameBuffer(screenSize.x, screenSize.y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);
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

        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            frameBuffer.bind();
            frameBuffer.clear();
            if(camera2DCallback != null) {
                camera2DCallback.preRender();
            }
            frameBuffer.unBind();

            for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                Optional<PostprocessingLayer> ppLayerFoundOptional = postprocessingLayers.stream().filter(ppLayer -> ppLayer.getEntitiesLayerToRender() == layer).findFirst();
                if(ppLayerFoundOptional.isPresent()) {
                    PostprocessingLayer ppLayerFound= ppLayerFoundOptional.get();

                    ppLayerFound.getFrameBuffer().bind();
                    ppLayerFound.getFrameBuffer().clear();

                    Graphics.getMainRenderer().render(ppLayerFound.getEntitiesLayerToRender(), this);

                    ppLayerFound.getFrameBuffer().unBind();
                } else {
                    frameBuffer.bind();
                    Graphics.getMainRenderer().render(layer, this);
                    frameBuffer.unBind();
                }
            }

            frameBuffer.bind();
            if(camera2DCallback != null) {
                camera2DCallback.postRender();
            }
            frameBuffer.unBind();

            // quad render -----------------------------------------------------

            resultFrameBuffer.bind();
            resultFrameBuffer.clear();
            ppQuadVertexArray.bind();
            for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                PostprocessingLayer ppLayerFound = null;

                Shader shader = postprocessingDefaultShader;
                FrameBuffer frameBufferToBind = frameBuffer;

                Optional<PostprocessingLayer> ppLayerFoundOptional = postprocessingLayers.stream().filter(ppLayer -> ppLayer.getEntitiesLayerToRender() == layer).findFirst();
                if(ppLayerFoundOptional.isPresent()) {
                    ppLayerFound = ppLayerFoundOptional.get();

                    shader = ppLayerFound.getShader();

                    frameBufferToBind = ppLayerFound.getFrameBuffer();
                }

                frameBufferToBind.bindTexture();

                shader.bind();

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "color",
                        new Vector4f(1.0f)
                );

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "sampler",
                        frameBufferToBind.getTextureBlock() - GL_TEXTURE0
                );

                // ray tracing ---------

                /*
                float time = (float) glfwGetTime();

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "time",
                        time
                );

                if(Keyboard.keyDown(GLFW.GLFW_KEY_W)) {
                    cameraPosition.x += 0.1f;
                }
                if(Keyboard.keyDown(GLFW.GLFW_KEY_S)) {
                    cameraPosition.x -= 0.1f;
                }
                if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                    cameraPosition.y -= 0.1f;
                }
                if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                    cameraPosition.y += 0.1f;
                }

                if(Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
                    Vector2f mousePos = Mouse.getMousePosition();
                    Vector2f offset = new Vector2f(lastMousePosition).add(new Vector2f(mousePos).negate());
                    lastMousePosition.set(mousePos);

                    mousePosition.add(offset);
                } else {
                    Vector2f mousePos = Mouse.getMousePosition();
                    lastMousePosition.set(mousePos);
                }

                lightPos.x += 0.01f;
                lightPos.y += 0.01f;

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "iResolution",
                        new Vector2f(windowSize.x, windowSize.y));

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "iMouse",
                        mousePosition);

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "cameraPosition",
                        cameraPosition);

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "lightPos",
                        lightPos);


                Vector2f rnd0 = new Vector2f(random.nextFloat() * 999.0f, random.nextFloat() * 999.0f);
                Vector2f rnd1 = new Vector2f(random.nextFloat() * 999.0f, random.nextFloat() * 999.0f);
                //System.out.println("x: " + rnd.x + ", " + rnd.y);

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "u_seed1",
                        rnd0);

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "u_seed2",
                        rnd1);

                float samplerPart = 1.0f / totalFrames;
                //System.out.println(samplerPart);

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "samplerPart",
                        samplerPart);


*/

                // нарисовать два треугольника
                OpenGL.glCall((params) -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0));

                frameBufferToBind.unBindTexture();
            }
            ppQuadVertexArray.unBind();
            resultFrameBuffer.unBind();
        }
    }

    @Override
    public void destroy()
    {
        setScene2DMainCamera2D(false);
        frameBuffer.destroy();
        ppQuadVertexArray.destroy();
        resultFrameBuffer.destroy();

        for(PostprocessingLayer ppLayer : postprocessingLayers) {
            ppLayer.destroy();
        }
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
            if(camera2DComponent != null) {
                camera2DComponent.isScene2DMainCamera2D = false;
                scene2D.setSceneMainCamera2D(null);
            }
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

    public FrameBuffer getFrameBuffer() { return frameBuffer; }

    public FrameBuffer getResultFrameBuffer() { return resultFrameBuffer; }

    public Shader getPostprocessingDefaultShader() { return postprocessingDefaultShader; }

    public void setPostprocessingDefaultShader(Shader postprocessingDefaultShader)
    {
        if(this.postprocessingDefaultShader != null) {
            this.postprocessingDefaultShader.destroy();
        }
        this.postprocessingDefaultShader = postprocessingDefaultShader;
    }

    public void addPostprocessingLayer(PostprocessingLayer postprocessingLayer)
    {
        if(postprocessingLayers.stream().noneMatch((ppLayer) -> ppLayer.getEntitiesLayerToRender() == postprocessingLayer.getEntitiesLayerToRender())) {
            postprocessingLayers.add(postprocessingLayer);
        }
    }

    public boolean isPostprocessingLayerExists(Layer layer)
    {
        return postprocessingLayers.stream().anyMatch((ppLayer) -> ppLayer.getEntitiesLayerToRender() == layer);
    }

    public int getPostprocessingLayersNum() { return postprocessingLayers.size(); }

    public PostprocessingLayer getPostprocessingLayer(int n)
    {
        return postprocessingLayers.get(n);
    }

    public PostprocessingLayer getPostprocessingLayerByName(String name)
    {
        Optional<PostprocessingLayer> foundLayer = postprocessingLayers.stream().filter(ppLayer -> {
            if(ppLayer.getEntitiesLayerToRender() != null) {
                return ppLayer.getEntitiesLayerToRender().getName().equals(name);
            }
            return false;
        }).findFirst();

        return foundLayer.orElse(null);
    }

    public Iterator<PostprocessingLayer> getPostprocessingLayersIterator() { return postprocessingLayers.iterator(); }
}
