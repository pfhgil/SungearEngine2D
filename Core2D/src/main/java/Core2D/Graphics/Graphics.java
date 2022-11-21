package Core2D.Graphics;

import Core2D.Audio.AudioListener;
import Core2D.CamerasManager.CamerasManager;
import Core2D.Component.Components.Camera2DComponent;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Settings;
import Core2D.GameObject.GameObject;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.ShaderUtils.FrameBufferObject;
import Core2D.Utils.ExceptionsUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11C;

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
    protected static int totalIterations = 0;

    private static ViewMode viewMode = ViewMode.VIEW_MODE_2D;

    private static FrameBufferObject pickingRenderTarget;

    // экран очищено
    public static boolean screenCleared = false;

    private static Vector4f screenClearColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private static Renderer mainRenderer;

    protected static void init()
    {
        // использовать возможности opengl
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Vector2i pickingRenderTargetSize = getScreenSize();

        pickingRenderTarget = new FrameBufferObject(pickingRenderTargetSize.x, pickingRenderTargetSize.y, FrameBufferObject.BuffersTypes.COLOR_BUFFER, GL_TEXTURE0);

        mainRenderer = new Renderer();
    }

    // отрисовка
    protected static void draw() {
        System.gc();

        // цикл отрисовки (работает до тех пор, пока окно не должно быть закрыто)
        Core2D.getDeltaTimer().start();

        long capDiff = System.currentTimeMillis();
        long capInit = System.currentTimeMillis();

        while (!glfwWindowShouldClose(Core2D.getWindow().getWindow())) {
            try {
                if(Settings.Core2D.sleepCore2D) {
                    Thread.sleep(1000);
                }
                //System.out.println("delay: " + delay + ", delta: " + Core2D.getDeltaTimer().getDeltaTime());

                Core2D.getDeltaTimer().startFrame();

                AudioListener.update();

                Vector2i windowSize = Core2D.getWindow().getSize();
                if (CamerasManager.mainCamera2D != null) {
                    Camera2DComponent camera2DComponent = CamerasManager.mainCamera2D.getComponent(Camera2DComponent.class);
                    if(camera2DComponent != null) {
                        camera2DComponent.setViewportSize(new Vector2f(windowSize.x, windowSize.y));
                    }
                }

                if (SceneManager.currentSceneManager != null &&
                        SceneManager.currentSceneManager.getCurrentScene2D() != null &&
                        SceneManager.currentSceneManager.getCurrentScene2D().getSceneMainCamera2D() != null) {
                    Camera2DComponent camera2DComponent = SceneManager
                            .currentSceneManager.getCurrentScene2D()
                            .getSceneMainCamera2D()
                            .getComponent(Camera2DComponent.class);
                    if(camera2DComponent == null) {
                        camera2DComponent.setViewportSize(new Vector2f(windowSize.x, windowSize.y));
                    }
                }

                if (!screenCleared) {
                    GL11C.glClearColor(screenClearColor.x, screenClearColor.y, screenClearColor.z, screenClearColor.w);
                    screenCleared = true;
                }

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
    public static GameObject getPickedObject2D(Vector2f oglPosition)
    {
        pickingRenderTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT);

        if(!screenCleared) {
            GL11C.glClearColor(screenClearColor.x, screenClearColor.y, screenClearColor.z, screenClearColor.w);
            screenCleared = true;
        }

        glDisable(GL_BLEND);

        SceneManager.currentSceneManager.drawCurrentScene2DPicking();

        Vector4f selectedPixelColor = getPixelColor(oglPosition);

        System.out.println("selectedPixelColor: " + selectedPixelColor.x + ", " + selectedPixelColor.y + ", " + selectedPixelColor.z + ", " + selectedPixelColor.w);

        glEnable(GL_BLEND);
        pickingRenderTarget.unBind();

        return SceneManager.currentSceneManager.getPickedObject2D(selectedPixelColor);
    }

    public static ViewMode getViewMode() { return viewMode; }
    public static void setViewMode(ViewMode newViewMode)
    {
        viewMode = newViewMode;
        if(CamerasManager.mainCamera2D != null) {
            Camera2DComponent camera2DComponent = CamerasManager.mainCamera2D.getComponent(Camera2DComponent.class);
            if(camera2DComponent == null) return;
            if (viewMode == ViewMode.VIEW_MODE_2D) {
                camera2DComponent.setViewportSize(new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y));
                //projectionMatrix = new Matrix4f().ortho2D(0, Core2D.getWindow().getSize().x, 0, Core2D.getWindow().getSize().y);
            } else if (viewMode == ViewMode.VIEW_MODE_3D) {
                // сделать настройки более гибкими
                camera2DComponent.getProjectionMatrix().perspective(
                        (float) Math.toRadians(90.0f),
                        (float) Core2D.getWindow().getSize().x / Core2D.getWindow().getSize().y,
                        0.1f,
                        250.0f
                );
            }
        }
    }

    public static FrameBufferObject getPickingRenderTarget() { return pickingRenderTarget; }

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
