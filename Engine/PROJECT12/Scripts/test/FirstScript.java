package test;

import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Input.PC.Keyboard;
import Core2D.Scripting.*;
import Core2D.Log.*;
import org.lwjgl.glfw.GLFW;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class FirstScript extends System
{
    @Override
    public void update()
    {
        if(Keyboard.keyDown(GLFW.GLFW_KEY_U)) {
            entity.getComponent(TransformComponent.class).getTransform().rotate(0.5f);
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {

    }

    // otherEntity - an entity, one of whose colliders entered one of the colliders of this entity.
    @Override
    public void collider2DEnter(Entity otherEntity)
    {

    }

    // otherEntity - an entity whose body came out of the colliders of this entity
    @Override
    public void collider2DExit(Entity otherEntity)
    {

    }

    // camera2DComponent - the camera that renders this entity.
    @Override
    public void render(Camera2DComponent camera2DComponent)
    {

    }

    // Use the "shader" parameter to render this entity.
    @Override
    public void render(Camera2DComponent camera2DComponent, Shader shader)
    {

    }
}
