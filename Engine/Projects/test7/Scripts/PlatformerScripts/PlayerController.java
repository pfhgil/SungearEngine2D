import Core2D.Object2D.*;
import Core2D.Camera2D.*;
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
    @InspectorView
    public Object2D player;

    @InspectorView
    public Camera2D playerCamera;

    @InspectorView
    public float movementSpeedX = 560.0f;

    @InspectorView
    public float jumpPower = 800.0f;

    private boolean canJump = false;

    private boolean rightLook = true;

    public void update()
    {

    }
    
    public void deltaUpdate(float deltaTime)
    {
        //player = SceneManager.getCurrentScene2D().findObject2DByName("roman");

        if(player != null) {
            Transform playerTransform = player.getComponent(TransformComponent.class).getTransform();
            //Systems.out.println("plyer pos: " + playerTransform.getPosition().x + ", " + playerTransform.getPosition().y);

            if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                playerTransform.translate(new Vector2f(movementSpeedX * deltaTime, 0.0f));
                if(!rightLook) {
                    playerTransform.setScale(new Vector2f(-playerTransform.getScale().x, playerTransform.getScale().y));
                    rightLook = true;
                }
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                playerTransform.translate(new Vector2f(-movementSpeedX * deltaTime, 0.0f));
                if(rightLook) {
                    playerTransform.setScale(new Vector2f(-playerTransform.getScale().x, playerTransform.getScale().y));
                    rightLook = false;
                }
            }
            //dfdfdf
            if(Keyboard.keyPressed(GLFW.GLFW_KEY_SPACE) && canJump) {
                playerTransform.applyLinearImpulse(new Vector2f(0.0f, jumpPower), new Vector2f(playerTransform.getPosition()).add(playerTransform.getCentre()));
                canJump = false;
            }

            playerCamera.lerpFollow(playerTransform, new Vector2f(0.1f));

            //playerCamera.getTransform().setPosition(new Vector2f(playerTransform.getPosition()).add(playerTransform.getCentre()).negate());
            //playerCamera.getTransform().moveTo(new Vector2f(playerTransform.getPosition()), new Vector2f(10.0f));
        }
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        if(otherObj != null && !otherObj.isShouldDestroy() && otherObj.getTag().getName().equals("deathSpace")) {
            Object2D spawnPoint = SceneManager.getCurrentScene2D().findObject2DByTag("spawnPoint");
            if(spawnPoint != null) {
                player.getComponent(TransformComponent.class).getTransform().setPosition(spawnPoint.getComponent(TransformComponent.class).getTransform().getPosition());
            }
        } else if(otherObj != null && !otherObj.isShouldDestroy() && otherObj.getTag().getName().equals("default")) {
            canJump = true;
        }
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}