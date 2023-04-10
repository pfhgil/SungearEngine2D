package sungear.project.test12.Scripts.Systems;

import Core2D.ECS.Entity;
import Core2D.ECS.ComponentsQuery;
import Core2D.ECS.System;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class SuperSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery) {

    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime) {

    }

    public void collider2DEnter(Entity otherObj)
    {
        
    }
    
    public void collider2DExit(Entity otherObj)
    {
        
    }
}