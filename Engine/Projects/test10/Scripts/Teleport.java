import Core2D.Object2D.*;
import Core2D.Scene2D.*;

public class Teleport
{
    public void update()
    {
        
    }
    
    public void deltaUpdate(float deltaTime)
    {
        
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        if(otherObj.getTag().getName().equals("player")) {
            //dsfdfdf
            SceneManager.currentSceneManager.setCurrentScene2D("lvlw");
        }
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}