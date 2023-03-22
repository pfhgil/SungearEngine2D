package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.Transform.MoveToComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TransformationsHelpingSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {

    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {
        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);
        if(transformComponent == null) return;
        moveTo(transformComponent, componentsQuery, deltaTime);
    }

    private void moveTo(TransformComponent transformComponent, ComponentsQuery componentsQuery, float deltaTime)
    {
        MoveToComponent moveToComponent = componentsQuery.getComponent(MoveToComponent.class);

        if(moveToComponent == null) return;

        if(moveToComponent.needMoveTo) {

            Vector2f dif = new Vector2f(moveToComponent.destinationPosition.x - transformComponent.position.x, moveToComponent.destinationPosition.y - transformComponent.position.y).mul(moveToComponent.ration).mul(deltaTime);
            if(Math.abs(dif.x) > moveToComponent.errorRate || Math.abs(dif.y) > moveToComponent.errorRate) {
                transformComponent.position.add(new Vector3f(dif.x, dif.y, 0f));
            } else {
                moveToComponent.needMoveTo= false;
            }
        }
    }
}
