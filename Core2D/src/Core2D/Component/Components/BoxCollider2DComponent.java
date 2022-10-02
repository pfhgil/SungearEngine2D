package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Physics.Collider2D.BoxCollider2D;
import Core2D.Scene2D.SceneManager;

/**
 * The Box Collider 2D component.
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
     * Applies component parameters to this component.
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
     */
    @Override
    public void init()
    {
        Rigidbody2DComponent rigidbody2DComponent = object2D.getComponent(Rigidbody2DComponent.class);
        if(rigidbody2DComponent != null) {
            if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                SceneManager.currentSceneManager.getCurrentScene2D().getPhysicsWorld().addBoxCollider2D(rigidbody2DComponent.getRigidbody2D(), boxCollider2D);
            }
        }
    }

    public BoxCollider2D getBoxCollider2D() { return boxCollider2D; }
}
