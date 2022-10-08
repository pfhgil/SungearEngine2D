import Core2D.Drawable.*;
import Core2D.Input.PC.*;
import org.lwjgl.glfw.GLFW;
import Core2D.Scene2D.*;
import Core2D.Scripting.*;
import Core2D.Log.*;

public class SceneController
{
    @InspectorView
    public String levelName = "";

    public void update()
    {
        //d
        if(Keyboard.keyPressed(GLFW.GLFW_KEY_C)) {
            Log.CurrentSession.println("level name: " + levelName, Log.MessageType.INFO);
            SceneManager.currentSceneManager.setCurrentScene2D(SceneManager.currentSceneManager.getScene2D(levelName));
        }
    }
    
    public void deltaUpdate(float deltaTime)
    {
        
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}