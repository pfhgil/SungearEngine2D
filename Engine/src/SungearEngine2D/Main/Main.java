package SungearEngine2D.Main;

import Core2D.Camera2D.Camera2D;
import Core2D.Component.Components.ScriptComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DUserCallback;
import Core2D.Graphics.Graphics;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.GUI.GUI;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.Scripting.Compiler;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    private static Core2DUserCallback core2DUserCallback;

    private static Object2D cameraAnchor;
    private static Camera2D mainCamera2D;

    public static Thread helpThread;

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


                helpThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Settings.initCompiler();
                        Settings.loadSettingsFile();

                        while(true) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                            }

                            if(SceneManager.getCurrentScene2D() != null) {
                                SceneManager.getCurrentScene2D().saveScriptsTempValues();

                                List<String> compiledScripts = new ArrayList<>();

                                for(int p = 0; p < SceneManager.getCurrentScene2D().getLayering().getLayers().size(); p++) {
                                    Layer layer = SceneManager.getCurrentScene2D().getLayering().getLayers().get(p);
                                    if(layer != null && layer.getRenderingObjects() != null) {
                                        for (int i = 0; i < layer.getRenderingObjects().size(); i++) {
                                            if (layer.getRenderingObjects().get(i).getObject() instanceof Object2D && !((Object2D) layer.getRenderingObjects().get(i).getObject()).isShouldDestroy()) {
                                                List<ScriptComponent> scriptComponents = ((Object2D) layer.getRenderingObjects().get(i).getObject()).getAllComponents(ScriptComponent.class);

                                                for (int k = 0; k < scriptComponents.size(); k++) {
                                                    // был ли уже скомпилирован скрипт
                                                    boolean alreadyCompiled = compiledScripts.contains(scriptComponents.get(k).getScript().getName());
                                                    if(alreadyCompiled) {
                                                        continue;
                                                    }
                                                    long lastModified = new File(scriptComponents.get(k).getScript().getPath() + ".java").lastModified();
                                                    if (lastModified != scriptComponents.get(k).getScript().getLastModified()) {
                                                        Settings.Playmode.canEnterPlaymode = false;
                                                        scriptComponents.get(k).getScript().setLastModified(lastModified);
                                                        boolean compiled = Compiler.compileScript(scriptComponents.get(k).getScript().getPath() + ".java");
                                                        if (compiled) {
                                                            scriptComponents.get(k).getScript().loadClass(new File(scriptComponents.get(k).getScript().getPath()).getParent(), FilenameUtils.getBaseName(new File(scriptComponents.get(k).getScript().getPath()).getName()));
                                                        }
                                                        compiledScripts.add(scriptComponents.get(k).getScript().getName());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                SceneManager.getCurrentScene2D().applyScriptsTempValues();
                            }
                        }
                    }
                });
                helpThread.start();

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
