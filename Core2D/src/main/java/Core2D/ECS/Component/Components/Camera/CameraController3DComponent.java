package Core2D.ECS.Component.Components.Camera;

import Core2D.ECS.Component.Component;
import org.joml.Vector3f;

public class CameraController3DComponent extends Component
{
    // tmp transformations
    public Vector3f cameraPosition = new Vector3f();
    public Vector3f cameraRotation = new Vector3f();
    public Vector3f cameraScale = new Vector3f(1f);
    // ----------------------------------------------

    // settings -------------------------------------
    public float horizontalMovementSpeed = 750f;
    public float forwardMovementSpeed = 1750f;
    public float verticalMovementSpeed = 750f;

    public float rotationSensitivity = 0.1f;

    public float acceleratingMultiplier = 2.5f;

    // flags ----------------------------------------
    //public boolean allowMouseRotation = false;
}
