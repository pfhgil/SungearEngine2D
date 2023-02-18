package sungear.project.test12.Scripts;

import Core2D.ECS.*;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.*;
import Core2D.Utils.ShaderUtils;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class AleshaMegaSystem extends System
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
    public void render(Camera2DComponent camera2DComponent)
    {
        //float time = (float) glfwGetTime();
        float time = 0.0f;

        MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
        if(meshComponent != null) {
            meshComponent.shader.bind();

            ShaderUtils.setUniform(
                    meshComponent.shader.getProgramHandler(),
                    "time",
                    time
            );
        }
    }

    @Override
    public void render(Camera2DComponent camera2DComponent, Shader shader)
    {
    }
}
