package Core2D.ECS.System.Systems;

import Core2D.Common.Interfaces.NonRemovable;
import Core2D.DataClasses.MeshData;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Utils.ShaderUtils;

import static org.lwjgl.opengl.GL11C.*;

public class MeshesRendererSystem extends System implements NonRemovable
{
    @Override
    public void update(ComponentsQuery componentsQuery) {

    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime) {

    }

    @Override
    public void renderEntity(Entity entity, CameraComponent cameraComponent)
    {
        renderMeshComponent(entity, cameraComponent,null);
    }

    @Override
    public void renderEntity(Entity entity, CameraComponent cameraComponent, Shader shader)
    {
        renderMeshComponent(entity, cameraComponent, shader);
    }

    //private void renderMeshComponent(Camera2DComponent camera2DComponent, TransformComponent transformComponent, MeshComponent meshComponent, Shader shader)
    private void renderMeshComponent(Entity entity, CameraComponent cameraComponent, Shader shader)
    {
        if(entity == null || !entity.active) return;

        MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        if(meshComponent == null || transformComponent == null || cameraComponent == null ||
        !meshComponent.active || !transformComponent.active || !cameraComponent.active) return;

        if(shader == null) {
            shader = meshComponent.shader;
        }

        if(meshComponent.modelData == null || meshComponent.modelData.meshesData == null) return;

        for(MeshData meshData : meshComponent.modelData.meshesData) {
            if(meshData == null) continue;

            // использую VAO, текстуру и шейдер
            meshData.getVertexArray().bind();
            OpenGL.glBindTexture(meshComponent.texture2DData.getHandler(), meshComponent.texture2DData.textureBlock);
            if (meshComponent.material2D != null && meshComponent.material2D.material2DData != null) {
                OpenGL.glCall((params) -> glBlendFunc(meshComponent.material2D.material2DData.blendSourceFactor, meshComponent.material2D.material2DData.blendDestinationFactor));
            }
            shader.bind();

            ShaderUtils.setUniform(
                    shader.getProgramHandler(),
                    "mvpMatrix",
                    ECSWorld.getCurrentECSWorld().transformationsSystem.getMVPMatrix(transformComponent, cameraComponent)
            );

            ShaderUtils.setUniform(
                    shader.getProgramHandler(),
                    "color",
                    // FIXME: перенести color в material
                    //new Vector4f(1.0f)
                    entity.getColor()
            );

            ShaderUtils.setUniform(
                    shader.getProgramHandler(),
                    "sampler",
                    meshComponent.texture2DData.getFormattedTextureBlock()
            );

            // нарисовать два треугольника
            OpenGL.glDrawElements(GL_TRIANGLES, meshData.indices.length, GL_UNSIGNED_INT, 0);
        }
    }
}
