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

    // z indices constants
    public static final float Z_INDEX_M40 = -40f;
    public static final float Z_INDEX_M38 = -38f;
    public static final float Z_INDEX_M36 = -36f;
    public static final float Z_INDEX_M34 = -34f;
    public static final float Z_INDEX_M32 = -32f;
    public static final float Z_INDEX_M30 = -30f;
    public static final float Z_INDEX_M28 = -28f;
    public static final float Z_INDEX_M26 = -26f;
    public static final float Z_INDEX_M24 = -24f;
    public static final float Z_INDEX_M22 = -22f;
    public static final float Z_INDEX_M20 = -20f;
    public static final float Z_INDEX_M18 = -18f;
    public static final float Z_INDEX_M16 = -16f;
    public static final float Z_INDEX_M14 = -14f;
    public static final float Z_INDEX_M12 = -12f;
    public static final float Z_INDEX_M10 = -10f;
    public static final float Z_INDEX_M8 = -8f;
    public static final float Z_INDEX_M6 = -6f;
    public static final float Z_INDEX_M4 = -4f;
    public static final float Z_INDEX_M2 = -2f;
    public static final float Z_INDEX_0 = 0f;
    public static final float Z_INDEX_P2 = 2f;
    public static final float Z_INDEX_P4 = 4f;
    public static final float Z_INDEX_P6 = 6f;
    public static final float Z_INDEX_P8 = 8f;
    public static final float Z_INDEX_P10 = 10f;
    public static final float Z_INDEX_P12 = 12f;
    public static final float Z_INDEX_P14 = 14f;
    public static final float Z_INDEX_P16 = 16f;
    public static final float Z_INDEX_P18 = 18f;
    public static final float Z_INDEX_P20 = 20f;
    public static final float Z_INDEX_P22 = 22f;
    public static final float Z_INDEX_P24 = 24f;
    public static final float Z_INDEX_P26 = 26f;
    public static final float Z_INDEX_P28 = 28f;
    public static final float Z_INDEX_P30 = 30f;
    public static final float Z_INDEX_P32 = 32f;
    public static final float Z_INDEX_P34 = 34f;
    public static final float Z_INDEX_P36 = 36f;
    public static final float Z_INDEX_P38 = 38f;
    public static final float Z_INDEX_P40 = 40f;

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
