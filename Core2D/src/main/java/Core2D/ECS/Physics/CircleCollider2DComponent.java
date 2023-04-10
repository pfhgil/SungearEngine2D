package Core2D.ECS.Physics;

public class CircleCollider2DComponent extends Collider2DComponent
{
    public float radius = 50f;
    protected transient float lastRadius = 0f;
}
