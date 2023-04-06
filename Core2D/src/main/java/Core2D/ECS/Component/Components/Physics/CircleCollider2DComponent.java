package Core2D.ECS.Component.Components.Physics;

public class CircleCollider2DComponent extends Collider2DComponent
{
    public float radius = 50f;
    public transient float lastRadius = 0f;
}
