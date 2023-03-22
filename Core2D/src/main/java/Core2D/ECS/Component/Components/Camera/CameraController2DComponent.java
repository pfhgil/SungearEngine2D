package Core2D.ECS.Component.Components.Camera;

import Core2D.ECS.Component.Component;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CameraController2DComponent extends Component
{
    // tmp transformations
    public Vector3f cameraPosition = new Vector3f();
    public Vector3f cameraRotation = new Vector3f();
    public Vector3f cameraScale = new Vector3f(1f);
    // ----------------------------------------------

    // settings -------------------------------------
    public float movementSensitivity = 1.2f;
    public float zoomSensitivity = 0.2f;
}
