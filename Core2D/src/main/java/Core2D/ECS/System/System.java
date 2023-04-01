package Core2D.ECS.System;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

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

    // events ------------------------------------------------
    // input -------------------------------------------------
    public void onMouseScroll(ComponentsQuery componentsQuery, double xOffset, double yOffset)
    {

    }

    public void onMousePositionChanged(ComponentsQuery componentsQuery, double posX, double posY)
    {

    }

    // physics ----------------------------------------------
    public void beginContact(Contact contact, Entity entityA, Entity entityB)
    {

    }

    public void endContact(Contact contact, Entity entityA, Entity entityB)
    {

    }

    public void preSolve(Contact contact, Entity entityA, Entity entityB, Manifold manifold)
    {

    }

    public void postSolve(Contact contact, Entity entityA, Entity entityB, ContactImpulse contactImpulse)
    {

    }
}
