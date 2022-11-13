import Core2D.Drawable.*;
import Core2D.Input.PC.*;
import org.lwjgl.glfw.GLFW;
import Core2D.Scene2D.*;
import Core2D.Log.*;
import Core2D.Camera2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Transform.*;
import Core2D.Physics.*;
import org.joml.Vector2f;
import Core2D.Utils.*;

public class SceneController
{
    @InspectorView
    public String levelName = "";

    @InspectorView
    public Object2D movableObject2D;

    @InspectorView
    public Camera2D camera2D; //dfdfddfdsfdfdffgfsdfsdfdfdfdfddf

    @InspectorView
    public AudioComponent fuckYouSound;

    @InspectorView
    public TransformComponent someComponent;

    @InspectorView
    public float speed = 200.0f;

    @InspectorView
    public float impulse = 100.0f;

    public void update()
    {
        if(Keyboard.keyPressed(GLFW.GLFW_KEY_C)) {
            Log.CurrentSession.println("level name: " + levelName, Log.MessageType.INFO);
            SceneManager.currentSceneManager.setCurrentScene2D(SceneManager.currentSceneManager.getScene2D(levelName));
        }

        if(Keyboard.keyPressed(GLFW.GLFW_KEY_U)) {
            if(movableObject2D != null && !movableObject2D.isShouldDestroy()) {
                Rigidbody2D rigidbody2D = movableObject2D.getComponent(Rigidbody2DComponent.class).getRigidbody2D();

                //Log.CurrentSession.println("rigidbody2d: " + rigidbody2D + ", body: " + rigidbody2D.getBody() + ", " + rigidbody2D.getBody().getType(), Log.MessageType.INFO);
            }

            if(fuckYouSound != null) {
                fuckYouSound.audio.play();
            }
        }
    }
    
    public void deltaUpdate(float deltaTime)
    {
        if(movableObject2D != null && !movableObject2D.isShouldDestroy()) {
            Transform transform = movableObject2D.getComponent(TransformComponent.class).getTransform();
            //tgf
            if(Keyboard.keyDown(GLFW.GLFW_KEY_SPACE)) {
                transform.applyLinearImpulse(new Vector2f(0.0f, impulse * deltaTime), new Vector2f());
                //transform.applyTorque(200.0f);
                //transform.applyForce(new Vector2f(0.0f, 10000 * deltaTime), new Vector2f(transform.getCentre()));
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                transform.translate(new Vector2f(speed * deltaTime, 0.0f));
                //transform.applyLinearImpulse(new Vector2f(0.0f, 10000 * deltaTime), new Vector2f(transform.getCentre()));
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                transform.translate(new Vector2f(-speed * deltaTime, 0.0f));
            }

            if(camera2D != null) {
                Vector2f pos = MatrixUtils.getPosition(transform.getResultModelMatrix());
                camera2D.getTransform().setPosition(new Vector2f(pos).negate());
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