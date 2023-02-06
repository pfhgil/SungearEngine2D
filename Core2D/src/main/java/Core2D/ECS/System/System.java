package Core2D.ECS.System;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;

public class System
{
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

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
