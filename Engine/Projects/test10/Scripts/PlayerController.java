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

public class PlayerController
{
    @InspectorView
    public Object2D player;

    @InspectorView
    public Camera2D playerCamera;

    @InspectorView
    public float movementSpeed = 400.0f;

    @InspectorView
    public float jumpForce = 400.0f;

    public void update()
    {
        
    }
    
    public void deltaUpdate(float deltaTime)
    {
        if(player != null) {
            Transform playerTransform = player.getComponent(TransformComponent.class).getTransform();

            if(player != null) {
                playerCamera.follow(playerTransform, deltaTime);
            }

            if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                playerTransform.translate(new Vector2f(movementSpeed * deltaTime, 0.0f));
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                playerTransform.translate(new Vector2f(-movementSpeed * deltaTime, 0.0f));
            }

            if(Keyboard.keyPressed(GLFW.GLFW_KEY_SPACE)) {
                playerTransform.applyLinearImpulse(new Vector2f(0.0f, jumpForce * deltaTime), new Vector2f());
            }
        }
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}