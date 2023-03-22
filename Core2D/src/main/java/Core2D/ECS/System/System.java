package Core2D.ECS.System;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;

public abstract class System
{
    public boolean active = true;

    public abstract void update(ComponentsQuery componentsQuery);

    public abstract void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime);

    // срабатывает при добавлении компонента в систему
    public void initComponentOnAdd(Component component)
    {

    }

    // удаляет компонент
    public void destroyComponent(Component component)
    {

    }

    public void collider2DEnter(Entity otherObj)
    {

    }

    public void collider2DExit(Entity otherObj)
    {

    }

    public void renderEntity(Entity entity, CameraComponent cameraComponent)
    {

    }

    public void renderEntity(Entity entity, CameraComponent cameraComponent, Shader shader)
    {

    }

    // other events ------------------------------------------------

    public void onMouseScroll(ComponentsQuery componentsQuery, double xOffset, double yOffset)
    {

    }

    public void onMousePositionChanged(ComponentsQuery componentsQuery, double posX, double posY)
    {

    }
}
