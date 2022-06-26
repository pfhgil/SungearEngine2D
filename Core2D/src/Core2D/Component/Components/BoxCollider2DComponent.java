package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Log.Log;
import Core2D.Physics.Collider2D.BoxCollider2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;

public class BoxCollider2DComponent extends Component implements AutoCloseable
{
    private BoxCollider2D boxCollider2D;

    public BoxCollider2DComponent()
    {
        boxCollider2D = new BoxCollider2D();
    }

    @Override
    public void destroy()
    {
        boxCollider2D.destroy();
        boxCollider2D = null;

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
        if(component instanceof BoxCollider2DComponent) {
            boxCollider2D.set(((BoxCollider2DComponent) component).getBoxCollider2D());
        }
    }

    @Override
    public void init()
    {
        Rigidbody2DComponent rigidbody2DComponent = object2D.getComponent(Rigidbody2DComponent.class);
        if(rigidbody2DComponent != null) {
            if(SceneManager.getCurrentScene2D() != null) {
                SceneManager.getCurrentScene2D().getPhysicsWorld().addBoxCollider2D(rigidbody2DComponent.getRigidbody2D(), boxCollider2D);
            }
        }
    }

    public BoxCollider2D getBoxCollider2D() { return boxCollider2D; }

    @Override
    public void close() throws Exception {

    }
}
