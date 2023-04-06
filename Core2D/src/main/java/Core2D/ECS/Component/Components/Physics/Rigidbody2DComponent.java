package Core2D.ECS.Component.Components.Physics;

import Core2D.Common.Interfaces.NonDuplicated;
import Core2D.ECS.Component.Component;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

public class Rigidbody2DComponent extends Component implements NonDuplicated
{
    public transient Body body;

    public BodyType bodyType = BodyType.STATIC;
    public transient BodyType lastBodyType = BodyType.STATIC;

    public float density = 1.0f;
    //public transient float lastDensity = 0f;

    public float restitution = 0f;
   // public transient float lastRestitution = -1f;

    public float friction = 0.1f;
    //public transient float lastFriction = 0f;

    public boolean sensor = false;
    // обратное значение, чтобы автоматически был обновлен в системе
    //public transient boolean lastSensor = !sensor;

    public boolean fixedRotation = false;
    // обратное значение, чтобы автоматически был обновлен в системе
    public transient boolean lastFixedRotation = !fixedRotation;
}
