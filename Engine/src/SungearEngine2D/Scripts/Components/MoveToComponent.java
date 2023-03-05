package SungearEngine2D.Scripts.Components;

import Core2D.ECS.Component.Component;
import org.joml.Vector2f;

public class MoveToComponent extends Component
{
    public boolean needMoveTo = false;
    public Vector2f destinationPosition = new Vector2f();
    public float ration = 0.1f;

    public float errorRate = 0.01f;
}
