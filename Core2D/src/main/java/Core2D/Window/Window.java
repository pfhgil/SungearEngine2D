package Core2D.Window;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Settings;
import Core2D.Graphics.OpenGL;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallbackI;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
    public static Vector2i defaultWindowSize = new Vector2i(1080, 720);

    public static String defaultWindowName = "Powered by Core2D";

    private long window;
    private Vector2i size = new Vector2i(defaultWindowSize);
    private String name = defaultWindowName;
    private int[] hintsNames;
    private int[] hintsValues;

    public Window()
    {
        create();
    }

    public Window(String name)
    {
        this.name = name;

        create();
    }

    public Window(String name, Vector2i size)
    {
        this.size = new Vector2i(size);
        this.name = name;

        create();
    }

    public Window(String name, int[] hintsNames, int[] hintsValues)
    {
        this.name = name;
        this.hintsNames = hintsNames;
        this.hintsValues = hintsValues;

        create();
    }

    public Window(String name, Vector2i size, int[] hintsNames, int[] hintsValues)
    {
        this.name = name;
        this.size = size;
        this.hintsNames = hintsNames;
        this.hintsValues = hintsValues;

        create();
    }

    private void create()
    {
        try {
            Log.CurrentSession.println("Starting Core2D...", Log.MessageType.INFO);
            Log.Console.println("starting window initialization...");

            GLFWErrorCallback.createPrint(System.err).set();

            // инициализация GLFW
            if (!glfwInit())
                throw new IllegalStateException("Unable to initialize GLFW!"); // если не удалось инициализировать GLFW, выдать исключение

            // конфигурация GLFW
            glfwDefaultWindowHints(); // установка для будущего окна дефолтных настроек
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // окно будет невидимым после его создания
            //glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // окну нельзя будет изменять размер

            if (hintsNames != null) {
                if(hintsNames.length != hintsValues.length) {
                    throw new RuntimeException("Hints names length must be == hints values length!");
                }

                for (int i = 0; i < hintsNames.length; i++) {
                    glfwWindowHint(hintsNames[i], hintsValues[i]);
                }
            }

            // создание окна
            window = glfwCreateWindow(size.x, size.y, name, NULL, NULL);
            if (window == NULL)
                throw new RuntimeException("Failed to create GLFW window!"); // если окно не создалось, выдать исключение

            // добавление обработчика на нажатие клавиш
            try(AutoCloseable ac = glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
                // если нажатая кнопка == escape, то окно закрывается
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
            })) {

            } catch (Exception e){
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }

            try(AutoCloseable ac = glfwSetWindowIconifyCallback(window, (window, iconified) -> Settings.Core2D.sleepCore2D = iconified)) {

            } catch(Exception e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }

            try(AutoCloseable ac = glfwSetWindowCloseCallback(window, (l) -> Core2D.stopCore2D())) {

            } catch (Exception e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }

            // проталкивается стак для следующего кадра
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1); // создаю IntBuffer для получения ширины окна
                IntBuffer pHeight = stack.mallocInt(1); // создаю IntBuffer для получения высоты окна

                // получить размер окна, переданный в glfwCreateWindow
                glfwGetWindowSize(window, pWidth, pHeight);

                // получить разрешение монитора
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

                // поставить окно посередине
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            }

            // сделать контекст OpenGL текущим
            glfwMakeContextCurrent(window);
            // включить вертикальную синхронизацию (частота кадров синхронизируется с герцовкой монитора)
            glfwSwapInterval(0);

            // сделать окно видимым
            glfwShowWindow(window);

            glfwSetInputMode(window, GLFW_LOCK_KEY_MODS, GLFW_TRUE);

            glfwSetWindowSizeCallback(window, (window, width, height) -> {
                size.x = width;
                size.y = height;

                // сделать настройки более гибкими
                //Graphics.setViewMode(Graphics.getViewMode());
                OpenGL.glCall((params) -> glViewport(0, 0, size.x, size.y));
            });

            Log.Console.println("Core2D started!");
            Log.CurrentSession.println("Core2D started!", Log.MessageType.SUCCESS);
        } catch(Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public long getWindow() { return window; }

    public Vector2i getSize() { return size; }
    public void setSize(Vector2i size)
    {
        this.size = size;
        glfwSetWindowSize(window, this.size.x, this.size.y);
    }

    public String getName() { return name; }
    public void setName(String name)
    {
        this.name = name;
        glfwSetWindowTitle(window, this.name);
    }

    public int[] getHintsNames() { return hintsNames; }

    public int[] getHintsValues() { return hintsValues; }
}
