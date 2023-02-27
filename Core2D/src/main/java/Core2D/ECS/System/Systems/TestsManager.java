package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Component.Test;
import Core2D.ECS.Entity;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;

public class TestsManager extends System
{
    public TestsManager()
    {
        componentsClasses.add(Test.class);
        componentsClasses.add(TransformComponent.class);
    }

    // обновляет все связки компонентов
    @Override
    public void update()
    {
        for(ComponentsQuery componentsQuery : componentsQueries) {
            Test test = componentsQuery.getComponent(Test.class);
            MeshComponent meshComponent = componentsQuery.getComponent(MeshComponent.class);

            if(meshComponent == null || test == null) continue;

            // do something with components
        }
    }

    // метод для одной сущности
    public void operateOne(Entity entity)
    {
        ComponentsQuery componentsQuery = findComponentsQuery(entity.ID);

        Test test = componentsQuery.getComponent(Test.class);
        MeshComponent meshComponent = componentsQuery.getComponent(MeshComponent.class);

        if(meshComponent == null || test == null) return;
        // do something with components
    }
}
