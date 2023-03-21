package SungearEngine2D.ECSOrientedScripts.Systems;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.CameraComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Input.PC.Mouse;
import Core2D.Log.Log;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.ECSOrientedScripts.Components.CameraController2DComponent;
import SungearEngine2D.ECSOrientedScripts.Components.MoveToComponent;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera2DControllerSystem extends System
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        CameraController2DComponent cameraController2DComponent = componentsQuery.getComponent(CameraController2DComponent.class);
        CameraComponent cameraComponent = componentsQuery.getComponent(CameraComponent.class);

        if(ViewsManager.isSceneViewFocused && cameraController2DComponent != null && cameraComponent != null &&
                cameraController2DComponent.active && cameraComponent.active && cameraComponent.viewMode == CameraComponent.ViewMode.VIEW_MODE_2D) {

            if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
                GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), Resources.Cursors.getCursorResizeAll());
            }
            if (Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
                GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), Resources.Cursors.getCursorArrow());
            }

            if (Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                MoveToComponent moveToComponent = componentsQuery.getComponent(MoveToComponent.class);
                if(moveToComponent != null) {
                    moveToComponent.needMoveTo = false;
                }

                Vector2f currentPosition = new Vector2f(Mouse.getMousePosition());
                Vector2f difference = new Vector2f(currentPosition.x - Mouse.getLastMousePosition().x, currentPosition.y - Mouse.getLastMousePosition().y);
                cameraComponent.position.add(
                        new Vector3f(-difference.x * (1.0f / cameraController2DComponent.scale.x) * cameraController2DComponent.movementSensitivity, -difference.y * (1.0f / cameraController2DComponent.scale.y) * cameraController2DComponent.movementSensitivity, 0f)
                );
            }
        }
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {

    }

    @Override
    public void onMouseScroll(ComponentsQuery componentsQuery, double xOffset, double yOffset)
    {
        CameraController2DComponent cameraController2DComponent = componentsQuery.getComponent(CameraController2DComponent.class);
        CameraComponent cameraComponent = componentsQuery.getComponent(CameraComponent.class);

        if(ViewsManager.isSceneViewFocused && cameraController2DComponent != null && cameraComponent != null &&
        cameraController2DComponent.active && cameraComponent.active && cameraComponent.viewMode == CameraComponent.ViewMode.VIEW_MODE_2D) {

            Vector2f scale = new Vector2f((float) yOffset * cameraController2DComponent.zoomSensitivity * cameraController2DComponent.scale.x,
                    (float) yOffset * cameraController2DComponent.zoomSensitivity * cameraController2DComponent.scale.y);

            cameraController2DComponent.scale.x += scale.x;
            cameraController2DComponent.scale.y += scale.y;
            cameraController2DComponent.scale.x = Math.abs(cameraController2DComponent.scale.x);
            cameraController2DComponent.scale.y = Math.abs(cameraController2DComponent.scale.y);

            Log.CurrentSession.println("scrolling", Log.MessageType.SUCCESS);
        }
    }
}
