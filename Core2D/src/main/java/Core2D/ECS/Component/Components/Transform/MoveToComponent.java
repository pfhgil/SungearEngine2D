
package Core2D.ECS.Component.Components.Transform;

import Core2D.ECS.Component.Component;
import org.joml.Vector2f;

public class MoveToComponent extends Component
{
    public boolean needMoveTo = false;
    public Vector2f destinationPosition = new Vector2f();
    public float ration = 10f;

    // погрешность
    public float errorRate = 0.01f;
}
