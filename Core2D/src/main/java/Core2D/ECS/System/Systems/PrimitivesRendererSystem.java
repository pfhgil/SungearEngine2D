package Core2D.ECS.System.Systems;

import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Components.Primitives.LineComponent;
import Core2D.ECS.Component.Components.Primitives.PrimitiveComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.System.System;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL;
import Core2D.Graphics.RenderParts.RenderMethod;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.Utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import static org.lwjgl.opengl.GL11C.*;

public class PrimitivesRendererSystem extends System
{
    @RenderMethod
    public void render()
    {
        if(entity != null) {
            if(entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            List<PrimitiveComponent> primitiveComponents = entity.getAllComponents(PrimitiveComponent.class);
            for (PrimitiveComponent primitiveComponent : primitiveComponents) {
                primitiveComponent.vertexArray.bind();

                // использовать шейдер
                primitiveComponent.shader.bind();

                if(primitiveComponent.scaleWithEntity) {
                    ShaderUtils.setUniform(
                            primitiveComponent.shader.getProgramHandler(),
                            "mvpMatrix",
                            transformComponent.getMvpMatrix()
                    );
                } else {
                    Matrix4f resultMatrix = new Matrix4f(transformComponent.getMvpMatrix());
                    Vector2f scale = MatrixUtils.getScale(transformComponent.getTransform().getResultModelMatrix());
                    resultMatrix.scale(new Vector3f(1.0f / scale.x, 1.0f / scale.y, 1.0f));
                    ShaderUtils.setUniform(
                            primitiveComponent.shader.getProgramHandler(),
                            "mvpMatrix",
                            resultMatrix
                    );
                }

                for(LineData lineData : primitiveComponent.getLinesData()) {
                    ShaderUtils.setUniform(
                            primitiveComponent.shader.getProgramHandler(),
                            "offset",
                            lineData.offset
                    );

                    ShaderUtils.setUniform(
                            primitiveComponent.shader.getProgramHandler(),
                            "color",
                            lineData.color
                    );

                    for(int i = 0; i < lineData.getVertices().length; i++) {
                        ShaderUtils.setUniform(
                                primitiveComponent.shader.getProgramHandler(),
                                "verticesPositions[" + i + "]",
                                lineData.getVertices()[i]
                        );
                    }

                    OpenGL.glCall((params) -> glLineWidth(lineData.lineWidth));
                    OpenGL.glCall((params) -> glDrawArrays(GL_LINES, 0, 2));
                    OpenGL.glCall((params) -> glLineWidth(1.0f));
                }

                primitiveComponent.shader.unBind();

                primitiveComponent.vertexArray.unBind();
            }
        }
    }
}
