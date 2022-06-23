import Core2D.Object2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Scene2D.*;
import Core2D.Controllers.PC.*;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class PlayerController
{
    private Object2D player;
    private Transform playerTransform;

    @InspectorView
    public float movementSpeedX = 650.0f;

    @InspectorView
    public float jumpPower = 600.0f;

    public void update()
    {
        player = SceneManager.getCurrentScene2D().findObject2D("roman");

        if(player != null) {
            playerTransform = player.getComponent(TransformComponent.class).getTransform();
        }
    }
    
    public void deltaUpdate(float deltaTime)
    {
        if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
            playerTransform.translate(new Vector2f(-movementSpeedX * deltaTime, 0.0f));
        }
        if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
            playerTransform.translate(new Vector2f(movementSpeedX * deltaTime, 0.0f));
        }
        if(Keyboard.keyPressed(GLFW.GLFW_KEY_SPACE)) {
            playerTransform.applyLinearImpulse(new Vector2f(0.0f, jumpPower), new Vector2f(playerTransform.getPosition()).add(playerTransform.getCentre()));
        }
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}