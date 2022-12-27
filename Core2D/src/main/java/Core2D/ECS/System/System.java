package Core2D.ECS.System;

import Core2D.ECS.Entity;

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

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
