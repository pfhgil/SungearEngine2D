import Core2D.ECS.Entity;
import Core2D.ECS.System.System;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class RotationSystem extends System
{
    @Override
    public void update()
    {
        
    }
    
    @Override
    public void deltaUpdate(float deltaTime)
    {
        entity.getComponent(Rigidbody2DComponent.class).getRigidbody2D().getBody().setAngularVelocity(-1.0f);
        //entity.getComponent(TransformComponent.class).getTransform().rotate(0.1f);
    }
    
    @Override
    public void collider2DEnter(Entity otherObj)
    {
        
    }
    
    @Override
    public void collider2DExit(Entity otherObj)
    {
        
    }
}