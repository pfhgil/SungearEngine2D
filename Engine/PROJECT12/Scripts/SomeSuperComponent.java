import Core2D.ECS.Component.Component;
import Core2D.ECS.Entity;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class SomeSuperComponent extends Component
{
    @Override
    public void update()
    {
        
    }
    
    @Override
    public void deltaUpdate(float deltaTime)
    {
        
    }
    
    public void collider2DEnter(Entity otherObj)
    {
        
    }
    
    public void collider2DExit(Entity otherObj)
    {
        
    }
}