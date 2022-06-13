package SungearEngine2D.Main;

import Core2D.Camera2D.Camera2D;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DUserCallback;
import Core2D.Core2D.Graphics;
import Core2D.Object2D.Object2D;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.GUI.GUI;
import SungearEngine2D.GUI.Views.MainView;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class Main
{
    private static Core2DUserCallback core2DUserCallback;

    private static Object2D cameraAnchor;
    private static Camera2D mainCamera2D;

    public static Thread settingsThread;

    public static void main(String[] main)
    {
        Graphics.setScreenClearColor(new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));

        core2DUserCallback = new Core2DUserCallback() {
            @Override
            public void onInit() {
                mainCamera2D = new Camera2D();
                cameraAnchor = new Object2D();
                cameraAnchor.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                cameraAnchor.getComponent(TransformComponent.class).getTransform().setPosition(new Vector2f(-50.0f, -50.0f));

                mainCamera2D.setAttachedObject2D(cameraAnchor);

                CameraController.controlledCamera2DAnchor = cameraAnchor;

                CameraController.init();

                GUI.init();

                GraphicsRenderer.init();

                settingsThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Settings.loadSettingsFile();
                    }
                });
                settingsThread.start();

                //Object2D newSceneObject2D = new Object2D();
                //Gson objSerializer = new GsonBuilder().setPrettyPrinting().create();
                //System.out.println(objSerializer.toJson(newSceneObject2D));
            }

            @Override
            public void onDrawFrame() {
                mainCamera2D.getTransform().setScale(new Vector2f(MainView.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                mainCamera2D.follow();
                CameraController.control();

                GUI.draw();

                GraphicsRenderer.draw();

                Core2D.getWindow().setName("Sungear Engine 2D. FPS: " + Core2D.getDeltaTimer().getFPS());
            }

            @Override
            public void onDeltaUpdate(float v) {

            }
        };

        Core2D.core2DUserCallback = core2DUserCallback;
        Core2D.start("Sungear Engine 2D", new int[] { GLFW.GLFW_SAMPLES }, new int[] { 8 });
    }

    public static Object2D getCameraAnchor() { return cameraAnchor; }

    public static Camera2D getMainCamera2D() { return mainCamera2D; }
}
