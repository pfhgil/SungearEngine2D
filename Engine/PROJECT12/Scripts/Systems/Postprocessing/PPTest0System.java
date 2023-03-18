import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.PostprocessingLayer;
import Core2D.ShaderUtils.*;

import static org.lwjgl.glfw.GLFW.glfwGetTime;


// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class PPTest0System extends System
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
        PostprocessingLayer ppLayer = camera2DComponent.getPostprocessingLayerByName("bloor_layer");

        if(ppLayer != null) {
            Shader shaderToBind = ppLayer.getShader();

            float time = (float) glfwGetTime();

            //shader.bind();
            shaderToBind.bind();

            ShaderUtils.setUniform(
                    shaderToBind.getProgramHandler(),
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
