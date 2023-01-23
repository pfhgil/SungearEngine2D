package Core2D.Graphics;

import Core2D.ECS.Component.Component;
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

        for (Component component : entity.getComponents()) {
            component.render(shader);
            /*
            if(component instanceof ScriptComponent scriptComponent && runScripts) {
                callRenderMethods(scriptComponent.script.getScriptClass(), scriptComponent.script.getScriptClassInstance(), shader);
            } else {
                callRenderMethods(component.getClass(), component, shader);
            }

             */
        }

        for (System system : entity.getSystems()) {
            system.render(shader);
            /*
            if(system instanceof ScriptableSystem scriptableSystem && runScripts) {
                callRenderMethods(scriptableSystem.script.getScriptClass(), scriptableSystem.script.getScriptClassInstance(), shader);
            } else {
                callRenderMethods(system.getClass(), system, shader);
            }

             */
        }
    }

    public void render(Entity entity)
    {
        if(!entity.active || entity.isShouldDestroy()) return;

        boolean runScripts = SceneManager.currentSceneManager != null &&
                SceneManager.currentSceneManager.getCurrentScene2D() != null &&
                SceneManager.currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts;

        for (Component component : entity.getComponents()) {
            component.render();
            /*
            if(component instanceof ScriptComponent scriptComponent && runScripts) {
                scriptComponent.callMethod((params) -> scriptComponent.render());
            } else {
                component.render();
            }

             */
            /*
            if(component instanceof ScriptComponent scriptComponent && runScripts) {
                callRenderMethods(scriptComponent.script.getScriptClass(), scriptComponent.script.getScriptClassInstance());
            } else {
                callRenderMethods(component.getClass(), component);
            }

             */
        }

        for (System system : entity.getSystems()) {
            system.render();
            /*
            if(system instanceof ScriptableSystem scriptableSystem && runScripts) {
                scriptableSystem.callMethod((params) -> scriptableSystem.render());
            } else {
                system.render();
            }

             */
            /*
            if(system instanceof ScriptableSystem scriptableSystem && runScripts) {
                callRenderMethods(scriptableSystem.script.getScriptClass(), scriptableSystem.script.getScriptClassInstance());
            } else {
                callRenderMethods(system.getClass(), system);
            }

             */
        }
    }

    /*
    private void callRenderMethods(Class<?> cls, Object clsInstance, Object... params)
    {
        for (Method method : cls.getMethods()) {
            if (method.isAnnotationPresent(RenderMethod.class)) {
                try {
                    Class<?>[] paramsTypes = method.getParameterTypes();
                    if(params.length == 0 && paramsTypes.length == 0) {
                        method.invoke(clsInstance);
                    } else if(paramsTypes.length > 0 && params.length > 0 && paramsTypes[0].isAssignableFrom(Shader.class) && params[0] instanceof Shader) {
                        method.invoke(clsInstance, params[0]);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                }
            }
        }
    }

     */

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
