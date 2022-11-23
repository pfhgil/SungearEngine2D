package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Log.Log;
import Core2D.Physics.Collider2D.BoxCollider2D;
import Core2D.Scene2D.SceneManager;

/**
 * The BoxCollider2D component.
 * @see Core2D.Physics.PhysicsWorld
 * @see BoxCollider2D
 * @see Rigidbody2DComponent
 * @see Core2D.Physics.Rigidbody2D
 * @see Component
 */
public class BoxCollider2DComponent extends Component
{
    private BoxCollider2D boxCollider2D = new BoxCollider2D();

    /**
     * Removes BoxCollider2D from the physical world.
     * @see Component#destroy()
     */
    @Override
    public void destroy()
    {
        boxCollider2D.destroy();
    }

    /**
     * Applies component parameters to this component.
     * @see Component#set(Component)
     * @param component BoxCollider2DComponent.
     */
    @Override
    public void set(Component component)
    {
        if(component instanceof BoxCollider2DComponent) {
            boxCollider2D.set(((BoxCollider2DComponent) component).getBoxCollider2D());
        }
    }

    /**
     * Initializes the component.
     * Adds BoxCollider2D to the physical world if the Object2D to which this component is bound has a Rigidbody2DComponent,
     * and the current scene is not null (set).
     * @see Component#init()
     */
    @Override
    public void init()
    {
        Rigidbody2DComponent rigidbody2DComponent = gameObject.getComponent(Rigidbody2DComponent.class);
        if(rigidbody2DComponent != null && rigidbody2DComponent.getRigidbody2D().getScene2D() != null) {
            rigidbody2DComponent.getRigidbody2D().getScene2D().getPhysicsWorld().addBoxCollider2D(rigidbody2DComponent.getRigidbody2D(), boxCollider2D);
            Log.CurrentSession.println("Box collider 2d added!", Log.MessageType.ERROR);
        }
    }

    public BoxCollider2D getBoxCollider2D() { return boxCollider2D; }
}
