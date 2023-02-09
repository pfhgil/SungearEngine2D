package sungear.project.test12.Scripts.test;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;

import java.util.ArrayList;
import java.util.List;

public class System
{
    private List<Component> componentsToProcess = new ArrayList<>();
    private Class<?> typeOfComponentsToProcess;

    public System(Class<?> typeOfComponentsToProcess)
    {
        this.typeOfComponentsToProcess = typeOfComponentsToProcess;
    }

    // вызывается при старте сцены
    public void start()
    {

    }

    public void destroy()
    {
        componentsToProcess.clear();
    }

    public void update()
    {
        for(Component component : componentsToProcess) {
            if(typeOfComponentsToProcess.isAssignableFrom(component.getClass())) {
                // что-то сделать с компонентом
            }
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        for(Component component : componentsToProcess) {
            if(typeOfComponentsToProcess.isAssignableFrom(component.getClass())) {
                // что-то сделать с компонентом
            }
        }
    }

    public void collider2DEnter(Entity other)
    {
        for(Component component : componentsToProcess) {
            if(typeOfComponentsToProcess.isAssignableFrom(component.getClass())) {
                // что-то сделать с компонентом
            }
        }
    }

    public void collider2DExit(Entity other)
    {
        for(Component component : componentsToProcess) {
            if(typeOfComponentsToProcess.isAssignableFrom(component.getClass())) {
                // что-то сделать с компонентом
            }
        }
    }

    public void render(Camera2DComponent camera2DComponent)
    {
        for(Component component : componentsToProcess) {
            if(typeOfComponentsToProcess.isAssignableFrom(component.getClass())) {
                // что-то сделать с компонентом
            }
        }
    }

    public void render(Camera2DComponent camera2DComponent, Shader shader)
    {
        for(Component component : componentsToProcess) {
            if(typeOfComponentsToProcess.isAssignableFrom(component.getClass())) {
                // что-то сделать с компонентом
            }
        }
    }

    public List<Component> getComponentsToProcess() { return componentsToProcess; }
}
