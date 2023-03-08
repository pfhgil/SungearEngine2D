package Core2D.ECS.System.Systems;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Log.Log;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class TransformationsSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        if(!active) return;

        TransformComponent transformComponent = componentsQuery.getComponent(TransformComponent.class);
        //Rigidbody2DComponent rigidbody2DComponent = componentsQuery.getComponent(Rigidbody2DComponent.class);

        if(transformComponent != null) {
            if(!transformComponent.active) return;
            if(hasTransformComponentChanged(transformComponent)) {
                // устанавливаем позицию у rigidbody2d и обновляем матрицы

                updateAllMatrices(transformComponent);

                transformComponent.lastPosition.set(transformComponent.position);
                transformComponent.lastRotation = transformComponent.rotation;
                transformComponent.lastScale.set(transformComponent.scale);

                Log.CurrentSession.println("transform has changed: " + transformComponent.entity.name, Log.MessageType.WARNING);
            }
            //Log.CurrentSession.println("transform exists", Log.MessageType.WARNING);
            /*
            if(transformComponent.isDirty()) {
                // устанавливаем позицию у rigidbody2d и обновляем матрицы

                updateTranslationMatrix(transformComponent);
                updateRotationMatrix(transformComponent);
                updateScaleMatrix(transformComponent);

                updateModelMatrix(transformComponent);
            } else if(!transformComponent.isDirty() && rigidbody2DComponent != null) {
                // устанавливаю позицию трансформа как у rigidbody2d

                Transform bodyTransform = rigidbody2DComponent.getRigidbody2D().getBody().getTransform();
                transformComponent.setPosition(bodyTransform.position.x, bodyTransform.position.y);
                transformComponent.setRotation((float) Math.toDegrees(bodyTransform.getAngle()));
            }

             */
        }

        //Log.CurrentSession.println("sdfsdf", Log.MessageType.WARNING);
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

    public void updateAllMatrices(TransformComponent transformComponent)
    {
        if(!active || !transformComponent.active) return;

        updateTranslationMatrix(transformComponent);
        updateRotationMatrix(transformComponent);
        updateScaleMatrix(transformComponent);

        updateModelMatrix(transformComponent);
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
