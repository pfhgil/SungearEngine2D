package Core2D.Prefab;

import Core2D.GameObject.GameObject;
import Core2D.Layering.Layer;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Prefab
{
    private GameObject prefabObject;
    private List<Prefab> childrenObjects = new ArrayList<>();

    public Prefab()
    {

    }

    public Prefab(GameObject prefabObject)
    {
        setPrefabObject(prefabObject);
    }

    public void save(String path)
    {
        String serialized = Utils.gson.toJson(this);
        FileUtils.serializeObject(path, serialized);
    }

    public static Prefab load(String path)
    {
        String deSerialized = (String) FileUtils.deSerializeObject(path);
        return Utils.gson.fromJson(deSerialized, Prefab.class);
    }

    public void applyObjectsToScene()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            Layer layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer(prefabObject.layerName);
            if(layer == null) {
                layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default");
            }
            SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;
            prefabObject.ID = SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID;
            prefabObject.setLayer(layer);
            prefabObject.getChildrenObjectsID().clear();
            for (Prefab prefab : childrenObjects) {
                layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer(prefab.getPrefabObject().layerName);
                if (layer == null) {
                    layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default");
                }
                SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;

                prefab.getPrefabObject().ID = SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID;
                prefab.getPrefabObject().setLayer(layer);
                prefabObject.getChildrenObjectsID().add(prefab.getPrefabObject().ID);
                applyChildPrefabToScene(prefab);
            }
        }
    }

    private void applyChildPrefabToScene(Prefab parentPrefab)
    {
        for (Prefab childPrefab : parentPrefab.getChildrenObjects()) {
            Layer layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer(childPrefab.getPrefabObject().layerName);
            if (layer == null) {
                layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default");
            }
            SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;

            childPrefab.getPrefabObject().ID = SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID;
            childPrefab.getPrefabObject().setLayer(layer);
            parentPrefab.getPrefabObject().getChildrenObjectsID().add(childPrefab.getPrefabObject().ID);
            applyChildPrefabToScene(childPrefab);
        }
    }

    public GameObject getPrefabObject() { return prefabObject; }
    public void setPrefabObject(GameObject prefabObject)
    {
        this.prefabObject = prefabObject;

        childrenObjects.clear();
        for (GameObject childObject : prefabObject.getChildrenObjects()) {
            childrenObjects.add(new Prefab(childObject));
        }
    }

    public List<Prefab> getChildrenObjects() { return childrenObjects; }
}
