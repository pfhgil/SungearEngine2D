package Core2D.ECS.System.Systems;

import Core2D.Debug.DebugDraw;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.Physics.Rigidbody2DComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Log.Log;
import Core2D.Physics.PhysicsWorld;
import Core2D.Scene2D.SceneManager;
import imgui.ImGui;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import javax.swing.*;

public class TransformationsSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        if(!active) return;

        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);
        Rigidbody2DComponent rigidbody2DComponent = componentsQuery.getComponent(Rigidbody2DComponent.class);

        if(transformComponent != null) {
            if(!transformComponent.active) return;

            // если TransformComponent был изменен, то сначала обновляется трансформ rigidbody2d
            // высчитывается разница между прошлым TransformComponent и текущим и разница добавляется к трансформу тела
            if(rigidbody2DComponent != null) {
                Vector2f posDif = new Vector2f(transformComponent.position).add(new Vector2f(transformComponent.lastPosition).negate());
                float rotDif = transformComponent.rotation - transformComponent.lastRotation;

                Vec2 bodyPos = rigidbody2DComponent.getRigidbody2D().getBody().getPosition();
                float bodyRot = rigidbody2DComponent.getRigidbody2D().getBody().getAngle();

                rigidbody2DComponent.getRigidbody2D().getBody().setTransform(bodyPos.add(new Vec2(posDif.x / PhysicsWorld.RATIO, posDif.y / PhysicsWorld.RATIO)),
                        bodyRot + Math.toRadians(rotDif));

                bodyPos = rigidbody2DComponent.getRigidbody2D().getBody().getPosition();
                bodyRot = rigidbody2DComponent.getRigidbody2D().getBody().getAngle();

                Vector2f dif = new Vector2f(bodyPos.x * PhysicsWorld.RATIO - transformComponent.position.x,
                        bodyPos.y * PhysicsWorld.RATIO - transformComponent.position.y);
                float rDif = (float) Math.toDegrees(bodyRot) - transformComponent.rotation;

                transformComponent.position.add(dif);
                transformComponent.rotation += rDif;
            }

            if(hasTransformComponentChanged(transformComponent)) {
                updateTransformComponent(transformComponent);
            }
        }
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {
        if(!active) return;
    }

    public boolean hasTransformComponentChanged(TransformComponent transformComponent)
    {
        if(!active || !transformComponent.active) return false;

        return !transformComponent.position.equals(transformComponent.lastPosition) ||
                transformComponent.rotation != transformComponent.lastRotation ||
                !transformComponent.scale.equals(transformComponent.lastScale);
    }

    public void updateTransformComponent(TransformComponent transformComponent)
    {
        updateTransformComponent(transformComponent, true);
    }

    public void updateTransformComponent(TransformComponent transformComponent, boolean updateLastTransformations)
    {
        if(!active || !transformComponent.active) return;

        updateTranslationMatrix(transformComponent);
        updateRotationMatrix(transformComponent);
        updateScaleMatrix(transformComponent);

        updateModelMatrix(transformComponent);

        if(!updateLastTransformations) return;

        transformComponent.lastPosition.set(transformComponent.position);
        transformComponent.lastRotation = transformComponent.rotation;
        transformComponent.lastScale.set(transformComponent.scale);
    }

    public void updateModelMatrix(TransformComponent transformComponent)
    {
        if(!active || !transformComponent.active) return;

        Matrix4f intermediateMatrix = new Matrix4f().mul(transformComponent.translationMatrix).mul(transformComponent.rotationMatrix).mul(transformComponent.scaleMatrix);

        if(transformComponent.parentTransformComponent != null) {
            transformComponent.modelMatrix.set(transformComponent.parentTransformComponent.modelMatrix).mul(intermediateMatrix);
        } else {
            transformComponent.modelMatrix.set(intermediateMatrix);
        }
    }

    public void updateTranslationMatrix(TransformComponent transformComponent)
    {
        if(!active || transformComponent.position.equals(transformComponent.lastPosition) || !transformComponent.active) return;

        //transformComponent.translationMatrix.identity();
        transformComponent.translationMatrix.setTranslation(transformComponent.position.x, transformComponent.position.y, 0.0f);
    }

    public void updateRotationMatrix(TransformComponent transformComponent)
    {
        if(!active || transformComponent.rotation == transformComponent.lastRotation || !transformComponent.active) return;

        transformComponent.rotationMatrix.identity();
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateLocalX(Math.toRadians(0));
        rotationQ.rotateLocalY(Math.toRadians(0));
        rotationQ.rotateLocalZ(Math.toRadians(transformComponent.rotation));

        transformComponent.rotationMatrix.rotateAround(rotationQ, transformComponent.centre.x, transformComponent.centre.y, 0.0f);
    }

    public void updateScaleMatrix(TransformComponent transformComponent)
    {
        if(!active || transformComponent.scale.equals(transformComponent.lastScale) || !transformComponent.active) return;

        transformComponent.scaleMatrix.identity();
        transformComponent.scaleMatrix.scale(transformComponent.scale.x, transformComponent.scale.y, 1.0f);
    }

    public Matrix4f getMVPMatrix(TransformComponent transformComponent, Camera2DComponent camera2DComponent)
    {
        return new Matrix4f(camera2DComponent.projectionMatrix).mul(camera2DComponent.viewMatrix)
                .mul(transformComponent.modelMatrix);
    }


    // вспомогательные методы для изменения трансформации
}
