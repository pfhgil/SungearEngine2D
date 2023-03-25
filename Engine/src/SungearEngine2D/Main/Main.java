package SungearEngine2D.Main;

import Core2D.AssetManager.Asset;
import Core2D.AssetManager.AssetManager;
import Core2D.CamerasManager.CamerasManager;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DUserCallback;
import Core2D.Core2D.Settings;
import Core2D.DataClasses.ScriptData;
import Core2D.DataClasses.ShaderData;
import Core2D.Debug.DebugDraw;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.Audio.AudioState;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.Camera.CameraController2DComponent;
import Core2D.ECS.Component.Components.Camera.CameraController3DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Component.Components.Transform.MoveToComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.Systems.Cameras.CamerasController2DSystem;
import Core2D.ECS.System.Systems.Cameras.CamerasController3DSystem;
import Core2D.ECS.System.Systems.TransformationsHelpingSystem;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Input.PC.Keyboard;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import SungearEngine2D.DebugDraw.CamerasDebugLines;
import SungearEngine2D.DebugDraw.EntitiesDebugDraw;
import SungearEngine2D.DebugDraw.Gizmo;
import SungearEngine2D.ECSOrientedScripts.Systems.SceneViewFixSystem;
import SungearEngine2D.GUI.GUI;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.AppData.AppDataManager;
import imgui.ImGui;
import org.apache.commons.io.FilenameUtils;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;

import static Core2D.Scene2D.SceneManager.currentSceneManager;
import static org.lwjgl.opengl.GL46C.*;

public class Main
{
    private static Core2DUserCallback core2DUserCallback;

    private static Entity mainCamera;

    public static Thread helpThread;

    // TODO: delete this
    public static AudioComponent djArbuzAudio;

    private static Shader onlyColorShader;

    private static CameraComponent mainCameraComponent;

    public static void main(String[] main)
    {
        Settings.Core2D.destinationFPS = 120;
        Graphics.setScreenClearColor(new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        AppDataManager.init();
        core2DUserCallback = new Core2DUserCallback() {
            @Override
            public void onInit() {
                //Log.CurrentSession.willPrintToFile = false;
                //Log.Console.willPrint = false;

                //Debugger.init();
                Resources.load();

                /*
                mainCamera2D = new Entity();
                mainCamera2D.name = "EditorCamera2D";
                mainCamera2D.addComponent(new TransformComponent());
                mainCamera2D.addComponent(new Camera2DComponent());
                mainCamera2D.addComponent(new MoveToComponent());

                 */
                // ------- systems add

                ECSWorld.getCurrentECSWorld().addSystem(new TransformationsHelpingSystem());
                ECSWorld.getCurrentECSWorld().addSystem(new CamerasController2DSystem());
                ECSWorld.getCurrentECSWorld().addSystem(new CamerasController3DSystem());
                ECSWorld.getCurrentECSWorld().addSystem(new SceneViewFixSystem());

                // -------------------

                mainCamera = Entity.createAsCamera2D();
                mainCamera.addComponent(new MoveToComponent());
                mainCamera.addComponent(new CameraController2DComponent());
                mainCamera.addComponent(new CameraController3DComponent());

                CamerasManager.mainCamera2D = mainCamera;

                mainCameraComponent = mainCamera.getComponent(CameraComponent.class);
                //mainCameraComponent.followScale = true;
                mainCameraComponent.viewMode = CameraComponent.ViewMode.VIEW_MODE_2D;

                GUI.init();


                String path = "test.txt";
                FileUtils.createFile(path);
                FileUtils.writeToFile(path, "привет мир!\nhello world!", true);
                java.lang.System.out.println(FileUtils.readAllFile("test.txt"));



                GraphicsRenderer.init();

                helpThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EngineSettings.initCompiler();

                        while(true) {
                            //Log.CurrentSession.println("test", Log.MessageType.ERROR);

                            try {
                                Thread.sleep(100);
                                if(Settings.Core2D.sleepCore2D) {
                                    Thread.sleep(1000);
                                }
                            } catch (InterruptedException e) {
                                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                            }

                            if(EngineSettings.Playmode.active) continue;

                            for(Asset asset : AssetManager.getInstance().getAssets()) {
                                if(asset.getAssetObject() instanceof ScriptData scriptData) {
                                    long fileLastModified = scriptData.getScriptFileLastModified();
                                    if(scriptData.lastModified != fileLastModified) {
                                        scriptData.lastModified = fileLastModified;

                                        EngineSettings.Playmode.canEnterPlaymode = false;

                                        String scriptPath = scriptData.getPath();

                                        ViewsManager.getBottomMenuView().addTaskToList(new StoppableTask("Compiling script " + new File(scriptPath).getName() + "... ", 1.0f, 0.0f) {
                                            public void run() {
                                                if (currentSceneManager.getCurrentScene2D() == null) return;
                                                boolean compiled = Compiler.compileScript(scriptData.getAbsolutePath().replace(".class", ".java"));

                                                AssetManager.getInstance().reloadAsset(scriptData.getPath(), ScriptData.class);

                                                for(ComponentsQuery componentsQuery : ECSWorld.getCurrentECSWorld().getComponentsQueries()) {
                                                    for (Component component : componentsQuery.getComponents()) {
                                                        if (!(component instanceof ScriptComponent scriptComponent) ||
                                                                !(scriptComponent.script.path.equals(scriptPath))) continue;

                                                        scriptComponent.script.saveTempValues();

                                                        if (!compiled) continue;

                                                        scriptComponent.script.loadClass(ProjectsManager.getCurrentProject().getScriptsPath(), scriptPath,
                                                                FilenameUtils.getBaseName(new File(scriptPath).getName()).replace("\\\\/", "."));

                                                        scriptComponent.script.applyTempValues();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                } else if(asset.getAssetObject() instanceof ShaderData shaderData) {
                                    if(shaderData.lastModified != shaderData.getScriptFileLastModified()) {
                                        shaderData.lastModified = shaderData.getScriptFileLastModified();

                                        String shaderPath = shaderData.getPath();

                                        for(ComponentsQuery componentsQuery : ECSWorld.getCurrentECSWorld().getComponentsQueries()) {
                                            for (Component component : componentsQuery.getComponents()) {
                                                if(component instanceof MeshComponent meshComponent && meshComponent.getShader().path.equals(shaderPath)) {
                                                    Compiler.addShaderToCompile(meshComponent.getShader());
                                                }
                                                if(component instanceof CameraComponent cameraComponent) {
                                                    for(PostprocessingLayer postprocessingLayer : cameraComponent.postprocessingLayers) {
                                                        if(postprocessingLayer.getShader().path.equals(shaderPath)) {
                                                            Compiler.addShaderToCompile(postprocessingLayer.getShader());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                helpThread.start();

                /**
                 * OpenAL TEST
                 */

                djArbuzAudio = ECSWorld.getCurrentECSWorld().audioSystem.createAudioComponent(AssetManager.getInstance().getAudioData("/data/audio/dj-arbuz.wav"));
                ECSWorld.getCurrentECSWorld().addComponent(djArbuzAudio);
                djArbuzAudio.state = AudioState.PLAYING;
                // СЕЙЧАС ИГРАЕТ - DJ ARBUZ - DJ ARBUZ
                // эт щас dj arbuz играет
                /** -------------------- */

                onlyColorShader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/common/only_color_shader.glsl"));

                // FIXME: исправить отрисовку
                OpenGL.glCall(func -> glStencilFunc(GL_NOTEQUAL, 1, 0xFF));
                OpenGL.glCall(func -> glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE));
                mainCamera.getComponent(CameraComponent.class).cameraCallbacks.add(new CameraComponent.CameraCallback() {
                    @Override
                    public void preRender()
                    {
                        if(ViewsManager.getInspectorView().getCurrentInspectingObject() != null) {
                            // обработка стенсил буфера отключена
                            OpenGL.glCall(func -> glStencilMask(0x00));

                            ((Entity) ViewsManager.getInspectorView().getCurrentInspectingObject()).active = false;
                        }
                    }

                    @Override
                    public void postRender()
                    {
                        if(ViewsManager.getInspectorView().getCurrentInspectingObject() != null) {
                            Entity inspectingEntity = (Entity) ViewsManager.getInspectorView().getCurrentInspectingObject();
                            inspectingEntity.active = true;

                            // первый проход рендера - отрисовывается объект в стенсил буфер и заполняется единицами
                            OpenGL.glCall(func -> glStencilFunc(GL_ALWAYS, 1, 0xFF));
                            OpenGL.glCall(func -> glStencilMask(0xFF));
                            Graphics.getMainRenderer().render(inspectingEntity, mainCameraComponent);

                            MeshComponent meshComponent = inspectingEntity.getComponent(MeshComponent.class);
                            TransformComponent transformComponent = inspectingEntity.getComponent(TransformComponent.class);
                            if(meshComponent != null && transformComponent != null) {
                                Vector3f lastScale = new Vector3f(transformComponent.scale);
                                Vector4f lastColor = new Vector4f(inspectingEntity.color);

                                transformComponent.scale.set(new Vector3f(lastScale).add(new Vector3f(0.35f, 0.35f, 0f)));

                                ECSWorld.getCurrentECSWorld().transformationsSystem.updateScaleMatrix(transformComponent);
                                ECSWorld.getCurrentECSWorld().transformationsSystem.updateModelMatrix(transformComponent);
                                transformComponent.lastScale.set(transformComponent.scale);

                                inspectingEntity.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));

                                // второй проход рендера - отрисовываю объект чуть побольше только одним цветом. все значения пикселей в стенсио буфере, которые не равняются 0xFF будут отрисованы
                                OpenGL.glCall(func -> glStencilFunc(GL_NOTEQUAL, 1, 0xFF));
                                OpenGL.glCall(func -> glStencilMask(0x00));
                                Graphics.getMainRenderer().render(inspectingEntity, mainCameraComponent, onlyColorShader);

                                // третяя обработка - все пиксели будут перезаписаны. включаю обработку стенсил буфера
                                OpenGL.glCall(func -> glStencilFunc(GL_ALWAYS, 0, 0xFF));
                                OpenGL.glCall(func -> glStencilMask(0xFF));

                                transformComponent.scale.set(lastScale);

                                ECSWorld.getCurrentECSWorld().transformationsSystem.updateScaleMatrix(transformComponent);
                                ECSWorld.getCurrentECSWorld().transformationsSystem.updateModelMatrix(transformComponent);
                                transformComponent.lastScale.set(transformComponent.scale);

                                inspectingEntity.setColor(lastColor);
                            }
                        }

                        OpenGL.glCall(func -> glDisable(GL_DEPTH_TEST));

                        CamerasDebugLines.draw();
                        EntitiesDebugDraw.draw();
                        Gizmo.draw();

                        OpenGL.glCall(func -> glEnable(GL_DEPTH_TEST));
                    }
                });


                java.lang.System.gc();
            }

            @Override
            public void onExit() {
                if(ProjectsManager.getCurrentProject() != null) {
                    ProjectsManager.getCurrentProject().save();
                }

                ImGui.saveIniSettingsToDisk("imgui.ini");
            }

            @Override
            public void onUpdate() {
                Core2D.getWindow().setName("Sungear Engine 2D. FPS: " + Core2D.getDeltaTimer().getFPS());

                Compiler.compileAllShaders();

                if (Keyboard.keyReleased(GLFW.GLFW_KEY_F)) {
                    Main.djArbuzAudio.state = AudioState.PLAYING;
                }
                if (Keyboard.keyReleased(GLFW.GLFW_KEY_P)) {
                    Main.djArbuzAudio.state = AudioState.PAUSED;
                }
                if (Keyboard.keyReleased(GLFW.GLFW_KEY_T)) {
                    Main.djArbuzAudio.state = AudioState.STOPPED;
                }

                mainCamera.update();

                if(!Keyboard.keyDown(GLFW.GLFW_KEY_F)) GUI.draw();

                GraphicsRenderer.draw();

                DebugDraw.drawLine("test_" + 0,
                        new Vector3f(0f),
                        new Vector3f(Math.sin(Math.toRadians((float) GLFW.glfwGetTime() * 100f)) * 1000f, Math.cos(Math.toRadians((float) GLFW.glfwGetTime() * 100f)) * 1000f, 1000f),
                        new Vector4f(1f, 0.7f, 0.5f, 1f));

                DebugDraw.drawBox("test_" + 1, new Vector3f(-250f, 0f, 500f), new Vector3f((float) GLFW.glfwGetTime() * 50f, 0f, 0f), new Vector2f(250f), new Vector4f(1f, 0f, 0f, 1f));
                DebugDraw.drawBox("test_" + 2, new Vector3f(0f, 0f, 500f), new Vector3f(0f, (float) GLFW.glfwGetTime() * 50f, 0f), new Vector2f(250f), new Vector4f(0f, 1f, 0f, 1f));
                DebugDraw.drawBox("test_" + 3, new Vector3f(250f, 0f, 500f), new Vector3f(0f, 0f, (float) GLFW.glfwGetTime() * 50f), new Vector2f(250f), new Vector4f(0f, 0f, 1f, 1f));

                DebugDraw.drawCircle("test_" + 4, new Vector3f(-250f, 0f, 500f), new Vector3f((float) GLFW.glfwGetTime() * 50f, 0f, 0f), 250f, new Vector4f(1f, 0f, 0f, 1f));
                DebugDraw.drawCircle("test_" + 5, new Vector3f(0f, 0f, 500f), new Vector3f(0f, (float) GLFW.glfwGetTime() * 50f, 0f), 250f, new Vector4f(0f, 1f, 0f, 1f));
                DebugDraw.drawCircle("test_" + 6, new Vector3f(250f, 0f, 500f), new Vector3f(0f, 0f, (float) GLFW.glfwGetTime() * 50f), 250f, new Vector4f(0f, 0f, 1f, 1f));
            }

            @Override
            public void onDeltaUpdate(float deltaTime) {
                GraphicsRenderer.deltaUpdate(deltaTime);
            }
        };

        Core2D.core2DUserCallback = core2DUserCallback;
        Core2D.start();
    }

    public static Entity getMainCamera() { return mainCamera; }

    public static CameraComponent getMainCamera2DComponent() { return mainCameraComponent; }
}
