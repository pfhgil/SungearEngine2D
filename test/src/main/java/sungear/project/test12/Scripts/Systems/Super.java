package sungear.project.test12.Scripts.Systems;

import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Log.Log;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class Super extends System
{
    @Override
    public void update()
    {
        Log.CurrentSession.println("Привет!", Log.MessageType.ERROR);
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