package sungear.project.test12.Scripts.Systems;

import Core2D.ECS.Entity;
import Core2D.ECS.System.System;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class SuperSystem extends System
{
    @Override
    public void update()
    {
        //entity.getComponent(TransformComponent.class).getTransform().rotate(1.0f);
        //entity.getComponent(LineComponent.class).getLinesData()[0].offset.set(new Vector2f(50.0f, 50.0f));
        //
        //entity.getComponent(BoxComponent.class).setSize(new Vector2f(50.0f, 50.0f));
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