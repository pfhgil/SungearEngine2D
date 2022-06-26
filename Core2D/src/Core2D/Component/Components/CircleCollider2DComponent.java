package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Log.Log;
import Core2D.Physics.Collider2D.CircleCollider2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;

public class CircleCollider2DComponent extends Component implements AutoCloseable
{
    private CircleCollider2D circleCollider2D;

    public CircleCollider2DComponent() { circleCollider2D = new CircleCollider2D(); }

    @Override
    public void destroy()
    {
        circleCollider2D.destroy();
        circleCollider2D = null;

        object2D = null;

        try {
            close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof CircleCollider2DComponent) {
            circleCollider2D.set(((CircleCollider2DComponent) component).getCircleCollider2D());
        }
    }

    @Override
    public void init()
    {
        Rigidbody2DComponent rigidbody2DComponent = object2D.getComponent(Rigidbody2DComponent.class);
        if(rigidbody2DComponent != null) {
            if(SceneManager.getCurrentScene2D() != null) {
                SceneManager.getCurrentScene2D().getPhysicsWorld().addCircleCollider2D(rigidbody2DComponent.getRigidbody2D(), circleCollider2D);
            }
        }
    }

    public CircleCollider2D getCircleCollider2D() { return circleCollider2D; }

    @Override
    public void close() throws Exception {

    }
}
