package Core2D.Graphics;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Scene2D.SceneManager;

public class Renderer
{
    public void render(Entity entity, Shader shader)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        boolean runScripts = SceneManager.currentSceneManager != null &&
                SceneManager.currentSceneManager.getCurrentScene2D() != null &&
                SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts;

        ECSWorld.getCurrentECSWorld().meshesRenderer.renderEntity(entity, shader);
        /*
        for (Component component : entity.getComponents()) {
            //component.render(camera2DComponent, shader);
            if(component instanceof ScriptComponent scriptComponent && runScripts) {
                callRenderMethods(scriptComponent.script.getScriptClass(), scriptComponent.script.getScriptClassInstance(), shader);
            } else {
                callRenderMethods(component.getClass(), component, shader);
            }


        }

         */

        // FIXME
        /*
        for (System system : entity.getSystems()) {
            system.render(camera2DComponent, shader);
            if(system instanceof ScriptableSystem scriptableSystem && runScripts) {
                callRenderMethods(scriptableSystem.script.getScriptClass(), scriptableSystem.script.getScriptClassInstance(), shader);
            } else {
                callRenderMethods(system.getClass(), system, shader);
            }


        }

         */
    }

    public void render(Entity entity)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        boolean runScripts = SceneManager.currentSceneManager != null &&
                SceneManager.currentSceneManager.getCurrentScene2D() != null &&
                SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts;

        ECSWorld.getCurrentECSWorld().meshesRenderer.renderEntity(entity);
        /*
        for (Component component : entity.getComponents()) {
            component.render(camera2DComponent);
        }

         */

        // FIXME
        /*
        for (System system : entity.getSystems()) {
            system.render(camera2DComponent);
        }

         */
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
