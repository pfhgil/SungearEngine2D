package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Physics.Collider2D.CircleCollider2D;
import Core2D.Scene2D.SceneManager;

/**
 * The CircleCollider2D component.
 * @see Core2D.Physics.PhysicsWorld
 * @see CircleCollider2D
 * @see Rigidbody2DComponent
 * @see Core2D.Physics.Rigidbody2D
 * @see Component
 */
public class CircleCollider2DComponent extends Component
{
    private CircleCollider2D circleCollider2D = new CircleCollider2D();

    /**
     * Removes CircleCollider2D from the physical world.
     * @see Component#destroy()
     */
    @Override
    public void destroy()
    {
        circleCollider2D.destroy();
    }

    /**
     * Applies component parameters to this component.
     * @see Component#set(Component)
     * @param component CircleCollider2DComponent.
     */
    @Override
    public void set(Component component)
    {
        if(component instanceof CircleCollider2DComponent) {
            circleCollider2D.set(((CircleCollider2DComponent) component).getCircleCollider2D());
        }
    }

    /**
     * Initializes the component.
     * Adds CircleCollider2D to the physical world if the Object2D to which this component is bound has a Rigidbody2DComponent,
     * and the current scene is not null (set).
     * @see Component#init()
     */
    @Override
    public void init()
    {
        Rigidbody2DComponent rigidbody2DComponent = gameObject.getComponent(Rigidbody2DComponent.class);
        if(rigidbody2DComponent != null) {
            SceneManager.currentSceneManager.getTmpPhysicsWorld().addCircleCollider2D(rigidbody2DComponent.getRigidbody2D(), circleCollider2D);
        }
    }

    public CircleCollider2D getCircleCollider2D() { return circleCollider2D; }
}
