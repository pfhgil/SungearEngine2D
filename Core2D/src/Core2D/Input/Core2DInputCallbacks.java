package Core2D.Input;

import Core2D.Core2D.Core2D;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.ArrayList;
import java.util.List;

public class Core2DInputCallbacks
{
    private List<Core2DUserInputCallback> core2DUserInputCallbacks = new ArrayList<>();

    public Core2DInputCallbacks()
    {
        GLFW.glfwSetKeyCallback(Core2D.getWindow().getWindow(), (window, key, scancode, action, mods) -> {
            for (Core2DUserInputCallback core2DUserInputCallback : core2DUserInputCallbacks) {
                core2DUserInputCallback.onInput(key, GLFW.glfwGetKeyName(key, scancode), mods);
            }
        });

        GLFW.glfwSetScrollCallback(Core2D.getWindow().getWindow(), (window, xoffset, yoffset) -> {
            for (Core2DUserInputCallback core2DUserInputCallback : core2DUserInputCallbacks) {
                core2DUserInputCallback.onScroll(xoffset, yoffset);
            }
        });
    }

    public List<Core2DUserInputCallback> getCore2DUserInputCallbacks() { return core2DUserInputCallbacks; }
}
