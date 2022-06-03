package Core2D.Layering;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Layering {
    private List<Layer> layers = new ArrayList<>();

    public void draw()
    {
        for(Layer layer : layers) {
            layer.draw();
        }
    }

    // рисует все объекты разными цветами при выборке объектов
    public void drawPicking()
    {
        for(Layer layer : layers) {
            layer.drawPicking();
        }
    }

    public Object2D getPickedObject2D(Vector3f pixelColor)
    {
        Object2D pickedObject2D = null;
        for(Layer layer : layers) {
            pickedObject2D = layer.getPickedObject2D(pixelColor);
            if(pickedObject2D != null) {
                return pickedObject2D;
            }
        }

        return null;
    }

    public void update(float deltaTime)
    {
        for(Layer layer : layers) {
            layer.update(deltaTime);
        }
    }

    public void sort()
    {
        layers.sort(new Comparator<Layer>() {
            @Override
            public int compare(Layer o1, Layer o2) {
                return Integer.compare(o2.getId(), o1.getId());
            }
        });
    }

    public void addLayer(Layer layer)
    {
        Layer foundLayer = getLayer(layer.getId());
        if(foundLayer == null) {
            foundLayer = getLayer(layer.getName());
        } else {
            Log.CurrentSession.println("Layer with ID " + foundLayer.getId() + " already exists");
            Log.showErrorDialog("Layer with ID " + foundLayer.getId() + " already exists");
            return;
        }

        if(foundLayer == null) {
            layers.add(layer);

            sort();
        } else {
            Log.CurrentSession.println("Layer with name '" + foundLayer.getName() + "' already exists");
            Log.showErrorDialog("Layer with name '" + foundLayer.getName() + "' already exists");
        }
    }

    public Layer getLayer(int id)
    {
        for(Layer layer : layers) {
            if(layer.getId() == id) return layer;
        }

        return null;
    }

    public Layer getLayer(String name)
    {
        for(Layer layer : layers) {
            if(layer.getName().equals(name)) return layer;
        }

        return null;
    }

    public void deleteLayer(Layer layer)
    {
        for(int i = 0; i < layer.getRenderingObjects().size(); i++) {
            CommonDrawableObjectsParameters objParams = (CommonDrawableObjectsParameters) layer.getRenderingObjects().get(i).getObject();
            objParams.setLayer(getLayer("default"));
        }

        layers.remove(layer);

        sort();
    }

    public List<Layer> getLayers() { return layers; }
}
