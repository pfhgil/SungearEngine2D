package Core2D.Core2D;

import Core2D.Controllers.PC.Keyboard;
import Core2D.Controllers.PC.Mouse;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.ShaderUtils.FrameBufferObject;
import Core2D.Utils.ExceptionsUtils;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11C;

import java.lang.Math;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

public abstract class Graphics
{
    public enum ViewMode
    {
        VIEW_MODE_2D,
        VIEW_MODE_3D
    }

    // матрица проекции
    private static Matrix4f projectionMatrix;

    protected static int totalIterations = 0;

    private static ViewMode viewMode = ViewMode.VIEW_MODE_2D;

    private static FrameBufferObject pickingRenderTarget;

    // экран очищено
    public static boolean screenCleared = false;

    private static Vector4f screenClearColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    protected static void init()
    {
        // использовать возможности opengl
        GL.createCapabilities();

        /**
         * НАСТРОЙКА ПАРАМЕТРОВ OPENGL
         */

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        /**
         *
         */

        Vector2i pickingRenderTargetSize = getScreenSize();

        projectionMatrix = new Matrix4f().ortho2D(0, Core2D.getWindow().getSize().x, 0, Core2D.getWindow().getSize().y);

        pickingRenderTarget = new FrameBufferObject(pickingRenderTargetSize.x, pickingRenderTargetSize.y, FrameBufferObject.BuffersTypes.COLOR_BUFFER, GL_TEXTURE0);
    }

    // отрисовка
    protected static void draw() {
        System.gc();

        // цикл отрисовки (работает до тех пор, пока окно не должно быть закрыто)
        Core2D.getDeltaTimer().start();

        while (!glfwWindowShouldClose(Core2D.getWindow().getWindow())) {
            try {
                if(Settings.System.sleepSystem) {
                    Thread.sleep(10000);
                }

                if(!screenCleared) {
                    GL11C.glClearColor(screenClearColor.x, screenClearColor.y, screenClearColor.z, screenClearColor.w);
                    screenCleared = true;
                }

                Core2D.getTimersManager().updateTimers();

                GL11C.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                if (Core2D.core2DUserCallback != null) {
                    Core2D.core2DUserCallback.onDrawFrame();
                }

                //physics.BodiesUpdate();

                Keyboard.handleKeyboardInput();
                Mouse.handleMouseInput();

                // заменяет цветовой буфер (большой буфер, содержащий значения цвета для каждого пикселя в GLFW окне), который использовался для отрисовки во время текущей итерации и показывает результат на экране
                glfwSwapBuffers(Core2D.getWindow().getWindow());

                // брать сообщения из очереди и обрабатывать их
                glfwPollEvents();
            } catch (Exception e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e));
            }
        }
    }

    public static Vector3f getPixelColor(Vector2f oglPosition)
    {

        ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(3);
        glReadPixels((int) oglPosition.x, (int) oglPosition.y, 1, 1, GL_RGB, GL_UNSIGNED_BYTE, pixelBuffer);

        Vector3f selectedPixelColor = new Vector3f(pixelBuffer.get(0), pixelBuffer.get(1), pixelBuffer.get(2));
        pixelBuffer.clear();
        pixelBuffer = null;

        /*
        FloatBuffer pixelBuffer = BufferUtils.createFloatBuffer(3);
        glReadPixels((int) oglPosition.x, (int) oglPosition.y, 1, 1, GL_RGB, GL_FLOAT, pixelBuffer);

        Vector3f selectedPixelColor = new Vector3f(pixelBuffer.get(0), pixelBuffer.get(1), pixelBuffer.get(2));
        pixelBuffer.clear();
        pixelBuffer = null;

         */

        return selectedPixelColor;
    }

    // полуячить выбранный мышкой объект
    public static Object2D getPickedObject2D(Vector2f oglPosition)
    {
        pickingRenderTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT);

        if(!screenCleared) {
            GL11C.glClearColor(screenClearColor.x, screenClearColor.y, screenClearColor.z, screenClearColor.w);
            screenCleared = true;
        }

        glDisable(GL_BLEND);

        SceneManager.drawCurrentScene2DPicking();

        Vector3f selectedPixelColor = getPixelColor(oglPosition);

        System.out.println("selectedPixelColor: " + selectedPixelColor.x + ", " + selectedPixelColor.y + ", " + selectedPixelColor.z);

        glEnable(GL_BLEND);
        pickingRenderTarget.unBind();

        return SceneManager.getPickedObject2D(selectedPixelColor);
    }

    public static ViewMode getViewMode() { return viewMode; }
    public static void setViewMode(ViewMode newViewMode)
    {
        viewMode = newViewMode;
        if(viewMode == ViewMode.VIEW_MODE_2D) {
            projectionMatrix = new Matrix4f().ortho2D(0, Core2D.getWindow().getSize().x, 0, Core2D.getWindow().getSize().y);
        } else if(viewMode == ViewMode.VIEW_MODE_3D) {
            // сделать настройки более гибкими
            projectionMatrix = new Matrix4f().perspective(
                    (float) Math.toRadians(90.0f),
                    (float) Core2D.getWindow().getSize().x / Core2D.getWindow().getSize().y,
                    0.1f,
                    250.0f
            );
        }
    }

    public static Matrix4f getProjectionMatrix() { return projectionMatrix; }

    public static FrameBufferObject getPickingRenderTarget() { return pickingRenderTarget; }

    public static Vector4f getScreenClearColor() { return screenClearColor; }
    public static void setScreenClearColor(Vector4f screenClearColor)
    {
        Graphics.screenClearColor.set(screenClearColor);
        screenCleared = false;
        screenClearColor = null;
    }

    public static Vector2i getScreenSize()
    {
        int sizeX = 0;
        int sizeY = 0;
        GLFWVidMode glfwVidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if(glfwVidMode != null) {
            sizeX = glfwVidMode.width();
            sizeY = glfwVidMode.height();
        } else {
            Log.CurrentSession.println("Error! Unable to get window target size (GLFWVidMode == null).");
            Log.showErrorDialog("Error! Unable to get window target size (GLFWVidMode == null).");
        }

        return new Vector2i(sizeX, sizeY);
    }
}
