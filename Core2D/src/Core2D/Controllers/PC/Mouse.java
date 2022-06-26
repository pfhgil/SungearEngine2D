package Core2D.Controllers.PC;

import Core2D.Core2D.Core2D;
import Core2D.Graphics.Graphics;
import org.joml.Vector2f;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse
{
    //Массив всех доступных кнопок в ASCII таблице / кодировке.
    private static boolean[] buttons = new boolean[GLFW_MOUSE_BUTTON_LAST];

    private static Vector2f mousePosition = new Vector2f();
    private static Vector2f screenMousePosition = new Vector2f();

    private static boolean customSettings = false;

    private static Vector2f viewportSize = new Vector2f();
    private static Vector2f viewportPosition = new Vector2f();

    // Возвращает true в том случае, если кнопка нажата / в удержании
    public static boolean buttonDown(int buttonId)
    {
        // кнопка нажата
        boolean buttonDown = false;

        buttonDown = glfwGetMouseButton(Core2D.getWindow().getWindow(), buttonId) == 1 && mousePosition.x > 0.0f && mousePosition.y > 0.0f;

        return buttonDown;
    }

    // Возвращает true в том случае, если кнопка нажата, иначе возвращает false
    public static boolean buttonPressed(int keyId)
    {
        return buttonDown(keyId) && !buttons[keyId] && mousePosition.x > 0.0f && mousePosition.y > 0.0f;
    }

    // Возвращает true в том случае, если кнопка отпущена, иначе возвращает false
    public static boolean buttonReleased(int keyId)
    {
        return !buttonDown(keyId) && buttons[keyId] && mousePosition.x > 0.0f && mousePosition.y > 0.0f;
    }

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

        mousePosition.x = currentX;
        mousePosition.y = currentY;
    }


    public static void setViewportSize(Vector2f viewportSize)
    {
        customSettings = true;
        Mouse.viewportSize = viewportSize;
    }

    public static void setViewportPosition(Vector2f viewportPosition)
    {
        customSettings = true;
        Mouse.viewportPosition = viewportPosition;
    }

    // получаю позицию курсора
    public static Vector2f getMousePosition() { return mousePosition; }

    public static Vector2f getScreenMousePosition() { return screenMousePosition; }

    public static Vector2f getViewportSize() { return viewportSize; }

    public static Vector2f getViewportPosition() { return viewportPosition; }
}
