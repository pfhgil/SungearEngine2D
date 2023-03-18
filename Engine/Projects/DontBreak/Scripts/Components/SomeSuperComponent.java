package sungear.project.test12.Scripts.Components;

import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.Scripting.*;
import Core2D.Log.*;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class SomeSuperComponent extends Component
{
    @Override
    public void update()
    {
        //Log.CurrentSession.println("NO!!", Log.MessageType.WARNING);
    }
    
    @Override
    public void deltaUpdate(float deltaTime)
    {
        //Log.CurrentSession.println("NO!!", Log.MessageType.WARNING);
    }
    
    public void collider2DEnter(Entity otherObj)
    {
        
    }
    
    public void collider2DExit(Entity otherObj)
    {
        
    }
}