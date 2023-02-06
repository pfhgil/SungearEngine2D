package Core2D.Graphics;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Scene2D.SceneManager;

public class Renderer
{
    public void render(Entity entity, Camera2DComponent camera2DComponent, Shader shader)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        boolean runScripts = SceneManager.currentSceneManager != null &&
                SceneManager.currentSceneManager.getCurrentScene2D() != null &&
                SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts;

        for (Component component : entity.getComponents()) {
            component.render(camera2DComponent, shader);
            /*
            if(component instanceof ScriptComponent scriptComponent && runScripts) {
                callRenderMethods(scriptComponent.script.getScriptClass(), scriptComponent.script.getScriptClassInstance(), shader);
            } else {
                callRenderMethods(component.getClass(), component, shader);
            }

             */
        }

        for (System system : entity.getSystems()) {
            system.render(camera2DComponent, shader);
            /*
            if(system instanceof ScriptableSystem scriptableSystem && runScripts) {
                callRenderMethods(scriptableSystem.script.getScriptClass(), scriptableSystem.script.getScriptClassInstance(), shader);
            } else {
                callRenderMethods(system.getClass(), system, shader);
            }

             */
        }
    }

    public void render(Entity entity, Camera2DComponent camera2DComponent)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        boolean runScripts = SceneManager.currentSceneManager != null &&
                SceneManager.currentSceneManager.getCurrentScene2D() != null &&
                SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts;

        for (Component component : entity.getComponents()) {
            component.render(camera2DComponent);
        }

        for (System system : entity.getSystems()) {
            system.render(camera2DComponent);
        }
    }

    public void render(Layering layering, Camera2DComponent camera2DComponent)
    {
        if(layering.isShouldDestroy()) return;
        int layersNum = layering.getLayers().size();
        for(int i = 0; i < layersNum; i++) {
            if(layering.isShouldDestroy()) break;
            render(layering.getLayers().get(i), camera2DComponent);
        }
    }

    public void render(Layer layer, Camera2DComponent camera2DComponent)
    {
        if(layer.isShouldDestroy()) return;

        int renderingObjectsNum = layer.getEntities().size();
        for(int i = 0; i < renderingObjectsNum; i++) {
            if(layer.isShouldDestroy()) break;
            render(layer.getEntities().get(i), camera2DComponent);
        }
    }
}
