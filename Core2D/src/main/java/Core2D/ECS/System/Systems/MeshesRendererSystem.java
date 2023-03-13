package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.NonRemovable;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Utils.ShaderUtils;

import static org.lwjgl.opengl.GL11C.*;

public class MeshesRendererSystem extends System implements NonRemovable
{
    @Override
    public void renderEntity(Entity entity, Camera2DComponent camera2DComponent)
    {
        renderMeshComponent(entity,  camera2DComponent,null);
    }

    @Override
    public void renderEntity(Entity entity, Camera2DComponent camera2DComponent, Shader shader)
    {
        renderMeshComponent(entity, camera2DComponent, shader);
    }

    //private void renderMeshComponent(Camera2DComponent camera2DComponent, TransformComponent transformComponent, MeshComponent meshComponent, Shader shader)
    private void renderMeshComponent(Entity entity, Camera2DComponent camera2DComponent, Shader shader)
    {
        if(entity == null) return;
        MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        //Camera2DComponent camera2DComponent = componentsQuery.getComponent(Camera2DComponent.class);
        //Log.CurrentSession.println("2 passed: " + componentsQuery.entityID + ", mesh: " + meshComponent + ", transform: " + transformComponent + ", cam: " + camera2DComponent, Log.MessageType.ERROR);
        if(meshComponent == null || transformComponent == null || camera2DComponent == null ||
        !meshComponent.active || !transformComponent.active || !camera2DComponent.active) return;

        if(shader == null) {
            shader = meshComponent.getShader();
        }

        //Log.CurrentSession.println("3 passed: " + componentsQuery.entityID, Log.MessageType.ERROR);

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
                ECSWorld.getCurrentECSWorld().transformationsSystem.getMVPMatrix(transformComponent, camera2DComponent)
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "color",
                // FIXME перенести color в mesh
                //new Vector4f(1.0f)
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
