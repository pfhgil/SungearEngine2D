package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.Primitives.LineComponent;
import Core2D.ECS.Component.Components.Primitives.PrimitiveComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL;
import Core2D.Graphics.RenderParts.RenderMethod;
import Core2D.ShaderUtils.ShaderUtils;
import org.joml.Vector4f;

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

                ShaderUtils.setUniform(
                        primitiveComponent.shader.getProgramHandler(),
                        "mvpMatrix",
                        transformComponent.getMvpMatrix()
                );
                ShaderUtils.setUniform(
                        primitiveComponent.shader.getProgramHandler(),
                        "color",
                        new Vector4f(entity.getColor())
                );

                if(primitiveComponent instanceof LineComponent) {
                    OpenGL.glCall((params) -> glLineWidth(((LineComponent) primitiveComponent).lineWidth));
                }
                OpenGL.glCall((params) -> glDrawElements(GL_LINES, 2, GL_UNSIGNED_SHORT, 0));
                OpenGL.glCall((params) -> glLineWidth(1.0f));

                primitiveComponent.shader.unBind();

                primitiveComponent.vertexArray.unBind();
            }
        }
    }
}
