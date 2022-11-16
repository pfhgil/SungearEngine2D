package Core2D.Graphics;

import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Component;
import Core2D.Component.Components.MeshRendererComponent;
import Core2D.Drawable.AtlasDrawing;
import Core2D.Drawable.Drawable;
import Core2D.Drawable.Object2D;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.Utils.WrappedObject;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL46C;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Renderer
{
    public void render(Object2D object2D)
    {
        object2D.update();
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

        int renderingObjectsNum = layer.getRenderingObjects().size();
        for(int i = 0; i < renderingObjectsNum; i++) {
            if(layer.isShouldDestroy()) break;
            render(layer.getRenderingObjects().get(i));
        }
    }

    public void render(WrappedObject wrappedObject)
    {
        Object object = wrappedObject.getObject();
        if(object instanceof Drawable && ((Drawable) object).isActive()) {
            ((Drawable) object).update();
        }
    }


}
