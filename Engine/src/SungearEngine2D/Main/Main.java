package SungearEngine2D.Main;

import Core2D.Camera2D.Camera2D;
import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Components.ScriptComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DUserCallback;
import Core2D.Core2D.Settings;
import Core2D.Drawable.Object2D;
import Core2D.Graphics.Graphics;
import Core2D.Input.PC.Keyboard;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.ExceptionsUtils;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.GUI.GUI;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.AppData.AppDataManager;
import SungearEngine2D.Utils.Debugger;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class Main
{
    private static Core2DUserCallback core2DUserCallback;

    private static Object2D cameraAnchor;
    private static Camera2D mainCamera2D;

    public static Thread helpThread;

    public static void main(String[] main)
    {
        Settings.Core2D.destinationFPS = 60;
        Graphics.setScreenClearColor(new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        AppDataManager.init();
        core2DUserCallback = new Core2DUserCallback() {
            @Override
            public void onInit() {
                //Debugger.init();
                Resources.load();

                mainCamera2D = new Camera2D();
                cameraAnchor = new Object2D();
                cameraAnchor.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                cameraAnchor.getComponent(TransformComponent.class).getTransform().setPosition(new Vector2f(0.0f, 0.0f));

                //mainCamera2D.getTransform().setParentTransform(cameraAnchor.getComponent(TransformComponent.class).getTransform());
                CamerasManager.setMainCamera2D(mainCamera2D);

                CameraController.controlledCamera2DAnchor = cameraAnchor;

                CameraController.init();

                GUI.init();

                GraphicsRenderer.init();

                helpThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EngineSettings.initCompiler();

                        while(true) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                            }

                            if(currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().isSceneLoaded()) {
                                try {
                                    if (!EngineSettings.Playmode.active) {
                                        currentSceneManager.getCurrentScene2D().saveScriptsTempValues();
                                    }

                                    List<String> compiledScripts = new ArrayList<>();

                                    for (int p = 0; p < currentSceneManager.getCurrentScene2D().getLayering().getLayers().size(); p++) {
                                        Layer layer = currentSceneManager.getCurrentScene2D().getLayering().getLayers().get(p);
                                        if (layer != null && layer.getRenderingObjects() != null) {
                                            for (int i = 0; i < layer.getRenderingObjects().size(); i++) {
                                                if (layer.getRenderingObjects().get(i).getObject() instanceof Object2D && !((Object2D) layer.getRenderingObjects().get(i).getObject()).isShouldDestroy()) {
                                                    List<ScriptComponent> scriptComponents = ((Object2D) layer.getRenderingObjects().get(i).getObject()).getAllComponents(ScriptComponent.class);

                                                    for (int k = 0; k < scriptComponents.size(); k++) {
                                                        // был ли уже скомпилирован скрипт
                                                        boolean alreadyCompiled = compiledScripts.contains(scriptComponents.get(k).getScript().getName());
                                                        if (alreadyCompiled) {
                                                            continue;
                                                        }
                                                        long lastModified = new File(scriptComponents.get(k).getScript().path + ".java").lastModified();
                                                        if (lastModified != scriptComponents.get(k).getScript().getLastModified()) {
                                                            EngineSettings.Playmode.canEnterPlaymode = false;
                                                            scriptComponents.get(k).getScript().setLastModified(lastModified);

                                                            int finalK = k;
                                                            String scriptPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + scriptComponents.get(finalK).getScript().path;
                                                            String lastScriptPath = scriptComponents.get(finalK).getScript().path;
                                                            ViewsManager.getBottomMenuView().addTaskToList(new StoppableTask("Compiling script " + new File(scriptPath).getName() + "... ", 1.0f, 0.0f) {
                                                                public void run()
                                                                {
                                                                    String newScriptPath = scriptPath.replace(".java", "");
                                                                    boolean compiled = Compiler.compileScript(newScriptPath + ".java");
                                                                    if (compiled) {
                                                                        scriptComponents.get(finalK).getScript().loadClass(new File(scriptPath).getParent(), FilenameUtils.getBaseName(new File(scriptPath).getName()));
                                                                        scriptComponents.get(finalK).getScript().path = lastScriptPath;
                                                                    }
                                                                    compiledScripts.add(scriptComponents.get(finalK).getScript().getName());
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (!EngineSettings.Playmode.active) {
                                        currentSceneManager.getCurrentScene2D().applyScriptsTempValues();
                                    }
                                } catch(Exception e) {
                                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                                }
                            }
                        }
                    }
                });
                helpThread.start();

                System.gc();
            }

            @Override
            public void onExit() {
                if(ProjectsManager.getCurrentProject() != null) {
                    ProjectsManager.getCurrentProject().saveProject();
                }
            }

            @Override
            public void onDrawFrame() {
                Core2D.getWindow().setName("Sungear Engine 2D. FPS: " + Core2D.getDeltaTimer().getFPS());

                mainCamera2D.getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                //cameraAnchor.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                CameraController.control();

                if(!Keyboard.keyDown(GLFW.GLFW_KEY_F)) GUI.draw();

                GraphicsRenderer.draw();

                //Core2D.getWindow().setName("Sungear Engine 2D. FPS: " + Core2D.getDeltaTimer().getFPS());
            }

            @Override
            public void onDeltaUpdate(float deltaTime) {
                cameraAnchor.getComponent(TransformComponent.class).getTransform().update(deltaTime);
                currentSceneManager.updateCurrentScene2D(deltaTime);
            }
        };

        Core2D.core2DUserCallback = core2DUserCallback;
        Core2D.start();
        //Core2D.start("Sungear Engine 2D", new int[] { GLFW.GLFW_SAMPLES }, new int[] { 8 });
    }

    public static Object2D getCameraAnchor() { return cameraAnchor; }

    public static Camera2D getMainCamera2D() { return mainCamera2D; }
}
