package Core2D.Utils;

import Core2D.ECS.Component;
import Core2D.ECS.Entity;
import Core2D.Layering.Layer;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class ComponentHandler
{
    public int layerID = -1;
    public int entityID = -1;
    public int componentID = -1;

    public ComponentHandler() { }

    public ComponentHandler(int layerID, int entityID, int componentID)
    {
        this.layerID = layerID;
        this.entityID = entityID;
        this.componentID = componentID;
    }

    public void setComponentToHandle(Component component)
    {
        if(component != null) {
            layerID = component.entity.getLayer().getID();
            entityID = component.entity.ID;
            componentID = component.ID;
        }
    }

    public Component getComponent()
    {
        if (currentSceneManager != null && currentSceneManager.getCurrentScene2D() != null) {
            Entity foundEntity = getEntity();
            if (foundEntity == null) return null;
            return foundEntity.findComponentByID(componentID);
        }

        return null;
    }

    public Entity getEntity()
    {
        if (currentSceneManager != null && currentSceneManager.getCurrentScene2D() != null) {
            Layer layer = currentSceneManager.getCurrentScene2D().getLayering().getLayer(layerID);
            if(layer == null) return null;
            return layer.getEntity(entityID);
        }

        return null;
    }

    public Layer getLayer()
    {
        if (currentSceneManager != null && currentSceneManager.getCurrentScene2D() != null) {
            return currentSceneManager.getCurrentScene2D().getLayering().getLayer(layerID);
        }

        return null;
    }

    public void reset()
    {
        layerID = -1;
        entityID = -1;
        componentID = -1;
    }
}
