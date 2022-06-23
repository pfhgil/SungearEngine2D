import Core2D.Object2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Scene2D.*;
import Core2D.Controllers.PC.*;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class FlipperController
{
    @InspectorView
    private Object2D flipper;

    @InspectorView
    public float rotationSpeed = -200;

    public void update()
    {
        
    }
    
    public void deltaUpdate(float deltaTime)
    {
        flipper = SceneManager.getCurrentScene2D().findObject2D("flipper");

        if(flipper != null) {
            Transform transform = flipper.getComponent(TransformComponent.class).getTransform();
            transform.rotate(rotationSpeed * deltaTime);
        }
    }
}