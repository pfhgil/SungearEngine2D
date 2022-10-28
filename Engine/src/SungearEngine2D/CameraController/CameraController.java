package SungearEngine2D.CameraController;

import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Drawable.Object2D;
import Core2D.Input.Core2DUserInputCallback;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Main;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class CameraController
{
    public static Object2D controlledCamera2DAnchor;

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
                if(controlledCamera2DAnchor != null && !ViewsManager.isSomeViewFocusedExceptSceneView && Main.getMainCamera2D().getID() == CamerasManager.getMainCamera2D().getID()) {
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
        if(Main.getMainCamera2D().getID() == CamerasManager.getMainCamera2D().getID() && allowMove) {
            if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                lastCursorPosition = new Vector2f(Mouse.getMousePosition());
            }
            if (Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                lastCursorPosition = new Vector2f(Mouse.getMousePosition());
            }
            if (controlledCamera2DAnchor != null && !ViewsManager.isSomeViewFocusedExceptSceneView) {
                if (Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                    controlledCamera2DAnchor.getComponent(TransformComponent.class).getTransform().setNeedToMoveToDestination(false);
                    Vector2f currentPosition = new Vector2f(Mouse.getMousePosition());
                    Vector2f difference = new Vector2f(currentPosition.x - lastCursorPosition.x, currentPosition.y - lastCursorPosition.y);
                    Main.getMainCamera2D().getTransform().translate(new Vector2f(difference.x * (1.0f / mouseCameraScale.x), difference.y * (1.0f / mouseCameraScale.y)));
                    lastCursorPosition = currentPosition;
                }

                if (Keyboard.keyDown(GLFW.GLFW_KEY_K)) {
                    Main.getMainCamera2D().getTransform().rotate(1.0f);
                }
                if (Keyboard.keyDown(GLFW.GLFW_KEY_J)) {
                    Main.getMainCamera2D().getTransform().rotate(-1.0f);
                }
                if (Keyboard.keyReleased(GLFW.GLFW_KEY_R)) {
                    Main.getMainCamera2D().getTransform().setPosition(new Vector2f(0.0f, 0.0f));
                }
                if (Keyboard.keyReleased(GLFW.GLFW_KEY_L)) {
                    Main.getMainCamera2D().getTransform().setRotation(0.0f);
                }
            }
        }
    }

    public static Vector2f getMouseCameraScale() { return mouseCameraScale; }
}
