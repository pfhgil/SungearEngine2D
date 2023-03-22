package sungear.project.test12.Scripts.test;

import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Entity;

public class SomeSystem extends System<SomeComponent>
{
    @Override
    public void start()
    {

    }

    @Override
    public void update()
    {
        for(SomeComponent component : componentsToProcess) {
            // что то сделать с компонентом
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        for(SomeComponent component : componentsToProcess) {
            // что то сделать с компонентом
        }
    }

    @Override
    public void collider2DEnter(Entity other)
    {
        for(Component component : componentsToProcess) {
            // что то сделать с компонентом
        }
    }

    @Override
    public void collider2DExit(Entity other)
    {
        for(SomeComponent component : componentsToProcess) {
            //if(component.entity.active && component.active && component.)
            // что то сделать с компонентом
        }
    }

    public void render(CameraComponent cameraComponent)
    {
        for(SomeComponent component : componentsToProcess) {
            // что то сделать с компонентом
        }
    }
}
