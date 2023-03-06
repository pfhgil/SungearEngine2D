import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.ShaderUtils.ShaderUtils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

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
    public void render()
    {
        float time = (float) glfwGetTime();

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
    public void render(Shader shader)
    {
    }
}
