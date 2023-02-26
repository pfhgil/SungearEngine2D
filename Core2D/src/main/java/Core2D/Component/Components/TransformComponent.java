package Core2D.Component.Components;

<<<<<<< Updated upstream:Core2D/src/main/java/Core2D/Component/Components/TransformComponent.java
import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Component.NonRemovable;
=======
import Core2D.ECS.Component.Component;
import Core2D.ECS.NonDuplicated;
import Core2D.ECS.NonRemovable;
>>>>>>> Stashed changes:Core2D/src/main/java/Core2D/ECS/Component/Components/TransformComponent.java
import Core2D.Transform.Transform;

/**
 * The TextureComponent. This component is NonDuplicated and NonDuplicated.
 * @see Transform
 * @see NonDuplicated
 * @see NonRemovable
 */
public class TransformComponent extends Component implements NonRemovable, NonDuplicated
{
    private Transform transform = new Transform();

    public TransformComponent() { }

    /**
     * Applies the passed transform parameters to the current transform.
     * @param transform Transform.
     */
    public TransformComponent(Transform transform)
    {
        this.transform = new Transform(transform);
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

    public Transform getTransform() { return transform; }
<<<<<<< Updated upstream:Core2D/src/main/java/Core2D/Component/Components/TransformComponent.java
=======

    public Matrix4f getMvpMatrix(Camera2DComponent camera2DComponent)
    {
        transform.updateModelMatrix();
        return new Matrix4f(camera2DComponent.getProjectionMatrix()).mul(camera2DComponent.getViewMatrix())
            .mul(transform.getGlobalModelMatrix());
    }
>>>>>>> Stashed changes:Core2D/src/main/java/Core2D/ECS/Component/Components/TransformComponent.java
}
