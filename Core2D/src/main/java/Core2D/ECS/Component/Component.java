package Core2D.ECS.Component;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;

/**
 * An abstract class that other components inherit.
 */
public class Component
{
    // уровень доступа к ComponentsQueries. если LOCAL, то данный компонент будет добавляться только в те, ComponentsQuery,
    // у которых entityID == entity.ID (смотреть поле entity в это классе далее). если уровень доступа == GLOBAL,
    // то будет добавляться абсолютно во все ComponentsQueries, которые принимают данный тип компонента
    public enum AccessLevelToQueries
    {
        LOCAL,
        GLOBAL
    }

    /**
     * The object to which this component is bound.
     */
    public transient Entity entity;

    private int object2DID = -1;

    public int ID = 0;

    public boolean active = true;

    public AccessLevelToQueries accessLevelToQueries = AccessLevelToQueries.LOCAL;

    public Component() { }

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

    public void collider2DEnter(Entity otherObj)
    {

    }

    public void collider2DExit(Entity otherObj)
    {

    }

    public void render(Camera2DComponent camera2DComponent)
    {

    }

    public void render(Camera2DComponent camera2DComponent, Shader shader)
    {

    }

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
