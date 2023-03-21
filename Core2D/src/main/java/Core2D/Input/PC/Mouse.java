package Core2D.Input.PC;

import Core2D.CamerasManager.CamerasManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Components.CameraComponent;
import Core2D.Graphics.Graphics;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Mouse class. If you want to track cursor buttons, then import GLFW and pass GLFW constants to the methods.
 */
public class Mouse
{
    private static boolean cursorHidden = false;

    //Массив всех доступных кнопок в ASCII таблице / кодировке.
    private static boolean[] buttons = new boolean[GLFW_MOUSE_BUTTON_LAST];

    private static Vector2f lastMousePosition = new Vector2f();

    private static Vector2f mousePosition = new Vector2f();

    private static Vector2f screenMousePosition = new Vector2f();

    private static boolean customSettings = false;

    private static Vector2f viewportSize = new Vector2f();

    private static Vector2f viewportPosition = new Vector2f();

    /**
     * @param buttonID Button ID in the range 0-8.
     * @return True if the button is pressed and held.
     */
    // Возвращает true в том случае, если кнопка нажата / в удержании
    public static boolean buttonDown(int buttonID)
    {
        return glfwGetMouseButton(Core2D.getWindow().getWindow(), buttonID) == 1 && mousePosition.x > 0.0f && mousePosition.y > 0.0f;
    }

    /**
     * @param buttonID Button ID in the range 0-8.
     * @return True if the button is pressed.
     */
    // Возвращает true в том случае, если кнопка нажата, иначе возвращает false
    public static boolean buttonPressed(int buttonID)
    {
        return buttonDown(buttonID) && !buttons[buttonID] && mousePosition.x > 0.0f && mousePosition.y > 0.0f;
    }

    /**
     * @param buttonID Button ID in the range 0-8.
     * @return True if the button is released.
     */
    // Возвращает true в том случае, если кнопка отпущена, иначе возвращает false
    public static boolean buttonReleased(int buttonID)
    {
        return !buttonDown(buttonID) && buttons[buttonID] && mousePosition.x > 0.0f && mousePosition.y > 0.0f;
    }

    public static void resetButtons()
    {
        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
        {
            buttons[i] = false;
        }
    }

    /**
     * Processes cursor input. Checks each button alternately whether it is clamped.
     * Calculates the cursor position relative to the screen and relative to viewportSize and viewportPosition.
     */
    // Удерживает весь ввод с мышки
    public static void handleMouseInput()
    {
        //выполняет это действие для каждой доступной кнопки
        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
        {
            buttons[i] = buttonDown(i);
        }

        // позиции курсора по x и y
        double[] curPosX = new double[1];
        double[] curPosY = new double[1];

        // получаю позицию курсора
        glfwGetCursorPos(Core2D.getWindow().getWindow(), curPosX,  curPosY);

        screenMousePosition.x = (float) curPosX[0];
        screenMousePosition.y = Core2D.getWindow().getSize().y - (float) curPosY[0];

        if(!customSettings) {
            viewportSize = new Vector2f(Core2D.getWindow().getSize());
        }

        Vector2i targetSize = Graphics.getScreenSize();
        float currentX = screenMousePosition.x - viewportPosition.x;
        currentX = (currentX / viewportSize.x) * targetSize.x;

        float currentY = screenMousePosition.y - viewportPosition.y;
        currentY = (currentY / viewportSize.y) * targetSize.y;

        lastMousePosition.set(mousePosition);

        mousePosition.set(currentX, currentY);
    }

    public static void setMousePosition(Vector2f mousePosition)
    {
        Mouse.mousePosition.set(mousePosition);
        glfwSetCursorPos(Core2D.getWindow().getWindow(), mousePosition.x, mousePosition.y);
    }

    /**
     * Sets viewportSize. (customSettings = true)
     * @param viewportSize Viewport size
     */
    public static void setViewportSize(Vector2f viewportSize)
    {
        customSettings = true;
        Mouse.viewportSize = viewportSize;
    }

    /**
     * Sets viewportPosition. (customSettings = true)
     * @param viewportPosition Viewport position
     */
    public static void setViewportPosition(Vector2f viewportPosition)
    {
        customSettings = true;
        Mouse.viewportPosition = viewportPosition;
    }

    public static Vector2f getLastMousePosition() { return lastMousePosition; }

    // получаю позицию курсора
    public static Vector2f getMousePosition() { return mousePosition; }

    /**
     * @param mousePosition Mouse position.
     * @return The position of the cursor in the world space.
     */
    public static Vector2f getMouseOGLPosition(Vector2f mousePosition)
    {
        if(CamerasManager.mainCamera2D != null) {
            float currentX = mousePosition.x / Graphics.getScreenSize().x * 2.0f - 1.0f;
            Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

            Matrix4f viewProjection = new Matrix4f();
            Matrix4f inverseView = new Matrix4f();
            Matrix4f inverseProjection = new Matrix4f();

            CameraComponent cameraComponent = CamerasManager.mainCamera2D.getComponent(CameraComponent.class);
            if(cameraComponent != null) {
                cameraComponent.viewMatrix.invert(inverseView);
                cameraComponent.projectionMatrix.invert(inverseProjection);
            }

            inverseView.mul(inverseProjection, viewProjection);
            tmp.mul(viewProjection);

            currentX = tmp.x;

            float currentY = mousePosition.y / Graphics.getScreenSize().y * 2.0f - 1.0f;

            tmp = new Vector4f(0, currentY, 0, 1);
            tmp.mul(viewProjection);

            currentY = tmp.y;

            return new Vector2f(currentX, currentY);
        }
        return new Vector2f();
    }

    public static Vector2f getScreenMousePosition() { return screenMousePosition; }

    public static Vector2f getViewportSize() { return viewportSize; }

    public static Vector2f getViewportPosition() { return viewportPosition; }

    public static boolean isCursorHidden() { return cursorHidden; }

    /**
     * Sets the visibility of the cursor.
     * @param cursorHidden Boolean.
     */
    public static void setCursorHidden(boolean cursorHidden)
    {
        Mouse.cursorHidden = cursorHidden;

        if(cursorHidden) {
            glfwSetInputMode(Core2D.getWindow().getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        } else {
            glfwSetInputMode(Core2D.getWindow().getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }
}
