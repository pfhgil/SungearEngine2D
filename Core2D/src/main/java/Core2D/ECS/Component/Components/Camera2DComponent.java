package Core2D.ECS.Component.Components;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.ECS.NonDuplicated;
import Core2D.Graphics.Graphics;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.ShaderUtils.FrameBuffer;
import Core2D.Utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

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

    private FrameBuffer frameBuffer;

    public Camera2DCallback camera2DCallback;

    public Camera2DComponent()
    {

    }

    @Override
    public void init()
    {
        if(frameBuffer != null) {
            //frameBuffer.destroy();
            //frameBuffer = null;
        }
        setScene2DMainCamera2D(isScene2DMainCamera2D);
        frameBuffer = new FrameBuffer(Graphics.getScreenSize().x, Graphics.getScreenSize().y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE1);
    }

    @Override
    public void update()
    {
        Vector2i windowSize = Core2D.getWindow().getSize();
        this.viewportSize.set(windowSize);
        projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

        updateViewMatrix();

        frameBuffer.bind();

        if(camera2DCallback != null) {
            camera2DCallback.preRender();
        }

        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().draw();
        }

        if(camera2DCallback != null) {
            camera2DCallback.postRender();
        }

        frameBuffer.unBind();
    }

    @Override
    public void destroy()
    {
        setScene2DMainCamera2D(false);
        frameBuffer.destroy();
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

    public FrameBuffer getFrameBuffer() { return frameBuffer; }
}
