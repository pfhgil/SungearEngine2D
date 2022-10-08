package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Log.Log;
import Core2D.Physics.Collider2D.CircleCollider2D;
import Core2D.Physics.Rigidbody2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;

/**
 * The Rigidbody2D component.
 * @see Core2D.Physics.PhysicsWorld
 * @see Rigidbody2D
 * @see BoxCollider2DComponent
 * @see CircleCollider2DComponent
 * @see Component
 */
public class Rigidbody2DComponent extends Component implements NonDuplicated
{
    private Rigidbody2D rigidbody2D = new Rigidbody2D();

    /**
     * Removes Rigidbody2D from the physical world.
     * @see Component#destroy()
     */
    @Override
    public void destroy()
    {
        rigidbody2D.destroy();
        if(object2D.getComponent(TransformComponent.class) != null) {
            object2D.getComponent(TransformComponent.class).getTransform().setRigidbody2D(null);
        }
    }

    /**
     * Applies component parameters to this component.
     * @see Component#set(Component)
     * @param component Rigidbody2DComponent.
     */
    @Override
    public void set(Component component)
    {
        if(component instanceof Rigidbody2DComponent) {
            rigidbody2D.set(((Rigidbody2DComponent) component).getRigidbody2D());
        }
    }

    /**
     * Initializes the component.
     * Adds Rigidbody2D to the physical world if the current scene is not null (set).
     * Sets this Rigidbody2D for the TransformComponent of the object to which this Rigidbody2DComponent is bound.
     * @see Component#init()
     * @see TransformComponent
     */
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
