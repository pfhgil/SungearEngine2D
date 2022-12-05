package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Physics.Rigidbody2D;
import Core2D.Scene2D.SceneManager;

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

    public Rigidbody2DComponent()
    {
        create();
    }

    @Override
    protected void create()
    {
        if(SceneManager.currentSceneManager != null) {
            rigidbody2D.setScene2D(SceneManager.currentSceneManager.getCurrentScene2D());
        }
    }

    /**
     * Removes Rigidbody2D from the physical world.
     * @see Component#destroy()
     */
    @Override
    public void destroy()
    {
        rigidbody2D.destroy();
        if(gameObject.getComponent(TransformComponent.class) != null) {
            gameObject.getComponent(TransformComponent.class).getTransform().setRigidbody2D(null);
        }
    }

    /**
     * Applies component parameters to this component.
     * @see Component#set(Component)
     * @param component Rigidbody2DComponent.
     */
    @Override
    public Rigidbody2DComponent set(Component component)
    {
        if(component instanceof Rigidbody2DComponent) {
            rigidbody2D.set(((Rigidbody2DComponent) component).getRigidbody2D());
        }

        return this;
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
        //SceneManager.currentSceneManager.getTmpPhysicsWorld().addRigidbody2D(gameObject);
        if(rigidbody2D.getScene2D() != null) {
            System.out.println("heeeh !!!");
            rigidbody2D.getScene2D().getPhysicsWorld().addRigidbody2D(gameObject, rigidbody2D.getScene2D());
        }
        gameObject.getComponent(TransformComponent.class).getTransform().setRigidbody2D(rigidbody2D);
    }

    public Rigidbody2D getRigidbody2D() { return rigidbody2D; }
}
