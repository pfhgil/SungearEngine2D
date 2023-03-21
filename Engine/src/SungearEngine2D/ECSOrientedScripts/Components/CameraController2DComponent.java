package SungearEngine2D.ECSOrientedScripts.Components;

import Core2D.ECS.Component.Component;
import org.joml.Vector2f;

public class CameraController2DComponent extends Component
{
    public float movementSensitivity = 1.2f;
    public float zoomSensitivity = 0.2f;
    public Vector2f scale = new Vector2f(1f);
}
