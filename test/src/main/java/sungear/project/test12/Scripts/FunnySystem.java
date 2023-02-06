package sungear.project.test12.Scripts;

import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.Scripting.*;
import Core2D.Log.*;
import Core2D.Graphics.RenderParts.*;
import Core2D.Timer.Timer;
import static org.lwjgl.glfw.GLFW.*;
import Core2D.ShaderUtils.ShaderUtils;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class FunnySystem extends System
{
    @Override
    public void update()
    {

    }

    @Override
    public void deltaUpdate(float deltaTime)
    {

    }

    @Override
    public void collider2DEnter(Entity otherObj)
    {

    }

    @Override
    public void collider2DExit(Entity otherObj)
    {

    }

    @Override
    public void render()
    {
        float time = (float) glfwGetTime();

        MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
        if(meshComponent != null) {
            meshComponent.getShader().bind();

            ShaderUtils.setUniform(
                    meshComponent.getShader().getProgramHandler(),
                    "time",
                    time
            );
        }
// ff
        //Log.CurrentSession.println("time: " + time, Log.MessageType.WARNING);
    }

    @Override
    public void render(Shader shader)
    {

    }
}