package Core2D.Systems;

import Core2D.Component.Components.ScriptComponent;
import Core2D.GameObject.GameObject;
import Core2D.Layering.Layer;
import Core2D.Scene2D.Scene2D;

import java.util.List;

public class ScriptSystem
{
    public transient boolean runScripts = true;

    public static void applyScriptsTempValues(Scene2D scene2D)
    {
        for (Layer layer : scene2D.getLayering().getLayers()) {
            for(int i = 0; i < layer.getGameObjects().size(); i++) {
                GameObject gameObject = layer.getGameObjects().get(i);
                if (!gameObject.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = gameObject.getAllComponents(ScriptComponent.class);

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
            for(int i = 0; i < layer.getGameObjects().size(); i++) {
                GameObject gameObject = layer.getGameObjects().get(i);
                if (!gameObject.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = gameObject.getAllComponents(ScriptComponent.class);

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
            for(int i = 0; i < layer.getGameObjects().size(); i++) {
                GameObject gameObject = layer.getGameObjects().get(i);
                if (!gameObject.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = gameObject.getAllComponents(ScriptComponent.class);

                    for (ScriptComponent scriptComponent : scriptComponents) {
                        scriptComponent.getScript().saveTempValues();
                    }
                }
            }
        }
    }
}
