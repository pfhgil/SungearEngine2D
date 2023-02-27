package Core2D.ECS.System.Systems;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.NonRemovable;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.ShaderUtils;
import org.joml.*;

import java.lang.Math;
import java.util.Optional;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

// система для рендера и управления камерами
public class CamerasManager extends System implements NonRemovable
{
    public CamerasManager()
    {
        // указываем какие компоненты будет принимать система
        componentsClasses.add(Camera2DComponent.class);
        componentsClasses.add(TransformComponent.class);
    }

    // обновление всех связок компонентов
    @Override
    public void update()
    {
        for(ComponentsQuery componentsQuery : componentsQueries) {
            Camera2DComponent camera2DComponent = componentsQuery.getComponent(Camera2DComponent.class);

            if(camera2DComponent != null) {
                Vector2i windowSize = Core2D.getWindow().getSize();
                camera2DComponent.viewportSize.set(windowSize);
                camera2DComponent.projectionMatrix = new Matrix4f().ortho2D(-camera2DComponent.viewportSize.x / 2.0f, camera2DComponent.viewportSize.x / 2.0f,
                        -camera2DComponent.viewportSize.y / 2.0f, camera2DComponent.viewportSize.y / 2.0f);

                updateViewMatrix(camera2DComponent);

                // TODO: вынести рендер в отдельный метод (чтобы в будущем была возможность разбить на потоки)
                if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                    camera2DComponent.frameBuffer.bind();
                    camera2DComponent.frameBuffer.clear();
                    if(camera2DComponent.camera2DCallback != null) {
                        camera2DComponent.camera2DCallback.preRender();
                    }
                    camera2DComponent.frameBuffer.unBind();

                    for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                        Optional<PostprocessingLayer> ppLayerFoundOptional =
                                camera2DComponent.postprocessingLayers.stream().filter(ppLayer -> ppLayer.getEntitiesLayerToRender() == layer).findFirst();
                        if(ppLayerFoundOptional.isPresent()) {
                            PostprocessingLayer ppLayerFound= ppLayerFoundOptional.get();

                            ppLayerFound.getFrameBuffer().bind();
                            ppLayerFound.getFrameBuffer().clear();

                            Graphics.getMainRenderer().render(ppLayerFound.getEntitiesLayerToRender());

                            ppLayerFound.getFrameBuffer().unBind();
                        } else {
                            camera2DComponent.frameBuffer.bind();
                            Graphics.getMainRenderer().render(layer);
                            camera2DComponent.frameBuffer.unBind();
                        }
                    }

                    camera2DComponent.frameBuffer.bind();
                    if(camera2DComponent.camera2DCallback != null) {
                        camera2DComponent.camera2DCallback.postRender();
                    }
                    camera2DComponent.frameBuffer.unBind();

                    // quad render -----------------------------------------------------

                    camera2DComponent.resultFrameBuffer.bind();
                    camera2DComponent.resultFrameBuffer.clear();
                    camera2DComponent.ppQuadVertexArray.bind();
                    for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                        PostprocessingLayer ppLayerFound = null;

                        Shader shader = camera2DComponent.postprocessingDefaultShader;
                        FrameBuffer frameBufferToBind = camera2DComponent.frameBuffer;

                        Optional<PostprocessingLayer> ppLayerFoundOptional =
                                camera2DComponent.postprocessingLayers.stream().filter(ppLayer -> ppLayer.getEntitiesLayerToRender() == layer).findFirst();
                        if(ppLayerFoundOptional.isPresent()) {
                            ppLayerFound = ppLayerFoundOptional.get();

                            shader = ppLayerFound.getShader();

                            frameBufferToBind = ppLayerFound.getFrameBuffer();

                            ppLayerFound.updateName();
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
    }

    private void updateViewMatrix(Camera2DComponent camera2DComponent)
    {
        TransformComponent transformComponent = findComponentsQuery(camera2DComponent.entity.ID).getComponent(TransformComponent.class);
        if(transformComponent != null) {
            Vector2f position = MatrixUtils.getPosition(transformComponent.getTransform().getResultModelMatrix());
            float rotation = MatrixUtils.getRotation(transformComponent.getTransform().getResultModelMatrix());
            Vector2f scale = MatrixUtils.getScale(transformComponent.getTransform().getResultModelMatrix());

            camera2DComponent.viewMatrix.identity();

            camera2DComponent.viewMatrix.scale(new Vector3f(scale.x, scale.y, 1f));
            camera2DComponent.viewMatrix.rotate((float) Math.toRadians(-rotation), 0f, 0f, 1f);
            camera2DComponent.viewMatrix.translate(new Vector3f(-position.x, -position.y, 1f));
        }
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
