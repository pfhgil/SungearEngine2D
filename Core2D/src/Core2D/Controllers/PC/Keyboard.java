package Core2D.Controllers.PC;

import Core2D.Core2D.Core2D;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class Keyboard
{
    //Массив всех доступных клавиш в ASCII таблице / кодировке.
    private static boolean[] keys = new boolean[GLFW_KEY_LAST];

    // Возвращает true в том случае, если клавиша нажата / в удержании
    public static boolean keyDown(int keyId)
    {
        // клавиша нажата
        boolean keyDown = false;

        // клавиш в диапазоне 0-31 не существует
        if(keyId > 31) keyDown = glfwGetKey(Core2D.getWindow().getWindow(), keyId) == 1;

        return keyDown;
    }

    // Возвращает true в том случае, если клавиша нажата, иначе возвращает false
    public static boolean keyPressed(int keyId)
    {
        return keyDown(keyId) && !keys[keyId];
    }

    // Возвращает true в том случае, если клавиша отпущена, иначе возвращает false
    public static boolean keyReleased(int keyId)
    {
        return !keyDown(keyId) && keys[keyId];
    }

    // Удерживает весь ввод с клавиатуры
    public static void handleKeyboardInput()
    {
        //выполняет это действие для каждой доступной клавиши
        for (int i = 0; i < GLFW_KEY_LAST; i++)
        {
            keys[i] = keyDown(i);
        }
    }
}
