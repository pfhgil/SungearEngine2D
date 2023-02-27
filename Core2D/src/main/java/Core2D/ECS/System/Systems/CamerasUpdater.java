package Core2D.ECS.System.Systems;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.ShaderUtils;
import org.joml.*;

import java.lang.Math;
import java.util.Optional;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class CamerasUpdater extends System
{
    public CamerasUpdater()
    {
        componentsClasses.add(Camera2DComponent.class);
        componentsClasses.add(TransformComponent.class);
    }

    @Override
    public void initComponentOnAdd(Component component)
    {
        if(component instanceof Camera2DComponent camera2DComponent) {
            camera2DComponent.accessLevelToQueries = Component.AccessLevelToQueries.GLOBAL;

            if(camera2DComponent.frameBuffer != null) {
                camera2DComponent.frameBuffer.destroy();
                camera2DComponent.frameBuffer = null;
            }
            if(camera2DComponent.resultFrameBuffer != null) {
                camera2DComponent.resultFrameBuffer.destroy();
                camera2DComponent.resultFrameBuffer = null;
            }

            loadCameraVAO(camera2DComponent);

            setScene2DMainCamera2D(camera2DComponent, camera2DComponent.isScene2DMainCamera2D);
            Vector2i screenSize = Graphics.getScreenSize();
            camera2DComponent.frameBuffer = new FrameBuffer(screenSize.x, screenSize.y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);
            camera2DComponent.resultFrameBuffer = new FrameBuffer(screenSize.x, screenSize.y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);
        }
    }

    private void loadCameraVAO(Camera2DComponent camera2DComponent)
    {
        if (camera2DComponent.ppQuadVertexArray != null) {
            camera2DComponent.ppQuadVertexArray.destroy();
            camera2DComponent.ppQuadVertexArray = null;
        }

        camera2DComponent.ppQuadVertexArray = new VertexArray();
        // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
        VertexBuffer vertexBuffer = new VertexBuffer(camera2DComponent.ppQuadData);
        // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
        IndexBuffer indexBuffer = new IndexBuffer(camera2DComponent.ppQuadIndices);

        // создаю описание аттрибутов в шейдерной программе
        BufferLayout attributesLayout = new BufferLayout(
                new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2),
                new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
        );

        vertexBuffer.setLayout(attributesLayout);
        camera2DComponent.ppQuadVertexArray.putVBO(vertexBuffer, false);
        camera2DComponent.ppQuadVertexArray.putIBO(indexBuffer);

        camera2DComponent.ppQuadIndices = null;

        // отвязываю vao
        camera2DComponent.ppQuadVertexArray.unBind();
    }

    @Override
    public void destroyComponent(Component component)
    {
        if(component instanceof Camera2DComponent camera2DComponent) {
            camera2DComponent.frameBuffer.destroy();
            camera2DComponent.ppQuadVertexArray.destroy();
            camera2DComponent.resultFrameBuffer.destroy();
            camera2DComponent.postprocessingDefaultShader.destroy();

            for(PostprocessingLayer ppLayer : camera2DComponent.postprocessingLayers) {
                ppLayer.destroy();
            }
        }
    }

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

    public void setScene2DMainCamera2D(Camera2DComponent camera2DComponent, boolean scene2DMainCamera2D, Scene2D scene2D)
    {
        if(scene2D != null &&
                scene2DMainCamera2D && scene2D.getSceneMainCamera2D() != null) {
            Camera2DComponent foundComponent = scene2D.getSceneMainCamera2D().getComponent(Camera2DComponent.class);
            if(foundComponent != null) {
                foundComponent.isScene2DMainCamera2D = false;
                scene2D.setSceneMainCamera2D(null);
            }
        }
        camera2DComponent.isScene2DMainCamera2D = scene2DMainCamera2D;
        if(scene2D != null) {
            scene2D.setSceneMainCamera2D(camera2DComponent.isScene2DMainCamera2D ? camera2DComponent.entity : scene2D.getSceneMainCamera2D());
        }
    }

    public void setScene2DMainCamera2D(Camera2DComponent camera2DComponent, boolean scene2DMainCamera2D)
    {
        if(SceneManager.currentSceneManager != null) {
            setScene2DMainCamera2D(camera2DComponent, scene2DMainCamera2D, SceneManager.currentSceneManager.getCurrentScene2D());
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
