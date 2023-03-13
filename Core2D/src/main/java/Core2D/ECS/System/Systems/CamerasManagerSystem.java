package Core2D.ECS.System.Systems;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.NonRemovable;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.FrameBuffer;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.ShaderUtils;
import org.joml.*;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.GL11;

import java.lang.Math;
import java.util.Optional;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

// система для рендера и управления камерами
public class CamerasManagerSystem extends System implements NonRemovable
{
    // обновление всех связок компонентов
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        Camera2DComponent camera2DComponent = componentsQuery.getComponent(Camera2DComponent.class);

        if (camera2DComponent != null) {
            //Log.CurrentSession.println("camera rendering", Log.MessageType.SUCCESS);

                //Log.CurrentSession.println("camera name: " + camera2DComponent.entity.name, Log.MessageType.INFO);
            Vector2i windowSize = Core2D.getWindow().getSize();
            camera2DComponent.viewportSize.set(windowSize);
            camera2DComponent.projectionMatrix = new Matrix4f().ortho2D(-camera2DComponent.viewportSize.x / 2.0f, camera2DComponent.viewportSize.x / 2.0f,
                    -camera2DComponent.viewportSize.y / 2.0f, camera2DComponent.viewportSize.y / 2.0f);

            updateViewMatrix(camera2DComponent);

            // TODO: вынести рендер в отдельный метод (чтобы в будущем была возможность разбить на потоки)
            if (SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null && camera2DComponent.render) {
                camera2DComponent.frameBuffer.bind();
                camera2DComponent.frameBuffer.clear();
                for(Camera2DComponent.Camera2DCallback camera2DCallback : camera2DComponent.camera2DCallbacks) {
                    camera2DCallback.preRender();
                }
                camera2DComponent.frameBuffer.unBind();

                for (Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                    PostprocessingLayer ppLayerFound = camera2DComponent.postprocessingLayers.stream().filter(ppl -> ppl.getEntitiesLayerToRender() == layer).findFirst().orElse(null);
                    if (ppLayerFound != null && ppLayerFound.render) {
                        ppLayerFound.getFrameBuffer().bind();
                        ppLayerFound.getFrameBuffer().clear();

                        Graphics.getMainRenderer().render(ppLayerFound.getEntitiesLayerToRender(), camera2DComponent);

                        ppLayerFound.getFrameBuffer().unBind();
                    } else if(ppLayerFound == null) {
                        camera2DComponent.frameBuffer.bind();
                        Graphics.getMainRenderer().render(layer, camera2DComponent);
                        camera2DComponent.frameBuffer.unBind();
                    }
                }

                camera2DComponent.frameBuffer.bind();
                for(Camera2DComponent.Camera2DCallback camera2DCallback : camera2DComponent.camera2DCallbacks) {
                    camera2DCallback.postRender();
                }
                camera2DComponent.frameBuffer.unBind();

                // quad render -----------------------------------------------------

                camera2DComponent.resultFrameBuffer.bind();
                camera2DComponent.resultFrameBuffer.clear();
                camera2DComponent.ppQuadVertexArray.bind();

                for (Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                    Shader shader = camera2DComponent.postprocessingDefaultShader;
                    FrameBuffer frameBufferToBind = camera2DComponent.frameBuffer;

                    PostprocessingLayer ppLayerFound = camera2DComponent.postprocessingLayers.stream().filter(ppLayer -> ppLayer.getEntitiesLayerToRender() == layer).findFirst().orElse(null);

                    if(ppLayerFound != null) {
                        //Log.CurrentSession.println("pp layer found: " + ppLayerFound.getEntitiesLayerToRenderName(), Log.MessageType.SUCCESS);

                        shader = ppLayerFound.getShader();

                        frameBufferToBind = ppLayerFound.getFrameBuffer();

                        ppLayerFound.updateName();

                        if(!ppLayerFound.overlay) continue;
                    }

                    frameBufferToBind.bindTexture();

                    shader.bind();

                    ShaderUtils.setUniform(
                            shader.getProgramHandler(),
                            "color",
                            new Vector4f(1.0f)
                    );

                    ShaderUtils.setUniform(
                            shader.getProgramHandler(),
                            "sampler",
                            frameBufferToBind.getTextureBlock() - GL_TEXTURE0
                    );

                    // нарисовать два треугольника
                    OpenGL.glCall((params) -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0));

                    frameBufferToBind.unBindTexture();
                }
                camera2DComponent.ppQuadVertexArray.unBind();
                camera2DComponent.resultFrameBuffer.unBind();
            }
        }
    }

    /*
    @Override
    public void renderEntity(Entity entity, Camera2DComponent camera2DComponent)
    {
        renderEntity(entity, camera2DComponent, null);
    }

    public void renderEntity(Entity entity)
    {
        for(ComponentsQuery componentsQuery : ECSWorld.getCurrentECSWorld().getComponentsQueries()) {
            Camera2DComponent camera2DComponent = componentsQuery.getComponent(Camera2DComponent.class);

            if(camera2DComponent != null) {
                renderEntity(entity, camera2DComponent);
            }
        }
    }

    public void renderEntity(Entity entity, Shader shader)
    {
        for(ComponentsQuery componentsQuery : ECSWorld.getCurrentECSWorld().getComponentsQueries()) {
            Camera2DComponent camera2DComponent = componentsQuery.getComponent(Camera2DComponent.class);

            if(camera2DComponent != null) {
                renderEntity(entity, camera2DComponent, shader);
            }
        }
    }

    @Override
    public void renderEntity(Entity entity, Camera2DComponent camera2DComponent, Shader entityShader)
    {
        if (camera2DComponent != null) {
            // TODO: вынести рендер в отдельный метод (чтобы в будущем была возможность разбить на потоки)
            if (SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                PostprocessingLayer ppLayerFound = camera2DComponent.postprocessingLayers.stream().filter(ppl -> ppl.getEntitiesLayerToRender() == entity.getLayer()).findFirst().orElse(null);
                if (ppLayerFound != null) {
                    ppLayerFound.getFrameBuffer().bind();

                    Graphics.getMainRenderer().render(entity, camera2DComponent, entityShader);

                    ppLayerFound.getFrameBuffer().unBind();
                } else {
                    camera2DComponent.frameBuffer.bind();
                    Graphics.getMainRenderer().render(entity, camera2DComponent, entityShader);
                    camera2DComponent.frameBuffer.unBind();
                }
            }
        }
    }

     */

    public void updateViewMatrix(Camera2DComponent camera2DComponent)
    {
        if(camera2DComponent.entity != null) {
            TransformComponent transformComponent = camera2DComponent.entity.getComponent(TransformComponent.class);

            if(transformComponent != null) {
                if (camera2DComponent.followTranslation) {
                    camera2DComponent.position.set(MatrixUtils.getPosition(transformComponent.modelMatrix));
                }
                if (camera2DComponent.followRotation) {
                    camera2DComponent.rotation = MatrixUtils.getRotation(transformComponent.modelMatrix);
                }
                if (camera2DComponent.followScale) {
                    camera2DComponent.scale.set(MatrixUtils.getScale(transformComponent.modelMatrix));
                }
            }
        }

        camera2DComponent.viewMatrix.identity();

        camera2DComponent.viewMatrix.scale(new Vector3f(camera2DComponent.scale.x, camera2DComponent.scale.y, 1f));
        camera2DComponent.viewMatrix.rotate((float) Math.toRadians(-camera2DComponent.rotation), 0f, 0f, 1f);
        camera2DComponent.viewMatrix.translate(new Vector3f(-camera2DComponent.position.x, -camera2DComponent.position.y, 1f));
    }

    public void setPostprocessingDefaultShader(Camera2DComponent camera2DComponent, Shader postprocessingDefaultShader)
    {
        if(camera2DComponent.postprocessingDefaultShader != null) {
            camera2DComponent.postprocessingDefaultShader.destroy();
        }
        camera2DComponent.postprocessingDefaultShader = postprocessingDefaultShader;
    }

    public boolean isPostprocessingLayerExists(Camera2DComponent camera2DComponent, Layer layer)
    {
        return camera2DComponent.postprocessingLayers.stream().anyMatch((ppLayer) -> ppLayer.getEntitiesLayerToRender() == layer);
    }

    public PostprocessingLayer getPostprocessingLayerByName(Camera2DComponent camera2DComponent, String name)
    {
        Optional<PostprocessingLayer> foundLayer = camera2DComponent.postprocessingLayers.stream().filter(ppLayer -> {
            if(ppLayer.getEntitiesLayerToRender() != null) {
                return ppLayer.getEntitiesLayerToRender().getName().equals(name);
            }
            return false;
        }).findFirst();

        return foundLayer.orElse(null);
    }
}
