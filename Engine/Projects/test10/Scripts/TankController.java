import Core2D.Core2D.Core2D;
import Core2D.Object2D.*;
import Core2D.Camera2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Scene2D.*;
import Core2D.Controllers.PC.*;
import Core2D.Utils.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

//fdfdffsd
public class TankController
{
    @InspectorView
    public Object2D tankHull;

    @InspectorView
    public Object2D tankTower;

    @InspectorView
    public Object2D crosshair;

    @InspectorView
    public Camera2D playerCamera;

    @InspectorView
    public float forwardSpeed = 400;

    @InspectorView
    public float backSpeed = 75;

    @InspectorView
    public float rotationSpeed = 60;

    @InspectorView
    public float towerRotationSpeed = 45.0f;

    private Vector2f currentSpeed = new Vector2f();

    private float originalRollingFriction = 0.1f;
    private float originalSlidingFriction = 0.75f;

    private float currentRollingFriction = 0.1f;
    private float currentSlidingFriction = 0.75f;

    private float rotation = 0.0f;

    public void update()
    {
        
    }
    
    public void deltaUpdate(float deltaTime)
    {
        if(tankHull != null) {
            Transform playerTransform = tankHull.getComponent(TransformComponent.class).getTransform();

            playerCamera.follow(playerTransform);

            float m = 0.0f;

            float speedAngle = playerTransform.getRotationOfLookAt(currentSpeed);

            rotation = 0.0f;

            if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                rotation = -rotationSpeed * deltaTime;
                playerTransform.rotate(rotation);
                //playerCamera.getTransform().rotate(rotationSpeed * deltaTime);
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                rotation = rotationSpeed * deltaTime;
                playerTransform.rotate(rotation);
                //playerCamera.getTransform().rotate(-rotationSpeed * deltaTime);
            }

            boolean someControlButtonDown = false;

            if(Keyboard.keyDown(GLFW.GLFW_KEY_W)) {
                currentSpeed.add(new Vector2f(-15.0f, 0.0f));
                someControlButtonDown = true;
            } else if(Keyboard.keyDown(GLFW.GLFW_KEY_S)) {
                currentSpeed.add(new Vector2f(15.0f, 0.0f));
                someControlButtonDown = true;
            }

            if(Keyboard.keyDown(GLFW.GLFW_KEY_SPACE)) {
                currentRollingFriction = originalSlidingFriction;
            } else {
                currentRollingFriction = originalRollingFriction;
                currentSlidingFriction = originalSlidingFriction;
            }

            m = mix(currentRollingFriction, currentSlidingFriction, (float) Math.abs(Math.sin(speedAngle - playerTransform.getRotation())));

            if(m >= originalSlidingFriction) {
                currentSpeed.y += m;
                if(rotation > 0.0f) {
                    playerTransform.rotate(m);
                } else if(rotation < 0.0f) {
                    playerTransform.rotate(-m);
                }
            }

            System.out.println(m);

            if(currentSpeed.x < -forwardSpeed) {
                currentSpeed.x = -forwardSpeed;
            } else if(currentSpeed.x > backSpeed) {
                currentSpeed.x = backSpeed;
            }

            if(currentSpeed.y < -forwardSpeed) {
                currentSpeed.y = -forwardSpeed;
            } else if(currentSpeed.y > backSpeed) {
                currentSpeed.y = backSpeed;
            }

            //dfdf
            if(currentSpeed.x < 0.0f) {
                currentSpeed.x += m * 10.0f;
            } else if(currentSpeed.x > 0.0f) {
                currentSpeed.x -= m * 10.0f;
            }

            if(currentSpeed.y < 0.0f) {
                currentSpeed.y += m * 10.0f;
            } else if(currentSpeed.y > 0.0f) {
                currentSpeed.y -= m * 10.0f;
            }

            playerTransform.translateInRotationDirection(new Vector2f(currentSpeed.x * deltaTime, currentSpeed.y * deltaTime));

            Vector2f oglPos = Mouse.getMouseOGLPosition(Mouse.getMousePosition());

            if(tankTower != null) {
                Transform towerTransform = tankTower.getComponent(TransformComponent.class).getTransform();
                towerTransform.lerpLookAt(new Vector2f(oglPos.x, oglPos.y), towerRotationSpeed * deltaTime);
            }

            if(crosshair != null) {
                Transform crosshairTransform = crosshair.getComponent(TransformComponent.class).getTransform();
                crosshairTransform.setPosition(oglPos);
            }
        }
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }

    public float mix(float x, float y, float a)
    {
        return x * (1 - a) + y * a;
    }
}