package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Test;
import Core2D.ECS.NonRemovable;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.*;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

// система для инициализации компонентов
public class ComponentsManager extends System implements NonRemovable
{
    public ComponentsManager()
    {
        componentsClasses.add(Camera2DComponent.class);
        componentsClasses.add(Test.class);
    }

    // инициализирует компонент при добавлении на сущность
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
        } else if(component instanceof Test test) {
            // initialize component
        }
    }

    // вызывается при удалении с сущности компонента (система ComponentsManager)
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
        } else if(component instanceof Test test) {
            // initialize component
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
}
