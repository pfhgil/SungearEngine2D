package Core2D.Input.PC;

import Core2D.Core2D.Core2D;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Keyboard class. If you want to track keystrokes on the keyboard, then import GLFW and pass GLFW constants to the methods.
 */
public class Keyboard
{
    //Массив всех доступных клавиш в ASCII таблице / кодировке.
    private static boolean[] keys = new boolean[GLFW_KEY_LAST];

    /**
     * @param keyId Key ID in the range 32-348.
     * @return True if the key is pressed and held.
     */
    // Возвращает true в том случае, если клавиша нажата / в удержании
    public static boolean keyDown(int keyId)
    {
        // клавиша нажата
        boolean keyDown = false;

        // клавиш в диапазоне 0-31 не существует
        if(keyId > 31) keyDown = glfwGetKey(Core2D.getWindow().getWindow(), keyId) == GLFW_PRESS;

        return keyDown;
    }

    /**
     * @param keyId Key ID in the range 32-348.
     * @return True if the key is pressed.
     */
    // Возвращает true в том случае, если клавиша нажата, иначе возвращает false
    public static boolean keyPressed(int keyId)
    {
        return keyDown(keyId) && !keys[keyId];
    }

    /**
     * @param keyId Key ID in the range 32-348.
     * @return True if the key is released.
     */
    // Возвращает true в том случае, если клавиша отпущена, иначе возвращает false
    public static boolean keyReleased(int keyId)
    {
        return !keyDown(keyId) && keys[keyId];
    }

    /**
     * Processes keyboard input. Checks each key alternately whether it is clamped.
     */
    // Удерживает весь ввод с клавиатуры
    public static void handleKeyboardInput()
    {
        //выполняет это действие для каждой доступной клавиши
        for (int i = 0; i < GLFW_KEY_LAST; i++)
        {
            keys[i] = keyDown(i);
        }
    }

    public static void resetKeys()
    {
        for (int i = 0; i < GLFW_KEY_LAST; i++)
        {
            keys[i] = false;
        }
    }
}
