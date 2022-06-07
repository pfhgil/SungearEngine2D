package Core2D.Component;

import Core2D.Object2D.Object2D;

public abstract class Component
{
    private transient Object2D object2D;

    public void set(Component component)
    {

    }

    public void destroy()
    {

    }

    protected void update(float deltaTime)
    {

    }

    public void init()
    {

    }

    public Object2D getObject2D() { return object2D; }
    public void setObject2D(Object2D object2D) { this.object2D = object2D; }
}
