import Core2D.Object2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Scene2D.*;
import Core2D.Controllers.PC.*;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import Core2D.Physics.Collider2D.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class PetSystem
{
    private boolean firstTime = true;

    private Object2D petObject;
    private Object2D playerObject;

    private Object2D lastSeenObject2D;

    private int seenObjectsNum = 0;

    @InspectorView
    public float reloadTime = 0.1f;

    @InspectorView
    public float bulletSpeed = 10000.0f;

    private Timer petShootTimer;

    public void update()
    {
        if(firstTime) {
            petShootTimer = new Timer(reloadTime, true);
            petShootTimer.getTimerCallbacks().add(new TimerCallback() {
                @Override
                public void deltaUpdate(float v) {
                }

                @Override
                public void update() {
                    if(petObject != null && lastSeenObject2D != null && !lastSeenObject2D.isShouldDestroy()) {
                        Object2D bullet = Object2D.instantiate();

                        BoxCollider2DComponent boxCollider2DComponent = new BoxCollider2DComponent();
                        boxCollider2DComponent.getBoxCollider2D().setScale(new Vector2f(0.2f, 0.1f));

                        bullet.addComponent(new Rigidbody2DComponent());
                        bullet.addComponent(boxCollider2DComponent);

                        Transform bulletTransform = bullet.getComponent(TransformComponent.class).getTransform();
                        Transform petTransform = petObject.getComponent(TransformComponent.class).getTransform();
                        Transform lastSeenObjectTransform = lastSeenObject2D.getComponent(TransformComponent.class).getTransform();

                        CircleCollider2D circleCollider2D = petObject.getComponent(CircleCollider2DComponent.class).getCircleCollider2D();

                        Vector2f dif = new Vector2f(petTransform.getPosition()).add(new Vector2f(lastSeenObjectTransform.getPosition()).negate());
                        Vector2f multiplier = new Vector2f(-dif.x / circleCollider2D.getRadius(), -dif.y / circleCollider2D.getRadius());

                        bulletTransform.setScale(new Vector2f(0.2f, 0.1f));
                        bulletTransform.setRotation(petTransform.getRotation());
                        bulletTransform.setPosition(new Vector2f(new Vector2f(petTransform.getPosition().x, petTransform.getPosition().y)));
                        bulletTransform.applyLinearImpulse(new Vector2f(bulletSpeed * multiplier.x, bulletSpeed * multiplier.y), new Vector2f(bulletTransform.getPosition()).add(bulletTransform.getCentre()));

                        bullet.setTag("bullet");
                        bullet.setColor(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
                    }
                }
            });
            petShootTimer.start();

            firstTime = false;
        }

        petShootTimer.startFrame();

        petObject = SceneManager.getCurrentScene2D().findObject2DByName("pet");
        playerObject = SceneManager.getCurrentScene2D().findObject2DByName("roman");
    }
    
    public void deltaUpdate(float deltaTime)
    {
        if(petObject != null && playerObject != null) {
            if(lastSeenObject2D != null && !lastSeenObject2D.isShouldDestroy()) {
                petObject.getComponent(TransformComponent.class).getTransform().lookAt(lastSeenObject2D.getComponent(TransformComponent.class).getTransform().getPosition());
            } else {
                petObject.getComponent(TransformComponent.class).getTransform().lookAt(
                        new Vector2f(playerObject.getComponent(TransformComponent.class).getTransform().getPosition())
                                .add(playerObject.getComponent(TransformComponent.class).getTransform().getCentre())
                );
            }
            petObject.getComponent(TransformComponent.class).getTransform().moveTo(playerObject.getComponent(TransformComponent.class).getTransform().getPosition(), new Vector2f(1.0f, 1.0f));
        }
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        if(otherObj != null && !otherObj.isShouldDestroy() && otherObj.getTag().getName().equals("enemy")) {
            lastSeenObject2D = otherObj;

            seenObjectsNum++;
        }
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        if(otherObj != null && !otherObj.isShouldDestroy() && otherObj.getTag().getName().equals("enemy")) {
            seenObjectsNum--;
            if (seenObjectsNum <= 0) {
                seenObjectsNum = 0;
                lastSeenObject2D = null;
            }
        }
    }
}