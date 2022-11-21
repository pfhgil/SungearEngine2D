package Core2D.Graphics;

import Core2D.GameObject.GameObject;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;

public class Renderer
{
    public void render(GameObject gameObject)
    {
        gameObject.update();
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
