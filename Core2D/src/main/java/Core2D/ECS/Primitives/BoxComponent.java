package Core2D.ECS.Primitives;

import org.joml.Vector2f;

public class BoxComponent extends PrimitiveComponent
{
    public Vector2f size = new Vector2f(100.0f, 100.0f);
    protected transient Vector2f lastSize = new Vector2f();
}
