package Core2D.Graphics;

import Core2D.Audio.AudioListener;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Settings;
import Core2D.ECS.Component.Components.CameraComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.OpenGL.FrameBuffer;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

public abstract class Graphics
{
    protected static int totalIterations = 0;

    private static FrameBuffer pickingRenderTarget;

    // экран очищено
    public static boolean screenCleared = false;

    private static Vector4f screenClearColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private static Renderer mainRenderer;

    protected static void init()
    {
        // использовать возможности opengl
        OpenGL.init();

        Vector2i pickingRenderTargetSize = getScreenSize();

        pickingRenderTarget = new FrameBuffer(pickingRenderTargetSize.x, pickingRenderTargetSize.y, FrameBuffer.BuffersTypes.COLOR_BUFFER, GL_TEXTURE0);

        mainRenderer = new Renderer();
    }

    // отрисовка
    protected static void draw() {
        System.gc();

        // цикл отрисовки (работает до тех пор, пока окно не должно быть закрыто)
        Core2D.getDeltaTimer().start();

        long capDiff = System.currentTimeMillis();
        long capInit = System.currentTimeMillis();

        Log.Console.println("first");

        while (!glfwWindowShouldClose(Core2D.getWindow().getWindow())) {
            try {
                if(Settings.Core2D.sleepCore2D) {
                    Thread.sleep(1000);
                }

                //System.out.println("delay: " + delay + ", delta: " + Core2D.getDeltaTimer().getDeltaTime());

                Core2D.getDeltaTimer().startFrame();

                AudioListener.update();

                if (!screenCleared) {
                    OpenGL.glCall((params) -> glClearColor(screenClearColor.x, screenClearColor.y, screenClearColor.z, 0.0f));
                    screenCleared = true;
                }

                OpenGL.glCall((params) -> glClear(GL_COLOR_BUFFER_BIT));

                SceneManager.currentSceneManager.updateCurrentScene2D();

                if (Core2D.core2DUserCallback != null) {
                    Core2D.core2DUserCallback.onUpdate();
                }

                //physics.BodiesUpdate();

                Keyboard.handleKeyboardInput();
                Mouse.handleMouseInput();

                // заменяет цветовой буфер (большой буфер, содержащий значения цвета для каждого пикселя в GLFW окне), который использовался для отрисовки во время текущей итерации и показывает результат на экране
                glfwSwapBuffers(Core2D.getWindow().getWindow());

                // брать сообщения из очереди и обрабатывать их
                glfwPollEvents();

                if(Settings.Core2D.destinationFPS > 0) {
                    capDiff = System.currentTimeMillis() - capInit;
                    long delay = 1000 / Settings.Core2D.destinationFPS;
                    if(capDiff < delay) {
                        try {
                            Thread.sleep(delay - capDiff);
                        } catch (Exception e) {
                            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                        }
                    }
                    capInit = System.currentTimeMillis();
                    
                }
            } catch (Exception e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        }
    }

    public static Vector4f getPixelColor(Vector2f oglPosition)
    {

        ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(4);
        glReadPixels((int) oglPosition.x, (int) oglPosition.y, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);

        Vector4f selectedPixelColor = new Vector4f(pixelBuffer.get(0), pixelBuffer.get(1), pixelBuffer.get(2), pixelBuffer.get(3));
        pixelBuffer.clear();

        return selectedPixelColor;
    }

    // полуячить выбранный мышкой объект
    public static Entity getPickedEntity(CameraComponent cameraComponent, Vector2f oglPosition)
    {
        pickingRenderTarget.bind();
        pickingRenderTarget.clear();

        if(!screenCleared) {
            OpenGL.glCall((params) -> glClearColor(screenClearColor.x, screenClearColor.y, screenClearColor.z, screenClearColor.w));
            screenCleared = true;
        }

        OpenGL.glCall((params) -> glDisable(GL_BLEND));

        SceneManager.currentSceneManager.drawCurrentScene2DPicking(cameraComponent);

        Vector4f selectedPixelColor = getPixelColor(oglPosition);

        Log.Console.println("selectedPixelColor: " + selectedPixelColor.x + ", " + selectedPixelColor.y + ", " + selectedPixelColor.z + ", " + selectedPixelColor.w);

        OpenGL.glCall((params) -> glEnable(GL_BLEND));
        pickingRenderTarget.unBind();

        return SceneManager.currentSceneManager.getPickedObject2D(selectedPixelColor);
    }

    public static FrameBuffer getPickingRenderTarget() { return pickingRenderTarget; }

    public static Vector4f getScreenClearColor() { return screenClearColor; }
    public static void setScreenClearColor(Vector4f screenClearColor)
    {
        Graphics.screenClearColor.set(screenClearColor);
        screenCleared = false;
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
            Log.CurrentSession.println("Error! Unable to get window target size (GLFWVidMode == null).", Log.MessageType.ERROR);
            Log.showErrorDialog("Error! Unable to get window target size (GLFWVidMode == null).");
        }

        return new Vector2i(sizeX, sizeY);
    }

    public static Renderer getMainRenderer() { return mainRenderer; }
}
