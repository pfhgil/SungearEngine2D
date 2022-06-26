package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Component.NonRemovable;
import Core2D.Log.Log;
import Core2D.Object2D.Transform;
import Core2D.Utils.ExceptionsUtils;

public class TransformComponent extends Component implements NonRemovable, NonDuplicated, AutoCloseable
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
        transform.destroy();
        transform = null;

        object2D = null;


        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        transform.update(deltaTime);
    }

    public Transform getTransform() { return transform; }

    @Override
    public void close() throws Exception {

    }
}
