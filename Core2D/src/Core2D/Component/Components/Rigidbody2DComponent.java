package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Log.Log;
import Core2D.Physics.Rigidbody2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;

public class Rigidbody2DComponent extends Component implements NonDuplicated
{
    private Rigidbody2D rigidbody2D;

    public Rigidbody2DComponent()
    {
        rigidbody2D = new Rigidbody2D();
    }

    @Override
    public void destroy()
    {
        rigidbody2D.destroy();
        if(object2D.getComponent(TransformComponent.class) != null) {
            object2D.getComponent(TransformComponent.class).getTransform().setRigidbody2D(null);
        }
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof Rigidbody2DComponent) {
            rigidbody2D.set(((Rigidbody2DComponent) component).getRigidbody2D());
        }
    }

    @Override
    public void init()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().getPhysicsWorld().addRigidbody2D(object2D);
        }
        object2D.getComponent(TransformComponent.class).getTransform().setRigidbody2D(this.getRigidbody2D());
    }

    public Rigidbody2D getRigidbody2D() { return rigidbody2D; }
}
