package Core2D.ECS.System;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Entity;
import Core2D.ECS.NonDuplicated;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class System
{
    protected transient List<ComponentsQuery> componentsQueries = new ArrayList<>();

    protected boolean active = true;

    // список классов компонентов, которые может принимать
    protected List<Class<?>> componentsClasses = new ArrayList<>();

    public System() {}

    public void update()
    {

    }

    public void deltaUpdate(float deltaTime)
    {

    }

    // срабатывает при добавлении компонента в систему
    public void initComponentOnAdd(Component component)
    {

    }

    // удаляет компонент
    public void destroyComponent(Component component)
    {

    }

    public void collider2DEnter(Entity otherObj)
    {

    }

    public void collider2DExit(Entity otherObj)
    {

    }

    public void renderEntity(Entity entity)
    {

    }

    public void renderEntity(Entity entity, Shader shader)
    {

    }

    // добавление компонента для обработки
    public void addComponent(Component component)
    {
        if(!componentsClasses.contains(component.getClass())) return;

        if(component.entity == null) {
            Log.CurrentSession.println("Value of field 'entity' in component '" + component + "' is equals null", Log.MessageType.ERROR, true);
            return;
        }

        //if(componentsClasses.)

        boolean foundQuery = false;
        for(ComponentsQuery componentsQuery : componentsQueries) {
            if(componentsQuery.entityID == component.entity.ID || component.accessLevelToQueries == Component.AccessLevelToQueries.GLOBAL) {
                if(component instanceof NonDuplicated) {
                    Optional<Component> foundComponent = componentsQuery.getComponents().stream().filter(c -> c.getClass().equals(component.getClass())).findAny();
                    if(foundComponent.isPresent()) {
                        Log.CurrentSession.println("Component '" + component + "' already exists in ComponentsQuery with 'entityID' == "
                                + componentsQuery.entityID + ". Component '" + component + "' is NonDuplicated", Log.MessageType.ERROR, true);
                        return;
                    }
                } else {
                    componentsQuery.getComponents().add(component);
                    foundQuery = true;
                    Log.CurrentSession.println("adding component: " + component + ". entity id: " + component.entity.ID + ". system: " + this, Log.MessageType.WARNING);
                }
            }
        }

        if(!foundQuery) {
            ComponentsQuery componentsQuery = new ComponentsQuery(component.entity.ID);
            if(componentsQuery.entityID == component.entity.ID || component.accessLevelToQueries == Component.AccessLevelToQueries.GLOBAL) {
                componentsQuery.getComponents().add(component);
            }
            componentsQueries.add(componentsQuery);
            Log.CurrentSession.println("created query. adding component: " + component + ". entity id: " + component.entity.ID + ". system: " + this, Log.MessageType.WARNING);
        }

        initComponentOnAdd(component);
    }

    // удаляет компонент из обработки
    public void removeComponent(Component component)
    {
        componentsQueries.forEach(componentsQuery -> {
            if(componentsQuery.entityID == component.entity.ID) {
                componentsQuery.getComponents().remove(component);
            }
        });

        destroyComponent(component);
    }

    public ComponentsQuery findComponentsQuery(int entityID)
    {
        return componentsQueries.stream().filter(componentsQuery -> componentsQuery.entityID == entityID).findAny().orElse(null);
    }

    //public void getComponent

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
