package Core2D.ECS.Camera;

import Core2D.Common.Interfaces.NonDuplicated;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Camera.CameraComponent;
import Core2D.ECS.Camera.CameraController3DComponent;
import Core2D.ECS.Transform.MoveToComponent;
import Core2D.ECS.ComponentsQuery;
import Core2D.ECS.System;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class CamerasController3DSystem extends System implements NonDuplicated
{
    @Override
    public void update(ComponentsQuery componentsQuery)
    {
    }

    private void translateDirectlyForward(CameraController3DComponent controllerComponent, Vector3f forwardDir)
    {
        Quaternionf rotationQ = new Quaternionf();
        rotationQ.rotateZ(Math.toRadians(controllerComponent.cameraRotation.z));
        rotationQ.rotateY(Math.toRadians(controllerComponent.cameraRotation.y));
        rotationQ.rotateX(Math.toRadians(controllerComponent.cameraRotation.x));

        forwardDir.rotate(rotationQ);

        controllerComponent.cameraPosition.add(forwardDir);
    }

    private void translateDirectlyHorizontal(CameraController3DComponent controllerComponent, Vector3f horizontalDir)
    {
        Quaternionf rotationQ = new Quaternionf();
        rotationQ.rotateZ(Math.toRadians(controllerComponent.cameraRotation.z));
        rotationQ.rotateY(Math.toRadians(controllerComponent.cameraRotation.y));

        horizontalDir.rotate(rotationQ);

        controllerComponent.cameraPosition.add(horizontalDir);
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime)
    {
        CameraComponent cameraComponent = componentsQuery.getComponent(CameraComponent.class);
        CameraController3DComponent controllerComponent = componentsQuery.getComponent(CameraController3DComponent.class);

        if(cameraComponent != null && controllerComponent != null &&
                cameraComponent.active && controllerComponent.active && cameraComponent.viewMode == CameraComponent.ViewMode.VIEW_MODE_3D) {
            // rotation
            if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
                GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), Mouse.DEFAULT_RESIZE_ALL_CURSOR);
            }
            if (Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
                GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), Mouse.DEFAULT_ARROW_CURSOR);
            }

            if (Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                MoveToComponent moveToComponent = componentsQuery.getComponent(MoveToComponent.class);
                if (moveToComponent != null) {
                    moveToComponent.needMoveTo = false;
                }

                Vector2f deltaMousePos = Mouse.getDeltaPosition();

                controllerComponent.cameraRotation.x += deltaMousePos.y * controllerComponent.rotationSensitivity;
                controllerComponent.cameraRotation.y -= deltaMousePos.x * controllerComponent.rotationSensitivity;
            }

            float speedMultiplier = 1f;
            if(Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                speedMultiplier = controllerComponent.acceleratingMultiplier;
            }
            if(Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
                speedMultiplier /= controllerComponent.acceleratingMultiplier;
            }

            boolean shiftDown = Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT);

            // moving
            if(!shiftDown && Keyboard.keyDown(GLFW.GLFW_KEY_W)) {
                translateDirectlyForward(controllerComponent, new Vector3f(0f, 0f, -controllerComponent.forwardMovementSpeed * speedMultiplier * deltaTime));
            } else if(shiftDown && Keyboard.keyDown(GLFW.GLFW_KEY_W)) {
                controllerComponent.cameraPosition.y += controllerComponent.verticalMovementSpeed * speedMultiplier * deltaTime;
            }

            if(!shiftDown && Keyboard.keyDown(GLFW.GLFW_KEY_S)) {
                translateDirectlyForward(controllerComponent, new Vector3f(0f, 0f, controllerComponent.forwardMovementSpeed * speedMultiplier * deltaTime));
            } else if(shiftDown && Keyboard.keyDown(GLFW.GLFW_KEY_S)) {
                controllerComponent.cameraPosition.y -= controllerComponent.verticalMovementSpeed * speedMultiplier * deltaTime;
            }

            if(Keyboard.keyDown(GLFW.GLFW_KEY_F) && Keyboard.keyReleased(GLFW.GLFW_KEY_EQUAL)) {
                cameraComponent.FOV += 1f;
            }

            if(Keyboard.keyDown(GLFW.GLFW_KEY_F) && Keyboard.keyReleased(GLFW.GLFW_KEY_MINUS)) {
                cameraComponent.FOV -= 1f;
            }

            // moving
            if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
                translateDirectlyHorizontal(controllerComponent, new Vector3f(-controllerComponent.horizontalMovementSpeed * speedMultiplier * deltaTime, 0f, 0f));
            }

            if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
                translateDirectlyHorizontal(controllerComponent, new Vector3f(controllerComponent.horizontalMovementSpeed * speedMultiplier * deltaTime, 0f, 0f));
            }

            if (Keyboard.keyDown(GLFW.GLFW_KEY_C) && Keyboard.keyPressed(GLFW.GLFW_KEY_2)) {
                cameraComponent.viewMode = CameraComponent.ViewMode.VIEW_MODE_2D;
            }

            if (Keyboard.keyDown(GLFW.GLFW_KEY_C) && Keyboard.keyDown(GLFW.GLFW_KEY_V) && Keyboard.keyPressed(GLFW.GLFW_KEY_R)) {
                controllerComponent.cameraPosition.set(0f);
                controllerComponent.cameraRotation.set(0f);
                controllerComponent.cameraScale.set(1f);
            }

            cameraComponent.position.set(controllerComponent.cameraPosition);
            cameraComponent.rotation.set(controllerComponent.cameraRotation);
            //cameraComponent.scale.set(controllerComponent.cameraScale);

           // Log.CurrentSession.println("controller 3d called", Log.MessageType.SUCCESS);
        }
    }

    @Override
    public void onMouseScroll(ComponentsQuery componentsQuery, double xOffset, double yOffset)
    {

    }
}
