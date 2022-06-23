import Core2D.Object2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import org.joml.Vector2f;

public class TeleportController
{
    @InspectorView
    public String teleportToName;

    private Object2D teleportToObject;
    private Transform teleportToTransform;

    public void update()
    {
        teleportToObject = SceneManager.getCurrentScene2D().findObject2D("teleportToName");

        if(teleportToObject != null) {
            teleportToTransform = teleportToObject.getComponent(TransformComponent.class).getTransform();
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
                    new Vector2f(resPos.x + 500.0f, resPos.y);
            );
        }
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}