package SungearEngine2D.Main;

import Core2D.AssetManager.AssetManager;
import Core2D.Audio.Audio;
import Core2D.CamerasManager.CamerasManager;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DUserCallback;
import Core2D.Core2D.Settings;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.Systems.ScriptableSystem;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Input.PC.Keyboard;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scripting.Script;
import Core2D.ShaderUtils.FrameBuffer;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.ExceptionsUtils;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.DebugDraw.CamerasDebugLines;
import SungearEngine2D.DebugDraw.EntitiesDebugDraw;
import SungearEngine2D.DebugDraw.Gizmo;
import SungearEngine2D.GUI.GUI;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.AppData.AppDataManager;
import imgui.ImGui;
import imgui.ImVec2;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;
import static org.lwjgl.opengl.GL46C.*;

public class Main
{
    private static Core2DUserCallback core2DUserCallback;

    private static Entity mainCamera2D;

    public static Thread helpThread;

    // TODO: delete this
    public static Audio fuckYouAudio = new Audio();

    private static Shader onlyColorShader;

    public static void main(String[] main)
    {
        Settings.Core2D.destinationFPS = 120;
        Graphics.setScreenClearColor(new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        AppDataManager.init();
        core2DUserCallback = new Core2DUserCallback() {
            @Override
            public void onInit() {
                //Debugger.init();
                Resources.load();

                mainCamera2D = Entity.createAsCamera2D();
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
                                if(Settings.Core2D.sleepCore2D) {
                                    Thread.sleep(1000);
                                }
                            } catch (InterruptedException e) {
                                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                            }

                            if(currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().isSceneLoaded() && !EngineSettings.Playmode.active) {
                                try {
                                    List<String> compiledScripts = new ArrayList<>();

                                    int layersNum = currentSceneManager.getCurrentScene2D().getLayering().getLayers().size();
                                    layersCycle: for (int p = 0; p < layersNum; p++) {
                                        Layer layer = currentSceneManager.getCurrentScene2D().getLayering().getLayers().get(p);
                                        if (layer == null || layer.isShouldDestroy()) continue;

                                        int renderingObjectsNum = layer.getEntities().size();
                                        for (int i = 0; i < renderingObjectsNum; i++) {
                                            if (layer.isShouldDestroy()) continue layersCycle;
                                            if (!layer.getEntities().get(i).isShouldDestroy()) {
                                                List<ScriptComponent> scriptComponents = layer.getEntities().get(i).getAllComponents(ScriptComponent.class);
                                                List<ScriptableSystem> scriptableSystems = layer.getEntities().get(i).getAllSystems(ScriptableSystem.class);
                                                List<Script> allScripts = new ArrayList<>();
                                                scriptComponents.forEach(scriptComponent -> allScripts.add(scriptComponent.script));
                                                scriptableSystems.forEach(scriptableSystem -> allScripts.add(scriptableSystem.script));

                                                for(Component component : layer.getEntities().get(i).getComponents()) {
                                                    Class<?> componentClass = component.getClass();
                                                    Object componentInstance = component;
                                                    if(component instanceof ScriptComponent scriptComponent) {
                                                        componentClass = scriptComponent.script.getScriptClassInstance().getClass();
                                                        componentInstance = scriptComponent.script.getScriptClassInstance();
                                                    }

                                                    for(Field field : componentClass.getFields()) {
                                                        if(field.getType().isAssignableFrom(Shader.class)) {
                                                            Shader shader = (Shader) field.get(componentInstance);

                                                            String shaderFullPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + shader.path;
                                                            File file = new File(shaderFullPath);

                                                            if(file.exists()) {
                                                                long lastModified = file.lastModified();
                                                                if (lastModified != shader.lastModified) {
                                                                    Compiler.addShaderToCompile(shader);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                for (int k = 0; k < allScripts.size(); k++) {
                                                    // был ли уже скомпилирован скрипт
                                                    boolean alreadyCompiled = compiledScripts.contains(allScripts.get(k).getPath());
                                                    if (alreadyCompiled) {
                                                        continue;
                                                    }

                                                    String scriptPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + allScripts.get(k).path;
                                                    long lastModified = new File(scriptPath.replace(".java", "") + ".java").lastModified();
                                                    if (lastModified != allScripts.get(k).getLastModified()) {
                                                        EngineSettings.Playmode.canEnterPlaymode = false;
                                                        allScripts.get(k).setLastModified(lastModified);

                                                        int finalK = k;
                                                        String lastScriptPath = allScripts.get(finalK).path;
                                                        ViewsManager.getBottomMenuView().addTaskToList(new StoppableTask("Compiling script " + new File(scriptPath).getName() + "... ", 1.0f, 0.0f) {
                                                            public void run() {
                                                                if (currentSceneManager.getCurrentScene2D() != null) {
                                                                    allScripts.get(finalK).saveTempValues();

                                                                    String newScriptPath = scriptPath.replace(".java", "");
                                                                    boolean compiled = Compiler.compileScript(newScriptPath + ".java");
                                                                    if (compiled) {
                                                                        allScripts.get(finalK).loadClass(ProjectsManager.getCurrentProject().getScriptsPath(), scriptPath, FilenameUtils.getBaseName(new File(scriptPath).getName()).replace("\\\\/", "."));
                                                                        allScripts.get(finalK).path = lastScriptPath;
                                                                    }
                                                                    compiledScripts.add(allScripts.get(finalK).getPath());

                                                                    allScripts.get(finalK).applyTempValues();
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

                onlyColorShader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/common/only_color_shader.glsl"));

                glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
                glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
                mainCamera2D.getComponent(Camera2DComponent.class).camera2DCallback = new Camera2DComponent.Camera2DCallback() {
                    @Override
                    public void preRender()
                    {
                        if(ViewsManager.getInspectorView().getCurrentInspectingObject() != null) {
                            // обработка стенсил буфера отключена
                            glStencilMask(0x00);

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
                            glStencilFunc(GL_ALWAYS, 1, 0xFF);
                            glStencilMask(0xFF);
                            Graphics.getMainRenderer().render(inspectingEntity);

                            MeshComponent meshComponent = inspectingEntity.getComponent(MeshComponent.class);
                            TransformComponent transformComponent = inspectingEntity.getComponent(TransformComponent.class);
                            if(meshComponent != null && transformComponent != null) {
                                Vector2f lastScale = new Vector2f(transformComponent.getTransform().getScale());
                                Vector4f lastColor = new Vector4f(inspectingEntity.color);

                                transformComponent.getTransform().setScale(new Vector2f(lastScale).add(new Vector2f(0.1f, 0.1f)));
                                transformComponent.update();
                                inspectingEntity.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));

                                // второй проход рендера - отрисовываю объект чуть побольше только одним цветом. все значения пикселей в стенсио буфере, которые не равняются 0xFF будут отрисованы
                                glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
                                glStencilMask(0x00);
                                Graphics.getMainRenderer().render(inspectingEntity, onlyColorShader);

                                // третяя обработка - все пиксели будут перезаписаны. включаю обработку стенсил буфера
                                glStencilFunc(GL_ALWAYS, 0, 0xFF);
                                glStencilMask(0xFF);

                                transformComponent.getTransform().setScale(lastScale);
                                inspectingEntity.setColor(lastColor);
                            }
                        }

                        CamerasDebugLines.draw();
                        EntitiesDebugDraw.draw();
                        Gizmo.draw();
                    }
                };


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

                Compiler.compileAllShaders();

                TransformComponent cameraTransformComponent = mainCamera2D.getComponent(TransformComponent.class);
                if(cameraTransformComponent != null) {
                    //System.out.println("ddd");
                    cameraTransformComponent.getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                }
                //cameraAnchor.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(ViewsManager.getSceneView().getRatioCameraScale()).mul(CameraController.getMouseCameraScale()));
                CameraController.control();

                mainCamera2D.update();

                if(!Keyboard.keyDown(GLFW.GLFW_KEY_F)) GUI.draw();

                GraphicsRenderer.draw();

                //Core2D.getWindow().setName("Sungear Engine 2D. FPS: " + Core2D.getDeltaTimer().getFPS());
            }

            @Override
            public void onDeltaUpdate(float deltaTime) {
                mainCamera2D.deltaUpdate(deltaTime);
                currentSceneManager.updateCurrentScene2D(deltaTime);

                GraphicsRenderer.deltaUpdate(deltaTime);
            }
        };

        Core2D.core2DUserCallback = core2DUserCallback;
        Core2D.start();
        //Core2D.start("Sungear Engine 2D", new int[] { GLFW.GLFW_SAMPLES }, new int[] { 8 });
    }

    public static Entity getMainCamera2D() { return mainCamera2D; }
}
