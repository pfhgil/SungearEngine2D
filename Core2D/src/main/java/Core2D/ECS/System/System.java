package Core2D.ECS.System;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;

import java.util.ArrayList;
import java.util.List;

public class System
{
    private transient List<ComponentsQuery> componentsQueries = new ArrayList<>();


    public transient Entity entity;

    protected boolean active = true;

    public System() {}

    public void destroy()
    {

    }

    public void update()
    {

    }

    public void deltaUpdate(float deltaTime)
    {

    }

    public void initComponents()
    {

    }

    public void initSystem()
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

    // добавление компонента для обработки
    public void addComponent(Component component)
    {
        componentsQueries.forEach(componentsQuery -> {
            if(componentsQuery.entityID == component.entity.ID) {
                componentsQuery.getComponents().add(component);
            }
        });
    }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public List<ComponentsQuery> getComponentsQueries() { return componentsQueries; }
}
