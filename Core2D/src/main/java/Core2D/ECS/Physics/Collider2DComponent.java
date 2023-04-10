package Core2D.ECS.Physics;

import Core2D.ECS.Component;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class Collider2DComponent extends Component
{
    public transient Fixture fixture;

    public Vector2f offset = new Vector2f();
    protected transient Vector2f lastOffset = new Vector2f(offset);
    public float angle = 0f;
    protected transient float lastAngle = angle;

    // -------------------------------------------------
    public float density = 1.0f;
    protected transient float lastDensity = 0f;
    public boolean followRigidbody2DDensity = true;

    public float restitution = 0.0f;
    protected transient float lastRestitution = -1f;
    public boolean followRigidbody2DRestitution = true;

    public float friction = 0.1f;
    protected transient float lastFriction = 0f;
    public boolean followRigidbody2DFriction = true;

    public boolean sensor = false;
    protected transient boolean lastSensor = !sensor;
    public boolean followRigidbody2DSensor = true;

    public boolean followTransformScale = true;
}
