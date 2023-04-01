package Core2D.ECS.Component.Components.Physics;

import org.joml.Vector2f;

public class BoxCollider2DComponent extends Collider2DComponent
{
    public Vector2f scale = new Vector2f(1f);
    public transient Vector2f lastScale = new Vector2f(scale);
}
