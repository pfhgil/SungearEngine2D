import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.ShaderUtils.ShaderUtils;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11.*;

public class LineMeshRenderer extends System
{
    public void render()
    {
        if(entity != null) {
            MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
            if (meshComponent != null) {
                if (entity.isShouldDestroy()) return;

                TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

                if (meshComponent != null) {
                    if (transformComponent == null) return;
                    // использую VAO, текстуру и шейдер
                    meshComponent.vertexArray.bind();
                    meshComponent.shader.bind();

                    ShaderUtils.setUniform(
                            meshComponent.shader.getProgramHandler(),
                            "mvpMatrix",
                            transformComponent.getMvpMatrix()
                    );
                    ShaderUtils.setUniform(
                            meshComponent.shader.getProgramHandler(),
                            "color",
                            new Vector4f(1.0f, 0.0f, 0.0f, 1.0f)
                    );
                    ShaderUtils.setUniform(
                            meshComponent.shader.getProgramHandler(),
                            "sampler",
                            meshComponent.texture.getFormattedTextureBlock()
                    ); //FIXME: сделать нормальный метод для того что бы задовать сразу несколько юниформ

                    glLineWidth(10.0f);
                    // нарисовать линии
                    glDrawElements(GL11C.GL_LINES, 6, GL_UNSIGNED_SHORT, 0);

                    // прекращаю использование шейдера, текстуры и VAO
                    //meshComponent.shader.unBind();
                    //meshComponent.texture.unBind();
                    meshComponent.vertexArray.unBind();
                }
            }
        }
    }

    public void update()
    {

    }
    
    public void deltaUpdate(float deltaTime)
    {
        
    }
    
    public void collider2DEnter(Entity otherObj)
    {
        
    }
    
    public void collider2DExit(Entity otherObj)
    {
        
    }
}