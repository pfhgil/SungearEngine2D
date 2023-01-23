package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.ShaderUtils.ShaderUtils;

import java.util.List;

import static org.lwjgl.opengl.GL11C.*;

public class MeshRendererSystem extends System
{
    public void render()
    {
        if(entity != null) {
            if (entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if (transformComponent == null) return;

            List<MeshComponent> meshComponents = entity.getAllComponents(MeshComponent.class);
            for(MeshComponent meshComponent : meshComponents) {
                renderMeshComponent(transformComponent, meshComponent, meshComponent.shader);
            }
        }
    }

    public void render(Shader shader)
    {
        if(entity != null) {
            if (entity.isShouldDestroy()) return;

            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            if (transformComponent == null) return;

            List<MeshComponent> meshComponents = entity.getAllComponents(MeshComponent.class);
            for(MeshComponent meshComponent : meshComponents) {
                renderMeshComponent(transformComponent, meshComponent, shader);
            }
        }
    }

    private void renderMeshComponent(TransformComponent transformComponent, MeshComponent meshComponent, Shader shader)
    {
        // использую VAO, текстуру и шейдер
        meshComponent.vertexArray.bind();
        meshComponent.texture.bind();
        if (meshComponent.material2D != null && meshComponent.material2D.material2DData != null) {
            OpenGL.glCall((params) -> glBlendFunc(meshComponent.material2D.material2DData.blendSourceFactor, meshComponent.material2D.material2DData.blendDestinationFactor));
        }
        shader.bind();

        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "mvpMatrix",
                transformComponent.getMvpMatrix()
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "color",
                entity.getColor()
        );
        ShaderUtils.setUniform(
                shader.getProgramHandler(),
                "sampler",
                meshComponent.texture.getFormattedTextureBlock()
        );

        // нарисовать два треугольника
        OpenGL.glCall((params) -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0));

        // прекращаю использование шейдера, текстуры и VAO
        meshComponent.texture.unBind();
        meshComponent.vertexArray.unBind();
    }
}
