import Core2D.Object2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Scene2D.*;
import org.joml.Vector2f;

public class TeleportController
{
    private Object2D teleportTo;
    private Transform teleportToTransform;

    @InspectorView
    public String teleportNameTo = "";

    public void update()
    {
        System.out.println(teleportNameTo);
        teleportTo = SceneManager.getCurrentScene2D().findObject2D(teleportNameTo);

        if(teleportTo != null) {
            teleportToTransform = teleportTo.getComponent(TransformComponent.class).getTransform();
        }
    }
    
    public void deltaUpdate(float deltaTime)
    {

    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        if(teleportToTransform != null) {
            Vector2f resPos = teleportToTransform.getPosition();

            otherObj.getComponent(TransformComponent.class).getTransform().setPosition(
                    new Vector2f(resPos.x + 500.0f, resPos.y)
            );
        }
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}