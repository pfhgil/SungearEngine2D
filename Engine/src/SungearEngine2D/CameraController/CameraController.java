package SungearEngine2D.CameraController;

import Core2D.CamerasManager.CamerasManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.Input.Core2DUserInputCallback;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.Resources;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class CameraController
{
    public static Entity controlledCamera2D;

    private static Vector2f lastCursorPosition = new Vector2f();

    // scale камеры, установленный с помощью колесика мышки
    private static Vector2f mouseCameraScale = new Vector2f(1);

    public static boolean allowMove = true;

    public static void init()
    {
        Core2D.getCore2DInputCallback().getCore2DUserInputCallbacks().add(new Core2DUserInputCallback() {
            @Override
            public void onInput(int i, String s, int i1) {

            }

            @Override
            public void onScroll(double xoffset, double yoffset) {
                if(controlledCamera2D != null && ViewsManager.isSceneViewFocused && controlledCamera2D.ID == CamerasManager.mainCamera2D.ID) {
                    Vector2f scale = new Vector2f((float) yoffset / 5.0f * mouseCameraScale.x, (float) yoffset / 5.0f * mouseCameraScale.y);
                    mouseCameraScale.x += scale.x;
                    mouseCameraScale.y += scale.y;
                    mouseCameraScale.x = Math.abs(mouseCameraScale.x);
                    mouseCameraScale.y = Math.abs(mouseCameraScale.y);
                }
            }
        });
    }

    public static void control()
    {
        if(CamerasManager.mainCamera2D != null && controlledCamera2D.ID == CamerasManager.mainCamera2D.ID && allowMove) {
            if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                lastCursorPosition = new Vector2f(Mouse.getMousePosition());

                ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
                GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), Resources.Cursors.getCursorResizeAll());
            }
            if (Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                lastCursorPosition = new Vector2f(Mouse.getMousePosition());

                ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
                GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), Resources.Cursors.getCursorArrow());
            }
            if (controlledCamera2D != null && ViewsManager.isSceneViewFocused) {
                TransformComponent cameraTransformComponent = controlledCamera2D.getComponent(TransformComponent.class);

                if(cameraTransformComponent != null) {
                    if (Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                        controlledCamera2D.getComponent(TransformComponent.class).getTransform().setNeedToMoveToDestination(false);
                        Vector2f currentPosition = new Vector2f(Mouse.getMousePosition());
                        Vector2f difference = new Vector2f(currentPosition.x - lastCursorPosition.x, currentPosition.y - lastCursorPosition.y);
                        cameraTransformComponent.getTransform().translate(
                                new Vector2f(-difference.x * (1.0f / mouseCameraScale.x), -difference.y * (1.0f / mouseCameraScale.y))
                        );
                        lastCursorPosition = currentPosition;
                    }

                    if (Keyboard.keyDown(GLFW.GLFW_KEY_K)) {
                        cameraTransformComponent.getTransform().rotate(1.0f);
                    }
                    if (Keyboard.keyDown(GLFW.GLFW_KEY_J)) {
                        cameraTransformComponent.getTransform().rotate(-1.0f);
                    }
                    if (Keyboard.keyReleased(GLFW.GLFW_KEY_R)) {
                        cameraTransformComponent.getTransform().setPosition(new Vector2f(0.0f, 0.0f));
                    }
                    if (Keyboard.keyReleased(GLFW.GLFW_KEY_L)) {
                        cameraTransformComponent.getTransform().setRotation(0.0f);
                    }
                    if (Keyboard.keyReleased(GLFW.GLFW_KEY_F)) {
                        Main.fuckYouAudio.play();
                    }
                    if (Keyboard.keyReleased(GLFW.GLFW_KEY_P)) {
                        Main.fuckYouAudio.pause();
                    }
                    if (Keyboard.keyReleased(GLFW.GLFW_KEY_T)) {
                        Main.fuckYouAudio.stop();
                    }
                }
            }
        }
    }

    public static Vector2f getMouseCameraScale() { return mouseCameraScale; }
}
