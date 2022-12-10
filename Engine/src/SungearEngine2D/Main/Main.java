package SungearEngine2D.Main;

import Core2D.Audio.Audio;
import Core2D.CamerasManager.CamerasManager;
import Core2D.Component.Components.ScriptComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DUserCallback;
import Core2D.Core2D.Settings;
import Core2D.GameObject.GameObject;
import Core2D.Graphics.Graphics;
import Core2D.Input.PC.Keyboard;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Tasks.StoppableTask;
import Core2D.Transform.Transform;
import Core2D.Utils.ExceptionsUtils;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.GUI.GUI;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.AppData.AppDataManager;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.Game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class Main
{
    private static Core2DUserCallback core2DUserCallback;

    private static GameObject mainCamera2D;

    public static Thread helpThread;

    // TODO: delete this
    public static Audio fuckYouAudio = new Audio();

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

                mainCamera2D = GameObject.createCamera2D();
                CamerasManager.mainCamera2D = mainCamera2D;
                CameraController.controlledCamera2D = mainCamera2D;

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
                                    List<String> compiledScripts = new ArrayList<>();

                                    int layersNum = currentSceneManager.getCurrentScene2D().getLayering().getLayers().size();
                                    layersCycle: for (int p = 0; p < layersNum; p++) {
                                        Layer layer = currentSceneManager.getCurrentScene2D().getLayering().getLayers().get(p);
                                        if (layer == null || layer.isShouldDestroy()) continue;

                                        int renderingObjectsNum = layer.getGameObjects().size();
                                        for (int i = 0; i < renderingObjectsNum; i++) {
                                            if (layer.isShouldDestroy()) continue layersCycle;
                                            if (!layer.getGameObjects().get(i).isShouldDestroy()) {
                                                List<ScriptComponent> scriptComponents = layer.getGameObjects().get(i).getAllComponents(ScriptComponent.class);

                                                for (int k = 0; k < scriptComponents.size(); k++) {
                                                    // был ли уже скомпилирован скрипт
                                                    boolean alreadyCompiled = compiledScripts.contains(scriptComponents.get(k).script.getName());
                                                    if (alreadyCompiled) {
                                                        continue;
                                                    }

                                                    String scriptPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + scriptComponents.get(k).script.path;
                                                    long lastModified = new File(scriptPath + ".java").lastModified();
                                                    if (lastModified != scriptComponents.get(k).script.getLastModified()) {
                                                        EngineSettings.Playmode.canEnterPlaymode = false;
                                                        scriptComponents.get(k).script.setLastModified(lastModified);

                                                        int finalK = k;
                                                        String lastScriptPath = scriptComponents.get(finalK).script.path;
                                                        ViewsManager.getBottomMenuView().addTaskToList(new StoppableTask("Compiling script " + new File(scriptPath).getName() + "... ", 1.0f, 0.0f) {
                                                            public void run() {
                                                                if (currentSceneManager.getCurrentScene2D() != null) {
                                                                    scriptComponents.get(finalK).script.saveTempValues();

                                                                    String newScriptPath = scriptPath.replace(".java", "");
                                                                    boolean compiled = Compiler.compileScript(newScriptPath + ".java");
                                                                    if (compiled) {
                                                                        scriptComponents.get(finalK).script.loadClass(new File(scriptPath).getParent(), FilenameUtils.getBaseName(new File(scriptPath).getName()));
                                                                        scriptComponents.get(finalK).script.path = lastScriptPath;
                                                                    }
                                                                    compiledScripts.add(scriptComponents.get(finalK).script.getName());

                                                                    scriptComponents.get(finalK).script.applyTempValues();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch(Exception e) {
                                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                                }
                            }
                        }
                    }
                });
                helpThread.start();

                /**
                 * OpenAL TEST
                 */

                fuckYouAudio.loadAndSetup(Core2D.class.getResourceAsStream("/data/audio/audio_1.wav"));

                /** -------------------- */


                System.gc();
            }

            @Override
            public void onExit() {
                if(ProjectsManager.getCurrentProject() != null) {
                    ProjectsManager.getCurrentProject().save();
                }
            }

            @Override
            public void onDrawFrame() {
                Core2D.getWindow().setName("Sungear Engine 2D. FPS: " + Core2D.getDeltaTimer().getFPS());

                TransformComponent cameraTransformComponent = mainCamera2D.getComponent(TransformComponent.class);
                if(cameraTransformComponent != null) {
                    cameraTransformComponent.getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                }
                //cameraAnchor.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                CameraController.control();

                if(!Keyboard.keyDown(GLFW.GLFW_KEY_F)) GUI.draw();

                GraphicsRenderer.draw();

                mainCamera2D.update();

                //Core2D.getWindow().setName("Sungear Engine 2D. FPS: " + Core2D.getDeltaTimer().getFPS());
            }

            @Override
            public void onDeltaUpdate(float deltaTime) {
                mainCamera2D.deltaUpdate(deltaTime);
                currentSceneManager.updateCurrentScene2D(deltaTime);
            }
        };

        Core2D.core2DUserCallback = core2DUserCallback;
        Core2D.start();
        //Core2D.start("Sungear Engine 2D", new int[] { GLFW.GLFW_SAMPLES }, new int[] { 8 });
    }

    public static GameObject getMainCamera2D() { return mainCamera2D; }
}
