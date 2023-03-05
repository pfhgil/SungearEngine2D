package SungearEngine2D.Scripts.Systems;

import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import SungearEngine2D.Scripts.Components.MoveToComponent;
import org.joml.Vector2f;

public class TransformationsHelpingSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {

    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {

    }

    private void moveTo(TransformComponent transformComponent, ComponentsQuery componentsQuery, float deltaTime)
    {
        MoveToComponent moveToComponent = componentsQuery.getComponent(MoveToComponent.class);

        if(moveToComponent == null) return;

        if(moveToComponent.needMoveTo) {
            Vector2f dif = new Vector2f(moveToComponent.destinationPosition.x - transformComponent.position.x, moveToComponent.destinationPosition.y - transformComponent.position.y).mul(moveToComponent.ration).mul(deltaTime);

            // 0.1f - погрешность, чтобы объект всегда достигал цели
            if(Math.abs(dif.x) > moveToComponent.errorRate || Math.abs(dif.y) > moveToComponent.errorRate) {
                transformComponent.position.add(new Vector2f(dif));
            } else {
                moveToComponent.needMoveTo= false;
            }
        }
    }
}
