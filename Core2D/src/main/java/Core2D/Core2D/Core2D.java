package Core2D.Core2D;

import Core2D.AssetManager.AssetManager;
import Core2D.Audio.OpenAL;
import Core2D.Camera2D.CamerasManager;
import Core2D.Graphics.Graphics;
import Core2D.Input.Core2DInputCallbacks;
import Core2D.Input.Core2DUserInputCallback;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Window.Window;
import org.joml.Vector2i;
import org.lwjgl.Version;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Core2D. Extends Graphics class. Responsible for starting the application or game.
 * @see Graphics
 */
public class Core2D extends Graphics
{
    private static Window window;

    private static Core2D core2D;

    /**
     * Core2D input callbacks. You can add your input callback using Core2D.getCore2DInputCallbacks().getUserInputCallbacks().add(...)
     * @see Core2DInputCallbacks#getCore2DUserInputCallbacks()
     * @see Core2DUserInputCallback
     */
    private static Core2DInputCallbacks core2DInputCallbacks;

    /**
     * Core2D use callback. Defines the methods onInit(), onExit(), onDrawFrame(), onDeltaUpdate(...).
     * @see Core2DUserCallback
     */
    public static Core2DUserCallback core2DUserCallback;

    private static Timer deltaTimer = new Timer(1.0f, true);

    /**
     * Current operating mode of the Core2D.
     * @see Core2DMode
     */
    public static Core2DMode core2DMode = Core2DMode.IN_ENGINE;

    /**
     * Starts Core2D.
     */
    public static void start() {
        create(Window.defaultWindowName, Window.defaultWindowSize, new int[] {}, new int[] {});
    }

    /**
     * Starts Core2D.
     * @param windowName The name of the window that will be created when Core2D is launched.
     */
    public static void start(String windowName) {
        create(windowName, Window.defaultWindowSize, new int[] {}, new int[] {});
    }

    /**
     * Starts Core2D.
     * @param windowName The name of the window that will be created when Core2D is launched.
     * @param windowSize The size of the window that will be created when Core2D is launched.
     */
    public static void start(String windowName, Vector2i windowSize) {
        create(windowName, windowSize, new int[] {}, new int[] {});
    }

    /**
     * Starts Core2D.
     * Warning! Length of windowHintsNames must be == length of windowHintsValues.
     * @param windowName The name of the window that will be created when Core2D is launched.
     * @param windowHintsNames Names of hints to launch the window.
     * @param windowHintsValues Values of hints to launch the window.
     */
    public static void start(String windowName, int[] windowHintsNames, int[] windowHintsValues) {
        create(windowName, Window.defaultWindowSize, windowHintsNames, windowHintsValues);
    }

    /**
     * Starts Core2D.
     * Warning! Length of windowHintsNames must be == length of windowHintsValues.
     * @param windowName The name of the window that will be created when Core2D is launched.
     * @param windowSize The size of the window that will be created when Core2D is launched.
     * @param windowHintsNames Names of hints to launch the window.
     * @param windowHintsValues Values of hints to launch the window.
     */
    public static void start(String windowName, Vector2i windowSize, int[] windowHintsNames, int[] windowHintsValues) {
        create(windowName, windowSize, windowHintsNames, windowHintsValues);
    }

    private static void create(String windowName, Vector2i windowSize, int[] windowHintsNames, int[] windowHintsValues)
    {
        Log.CurrentSession.createCurrentSession();

        window = new Window(windowName, windowSize, windowHintsNames, windowHintsValues);
        core2D = new Core2D();
        core2D.run();
    }

    // запустить игру
    private void run() {
        Log.CurrentSession.println("LWJGL version is " + Version.getVersion(), Log.MessageType.INFO);

        try {
            initCore();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        draw();

        stopCore2D();
    }

    // инициализация самой игры
    private void initCore() {
        try {
            Graphics.init();
            OpenAL.init();

            core2DInputCallbacks = new Core2DInputCallbacks();

            /** инициализация **/

            AssetManager.init();

            deltaTimer.getTimerCallbacks().add(new TimerCallback() {
                @Override
                public void deltaUpdate(float deltaTime) {
                    // для того, чтобы deltaTime пришел в норму
                    if (totalIterations > 0) {
                        if (core2DUserCallback != null) {
                            core2DUserCallback.onDeltaUpdate(deltaTime);
                        }

                        if(CamerasManager.getMainCamera2D() != null) {
                            CamerasManager.getMainCamera2D().getTransform().update(deltaTime);
                        }
                        SceneManager.currentSceneManager.updateCurrentScene2D(deltaTime);
                    }
                    totalIterations++;
                }

                @Override
                public void update() {

                }
            });
            deltaTimer.setMaxDelta(0.15f);
            //deltaTimer.setMaxDelta(1.0f);

            if (core2DUserCallback != null) {
                core2DUserCallback.onInit();
            }
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    /**
     * Stops Core2D. Calls the onExit() method in the core2DUserCallback.
     */
    public static void stopCore2D() {
        // освобождать колбэки окна и удалять окно
        glfwFreeCallbacks(window.getWindow());
        glfwDestroyWindow(window.getWindow());

        // прекратить работу glfw и освободить все колбэки ошибок
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        if(Core2D.core2DUserCallback != null) {
            Core2D.core2DUserCallback.onExit();
        }

        System.exit(130);
    }

    public static Window getWindow() {
        return window;
    }

    public static Core2DInputCallbacks getCore2DInputCallback() {
        return core2DInputCallbacks;
    }

    /**
     * @return Cyclic timer.
     */
    public static Timer getDeltaTimer() {
        return deltaTimer;
    }
}