package Core2D.Layering;

import Core2D.Component.Component;
import Core2D.Component.Components.MeshRendererComponent;
import Core2D.Drawable.Drawable;
import Core2D.Drawable.Object2D;
import Core2D.Graphics.Graphics;
import Core2D.Texture2D.Texture2D;
import Core2D.Texture2D.TextureDrawModes;
import Core2D.Utils.WrappedObject;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Layer
{
    private List<WrappedObject> renderingObjects = new ArrayList<>();

    private int ID;
    private String name;

    private transient boolean shouldDestroy;

    public Layer(int ID, String name)
    {
        this.ID = ID;
        this.name = name;
    }

    // рисует все объекты разными цветами при выборке объектов
    // тут я ставлю цвет объекта для pick и отключаю текстуру
    // TODO: сделать не только для объектов отрисовку
    public void drawPicking()
    {
        for(int i = 0; i < renderingObjects.size(); i++) {
            WrappedObject wrappedObject = renderingObjects.get(i);
            if (wrappedObject.getObject() instanceof Object2D) {
                Object2D object2D = ((Object2D) wrappedObject.getObject());

                Vector4f lastColor = new Vector4f(object2D.getColor());

                object2D.setColor(new Vector4f(object2D.getPickColor().x / 255.0f, object2D.getPickColor().y / 255.0f, object2D.getPickColor().z / 255.0f,  1.0f));
                MeshRendererComponent mesh = object2D.getComponent(MeshRendererComponent.class);
                mesh.textureDrawMode = TextureDrawModes.ONLY_ALPHA;
                Graphics.getMainRenderer().render(object2D);
                object2D.setColor(lastColor);
                mesh.textureDrawMode = TextureDrawModes.DEFAULT;
            }
        }
    }

    public Object2D getPickedObject2D(Vector4f pixelColor)
    {
        for(int i = 0; i < renderingObjects.size(); i++) {
            WrappedObject wrappedObject = renderingObjects.get(i);
            if(wrappedObject.getObject() instanceof Object2D) {
                Object2D object2D = ((Object2D) wrappedObject.getObject());
                if(object2D.getPickColor().x == pixelColor.x &&
                        object2D.getPickColor().y == pixelColor.y &&
                        object2D.getPickColor().z == pixelColor.z &&
                        pixelColor.w != 0.0f) {
                    return object2D;
                }
            }
        }

        return null;
    }

    public void deltaUpdate(float deltaTime)
    {
        Iterator<WrappedObject> layerObjectIterator = renderingObjects.iterator();
        while(layerObjectIterator.hasNext()) {
            WrappedObject wrappedObject = layerObjectIterator.next();
            Drawable objParams = (Drawable) wrappedObject.getObject();
            if(objParams.isShouldDestroy()) {
                if(wrappedObject.getObject() instanceof Object2D) {
                    Object2D object2D = (Object2D) wrappedObject.getObject();
                    if (object2D.getParentObject2D() != null) {
                        object2D.getParentObject2D().removeChild(object2D);
                        object2D.parentObject2D = null;
                    }

                    Iterator<Component> componentsIterator = object2D.getComponents().iterator();
                    while(componentsIterator.hasNext()) {
                        Component component = componentsIterator.next();
                        component.destroy();
                        componentsIterator.remove();
                    }

                    Iterator<Object2D> childrenIterator = object2D.getChildrenObjects().iterator();
                    while(childrenIterator.hasNext()) {
                        Object2D child = childrenIterator.next();
                        child.destroy();
                        childrenIterator.remove();
                    }
                }
                objParams.destroyParams();
                wrappedObject.setObject(null);
                layerObjectIterator.remove();
            } else {
                objParams.deltaUpdate(deltaTime);
            }
        }
    }

    public void destroy()
    {
        shouldDestroy = true;

        Iterator<WrappedObject> layerObjectIterator = renderingObjects.iterator();
        while(layerObjectIterator.hasNext()) {
            WrappedObject wrappedObject = layerObjectIterator.next();
            Drawable objParams = (Drawable) wrappedObject.getObject();
            objParams.destroy();
            wrappedObject.setObject(null);
            layerObjectIterator.remove();
        }

        renderingObjects = null;
    }

    public List<WrappedObject> getRenderingObjects() { return renderingObjects; }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isShouldDestroy() { return shouldDestroy; }
}
