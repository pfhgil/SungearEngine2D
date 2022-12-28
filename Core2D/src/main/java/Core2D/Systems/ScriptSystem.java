package Core2D.Systems;

import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.Systems.ScriptableSystem;
import Core2D.Layering.Layer;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.FlexibleURLClassLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

public class ScriptSystem
{
    public transient boolean runScripts = false;

    private static FlexibleURLClassLoader globalFlexibleURLClassLoader = new FlexibleURLClassLoader(new URL[] { });

    public static void applyScriptsTempValues(Scene2D scene2D)
    {
        for (Layer layer : scene2D.getLayering().getLayers()) {
            for(int i = 0; i < layer.getEntities().size(); i++) {
                Entity entity = layer.getEntities().get(i);
                if (!entity.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = entity.getAllComponents(ScriptComponent.class);

                    for (ScriptComponent scriptComponent : scriptComponents) {
                        scriptComponent.script.applyTempValues();
                    }
                }
            }
        }
    }

    public static void destroyScriptsTempValues(Scene2D scene2D)
    {
        for (Layer layer : scene2D.getLayering().getLayers()) {
            for(int i = 0; i < layer.getEntities().size(); i++) {
                Entity entity = layer.getEntities().get(i);
                if (!entity.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = entity.getAllComponents(ScriptComponent.class);

                    for (ScriptComponent scriptComponent : scriptComponents) {
                        scriptComponent.script.destroyTempValues();
                    }
                }
            }
        }
    }

    public static void saveScriptsTempValues(Scene2D scene2D)
    {
        for (Layer layer : scene2D.getLayering().getLayers()) {
            for(int i = 0; i < layer.getEntities().size(); i++) {
                Entity entity = layer.getEntities().get(i);
                if (!entity.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = entity.getAllComponents(ScriptComponent.class);

                    for (ScriptComponent scriptComponent : scriptComponents) {
                        scriptComponent.script.saveTempValues();
                    }
                }
            }
        }
    }

    public static void reloadAllSceneScriptsWithGlobalClassLoader()
    {
        globalFlexibleURLClassLoader = new FlexibleURLClassLoader(new URL[] { });

        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                for (Entity entity : layer.getEntities()) {
                    List<ScriptComponent> scriptComponents = entity.getAllComponents(ScriptComponent.class);
                    List<ScriptableSystem> scriptableSystems = entity.getAllSystems(ScriptableSystem.class);

                    for(ScriptComponent scriptComponent : scriptComponents) {
                        String lastScriptPath = scriptComponent.script.path;
                        scriptComponent.script.loadClass(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + new File(scriptComponent.script.path).getParent(),
                                FilenameUtils.getBaseName(new File(scriptComponent.script.path).getName()), globalFlexibleURLClassLoader);
                        scriptComponent.script.path = lastScriptPath;

                        scriptComponent.script.setFieldValue(scriptComponent.script.getField("entity"), scriptComponent.entity);
                    }

                    for(ScriptableSystem scriptableSystem : scriptableSystems) {
                        String lastScriptPath = scriptableSystem.script.path;
                        scriptableSystem.script.loadClass(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + new File(scriptableSystem.script.path).getParent(),
                                FilenameUtils.getBaseName(new File(scriptableSystem.script.path).getName()), globalFlexibleURLClassLoader);
                        scriptableSystem.script.path = lastScriptPath;

                        scriptableSystem.script.setFieldValue(scriptableSystem.script.getField("entity"), scriptableSystem.entity);
                    }
                }
            }
        }
    }

    public static void reloadAllSceneScriptsWithUniqueClassLoader()
    {
        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                for (Entity entity : layer.getEntities()) {
                    List<ScriptComponent> scriptComponents = entity.getAllComponents(ScriptComponent.class);
                    List<ScriptableSystem> scriptableSystems = entity.getAllSystems(ScriptableSystem.class);

                    for(ScriptComponent scriptComponent : scriptComponents) {
                        String lastScriptPath = scriptComponent.script.path;
                        scriptComponent.script.loadClass(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + new File(scriptComponent.script.path).getParent(),
                                FilenameUtils.getBaseName(new File(scriptComponent.script.path).getName()));
                        scriptComponent.script.path = lastScriptPath;

                        scriptComponent.script.setFieldValue(scriptComponent.script.getField("entity"), scriptComponent.entity);

                        System.out.println("lastScriptPath: " + lastScriptPath);
                    }

                    for(ScriptableSystem scriptableSystem : scriptableSystems) {
                        String lastScriptPath = scriptableSystem.script.path;
                        scriptableSystem.script.loadClass(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + new File(scriptableSystem.script.path).getParent(),
                                FilenameUtils.getBaseName(new File(scriptableSystem.script.path).getName()));
                        scriptableSystem.script.path = lastScriptPath;

                        scriptableSystem.script.setFieldValue(scriptableSystem.script.getField("entity"), scriptableSystem.entity);
                    }
                }
            }

            applyScriptsTempValues(SceneManager.currentSceneManager.getCurrentScene2D());
        }
    }

    public static FlexibleURLClassLoader getGlobalFlexibleURLClassLoader() { return globalFlexibleURLClassLoader; }
}
