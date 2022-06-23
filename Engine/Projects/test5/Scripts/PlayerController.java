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
    public float movingSpeedX = 400;
    @InspectorView
    public float movingSpeedY = 400;

    @InspectorView
    public float jumpPower = 600;

    public void update()
    {

    }
    
    public void deltaUpdate(float deltaTime)
    {
        player = SceneManager.getCurrentScene2D().findObject2D("roman");

        if(player != null) {
            playerTransform = player.getComponent(TransformComponent.class).getTransform();
            if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                playerTransform.translate(new Vector2f(movingSpeedX * deltaTime, 0.0f));
            } if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                playerTransform.translate(new Vector2f(-movingSpeedX * deltaTime, 0.0f));
            } if(Keyboard.keyReleased(GLFW.GLFW_KEY_SPACE)) {
                playerTransform.applyLinearImpulse(new Vector2f(0.0f, jumpPower), new Vector2f(playerTransform.getPosition().x + playerTransform.getCentre().x, playerTransform.getPosition().y + playerTransform.getCentre().y));
            }
        }
    }

    public void collider2DEnter(Object2D otherObj)
    {
        if(player != null) {
            if (otherObj.getName().equals("tp_point_0")) {
                Object2D tp_point_1 = SceneManager.getCurrentScene2D().findObject2D("tp_point_1");

                Vector2f resPos = tp_point_1.getComponent(TransformComponent.class).getTransform().getPosition();
                playerTransform.setPosition(
                        new Vector2f(resPos.x + 500.0f, resPos.y)
                );
            } else if (otherObj.getName().equals("tp_point_1")) {
                Object2D tp_point_0 = SceneManager.getCurrentScene2D().findObject2D("tp_point_0");

                Vector2f resPos = tp_point_0.getComponent(TransformComponent.class).getTransform().getPosition();
                playerTransform.setPosition(
                        new Vector2f(resPos.x - 500.0f, resPos.y)
                );
            }
        }
    }

    public void collider2DExit(Object2D otherObj)
    {

    }
}