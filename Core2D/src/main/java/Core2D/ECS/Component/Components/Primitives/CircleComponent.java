package Core2D.ECS.Component.Components.Primitives;

import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Component;
import Core2D.Utils.MathUtils;
import org.joml.Vector2f;

public class CircleComponent extends PrimitiveComponent
{
    public float radius = 10.0f;
    public transient float lastRadius = 0f;

    public int angleIncrement = 10;
    public transient int lastAngleIncrement = 0;
}
