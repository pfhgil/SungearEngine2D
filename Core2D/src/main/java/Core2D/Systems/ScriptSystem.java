package Core2D.Systems;

import Core2D.Component.Components.ScriptComponent;
import Core2D.Drawable.Object2D;
import Core2D.Layering.Layer;
import Core2D.Scene2D.Scene2D;
import Core2D.Utils.WrappedObject;

import java.util.List;

public class ScriptSystem
{
    public transient boolean runScripts = true;

    public static void applyScriptsTempValues(Scene2D scene2D)
    {
        for (Layer layer : scene2D.getLayering().getLayers()) {
            for(int i = 0; i < layer.getRenderingObjects().size(); i++) {
                WrappedObject wrappedObject = layer.getRenderingObjects().get(i);
                if (wrappedObject.getObject() instanceof Object2D && !((Object2D) wrappedObject.getObject()).isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = ((Object2D) wrappedObject.getObject()).getAllComponents(ScriptComponent.class);

                    for (ScriptComponent scriptComponent : scriptComponents) {
                        scriptComponent.getScript().applyTempValues();
                    }
                }
            }
        }
    }

    public static void destroyScriptsTempValues(Scene2D scene2D)
    {
        for (Layer layer : scene2D.getLayering().getLayers()) {
            for(int i = 0; i < layer.getRenderingObjects().size(); i++) {
                WrappedObject wrappedObject = layer.getRenderingObjects().get(i);
                if (wrappedObject.getObject() instanceof Object2D && !((Object2D) wrappedObject.getObject()).isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = ((Object2D) wrappedObject.getObject()).getAllComponents(ScriptComponent.class);

                    for (ScriptComponent scriptComponent : scriptComponents) {
                        scriptComponent.getScript().destroyTempValues();
                    }
                }
            }
        }
    }

    public static void saveScriptsTempValues(Scene2D scene2D)
    {
        for (Layer layer : scene2D.getLayering().getLayers()) {
            for(int i = 0; i < layer.getRenderingObjects().size(); i++) {
                WrappedObject wrappedObject = layer.getRenderingObjects().get(i);
                if (wrappedObject.getObject() instanceof Object2D && !((Object2D) wrappedObject.getObject()).isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = ((Object2D) wrappedObject.getObject()).getAllComponents(ScriptComponent.class);

                    for (ScriptComponent scriptComponent : scriptComponents) {
                        scriptComponent.getScript().saveTempValues();
                    }
                }
            }
        }
    }
<<<<<<< Updated upstream
=======

    public static void reloadAllSceneScriptsWithGlobalClassLoader()
    {
        globalFlexibleURLClassLoader = new FlexibleURLClassLoader(new URL[] { });

        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
                for (Entity entity : layer.getEntities()) {
                    List<ScriptComponent> scriptComponents = entity.getAllComponents(ScriptComponent.class);

                    for(ScriptComponent scriptComponent : scriptComponents) {
                        String lastScriptPath = scriptComponent.script.path;
                        scriptComponent.script.loadClass(ProjectsManager.getCurrentProject().getScriptsPath(),
                                ProjectsManager.getCurrentProject().getProjectPath() + File.separator + new File(scriptComponent.script.path),
                                FilenameUtils.getBaseName(new File(scriptComponent.script.path).getName()), globalFlexibleURLClassLoader);
                        scriptComponent.script.path = lastScriptPath;

                        scriptComponent.script.setFieldValue(scriptComponent.script.getField("entity"), scriptComponent.entity);
                    }

                    /*
                    for(ScriptableSystem scriptableSystem : scriptableSystems) {
                        String lastScriptPath = scriptableSystem.script.path;
                        scriptableSystem.script.loadClass(ProjectsManager.getCurrentProject().getScriptsPath(),
                                ProjectsManager.getCurrentProject().getProjectPath() + File.separator + new File(scriptableSystem.script.path),
                                FilenameUtils.getBaseName(new File(scriptableSystem.script.path).getName()), globalFlexibleURLClassLoader);
                        scriptableSystem.script.path = lastScriptPath;

                        scriptableSystem.script.setFieldValue(scriptableSystem.script.getField("entity"), scriptableSystem.entity);
                    }

                     */
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
                    //List<ScriptableSystem> scriptableSystems = entity.getAllSystems(ScriptableSystem.class);

                    for(ScriptComponent scriptComponent : scriptComponents) {
                        String lastScriptPath = scriptComponent.script.path;
                        scriptComponent.script.loadClass(ProjectsManager.getCurrentProject().getScriptsPath(),
                                ProjectsManager.getCurrentProject().getProjectPath() + File.separator + new File(scriptComponent.script.path),
                                FilenameUtils.getBaseName(new File(scriptComponent.script.path).getName()));
                        scriptComponent.script.path = lastScriptPath;

                        scriptComponent.script.setFieldValue(scriptComponent.script.getField("entity"), scriptComponent.entity);

                        Log.Console.println("lastScriptPath: " + lastScriptPath);
                    }

                    /*
                    for(ScriptableSystem scriptableSystem : scriptableSystems) {
                        String lastScriptPath = scriptableSystem.script.path;
                        scriptableSystem.script.loadClass(ProjectsManager.getCurrentProject().getScriptsPath(),
                                ProjectsManager.getCurrentProject().getProjectPath() + File.separator + new File(scriptableSystem.script.path),
                                FilenameUtils.getBaseName(new File(scriptableSystem.script.path).getName()));
                        scriptableSystem.script.path = lastScriptPath;

                        scriptableSystem.script.setFieldValue(scriptableSystem.script.getField("entity"), scriptableSystem.entity);
                    }

                     */
                }
            }

            applyScriptsTempValues(SceneManager.currentSceneManager.getCurrentScene2D());
        }
    }

    public static void loadAllChildURLs(FlexibleURLClassLoader flexibleURLClassLoader, String parentPath)
    {
        File parent = new File(parentPath);

        if(parent.exists()) {
            File[] listFile = parent.listFiles();
            if(listFile == null) return;
            for(File child : listFile) {
                if(child.isDirectory()) {
                    try {
                        flexibleURLClassLoader.addURL(child.toURI().toURL());
                    } catch (MalformedURLException e) {
                        Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                    }
                    loadAllChildURLs(flexibleURLClassLoader, child.getPath());
                }
            }
        }
    }

    public static FlexibleURLClassLoader getGlobalFlexibleURLClassLoader() { return globalFlexibleURLClassLoader; }
>>>>>>> Stashed changes
}
