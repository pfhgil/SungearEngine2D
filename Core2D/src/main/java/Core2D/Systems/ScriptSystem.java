package Core2D.Systems;

import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Entity;
import Core2D.Layering.Layer;
import Core2D.Scene2D.Scene2D;

import java.util.List;

public class ScriptSystem
{
    public transient boolean runScripts = false;

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
}
