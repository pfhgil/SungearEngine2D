package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Core2D.Core2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera2DComponent extends Component implements NonDuplicated
{
    private Vector2f viewportSize = new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);

    private transient Matrix4f projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

    private transient Matrix4f viewMatrix = new Matrix4f();

    @Override
    public void update()
    {
        updateViewMatrix();
    }

    public void updateViewMatrix()
    {
        TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
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
}
