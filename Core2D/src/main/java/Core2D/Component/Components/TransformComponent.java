package Core2D.Component.Components;

import Core2D.CamerasManager.CamerasManager;
import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Component.NonRemovable;
import Core2D.Transform.Transform;
import org.joml.Matrix4f;

/**
 * The TextureComponent. This component is NonDuplicated and NonDuplicated.
 * @see Transform
 * @see NonDuplicated
 * @see NonRemovable
 */
public class TransformComponent extends Component implements NonDuplicated
{
    private Transform transform = new Transform();

    private transient Matrix4f mvpMatrix = new Matrix4f();

    public TransformComponent() { }

    /**
     * Applies the passed transform parameters to the current transform.
     * @param transform Transform.
     */
    public TransformComponent(Transform transform)
    {
        this.transform = new Transform(transform);
    }

    @Override
    public void init()
    {
        transform.init();
    }

    /**
     * Applies component parameters to this component.
     * @see Component#set(Component)
     * @param component TransformComponent.
     */
    @Override
    public void set(Component component)
    {
        if(component instanceof TransformComponent) {
            TransformComponent transformComponent = (TransformComponent) component;

            this.transform.set(transformComponent.getTransform());
        }
    }

    /**
     * @see Component#deltaUpdate(float)
     * @param deltaTime deltaTime.
     */
    @Override
    public void deltaUpdate(float deltaTime)
    {
        transform.update(deltaTime);
    }

    @Override
    public void update()
    {
        Matrix4f modelMatrix = new Matrix4f().set(gameObject.getComponent(TransformComponent.class).getTransform().getResultModelMatrix());

        if(CamerasManager.mainCamera2D != null && !gameObject.isUIElement) {
            Camera2DComponent camera2DComponent = CamerasManager.mainCamera2D.getComponent(Camera2DComponent.class);
            if(camera2DComponent != null) {
                mvpMatrix = new Matrix4f(camera2DComponent.getProjectionMatrix()).mul(camera2DComponent.getViewMatrix())
                        .mul(modelMatrix);
            }
        } else {
            mvpMatrix = new Matrix4f().mul(modelMatrix);
        }
    }

    public Transform getTransform() { return transform; }

    public Matrix4f getMvpMatrix() { return mvpMatrix; }
}
