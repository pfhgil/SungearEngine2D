package Core2D.Controllers.PC;

import Core2D.Core2D.Core2D;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse
{
    //Массив всех доступных кнопок в ASCII таблице / кодировке.
    private static boolean[] buttons = new boolean[GLFW_MOUSE_BUTTON_LAST];

    private static Vector2f mousePosition = new Vector2f();

    // Возвращает true в том случае, если кнопка нажата / в удержании
    public static boolean buttonDown(int buttonId)
    {
        // кнопка нажата
        boolean buttonDown = false;

        buttonDown = glfwGetMouseButton(Core2D.getWindow().getWindow(), buttonId) == 1;

        return buttonDown;
    }

    // Возвращает true в том случае, если кнопка нажата, иначе возвращает false
    public static boolean buttonPressed(int keyId)
    {
        return buttonDown(keyId) && !buttons[keyId];
    }

    // Возвращает true в том случае, если кнопка отпущена, иначе возвращает false
    public static boolean buttonReleased(int keyId)
    {
        return !buttonDown(keyId) && buttons[keyId];
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

        mousePosition.x = (float) curPosX[0];
        mousePosition.y = Core2D.getWindow().getSize().y - (float) curPosY[0];
    }
    // получаю позицию курсора
    public static Vector2f getMousePosition() { return mousePosition; }
}
