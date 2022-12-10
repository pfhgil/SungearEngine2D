package Core2D.Graphics;

import Core2D.Component.Component;
import Core2D.Component.Components.MeshRendererComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.GameObject.GameObject;
import Core2D.GameObject.RenderParts.RenderMethod;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.Utils.ExceptionsUtils;
import org.lwjgl.opengl.GL11C;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.lwjgl.opengl.GL11.*;

public class Renderer
{
    public void render(GameObject gameObject)
    {
        for(Component component : gameObject.getComponents()) {
            for(Method method : component.getClass().getMethods()) {
                if(method.isAnnotationPresent(RenderMethod.class)) {
                    try {
                        method.invoke(component);
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

        int renderingObjectsNum = layer.getGameObjects().size();
        for(int i = 0; i < renderingObjectsNum; i++) {
            if(layer.isShouldDestroy()) break;
            render(layer.getGameObjects().get(i));
        }
    }
}
