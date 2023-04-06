package Core2D.ECS.System.Systems.Cameras;

import Core2D.Common.Interfaces.NonDuplicated;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.Camera.CameraController2DComponent;
import Core2D.ECS.Component.Components.Transform.MoveToComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import Core2D.Log.Log;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class CamerasController2DSystem extends System implements NonDuplicated
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        CameraController2DComponent controllerComponent = componentsQuery.getComponent(CameraController2DComponent.class);
        CameraComponent cameraComponent = componentsQuery.getComponent(CameraComponent.class);

        if(controllerComponent != null && cameraComponent != null &&
                controllerComponent.active && cameraComponent.active && cameraComponent.viewMode == CameraComponent.ViewMode.VIEW_MODE_2D) {

            if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
                GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), Mouse.DEFAULT_RESIZE_ALL_CURSOR);

                //Mouse.updateMousePos();
            }
            if (Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
                GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), Mouse.DEFAULT_ARROW_CURSOR);
            }

            if (Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                MoveToComponent moveToComponent = componentsQuery.getComponent(MoveToComponent.class);
                if(moveToComponent != null) {
                    moveToComponent.needMoveTo = false;
                }

                //Mouse.updateMousePos();

                Vector2f deltaMousePos = Mouse.getDeltaPosition();
                controllerComponent.cameraPosition.add(
                        new Vector3f(-deltaMousePos.x * (1.0f / controllerComponent.cameraScale.x) * controllerComponent.movementSensitivity,
                                -deltaMousePos.y * (1.0f / controllerComponent.cameraScale.y) * controllerComponent.movementSensitivity, 0f)
                );
            }

            if (Keyboard.keyDown(GLFW.GLFW_KEY_C) && Keyboard.keyDown(GLFW.GLFW_KEY_V) && Keyboard.keyPressed(GLFW.GLFW_KEY_R)) {
                controllerComponent.cameraPosition.set(0f);
                controllerComponent.cameraRotation.set(0f);
                controllerComponent.cameraScale.set(1f);
            }

            if(Keyboard.keyDown(GLFW.GLFW_KEY_C) && Keyboard.keyPressed(GLFW.GLFW_KEY_3)) {
                cameraComponent.viewMode = CameraComponent.ViewMode.VIEW_MODE_3D;
            }

            cameraComponent.position.set(controllerComponent.cameraPosition);
            cameraComponent.rotation.set(controllerComponent.cameraRotation);
            cameraComponent.scale.set(controllerComponent.cameraScale);
        }
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {

    }

    @Override
    public void onMouseScroll(ComponentsQuery componentsQuery, double xOffset, double yOffset)
    {
        CameraController2DComponent cameraController = componentsQuery.getComponent(CameraController2DComponent.class);
        CameraComponent cameraComponent = componentsQuery.getComponent(CameraComponent.class);

        if(cameraController != null && cameraComponent != null &&
        cameraController.active && cameraComponent.active && cameraComponent.viewMode == CameraComponent.ViewMode.VIEW_MODE_2D) {

            Vector2f scale = new Vector2f((float) yOffset * cameraController.zoomSensitivity * cameraController.cameraScale.x,
                    (float) yOffset * cameraController.zoomSensitivity * cameraController.cameraScale.y);

            cameraController.cameraScale.x += scale.x;
            cameraController.cameraScale.y += scale.y;
            cameraController.cameraScale.x = Math.abs(cameraController.cameraScale.x);
            cameraController.cameraScale.y = Math.abs(cameraController.cameraScale.y);

            Log.CurrentSession.println("scrolling", Log.MessageType.SUCCESS);
        }
    }
}
