package sungear.project.test12.Scripts.Systems.Postprocessing;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.ECS.*;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.ShaderUtils.*;
import Core2D.Utils.MatrixUtils;
import org.joml.*;
import Core2D.Log.Log;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL46C.*;
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
