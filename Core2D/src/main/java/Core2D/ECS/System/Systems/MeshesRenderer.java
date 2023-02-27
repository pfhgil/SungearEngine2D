package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.NonRemovable;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ShaderUtils;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11C.*;

public class MeshesRenderer extends System implements NonRemovable
{
    public MeshesRenderer()
    {
        componentsClasses.add(MeshComponent.class);
        componentsClasses.add(TransformComponent.class);
        componentsClasses.add(Camera2DComponent.class);
    }

    @Override
    public void renderEntity(Entity entity)
    {
        renderMeshComponent(findComponentsQuery(entity.ID), null);
        /*
        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                for(Entity entity : layer.getEntities()) {
                    if(entity.isShouldDestroy()) return;


                }
            }
        }

         */

        /*
        if(entity != null) {
            if (entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if (transformComponent == null) return;

            List<MeshComponent> meshComponents = entity.getAllComponents(MeshComponent.class);
            for(MeshComponent meshComponent : meshComponents) {
                renderMeshComponent(camera2DComponent, transformComponent, meshComponent, meshComponent.getShader());
            }
        }

         */
    }

    @Override
    public void renderEntity(Entity entity, Shader shader)
    {
        renderMeshComponent(findComponentsQuery(entity.ID), shader);
        /*
        if(entity != null) {
            if (entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if (transformComponent == null) return;

            List<MeshComponent> meshComponents = entity.getAllComponents(MeshComponent.class);
            for(MeshComponent meshComponent : meshComponents) {
                renderMeshComponent(camera2DComponent, transformComponent, meshComponent, shader);
            }
        }

         */


    }

    //private void renderMeshComponent(Camera2DComponent camera2DComponent, TransformComponent transformComponent, MeshComponent meshComponent, Shader shader)
    private void renderMeshComponent(ComponentsQuery componentsQuery, Shader shader)
    {
        if(componentsQuery == null) return;
        MeshComponent meshComponent = componentsQuery.getComponent(MeshComponent.class);
        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);
        Camera2DComponent camera2DComponent = componentsQuery.getComponent(Camera2DComponent.class);
        //Log.CurrentSession.println("2 passed: " + componentsQuery.entityID + ", mesh: " + meshComponent + ", transform: " + transformComponent + ", cam: " + camera2DComponent, Log.MessageType.ERROR);
        if(meshComponent == null || transformComponent == null || camera2DComponent == null) return;

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
                transformComponent.getMvpMatrix(camera2DComponent)
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "color",
                // FIXME перенести color в mesh
                new Vector4f(1.0f)
                //entity.getColor()
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
