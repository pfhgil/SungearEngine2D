package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL;
import Core2D.Graphics.RenderParts.RenderMethod;
import Core2D.ShaderUtils.ShaderUtils;

import java.util.List;

import static org.lwjgl.opengl.GL11C.*;

public class MeshRendererSystem extends System
{
    @RenderMethod
    public void render()
    {
        if(entity != null) {
            if (entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            List<MeshComponent> meshComponents = entity.getAllComponents(MeshComponent.class);
            for(MeshComponent meshComponent : meshComponents) {
                if (meshComponent != null) {
                    if (transformComponent == null) return;
                    // использую VAO, текстуру и шейдер
                    meshComponent.vertexArray.bind();
                    meshComponent.texture.bind();
                    if (meshComponent.material2D != null && meshComponent.material2D.material2DData != null) {
                        OpenGL.glCall((params) -> glBlendFunc(meshComponent.material2D.material2DData.blendSourceFactor, meshComponent.material2D.material2DData.blendDestinationFactor));
                    }
                    meshComponent.shader.bind();

                    ShaderUtils.setUniform(
                            meshComponent.shader.getProgramHandler(),
                            "mvpMatrix",
                            transformComponent.getMvpMatrix()
                    );
                    ShaderUtils.setUniform(
                            meshComponent.shader.getProgramHandler(),
                            "color",
                            entity.getColor()
                    );
                    ShaderUtils.setUniform(
                            meshComponent.shader.getProgramHandler(),
                            "drawMode",
                            meshComponent.textureDrawMode
                    );
                    ShaderUtils.setUniform(
                            meshComponent.shader.getProgramHandler(),
                            "sampler",
                            meshComponent.texture.getFormattedTextureBlock()
                    ); //FIXME: сделать нормальный метод для того что бы задовать сразу несколько юниформ

                    // нарисовать два треугольника
                    OpenGL.glCall((params) -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0));

                    // прекращаю использование шейдера, текстуры и VAO
                    meshComponent.shader.unBind();
                    meshComponent.texture.unBind();
                    meshComponent.vertexArray.unBind();
                }
            }
        }
    }
}
