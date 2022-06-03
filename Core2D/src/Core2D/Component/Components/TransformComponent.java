package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Component.NonRemovable;
import Core2D.Object2D.Transform;

public class TransformComponent extends Component implements NonRemovable, NonDuplicated
{
    private Transform transform;

    public TransformComponent()
    {
        this.transform = new Transform();
    }

    public TransformComponent(Transform transform)
    {
        this.transform = new Transform(transform);
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof TransformComponent) {
            TransformComponent transformComponent = (TransformComponent) component;

            this.transform.set(transformComponent.getTransform());

            transformComponent = null;
        }

        component = null;
    }

    @Override
    public void destroy()
    {
        this.transform.destroy();
        this.transform = null;
    }

    @Override
    public void update(float deltaTime)
    {
        transform.update(deltaTime);
    }

    public Transform getTransform() { return transform; }
}
