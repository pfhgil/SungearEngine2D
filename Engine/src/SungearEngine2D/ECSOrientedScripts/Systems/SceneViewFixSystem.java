package SungearEngine2D.ECSOrientedScripts.Systems;

import Core2D.Common.Interfaces.NonDuplicated;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.Camera.CameraController2DComponent;
import Core2D.ECS.Component.Components.Camera.CameraController3DComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import SungearEngine2D.GUI.Views.ViewsManager;

public class SceneViewFixSystem extends System implements NonDuplicated
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        CameraComponent cameraComponent = componentsQuery.getComponent(CameraComponent.class);
        CameraController2DComponent cameraController2DComponent = componentsQuery.getComponent(CameraController2DComponent.class);
        CameraController3DComponent cameraController3DComponent = componentsQuery.getComponent(CameraController3DComponent.class);

        if(cameraComponent != null) {
            if(cameraController2DComponent != null && cameraComponent.viewMode == CameraComponent.ViewMode.VIEW_MODE_2D) {
                cameraComponent.scale.set(cameraController2DComponent.cameraScale)
                        .mul(ViewsManager.getSceneView().getRatioCameraScale().x, ViewsManager.getSceneView().getRatioCameraScale().y, 1f);
            } else if(cameraController3DComponent != null && cameraComponent.viewMode == CameraComponent.ViewMode.VIEW_MODE_3D) {
                cameraComponent.scale.set(cameraController3DComponent.cameraScale)
                        .mul(ViewsManager.getSceneView().getRatioCameraScale().x, ViewsManager.getSceneView().getRatioCameraScale().y, 1f);
            }
        }
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {

    }
}
