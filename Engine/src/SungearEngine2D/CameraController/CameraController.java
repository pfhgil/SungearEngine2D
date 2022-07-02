package SungearEngine2D.CameraController;

import Core2D.Component.Components.TransformComponent;
import Core2D.Controllers.PC.Mouse;
import Core2D.Core2D.Core2D;
import Core2D.Input.UserInputCallback;
import Core2D.Object2D.Object2D;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.Main.Main;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class CameraController
{
    public static Object2D controlledCamera2DAnchor;

    private static Vector2f lastCursorPosition = new Vector2f();

    // scale камеры, установленный с помощью колесика мышки
    private static Vector2f mouseCameraScale = new Vector2f(1);

    public static boolean controlCamera = true;

    public static void init()
    {
        Core2D.getCore2DInputCallback().getUserInputCallbacks().add(new UserInputCallback() {
            @Override
            public void onInput(int i, String s, int i1) {

            }

            @Override
            public void onScroll(double xoffset, double yoffset) {
                if(controlledCamera2DAnchor != null && !MainView.isSomeViewFocusedExceptSceneView && controlCamera) {
                    mouseCameraScale.x += (float) (yoffset / 5.0f) * mouseCameraScale.x;
                    mouseCameraScale.y += (float) (yoffset / 5.0f) * mouseCameraScale.y;
                    mouseCameraScale.x = Math.abs(mouseCameraScale.x);
                    mouseCameraScale.y = Math.abs(mouseCameraScale.y);
                }
            }
        });
    }

    public static void control()
    {
        if(controlCamera) {
            if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                lastCursorPosition = new Vector2f(Mouse.getMousePosition());
            }
            if (Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                lastCursorPosition = new Vector2f(Mouse.getMousePosition());
            }
            if (controlledCamera2DAnchor != null && !MainView.isSomeViewFocusedExceptSceneView) {
                if (Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                    Vector2f currentPosition = new Vector2f(Mouse.getMousePosition());
                    Vector2f difference = new Vector2f(currentPosition.x - lastCursorPosition.x, currentPosition.y - lastCursorPosition.y);
                    controlledCamera2DAnchor.getComponent(TransformComponent.class).getTransform().translate(difference.negate().div(Main.getMainCamera2D().getTransform().getScale()));
                    lastCursorPosition = currentPosition;
                }
            /*
            if(Keyboard.KeyDown(GLFW.GLFW_KEY_S)) {
                Main.getMainCamera2D().getTransform().getModelMatrix().translate(new Vector3f(0.0f, 0.0f, -1f));
            }
            if(Keyboard.KeyDown(GLFW.GLFW_KEY_W)) {
                Main.getMainCamera2D().getTransform().getModelMatrix().translate(new Vector3f(0.0f, 0.0f, 1f));
            }
            if(Keyboard.KeyReleased(GLFW.GLFW_KEY_V)) {
                if(Core2D.getViewMode() == Graphics.ViewMode.VIEW_MODE_2D) {
                    Core2D.setViewMode(Graphics.ViewMode.VIEW_MODE_3D);
                } else {
                    Core2D.setViewMode(Graphics.ViewMode.VIEW_MODE_2D);
                }
            }

             */
            }
        }
    }

    public static Vector2f getMouseCameraScale() { return mouseCameraScale; }
}
