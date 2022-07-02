package Core2D.Window;

import Core2D.Graphics.Graphics;
import Core2D.Core2D.Settings;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
    private long window;
    private Vector2i size = new Vector2i(1000, 1000);
    private String name = "Core2D Window";
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
        System.out.println("начало инициализации окна");

        GLFWErrorCallback.createPrint(System.err).set();

        // инициализация GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW!"); // если не удалось инициализировать GLFW, выдать исключение

        // конфигурация GLFW
        glfwDefaultWindowHints(); // установка для будущего окна дефолтных настроек
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // окно будет невидимым после его создания
        //glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // окну нельзя будет изменять размер

        if(hintsNames != null) {
            for(int i = 0; i < hintsNames.length; i++) {
                glfwWindowHint(hintsNames[i], hintsValues[i]);
            }
        }

        // создание окна
        window = glfwCreateWindow(size.x, size.y, name, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create GLFW window!"); // если окно не создалось, выдать исключение

        // добавление обработчика на нажатие клавиш
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            // если нажатая кнопка == escape, то окно закрывается
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        glfwSetWindowIconifyCallback(window, new GLFWWindowIconifyCallbackI() {
            @Override
            public void invoke(long window, boolean iconified) {
                Settings.System.sleepSystem = iconified;
            }
        });

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

        glfwSetInputMode(window, GLFW_LOCK_KEY_MODS , GLFW_TRUE);

        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallbackI() {
            @Override
            public void invoke(long window, int width, int height) {
                size.x = width;
                size.y = height;

                // сделать настройки более гибкими
                Graphics.setViewMode(Graphics.getViewMode());
                GL11C.glViewport(0,  0, size.x, size.y);

                System.out.println("size: " + size.x + ", " + size.y);
            }
        });

        /*
        glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallbackI() {
            @Override
            public void invoke(long window, int width, int height) {
                size.x = width;
                size.y = height;

                // сделать настройки более гибкими
                Graphics.setViewMode(Graphics.getViewMode());
                GL11C.glViewport(0,  0, size.x, size.y);

                System.out.println("lol1");
            }
        });

         */



        System.out.println("окно проинициализировано");
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
