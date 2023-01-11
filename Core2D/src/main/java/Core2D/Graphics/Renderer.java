package Core2D.Graphics;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.ScriptableSystem;
import Core2D.Graphics.RenderParts.RenderMethod;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Renderer
{
    public void render(Entity entity)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        boolean runScripts = SceneManager.currentSceneManager != null &&
                SceneManager.currentSceneManager.getCurrentScene2D() != null &&
                SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts;

        for (Component component : entity.getComponents()) {
            if(component instanceof ScriptComponent scriptComponent && runScripts) {
                callRenderMethods(scriptComponent.script.getScriptClass(), scriptComponent.script.getScriptClassInstance());
            } else {
                callRenderMethods(component.getClass(), component);
            }
        }

        for (System system : entity.getSystems()) {
            if(system instanceof ScriptableSystem scriptableSystem && runScripts) {
                callRenderMethods(scriptableSystem.script.getScriptClass(), scriptableSystem.script.getScriptClassInstance());
            } else {
                callRenderMethods(system.getClass(), system);
            }
        }
    }

    private void callRenderMethods(Class<?> cls, Object clsInstance)
    {
        for (Method method : cls.getMethods()) {
            if (method.isAnnotationPresent(RenderMethod.class)) {
                try {
                    method.invoke(clsInstance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                }
            }
        }
    }

    public void render(Layering layering)
    {
        if(layering.isShouldDestroy()) return;
        int layersNum = layering.getLayers().size();
        for(int i = 0; i < layersNum; i++) {
            if(layering.isShouldDestroy()) break;
            render(layering.getLayers().get(i));
        }
    }

    public void render(Layer layer)
    {
        if(layer.isShouldDestroy()) return;

        int renderingObjectsNum = layer.getEntities().size();
        for(int i = 0; i < renderingObjectsNum; i++) {
            if(layer.isShouldDestroy()) break;
            render(layer.getEntities().get(i));
        }
    }
}
