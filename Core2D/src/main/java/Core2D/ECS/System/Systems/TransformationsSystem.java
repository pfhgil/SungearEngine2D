package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.Physics.Rigidbody2DComponent;
import Core2D.ECS.Component.Components.Transform.MoveToComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Settings.PhysicsSettings;
import org.jbox2d.common.Vec2;
import org.joml.*;
import org.joml.Math;

public class TransformationsSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);
        Rigidbody2DComponent rigidbody2DComponent = componentsQuery.getComponent(Rigidbody2DComponent.class);

        if(transformComponent != null) {
            if(!transformComponent.active) return;

            // если TransformComponent был изменен, то сначала обновляется трансформ rigidbody2d
            // высчитывается разница между прошлым TransformComponent и текущим и разница добавляется к трансформу тела
            if(rigidbody2DComponent != null) {
                updateRigidbody2D(transformComponent, rigidbody2DComponent);
            }

            if(hasTransformComponentChanged(transformComponent)) {
                updateTransformComponent(transformComponent);
            }
        }
    }

    // for 2d (box2d)
    public void updateRigidbody2D(TransformComponent transformComponent, Rigidbody2DComponent rigidbody2DComponent)
    {
        if(rigidbody2DComponent.body == null) return;

        Vector3f posDif = new Vector3f(transformComponent.position).add(new Vector3f(transformComponent.lastPosition).negate());
        float rotDifZ = transformComponent.rotation.z - transformComponent.lastRotation.z;

        Vec2 bodyPos = rigidbody2DComponent.body.getPosition();
        float bodyRot = rigidbody2DComponent.body.getAngle();

        rigidbody2DComponent.body.setTransform(bodyPos.add(new Vec2(posDif.x / PhysicsSettings.ratio, posDif.y / PhysicsSettings.ratio)),
                bodyRot + Math.toRadians(rotDifZ));

        bodyPos = rigidbody2DComponent.body.getPosition();
        bodyRot = rigidbody2DComponent.body.getAngle();

        Vector2f dif = new Vector2f(bodyPos.x * PhysicsSettings.ratio - transformComponent.position.x,
                bodyPos.y * PhysicsSettings.ratio - transformComponent.position.y);
        float box2DRotDifZ = (float) Math.toDegrees(bodyRot) - transformComponent.rotation.z;

        transformComponent.position.add(new Vector3f(dif.x, dif.y, 0f));
        transformComponent.rotation.z += box2DRotDifZ;
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {
        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);
        if (transformComponent == null) return;
        moveTo(transformComponent, componentsQuery, deltaTime);
    }

    public boolean hasTransformComponentChanged(TransformComponent transformComponent)
    {
        if(!active || !transformComponent.active) return false;

        return !transformComponent.position.equals(transformComponent.lastPosition) ||
                !transformComponent.rotation.equals(transformComponent.lastRotation) ||
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
        transformComponent.lastRotation.set(transformComponent.rotation);
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
        transformComponent.translationMatrix.setTranslation(transformComponent.position.x, transformComponent.position.y, transformComponent.position.z);
    }

    public void updateRotationMatrix(TransformComponent transformComponent)
    {
        if(!active || transformComponent.rotation.equals(transformComponent.lastRotation) || !transformComponent.active) return;

        transformComponent.rotationMatrix.identity();
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateLocalX(Math.toRadians(transformComponent.rotation.x));
        rotationQ.rotateLocalY(Math.toRadians(transformComponent.rotation.y));
        rotationQ.rotateLocalZ(Math.toRadians(transformComponent.rotation.z));

        transformComponent.rotationMatrix.rotateAround(rotationQ, transformComponent.center.x, transformComponent.center.y, transformComponent.center.z);
    }

    public void updateScaleMatrix(TransformComponent transformComponent)
    {
        if(!active || transformComponent.scale.equals(transformComponent.lastScale) || !transformComponent.active) return;

        transformComponent.scaleMatrix.identity();
        transformComponent.scaleMatrix.scale(transformComponent.scale.x, transformComponent.scale.y, transformComponent.scale.z);
    }

    public Matrix4f getMVPMatrix(TransformComponent transformComponent, CameraComponent cameraComponent)
    {
        return new Matrix4f(cameraComponent.projectionMatrix).mul(cameraComponent.viewMatrix)
                .mul(transformComponent.modelMatrix);
    }


    // вспомогательные методы для изменения трансформации
    private void moveTo(TransformComponent transformComponent, ComponentsQuery componentsQuery, float deltaTime)
    {
        MoveToComponent moveToComponent = componentsQuery.getComponent(MoveToComponent.class);

        if(moveToComponent == null) return;

        if(moveToComponent.needMoveTo) {

            Vector2f dif = new Vector2f(moveToComponent.destinationPosition.x - transformComponent.position.x, moveToComponent.destinationPosition.y - transformComponent.position.y).mul(moveToComponent.ration).mul(deltaTime);
            if(java.lang.Math.abs(dif.x) > moveToComponent.errorRate || java.lang.Math.abs(dif.y) > moveToComponent.errorRate) {
                transformComponent.position.add(new Vector3f(dif.x, dif.y, 0f));
            } else {
                moveToComponent.needMoveTo= false;
            }
        }
    }
}
