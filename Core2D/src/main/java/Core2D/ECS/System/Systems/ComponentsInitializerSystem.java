package Core2D.ECS.System.Systems;

import Core2D.AssetManager.AssetManager;
import Core2D.Audio.OpenAL;
import Core2D.Debug.DebugDraw;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Physics.Collider2DComponent;
import Core2D.ECS.Component.Components.Physics.Rigidbody2DComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.Common.Interfaces.NonRemovable;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.*;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import org.joml.Vector2i;
import org.lwjgl.openal.AL10;

import static org.lwjgl.opengl.GL11C.glDrawElements;
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

            setScene2DMainCamera2D(cameraComponent, cameraComponent.sceneMainCamera);

            Vector2i screenSize = Graphics.getScreenSize();
            cameraComponent.frameBuffer = new FrameBuffer(screenSize.x, screenSize.y, FrameBuffer.BuffersTypes.ALL_BUFFER, GL_TEXTURE0);
            cameraComponent.resultFrameBuffer = new FrameBuffer(screenSize.x, screenSize.y, FrameBuffer.BuffersTypes.ALL_BUFFER, GL_TEXTURE0);

            cameraComponent.cameraCallbacks.add(new CameraComponent.CameraCallback() {
                @Override
                public void preRender() {

                }

                @Override
                public void postRender() {
                    for(Entity debugEntity : DebugDraw.getDebugPrimitives().values()) {
                        Graphics.getMainRenderer().render(debugEntity, cameraComponent);
                        debugEntity.active = false;
                    }
                }
            });
        } else if(component instanceof TransformComponent transformComponent) {
            ECSWorld.getCurrentECSWorld().transformationsSystem.updateTransformComponent(transformComponent);
        } else if(component instanceof AudioComponent audioComponent) {
            ECSWorld.getCurrentECSWorld().audioSystem.setAudioComponentData(audioComponent,
                    AssetManager.getInstance().getAudioData(audioComponent.path));
        } else if(component instanceof Rigidbody2DComponent rigidbody2DComponent) {
            ECSWorld.getCurrentECSWorld().physicsSystem.addRigidbody2D(rigidbody2DComponent);
        }
    }

    // вызывается при удалении с сущности компонента (система ComponentsManager)
    public void destroyComponent(Component component)
    {
        if(component instanceof CameraComponent cameraComponent) {
            setScene2DMainCamera2D(cameraComponent, false);
            cameraComponent.frameBuffer.destroy();
            cameraComponent.resultFrameBuffer.destroy();
            //camera2DComponent.postprocessingDefaultShader.destroy();

            for(PostprocessingLayer ppLayer : cameraComponent.postprocessingLayers) {
                ppLayer.destroy();
            }
        } else if(component instanceof AudioComponent audioComponent) {
            OpenAL.alCall(params -> AL10.alDeleteSources(audioComponent.sourceHandler));
        } else if(component instanceof Rigidbody2DComponent rigidbody2DComponent) {
            if(rigidbody2DComponent.body != null) {
                ECSWorld.getCurrentECSWorld().physicsSystem.getWorld().destroyBody(rigidbody2DComponent.body);
            }
        } else if(component instanceof Collider2DComponent collider2DComponent) {
            Rigidbody2DComponent rigidbody2DComponent = component.entity.getComponent(Rigidbody2DComponent.class);

            if(rigidbody2DComponent != null) {
                rigidbody2DComponent.body.destroyFixture(collider2DComponent.fixture);
            }
        } else if(component instanceof MeshComponent meshComponent) {
            meshComponent.shader.destroy();
        }
    }

    public void setScene2DMainCamera2D(CameraComponent cameraComponent, boolean scene2DMainCamera2D, Scene2D scene2D)
    {
        if(scene2D != null &&
                scene2DMainCamera2D && scene2D.getSceneMainCamera2D() != null) {
            CameraComponent foundComponent = scene2D.getSceneMainCamera2D().getComponent(CameraComponent.class);
            if(foundComponent != null) {
                foundComponent.sceneMainCamera = false;
                scene2D.setSceneMainCamera2D(null);
            }
        }
        cameraComponent.sceneMainCamera = scene2DMainCamera2D;
        if(scene2D != null) {
            scene2D.setSceneMainCamera2D(cameraComponent.sceneMainCamera ? cameraComponent.entity : scene2D.getSceneMainCamera2D());
        }
    }

    public void setScene2DMainCamera2D(CameraComponent cameraComponent, boolean scene2DMainCamera2D)
    {
        if(SceneManager.currentSceneManager != null) {
            setScene2DMainCamera2D(cameraComponent, scene2DMainCamera2D, SceneManager.currentSceneManager.getCurrentScene2D());
        }
    }
}
