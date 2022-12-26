package Core2D.ECS.Component;

import Core2D.ECS.Entity;

/**
 * An abstract class that other components inherit.
 */
public class Component
{
    /**
     * The object to which this component is bound.
     */
    public transient Entity entity;

    private int object2DID = -1;

    public int componentID = 0;

    protected boolean active = true;

    public Component() {}

    /**
     * Sets the parameters of the transmitted component for this component.
     * @param component Component.
     */
    public void set(Component component)
    {

    }

    /**
     * Deletes a component. In order to completely remove a component from an object, use object.removeComponent(...).
     * @see Entity#removeFirstComponent(Class)
     */
    public void destroy()
    {

    }

    /**
     * Updates the component. For actions with transformations, use deltaUpdate(...).
     * @see Component#deltaUpdate(float)
     */
    public void update()
    {
        if(entity != null) {
            object2DID = entity.ID;
        }
    }

    /**
     * The deltaUpdate method provides the same conversion rates for object transform.
     * You need to multiply the transformation conversion metric by deltaTime.
     * @param deltaTime The time elapsed between the past and the current frame.
     */
    public void deltaUpdate(float deltaTime)
    {

    }

    /**
     * Initializes the component.
     */
    public void init()
    {

    }

    public boolean isActive() { return active; }

    /**
     * Sets whether the object is active.
     * @param active
     */
    public void setActive(boolean active) { this.active = active; }

    public int getObject2DID() { return object2DID; }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for(Component component : entity.getComponents()) {
            if(component.getClass().isAssignableFrom(componentClass)) {
                return componentClass.cast(component);
            }
        }

        return null;
    }
}
