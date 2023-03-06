package Core2D.ECS.System.Systems;

import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.Primitives.PrimitiveComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.ShaderUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;

public class PrimitivesRendererSystem extends System
{
    @Override
    public void renderEntity(Entity entity, Camera2DComponent camera2DComponent)
    {
        /*
        if(entity != null) {
            if(entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if(transformComponent == null) return;

            List<PrimitiveComponent> primitiveComponents = entity.getAllComponents(PrimitiveComponent.class);
            for (PrimitiveComponent primitiveComponent : primitiveComponents) {
                renderPrimitiveComponent(camera2DComponent, transformComponent, primitiveComponent, primitiveComponent.shader);
            }
        }

         */
    }

    @Override
    public void renderEntity(Entity entity, Camera2DComponent camera2DComponent, Shader shader)
    {
        /*
        if(entity != null) {
            if(entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if(transformComponent == null) return;

            List<PrimitiveComponent> primitiveComponents = entity.getAllComponents(PrimitiveComponent.class);
            for (PrimitiveComponent primitiveComponent : primitiveComponents) {
                renderPrimitiveComponent(camera2DComponent, transformComponent, primitiveComponent, shader);
            }
        }

         */
    }

    private void renderPrimitiveComponent(Camera2DComponent camera2DComponent, TransformComponent transformComponent, PrimitiveComponent primitiveComponent, Shader shader)
    {
        primitiveComponent.vertexArray.bind();

        // использовать шейдер
        shader.bind();

        if(primitiveComponent.scaleWithEntity) {
            ShaderUtils.setUniform(
                    shader.getProgramHandler(),
                    "mvpMatrix",
                    ECSWorld.getCurrentECSWorld().transformationsSystem.getMVPMatrix(transformComponent, camera2DComponent)
            );
        } else {
            Matrix4f resultMatrix = new Matrix4f(ECSWorld.getCurrentECSWorld().transformationsSystem.getMVPMatrix(transformComponent, camera2DComponent));
            Vector2f scale = MatrixUtils.getScale(transformComponent.modelMatrix);
            resultMatrix.scale(new Vector3f(1.0f / scale.x, 1.0f / scale.y, 1.0f));
            ShaderUtils.setUniform(
                    shader.getProgramHandler(),
                    "mvpMatrix",
                    resultMatrix
            );
        }

        for(LineData lineData : primitiveComponent.getLinesData()) {
            ShaderUtils.setUniform(
                    shader.getProgramHandler(),
                    "offset",
                    lineData.offset
            );

            ShaderUtils.setUniform(
                    shader.getProgramHandler(),
                    "color",
                    lineData.color
            );

            for(int i = 0; i < lineData.getVertices().length; i++) {
                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "verticesPositions[" + i + "]",
                        lineData.getVertices()[i]
                );
            }

            OpenGL.glCall((params) -> glLineWidth(lineData.lineWidth));
            OpenGL.glCall((params) -> glDrawArrays(GL_LINES, 0, 2));
            OpenGL.glCall((params) -> glLineWidth(1.0f));
        }

        primitiveComponent.vertexArray.unBind();
    }
}
