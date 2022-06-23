package Core2D.Layering;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Components.TextureComponent;
import Core2D.Graphics.Graphics;
import Core2D.Object2D.Object2D;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Layer
{
    private List<LayerObject> renderingObjects = new ArrayList<>();

    private int id;
    private String name;

    public Layer(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    // рисует все объекты разными цветами при выборке объектов
    // тут я ставлю цвет объекта для pick и отключаю текстуру
    // TODO: сделать не только для объектов отрисовку
    public void drawPicking()
    {
        for(LayerObject layerObject : renderingObjects) {
            if (layerObject.getObject() instanceof Object2D) {
                Object2D object2D = ((Object2D) layerObject.getObject());

                //System.out.println("name: " + object2D.getName() + ", color: " + object2D.getPickColor().x + ", " + object2D.getPickColor().y + ", " + object2D.getPickColor().z);

                Vector4f lastColor = new Vector4f(object2D.getColor());

                object2D.setColor(new Vector4f(object2D.getPickColor().x / 255.0f, object2D.getPickColor().y / 255.0f, object2D.getPickColor().z / 255.0f,  1.0f));
                TextureComponent textureComponent = object2D.getComponent(TextureComponent.class);
                if(textureComponent != null) {
                    textureComponent.setActive(false);
                }
                Graphics.getMainRenderer().render(object2D);
                object2D.setColor(lastColor);
                if(textureComponent != null) {
                    textureComponent.setActive(true);
                }

                lastColor = null;
                object2D = null;
            }
        }
    }

    public Object2D getPickedObject2D(Vector3f pixelColor)
    {
        for(LayerObject layerObject : renderingObjects) {
            if(layerObject.getObject() instanceof Object2D) {
                Object2D object2D = ((Object2D) layerObject.getObject());
                if(object2D.getPickColor().x == pixelColor.x &&
                        object2D.getPickColor().y == pixelColor.y &&
                        object2D.getPickColor().z == pixelColor.z) {
                    return object2D;
                }

                object2D = null;
            }
        }

        return null;
    }

    public void update(float deltaTime)
    {
        for(int i = 0; i < renderingObjects.size(); i++) {
            ((CommonDrawableObjectsParameters) renderingObjects.get(i).getObject()).deltaUpdate(deltaTime);
        }
    }

    public void destroy()
    {
        name = null;

        /*
        Iterator<LayerObject> layerObjectIterator = renderingObjects.iterator();
        while(layerObjectIterator.hasNext()) {
            LayerObject layerObject = layerObjectIterator.next();
            CommonDrawableObjectsParameters objParams = (CommonDrawableObjectsParameters) layerObject.getObject();
            objParams.destroy();
            layerObjectIterator.remove();
            objParams = null;
            layerObject.setObject(null);
            layerObject = null;
        }

        layerObjectIterator = null;

         */
        for(int i = 0; i < renderingObjects.size(); i++) {
            ((CommonDrawableObjectsParameters) renderingObjects.get(i).getObject()).destroy();
        }

        renderingObjects = null;
    }

    public List<LayerObject> getRenderingObjects() { return renderingObjects; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
