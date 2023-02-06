package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Log.Log;
import Core2D.ShaderUtils.ShaderUtils;
import org.joml.Matrix4f;

import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class MeshRendererSystem extends System
{
    public void render(Camera2DComponent camera2DComponent)
    {
        if(entity != null) {
            if (entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if (transformComponent == null) return;

            List<MeshComponent> meshComponents = entity.getAllComponents(MeshComponent.class);
            for(MeshComponent meshComponent : meshComponents) {
                renderMeshComponent(camera2DComponent, transformComponent, meshComponent, meshComponent.getShader());
            }
        }
    }

    public void render(Camera2DComponent camera2DComponent, Shader shader)
    {
        if(entity != null) {
            if (entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if (transformComponent == null) return;

            List<MeshComponent> meshComponents = entity.getAllComponents(MeshComponent.class);
            for(MeshComponent meshComponent : meshComponents) {
                renderMeshComponent(camera2DComponent, transformComponent, meshComponent, shader);
            }
        }
    }

    private void renderMeshComponent(Camera2DComponent camera2DComponent, TransformComponent transformComponent, MeshComponent meshComponent, Shader shader)
    {
        /*
        boolean instanceOfTarget = meshComponent instanceof CameraRenderTargetComponent;

        Shader shaderToApply = instanceOfTarget ? camera2DComponent.getPostprocessingShader() : shader;
        int formattedTextureBlock = instanceOfTarget ? camera2DComponent.getFrameBuffer().getTextureBlock() - GL_TEXTURE0 : meshComponent.getTexture().getFormattedTextureBlock();

        Log.Console.println("instanceof rendertarget: " + instanceOfTarget);

        meshComponent.getVertexArrayObject().bind();

        if(instanceOfTarget) {
            camera2DComponent.getFrameBuffer().bind();
        } else {
            meshComponent.getTexture().bind();
        }
        if (meshComponent.material2D != null && meshComponent.material2D.material2DData != null) {
            OpenGL.glCall((params) -> glBlendFunc(meshComponent.material2D.material2DData.blendSourceFactor, meshComponent.material2D.material2DData.blendDestinationFactor));
        }

        shaderToApply.bind();

        //Matrix4f cameraResProjectionMatrix
        String matrixUniformName = instanceOfTarget ? "mpMatrix" : "mvpMatrix";
        Matrix4f resMatrix = instanceOfTarget ?
                new Matrix4f(transformComponent.getTransform().getResultModelMatrix()) : transformComponent.getMvpMatrix(camera2DComponent);
        ShaderUtils.setUniform(
                shaderToApply.getProgramHandler(),
                matrixUniformName,
                resMatrix
        );
        ShaderUtils.setUniform(
                shaderToApply.getProgramHandler(),
                "color",
                entity.getColor()
        );

        ShaderUtils.setUniform(
                shaderToApply.getProgramHandler(),
                "sampler",
                formattedTextureBlock
        );

        OpenGL.glCall((params) -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0));

        if(instanceOfTarget) {
            camera2DComponent.getFrameBuffer().unBind();
        } else {
            meshComponent.getTexture().unBind();
        }
        //meshComponent.getTexture().unBind();
        meshComponent.getVertexArrayObject().unBind();

         */
        // использую VAO, текстуру и шейдер
        meshComponent.getVertexArrayObject().bind();
        meshComponent.getTexture().bind();
        if (meshComponent.material2D != null && meshComponent.material2D.material2DData != null) {
            OpenGL.glCall((params) -> glBlendFunc(meshComponent.material2D.material2DData.blendSourceFactor, meshComponent.material2D.material2DData.blendDestinationFactor));
        }
        shader.bind();

        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "mvpMatrix",
                transformComponent.getMvpMatrix(camera2DComponent)
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "color",
                entity.getColor()
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "sampler",
                meshComponent.getTexture().getFormattedTextureBlock()
        );

        // нарисовать два треугольника
        OpenGL.glCall((params) -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0));

        // прекращаю использование шейдера, текстуры и VAO
        meshComponent.getTexture().unBind();
        meshComponent.getVertexArrayObject().unBind();
    }
}
