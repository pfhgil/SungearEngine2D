import Core2D.Object2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Scene2D.*;
import Core2D.Controllers.PC.*;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class TeleportSystem
{
    private Object2D teleportObjectTo;

    @InspectorView
    public String teleportNameTo = "";

    public void update()
    {
        teleportObjectTo = SceneManager.getCurrentScene2D().findObject2DByName(teleportNameTo);
    }
    
    public void deltaUpdate(float deltaTime)
    {
        
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        if(teleportObjectTo != null) {
            Vector2f teleportToPos = teleportObjectTo.getComponent(TransformComponent.class).getTransform().getPosition();

            Vector2f resPos = new Vector2f(teleportToPos.x + 500.0f, teleportToPos.y);

            otherObj.getComponent(TransformComponent.class).getTransform().setPosition(resPos);
        }
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}