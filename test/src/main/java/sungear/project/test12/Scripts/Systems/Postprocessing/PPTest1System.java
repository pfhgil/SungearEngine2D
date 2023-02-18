package sungear.project.test12.Scripts.Systems.Postprocessing;

import Core2D.Core2D.Core2D;
import Core2D.ECS.*;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Utils.ShaderUtils;
import org.joml.*;

import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import org.lwjgl.glfw.GLFW;

// Attention! Do not declare fields with the @InspectorView annotation in systems. They will not be processed and shown in the Inspector.
public class PPTest1System extends System
{
    private transient Random random = new Random();

    private transient int totalFrames = 0;

    private transient Vector3f cameraPosition = new Vector3f(-3.9999986f, 2.9999995f, 0.0f);
    private transient Vector2f lightPos = new Vector2f(0.0f, 0.0f);
    private transient Vector2f lastMousePosition = new Vector2f();
    private transient Vector2f mousePosition = new Vector2f();

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
        PostprocessingLayer ppLayer = camera2DComponent.getPostprocessingLayerByName("ray_traced_layer");

        if(ppLayer != null) {
            Shader shaderToBind = ppLayer.getShader();

            shaderToBind.bind();

            Vector2i windowSize = Core2D.getWindow().getSize();

            if(Keyboard.keyDown(GLFW.GLFW_KEY_W)) {
                cameraPosition.x += 0.1f;
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_S)) {
                cameraPosition.x -= 0.1f;
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                cameraPosition.y -= 0.1f;
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                cameraPosition.y += 0.1f;
            }


            if(Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
                Vector2f mousePos = Mouse.getMousePosition();
                Vector2f offset = new Vector2f(lastMousePosition).add(new Vector2f(mousePos).negate());
                lastMousePosition.set(mousePos);

                mousePosition.add(offset);
            } else {
                Vector2f mousePos = Mouse.getMousePosition();
                lastMousePosition.set(mousePos);
            }

            lightPos.x += 0.01f;
            lightPos.y += 0.01f;


            ShaderUtils.setUniform(
                    shaderToBind.getProgramHandler(),
                    "iResolution",
                    new Vector2f(windowSize.x, windowSize.y));

            ShaderUtils.setUniform(
                    shaderToBind.getProgramHandler(),
                    "iMouse",
                    mousePosition);

            ShaderUtils.setUniform(
                    shaderToBind.getProgramHandler(),
                    "cameraPosition",
                    cameraPosition);

            ShaderUtils.setUniform(
                    shaderToBind.getProgramHandler(),
                    "lightPos",
                    lightPos);


            Vector2f rnd0 = new Vector2f(random.nextFloat() * 999.0f, random.nextFloat() * 999.0f);
            Vector2f rnd1 = new Vector2f(random.nextFloat() * 999.0f, random.nextFloat() * 999.0f);
            //System.out.println("x: " + rnd.x + ", " + rnd.y);

            ShaderUtils.setUniform(
                    shaderToBind.getProgramHandler(),
                    "u_seed1",
                    rnd0);

            ShaderUtils.setUniform(
                    shaderToBind.getProgramHandler(),
                    "u_seed2",
                    rnd1);

            float samplerPart = 1.0f / totalFrames;
            //System.out.println(samplerPart);

            ShaderUtils.setUniform(
                    shaderToBind.getProgramHandler(),
                    "samplerPart",
                    samplerPart);
            //

        }
    }

    @Override
    public void render(Camera2DComponent camera2DComponent, Shader shader)
    {

    }
}
