package SungearEngine2D.Main;

import Core2D.Audio.Audio;
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
import Core2D.Scene2D.SceneManager;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.GUI.GUI;
import SungearEngine2D.GUI.ImGuiUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.AppData.AppDataManager;
<<<<<<< Updated upstream
import SungearEngine2D.Utils.Debugger;
=======
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiMouseCursor;
>>>>>>> Stashed changes
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class Main
{
    private static Core2DUserCallback core2DUserCallback;

    private static Object2D cameraAnchor;
    private static Camera2D mainCamera2D;

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


                // тест кодировки
                /*

                String path = "test.txt";
                //FileUtils.createFile(path);
                FileUtils.writeToFile(path, "привет мир!\nhello world!", true);

                 */



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

                                        int renderingObjectsNum = layer.getRenderingObjects().size();
                                        for (int i = 0; i < renderingObjectsNum; i++) {
                                            if (layer.isShouldDestroy()) continue layersCycle;
                                            if (layer.getRenderingObjects().get(i).getObject() instanceof Object2D && !((Object2D) layer.getRenderingObjects().get(i).getObject()).isShouldDestroy()) {
                                                List<ScriptComponent> scriptComponents = ((Object2D) layer.getRenderingObjects().get(i).getObject()).getAllComponents(ScriptComponent.class);

                                                for (int k = 0; k < scriptComponents.size(); k++) {
                                                    // был ли уже скомпилирован скрипт
                                                    boolean alreadyCompiled = compiledScripts.contains(scriptComponents.get(k).getScript().getName());
                                                    if (alreadyCompiled) {
                                                        continue;
                                                    }

                                                    String scriptPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + scriptComponents.get(k).getScript().path;
                                                    long lastModified = new File(scriptPath + ".java").lastModified();
                                                    if (lastModified != scriptComponents.get(k).getScript().getLastModified()) {
                                                        EngineSettings.Playmode.canEnterPlaymode = false;
                                                        scriptComponents.get(k).getScript().setLastModified(lastModified);

                                                        int finalK = k;
                                                        String lastScriptPath = scriptComponents.get(finalK).getScript().path;
                                                        ViewsManager.getBottomMenuView().addTaskToList(new StoppableTask("Compiling script " + new File(scriptPath).getName() + "... ", 1.0f, 0.0f) {
                                                            public void run() {
                                                                if (currentSceneManager.getCurrentScene2D() != null) {
                                                                    scriptComponents.get(finalK).getScript().saveTempValues();

                                                                    String newScriptPath = scriptPath.replace(".java", "");
                                                                    boolean compiled = Compiler.compileScript(newScriptPath + ".java");
                                                                    if (compiled) {
                                                                        scriptComponents.get(finalK).getScript().loadClass(new File(scriptPath).getParent(), FilenameUtils.getBaseName(new File(scriptPath).getName()));
                                                                        scriptComponents.get(finalK).getScript().path = lastScriptPath;
                                                                    }
                                                                    compiledScripts.add(scriptComponents.get(finalK).getScript().getName());

                                                                    scriptComponents.get(finalK).getScript().applyTempValues();
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
                    ProjectsManager.getCurrentProject().saveProject();
                }
            }

            @Override
            public void onDrawFrame() {
                //GLFW.glfwFocusWindow(Core2D.getWindow().getWindow());
                Core2D.getWindow().setName("Sungear Engine 2D. FPS: " + Core2D.getDeltaTimer().getFPS());

<<<<<<< Updated upstream
                mainCamera2D.getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
=======
                //GLFW.glfwSetCursor(Core2D.getWindow().getWindow(),  GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR));

                Compiler.compileAllShaders();

                TransformComponent cameraTransformComponent = mainCamera2D.getComponent(TransformComponent.class);
                if(cameraTransformComponent != null) {
                    //System.out.println("ddd");
                    cameraTransformComponent.getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                }
>>>>>>> Stashed changes
                //cameraAnchor.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                CameraController.control();
                //GLFW.glfwSetCursor(Core2D.getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR));

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
