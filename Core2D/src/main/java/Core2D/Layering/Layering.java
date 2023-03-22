package Core2D.Layering;

import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Entity;
import Core2D.Log.Log;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Layering {
    private List<Layer> layers = new ArrayList<>();

    private transient boolean shouldDestroy = false;

    // рисует все объекты разными цветами при выборке объектов
    public void drawPicking(CameraComponent cameraComponent)
    {
        for(Layer layer : layers) {
            layer.drawPicking(cameraComponent);
        }
    }

    public Entity getPickedEntity(Vector4f pixelColor)
    {
        Entity pickedEntity = null;
        for(Layer layer : layers) {
            pickedEntity = layer.getPickedEntity(pixelColor);
            if(pickedEntity != null) {
                return pickedEntity;
            }
        }

        return null;
    }

    public void update()
    {
        for(Layer layer : layers) {
            layer.update();
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        for(Layer layer : layers) {
            layer.deltaUpdate(deltaTime);
        }
    }

    public void sort()
    {
        layers.sort(new Comparator<Layer>() {
            @Override
            public int compare(Layer o1, Layer o2) {
                return Integer.compare(o2.getID(), o1.getID());
            }
        });
    }

    public void addLayer(Layer layer)
    {
        Layer foundLayer = getLayer(layer.getID());
        if(foundLayer == null) {
            foundLayer = getLayer(layer.getName());
        } else {
            Log.CurrentSession.println("Layer with ID " + foundLayer.getID() + " already exists", Log.MessageType.ERROR);
            Log.showErrorDialog("Layer with ID " + foundLayer.getID() + " already exists");
            return;
        }

        if(foundLayer == null) {
            layers.add(layer);
            /*
            for(Layer layerToAddPP : layers) {
                for(Entity entity : layerToAddPP.getEntities()) {
                    List<Camera2DComponent> camera2DComponents = entity.getAllComponents(Camera2DComponent.class);
                    for(Camera2DComponent camera2DComponent : camera2DComponents) {
                        camera2DComponent.addPostprocessingLayer(new PostprocessingLayer(layerToAddPP));
                        Log.Console.println("added layer: " + layer.getName());
                    }
                }
            }

             */

            sort();
        } else {
            Log.CurrentSession.println("Layer with name '" + foundLayer.getName() + "' already exists", Log.MessageType.ERROR);
            Log.showErrorDialog("Layer with name '" + foundLayer.getName() + "' already exists");
        }
    }

    public int getLayersMaxID()
    {
        int maxID = 0;
        for(Layer layer : layers) {
            if(layer.getID() > maxID) {
                maxID = layer.getID();
            }
        }
        return maxID;
    }

    public Layer getLayer(int id)
    {
        for(Layer layer : layers) {
            if(layer.getID() == id) return layer;
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
        for(int i = 0; i < layer.getEntities().size(); i++) {
            layer.getEntities().get(i).setLayer(getLayer("default"));
        }

        layers.remove(layer);

        sort();
    }

    public void destroy()
    {
        shouldDestroy = true;

        Iterator<Layer> layerIterator = layers.iterator();
        while(layerIterator.hasNext()) {
            Layer layer = layerIterator.next();
            layer.destroy();
            layerIterator.remove();
        }
    }

    public List<Layer> getLayers() { return layers; }

    public boolean isShouldDestroy() { return shouldDestroy; }
}
