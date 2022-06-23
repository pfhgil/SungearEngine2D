import Core2D.Object2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Scene2D.*;
import Core2D.Controllers.PC.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class PlayerController
{
    private Object2D player;

    @InspectorView
    public float movementSpeedX = 560.0f;

    @InspectorView
    public float jumpPower = 800.0f;

    //dfsd

    public void update()
    {
        
    }
    
    public void deltaUpdate(float deltaTime)
    {
        player = SceneManager.getCurrentScene2D().findObject2DByName("roman");

        if(player != null) {
            Transform playerTransform = player.getComponent(TransformComponent.class).getTransform();

            if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                playerTransform.translate(new Vector2f(movementSpeedX * deltaTime, 0.0f));
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                playerTransform.translate(new Vector2f(-movementSpeedX * deltaTime, 0.0f));
            }
            if(Keyboard.keyPressed(GLFW.GLFW_KEY_SPACE)) {
                playerTransform.applyLinearImpulse(new Vector2f(0.0f, jumpPower), new Vector2f(playerTransform.getPosition()).add(playerTransform.getCentre()));
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