package Core2D.ECS.System.Systems;

import Core2D.AssetManager.AssetManager;
import Core2D.Audio.OpenAL;
import Core2D.Debug.DebugDraw;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.CameraComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.NonRemovable;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.*;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import org.joml.Vector2i;
import org.lwjgl.openal.AL10;

import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

// система для инициализации компонентов
public class ComponentsInitializerSystem extends System implements NonRemovable
{
    @Override
    public void update(ComponentsQuery componentsQuery) {

    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime) {

    }

    // инициализирует компонент при добавлении на сущность
    public void initComponentOnAdd(Component component)
    {
        if(component instanceof CameraComponent cameraComponent) {
            if(cameraComponent.frameBuffer != null) {
                cameraComponent.frameBuffer.destroy();
                cameraComponent.frameBuffer = null;
            }
            if(cameraComponent.resultFrameBuffer != null) {
                cameraComponent.resultFrameBuffer.destroy();
                cameraComponent.resultFrameBuffer = null;
            }

            loadCameraVAO(cameraComponent);

            setScene2DMainCamera2D(cameraComponent, cameraComponent.scene2DMainCamera2D);

            Vector2i screenSize = Graphics.getScreenSize();
            cameraComponent.frameBuffer = new FrameBuffer(screenSize.x, screenSize.y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);
            cameraComponent.resultFrameBuffer = new FrameBuffer(screenSize.x, screenSize.y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);

            cameraComponent.cameraCallbacks.add(new CameraComponent.CameraCallback() {
                @Override
                public void preRender() {

                }

                @Override
                public void postRender() {
                    for(Entity debugEntity : DebugDraw.getDebugPrimitives().values()) {
                        Graphics.getMainRenderer().render(debugEntity, cameraComponent);
                    }
                }
            });
        } else if(component instanceof TransformComponent transformComponent) {
            ECSWorld.getCurrentECSWorld().transformationsSystem.updateTransformComponent(transformComponent);
        } else if(component instanceof AudioComponent audioComponent) {
            ECSWorld.getCurrentECSWorld().audioSystem.setAudioComponentData(audioComponent,
                    AssetManager.getInstance().getAudioData(audioComponent.path));
            //Log.CurrentSession.println("audio initialized: " + audioComponent.path, Log.MessageType.SUCCESS);
        }
    }

    // вызывается при удалении с сущности компонента (система ComponentsManager)
    public void destroyComponent(Component component)
    {
        if(component instanceof CameraComponent cameraComponent) {
            setScene2DMainCamera2D(cameraComponent, false);
            cameraComponent.frameBuffer.destroy();
            cameraComponent.ppQuadVertexArray.destroy();
            cameraComponent.resultFrameBuffer.destroy();
            //camera2DComponent.postprocessingDefaultShader.destroy();

            for(PostprocessingLayer ppLayer : cameraComponent.postprocessingLayers) {
                ppLayer.destroy();
            }
        } else if(component instanceof AudioComponent audioComponent) {
            OpenAL.alCall(params -> AL10.alDeleteSources(audioComponent.sourceHandler));
            //Log.CurrentSession.println("audio destroyed: " + audioComponent.path, Log.MessageType.SUCCESS);
        }
    }

    private void loadCameraVAO(CameraComponent cameraComponent)
    {
        if (cameraComponent.ppQuadVertexArray != null) {
            cameraComponent.ppQuadVertexArray.destroy();
            cameraComponent.ppQuadVertexArray = null;
        }

        cameraComponent.ppQuadVertexArray = new VertexArray();
        // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
        VertexBuffer vertexBuffer = new VertexBuffer(cameraComponent.ppQuadData);
        // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
        IndexBuffer indexBuffer = new IndexBuffer(cameraComponent.ppQuadIndices);

        // создаю описание аттрибутов в шейдерной программе
        BufferLayout attributesLayout = new BufferLayout(
                new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2),
                new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
        );

        vertexBuffer.setLayout(attributesLayout);
        cameraComponent.ppQuadVertexArray.putVBO(vertexBuffer, false);
        cameraComponent.ppQuadVertexArray.putIBO(indexBuffer);

        cameraComponent.ppQuadIndices = null;

        // отвязываю vao
        cameraComponent.ppQuadVertexArray.unBind();
    }

    public void setScene2DMainCamera2D(CameraComponent cameraComponent, boolean scene2DMainCamera2D, Scene2D scene2D)
    {
        if(scene2D != null &&
                scene2DMainCamera2D && scene2D.getSceneMainCamera2D() != null) {
            CameraComponent foundComponent = scene2D.getSceneMainCamera2D().getComponent(CameraComponent.class);
            if(foundComponent != null) {
                foundComponent.scene2DMainCamera2D = false;
                scene2D.setSceneMainCamera2D(null);
            }
        }
        cameraComponent.scene2DMainCamera2D = scene2DMainCamera2D;
        if(scene2D != null) {
            scene2D.setSceneMainCamera2D(cameraComponent.scene2DMainCamera2D ? cameraComponent.entity : scene2D.getSceneMainCamera2D());
        }
    }

    public void setScene2DMainCamera2D(CameraComponent cameraComponent, boolean scene2DMainCamera2D)
    {
        if(SceneManager.currentSceneManager != null) {
            setScene2DMainCamera2D(cameraComponent, scene2DMainCamera2D, SceneManager.currentSceneManager.getCurrentScene2D());
        }
    }
}
