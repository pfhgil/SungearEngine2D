package Core2D.Settings;

import org.jbox2d.common.Vec2;

public class PhysicsSettings
{
    public static Vec2 gravity = new Vec2(0f, -2f);

    public static int velocityIterations = 6;
    public static int positionIterations = 2;

    public static float velocityThreshold = 0f;

    public static float ratio = 30f;
}
