package Core2D.ECS.System.Systems;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.CameraComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.NonRemovable;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.FrameBuffer;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.ShaderUtils;
import org.joml.*;

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
        CameraComponent cameraComponent = componentsQuery.getComponent(CameraComponent.class);

        if (cameraComponent != null) {
            //Log.CurrentSession.println("camera rendering", Log.MessageType.SUCCESS);

                //Log.CurrentSession.println("camera name: " + camera2DComponent.entity.name, Log.MessageType.INFO);
            Vector2i windowSize = Core2D.getWindow().getSize();
            cameraComponent.viewportSize.set(windowSize);

            if(cameraComponent.viewMode == CameraComponent.ViewMode.VIEW_MODE_2D) {
                cameraComponent.projectionMatrix.identity().ortho2D(-cameraComponent.viewportSize.x / 2.0f, cameraComponent.viewportSize.x / 2.0f,
                        -cameraComponent.viewportSize.y / 2.0f, cameraComponent.viewportSize.y / 2.0f);
                //Log.CurrentSession.println("dfdsfs", Log.MessageType.SUCCESS);
            } else {
                cameraComponent.projectionMatrix.identity().perspective(
                        cameraComponent.FOV,
                        windowSize.x / (float) windowSize.y,
                        cameraComponent.nearPlane,
                        cameraComponent.farPlane
                        );
            }

            //camera2DComponent.projectionMatrix.scale(camera2DComponent.zoom);

            updateViewMatrix(cameraComponent);

            // TODO: вынести рендер в отдельный метод (чтобы в будущем была возможность разбить на потоки)
            if (SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null && cameraComponent.render) {
                cameraComponent.frameBuffer.bind();
                cameraComponent.frameBuffer.clear();
                for(CameraComponent.CameraCallback cameraCallback : cameraComponent.cameraCallbacks) {
                    cameraCallback.preRender();
                }
                cameraComponent.frameBuffer.unBind();

                for (Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                    PostprocessingLayer ppLayerFound = cameraComponent.postprocessingLayers.stream().filter(ppl -> ppl.getEntitiesLayerToRender() == layer).findFirst().orElse(null);
                    if (ppLayerFound != null && ppLayerFound.render) {
                        ppLayerFound.getFrameBuffer().bind();
                        ppLayerFound.getFrameBuffer().clear();

                        Graphics.getMainRenderer().render(ppLayerFound.getEntitiesLayerToRender(), cameraComponent);

                        ppLayerFound.getFrameBuffer().unBind();
                    } else if(ppLayerFound == null) {
                        cameraComponent.frameBuffer.bind();
                        Graphics.getMainRenderer().render(layer, cameraComponent);
                        cameraComponent.frameBuffer.unBind();
                    }
                }

                cameraComponent.frameBuffer.bind();
                for(CameraComponent.CameraCallback cameraCallback : cameraComponent.cameraCallbacks) {
                    cameraCallback.postRender();
                }
                cameraComponent.frameBuffer.unBind();

                // quad render -----------------------------------------------------

                cameraComponent.resultFrameBuffer.bind();
                cameraComponent.resultFrameBuffer.clear();
                cameraComponent.ppQuadVertexArray.bind();

                for (Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                    Shader shader = cameraComponent.postprocessingDefaultShader;
                    FrameBuffer frameBufferToBind = cameraComponent.frameBuffer;

                    PostprocessingLayer ppLayerFound = cameraComponent.postprocessingLayers.stream().filter(ppLayer -> ppLayer.getEntitiesLayerToRender() == layer).findFirst().orElse(null);

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
                cameraComponent.ppQuadVertexArray.unBind();
                cameraComponent.resultFrameBuffer.unBind();
            }
        }
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime) {

    }

    public void updateViewMatrix(CameraComponent cameraComponent)
    {
        if(cameraComponent.entity != null) {
            TransformComponent transformComponent = cameraComponent.entity.getComponent(TransformComponent.class);

            if(transformComponent != null) {
                if (cameraComponent.followTranslation) {
                    cameraComponent.position.set(MatrixUtils.getPosition(transformComponent.modelMatrix));
                }
                if (cameraComponent.followRotation) {
                    cameraComponent.rotation.set(MatrixUtils.getEulerRotation(transformComponent.modelMatrix));
                }
                if (cameraComponent.followScale) {
                    cameraComponent.scale.set(MatrixUtils.getScale(transformComponent.modelMatrix));
                }
            }
        }

        cameraComponent.viewMatrix.identity();

        cameraComponent.viewMatrix.scale(new Vector3f(cameraComponent.scale.x, cameraComponent.scale.y, cameraComponent.scale.z));

        Quaternionf rotationQuaternionf = new Quaternionf();
        rotationQuaternionf.rotateLocalX(org.joml.Math.toRadians(-cameraComponent.rotation.x));
        rotationQuaternionf.rotateLocalY(org.joml.Math.toRadians(-cameraComponent.rotation.x));
        rotationQuaternionf.rotateLocalZ(org.joml.Math.toRadians(-cameraComponent.rotation.x));
        cameraComponent.viewMatrix.rotate(rotationQuaternionf);

        cameraComponent.viewMatrix.translate(new Vector3f(-cameraComponent.position.x, -cameraComponent.position.y, 1f));

        //cameraComponent.viewMatrix.m00(cameraComponent.viewMatrix.m00() + cameraComponent.zoom);
        //cameraComponent.viewMatrix.m11(cameraComponent.viewMatrix.m11() + cameraComponent.zoom);
        //cameraComponent.viewMatrix.m22(cameraComponent.viewMatrix.m22() + cameraComponent.zoom);

        //camera2DComponent.viewMatrix.m03(camera2DComponent.viewMatrix.m03() + camera2DComponent.zoom);
        //camera2DComponent.viewMatrix.m13(camera2DComponent.viewMatrix.m13() + camera2DComponent.zoom);
        //camera2DComponent.viewMatrix.m23(camera2DComponent.viewMatrix.m23() + camera2DComponent.zoom);
    }

    public void setPostprocessingDefaultShader(CameraComponent cameraComponent, Shader postprocessingDefaultShader)
    {
        if(cameraComponent.postprocessingDefaultShader != null) {
            cameraComponent.postprocessingDefaultShader.destroy();
        }
        cameraComponent.postprocessingDefaultShader = postprocessingDefaultShader;
    }

    public boolean isPostprocessingLayerExists(CameraComponent cameraComponent, Layer layer)
    {
        return cameraComponent.postprocessingLayers.stream().anyMatch((ppLayer) -> ppLayer.getEntitiesLayerToRender() == layer);
    }

    public PostprocessingLayer getPostprocessingLayerByName(CameraComponent cameraComponent, String name)
    {
        Optional<PostprocessingLayer> foundLayer = cameraComponent.postprocessingLayers.stream().filter(ppLayer -> {
            if(ppLayer.getEntitiesLayerToRender() != null) {
                return ppLayer.getEntitiesLayerToRender().getName().equals(name);
            }
            return false;
        }).findFirst();

        return foundLayer.orElse(null);
    }
}
