package SungearEngine2D.Scripts.Systems;

import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Log.Log;
import SungearEngine2D.Scripts.Components.MoveToComponent;
import org.joml.Vector2f;

import java.util.Enumeration;

public class TransformationsHelpingSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        //Log.CurrentSession.println("test2", Log.MessageType.SUCCESS);
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {
        //Log.CurrentSession.println("test0", Log.MessageType.SUCCESS);
        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);
        if(transformComponent == null) return;
        //Log.CurrentSession.println("test1", Log.MessageType.SUCCESS);
        moveTo(transformComponent, componentsQuery, deltaTime);
        //moveTo();
    }

    private void moveTo(TransformComponent transformComponent, ComponentsQuery componentsQuery, float deltaTime)
    {
        MoveToComponent moveToComponent = componentsQuery.getComponent(MoveToComponent.class);

        if(moveToComponent == null) return;

        if(moveToComponent.needMoveTo) {
            //Log.CurrentSession.println("moving to", Log.MessageType.WARNING);

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
