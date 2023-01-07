package Core2D.ECS.Component.Components;

import Core2D.ECS.Component.Component;
import Core2D.ECS.NonDuplicated;
import Core2D.Core2D.Core2D;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.ShaderUtils.FrameBuffer;
import Core2D.Utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;

public class Camera2DComponent extends Component implements NonDuplicated
{
    private Vector2f viewportSize = new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);

    private transient Matrix4f projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

    private transient Matrix4f viewMatrix = new Matrix4f();

    private boolean isScene2DMainCamera2D = false;

    private FrameBuffer frameBuffer;

    @Override
    public void init()
    {
        setScene2DMainCamera2D(isScene2DMainCamera2D);
        frameBuffer = new FrameBuffer(Graphics.getScreenSize().x, Graphics.getScreenSize().y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE1);
    }

    @Override
    public void update()
    {
        updateViewMatrix();

        frameBuffer.bind();

        OpenGL.glCall((params) -> glClear(GL_COLOR_BUFFER_BIT));

        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().draw();
        }

        frameBuffer.unBind();
    }

    @Override
    public void destroy()
    {
        setScene2DMainCamera2D(false);
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
            viewMatrix.rotate((float) Math.toRadians(rotation), 0f, 0f, 1f);
            viewMatrix.translate(new Vector3f(position.x, position.y, 1f));
        }
    }

    public Vector2f getViewportSize() { return viewportSize; }
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

    public FrameBuffer getFrameBuffer() { return frameBuffer; }
}
