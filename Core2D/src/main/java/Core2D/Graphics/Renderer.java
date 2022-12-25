package Core2D.Graphics;

import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.RenderMethod;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Renderer
{
    public void render(Entity entity)
    {
        for (System system : entity.getSystems()) {
            for (Method method : system.getClass().getMethods()) {
                if (method.isAnnotationPresent(RenderMethod.class)) {
                    try {
                        method.invoke(system);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                    }
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
