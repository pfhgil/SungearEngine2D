package Core2D.Input;

import Core2D.Core2D.Core2D;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.ArrayList;
import java.util.List;

public class Core2DInputCallback
{
    private List<UserInputCallback> userInputCallbacks = new ArrayList<>();

    public Core2DInputCallback()
    {
        GLFW.glfwSetKeyCallback(Core2D.getWindow().getWindow(), new GLFWKeyCallbackI() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                for(UserInputCallback userInputCallback : userInputCallbacks) {
                    userInputCallback.onInput(key, GLFW.glfwGetKeyName(key, scancode), mods);
                }
            }
        });

        GLFW.glfwSetScrollCallback(Core2D.getWindow().getWindow(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                for(UserInputCallback userInputCallback : userInputCallbacks) {
                    userInputCallback.onScroll(xoffset, yoffset);
                }
            }
        });
    }

    public List<UserInputCallback> getUserInputCallbacks() { return userInputCallbacks; }
}
