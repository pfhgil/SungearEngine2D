package Core2D.ECS.System.Systems;

import Core2D.Common.Interfaces.RunInThread;
import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.Primitives.BoxComponent;
import Core2D.ECS.Component.Components.Primitives.CircleComponent;
import Core2D.ECS.Component.Components.Primitives.PrimitiveComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Log.Log;
import Core2D.Utils.MathUtils;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.ShaderUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.swing.*;

import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL11C.glLineWidth;

public class PrimitivesSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        PrimitiveComponent primitiveComponent = componentsQuery.getComponent(PrimitiveComponent.class);

        if(primitiveComponent != null && primitiveComponent.active) {
            updatePrimitiveBaseComponent(primitiveComponent);

            if(primitiveComponent instanceof CircleComponent circleComponent) {
                updateCircleComponent(circleComponent);
            } else if(primitiveComponent instanceof BoxComponent boxComponent) {
                updateBoxComponent(boxComponent);
            }
        }
    }

    public void updatePrimitiveBaseComponent(PrimitiveComponent primitiveComponent)
    {
        if(primitiveComponent == null || !primitiveComponent.active || !primitiveComponent.entity.active) return;

        if (!primitiveComponent.color.equals(primitiveComponent.lastColor)) {
            primitiveComponent.lastColor.set(primitiveComponent.color);

            for (LineData lineData : primitiveComponent.linesData) {
                lineData.color.set(primitiveComponent.color);
            }
        }

        if (!primitiveComponent.offset.equals(primitiveComponent.lastOffset)) {
            primitiveComponent.lastOffset.set(primitiveComponent.offset);

            for (LineData lineData : primitiveComponent.linesData) {
                lineData.offset.set(primitiveComponent.offset);
            }
        }

        if (primitiveComponent.linesWidth != primitiveComponent.lastLinesWidth) {
            primitiveComponent.lastLinesWidth = primitiveComponent.linesWidth;

            for (LineData lineData : primitiveComponent.linesData) {
                lineData.lineWidth = primitiveComponent.linesWidth;
            }
        }
    }

    public void updateCircleComponent(CircleComponent circleComponent)
    {
        if(circleComponent == null || !circleComponent.active || !circleComponent.entity.active) return;

        if (circleComponent.lastAngleIncrement != circleComponent.angleIncrement) {
            circleComponent.lastAngleIncrement = circleComponent.angleIncrement;

            int linesNum = 360 / circleComponent.angleIncrement;

            circleComponent.linesData = new LineData[linesNum];
            for (int i = 0; i < circleComponent.linesData.length; i++) {
                circleComponent.linesData[i] = new LineData();
                circleComponent.linesData[i].color.set(circleComponent.color);
                circleComponent.linesData[i].offset.set(circleComponent.offset);
                circleComponent.linesData[i].lineWidth = circleComponent.linesWidth;
            }

            Log.CurrentSession.println("invoked", Log.MessageType.WARNING);


            circleComponent.lastRadius = -1f;
        }

        if (circleComponent.lastRadius != circleComponent.radius) {
            circleComponent.lastRadius = circleComponent.radius;

            int currentAngle = 0;

            Vector2f currentPoint = new Vector2f(0, circleComponent.radius);
            MathUtils.rotate(currentPoint, -circleComponent.angleIncrement, new Vector2f());
            Vector2f lastPoint = new Vector2f();

            for (int i = 0; i < circleComponent.linesData.length; i++) {
                Vector2f tmp = new Vector2f(0, circleComponent.radius);
                MathUtils.rotate(tmp, currentAngle, new Vector2f());
                lastPoint.set(currentPoint);
                currentPoint.set(tmp);

                circleComponent.linesData[i].start.set(lastPoint, circleComponent.linesData[i].start.z);
                circleComponent.linesData[i].end.set(currentPoint, circleComponent.linesData[i].end.z);

                currentAngle += circleComponent.angleIncrement;
            }
        }
    }

    public void updateBoxComponent(BoxComponent boxComponent)
    {
        if(boxComponent == null || !boxComponent.active || !boxComponent.entity.active) return;

        if(!boxComponent.lastSize.equals(boxComponent.size)) {
            boxComponent.lastSize.set(boxComponent.size);

            Vector2f size = boxComponent.size;

            boxComponent.linesData[0].start.set(-size.x / 2.0f, -size.y / 2.0f, boxComponent.linesData[0].start.z);
            boxComponent.linesData[0].end.set(-size.x / 2.0f, size.y / 2.0f, boxComponent.linesData[0].end.z);

            boxComponent.linesData[1].start.set(-size.x / 2.0f, size.y / 2.0f, boxComponent.linesData[1].start.z);
            boxComponent.linesData[1].end.set(size.x / 2.0f, size.y / 2.0f, boxComponent.linesData[1].end.z);

            boxComponent.linesData[2].start.set(size.x / 2.0f, size.y / 2.0f, boxComponent.linesData[2].start.z);
            boxComponent.linesData[2].end.set(size.x / 2.0f, -size.y / 2.0f, boxComponent.linesData[2].end.z);

            boxComponent.linesData[3].start.set(size.x / 2.0f, -size.y / 2.0f, boxComponent.linesData[3].start.z);
            boxComponent.linesData[3].end.set(-size.x / 2.0f, -size.y / 2.0f, boxComponent.linesData[3].end.z);
        }
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime) {

    }

    @Override
    public void renderEntity(Entity entity, CameraComponent cameraComponent)
    {
        renderPrimitiveComponent(entity, cameraComponent,null);
    }

    @Override
    public void renderEntity(Entity entity, CameraComponent cameraComponent, Shader shader)
    {
        renderPrimitiveComponent(entity, cameraComponent,shader);
    }

    private void renderPrimitiveComponent(Entity entity, CameraComponent cameraComponent, Shader shader)
    {
        if(!entity.active) return;

        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        if(transformComponent == null || !transformComponent.active) return;

        for(PrimitiveComponent primitiveComponent : entity.getAllComponents(PrimitiveComponent.class)) {
            if(!primitiveComponent.active) return;

            if(shader == null) {
                shader = primitiveComponent.shader;
            }

            // использовать шейдер
            shader.bind();

            if (primitiveComponent.scaleWithEntity) {
                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "mvpMatrix",
                        ECSWorld.getCurrentECSWorld().transformationsSystem.getMVPMatrix(transformComponent, cameraComponent)
                );
            } else {
                Matrix4f resultMatrix = new Matrix4f(ECSWorld.getCurrentECSWorld().transformationsSystem.getMVPMatrix(transformComponent, cameraComponent));
                Vector3f scale = MatrixUtils.getScale(transformComponent.modelMatrix);
                resultMatrix.scale(new Vector3f(1.0f / scale.x, 1.0f / scale.y, 1.0f / scale.z));
                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "mvpMatrix",
                        resultMatrix
                );
            }

            for (LineData lineData : primitiveComponent.linesData) {
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

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "verticesPositions[0]",
                        lineData.start
                );

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "verticesPositions[1]",
                        lineData.end
                );

                OpenGL.glCall((params) -> glLineWidth(lineData.lineWidth));
                OpenGL.glDrawArrays(GL_LINES, 0, 2);
                OpenGL.glCall((params) -> glLineWidth(1.0f));
            }
        }
    }
}
