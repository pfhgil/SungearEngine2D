package Core2D.ECS.System;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;

public class System
{
    protected boolean active = true;

    public System() {}

    public void update(ComponentsQuery componentsQuery)
    {

    }

    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
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

    public void renderEntity(Entity entity, Camera2DComponent camera2DComponent)
    {

    }

    public void renderEntity(Entity entity, Camera2DComponent camera2DComponent, Shader shader)
    {

    }

    //public void getComponent

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
