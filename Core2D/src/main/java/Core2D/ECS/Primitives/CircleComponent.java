package Core2D.ECS.Primitives;

public class CircleComponent extends PrimitiveComponent
{
    public float radius = 50.0f;
    protected transient float lastRadius = 0f;

    public int angleIncrement = 10;
    protected transient int lastAngleIncrement = 0;
}
