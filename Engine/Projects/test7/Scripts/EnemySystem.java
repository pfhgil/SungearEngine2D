import Core2D.Object2D.*;
import Core2D.Scripting.*;
import Core2D.Component.*;
import Core2D.Component.Components.*;
import Core2D.Scene2D.*;
import Core2D.Controllers.PC.*;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class EnemySystem
{
    private Object2D enemy;

    @InspectorView
    public String enemyName;

    @InspectorView
    public float enemyHealth = 100.0f;

    @InspectorView
    public float bulletDamage = 5.0f;

    public void update()
    {
        enemy = SceneManager.getCurrentScene2D().findObject2DByName(enemyName);
    }
    
    public void deltaUpdate(float deltaTime)
    {
        
    }
    
    public void collider2DEnter(Object2D otherObj)
    {
        if(otherObj.getTag().getName().equals("bullet")) {
            if(enemy != null) {
                enemyHealth -= bulletDamage;

                if(enemyHealth <= 0.0f) {
                    enemy.destroy();
                    enemyHealth = 0.0f;
                }
            }
            otherObj.destroy();
        }
    }
    
    public void collider2DExit(Object2D otherObj)
    {
        
    }
}