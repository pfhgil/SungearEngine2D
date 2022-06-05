package Core2D.Core2D;

import Core2D.Camera2D.Camera2D;
import Core2D.Input.Core2DInputCallback;
import Core2D.Log.Log;
import Core2D.Physics.PhysicsWorld;
import Core2D.Scene2D.SceneManager2D;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import Core2D.Timer.TimersManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Window.Window;
import org.joml.Vector2i;
import org.lwjgl.Version;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class Core2D extends Graphics
{
    private static Window window;

    private static Core2D core2D;

    private static Core2DInputCallback core2DInputCallback;

    public static Core2DUserCallback core2DUserCallback;

    private static TimersManager timersManager = new TimersManager();

    private static SceneManager2D sceneManager2D = new SceneManager2D();

    public static Camera2D currentCamera2D;

    // поток для сборка мусора
    private static Thread gcThread;
    private static boolean gcThreadRunning = false;

    private static Timer deltaTimer = new Timer(1.0f, true);

    public static void start() {
        Log.CurrentSession.createCurrentSession();

        window = new Window();
        core2D = new Core2D();
        core2D.run();
    }

    public static void start(String windowName) {
        Log.CurrentSession.createCurrentSession();

        window = new Window(windowName);
        core2D = new Core2D();
        core2D.run();
    }

    public static void start(String windowName, Vector2i windowSize) {
        Log.CurrentSession.createCurrentSession();

        window = new Window(windowName, windowSize);
        core2D = new Core2D();
        core2D.run();
    }

    public static void start(String windowName, int[] windowHintsNames, int[] windowHintsValues) {
        Log.CurrentSession.createCurrentSession();

        window = new Window(windowName, windowHintsNames, windowHintsValues);
        core2D = new Core2D();
        core2D.run();
    }

    public static void start(String windowName, Vector2i windowSize, int[] windowHintsNames, int[] windowHintsValues) {
        Log.CurrentSession.createCurrentSession();

        window = new Window(windowName, windowSize, windowHintsNames, windowHintsValues);
        core2D = new Core2D();
        core2D.run();
    }

    // запустить игру
    private void run() {
        System.out.println("LWJGL version is " + Version.getVersion());

        try {
            initCore();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }

        draw();

        stopCore2D();
    }

    // инициализация самой игры
    private void initCore() {
        try {
            Graphics.init();

            core2DInputCallback = new Core2DInputCallback();

            /** инициализация **/

            gcThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (gcThreadRunning) {
                            // сбор мусора каждые 10 сек =)
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Runtime.getRuntime().gc();
                        }
                    }
                }
            });

            deltaTimer.getTimerCallbacks().add(new TimerCallback() {
                @Override
                public void deltaUpdate(float deltaTime) {
                    // для того, чтобы deltaTime пришел в норму
                    if (totalIterations > 0) {
                        if (core2DUserCallback != null) {
                            core2DUserCallback.onDeltaUpdate(deltaTime);
                        }

                        sceneManager2D.updateCurrentScene2D(deltaTime);
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
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }
    }

    public static void stopCore2D() {
        // освобождать колбэки окна и удалять окно
        glfwFreeCallbacks(window.getWindow());
        glfwDestroyWindow(window.getWindow());

        // прекратить работу glfw и освободить все колбэки ошибок
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        stopGCThread();

        System.exit(130);
    }

    public static void startGCThread() {
        gcThreadRunning = true;
        gcThread.start();
    }

    public static void stopGCThread() {
        if (gcThreadRunning) {
            gcThreadRunning = false;
            gcThread.interrupt();
        }
    }

    public static Window getWindow() {
        return window;
    }

    public static Core2DInputCallback getCore2DInputCallback() {
        return core2DInputCallback;
    }

    public static TimersManager getTimersManager() {
        return timersManager;
    }

    public static SceneManager2D getSceneManager2D() {
        return sceneManager2D;
    }

    public static Timer getDeltaTimer() {
        return deltaTimer;
    }
}