package Core2D.Prefab;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Component;
import Core2D.Deserializers.ComponentDeserializer;
import Core2D.Deserializers.Object2DDeserializer;
import Core2D.Deserializers.PrefabDeserializer;
import Core2D.Layering.Layer;
import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class Prefab
{
    private Object prefabObject;
    private List<Prefab> childrenObjects = new ArrayList<>();

    public Prefab()
    {

    }

    public Prefab(Object prefabObject)
    {
        setPrefabObject(prefabObject);
    }

    public void save(String path)
    {
        if(prefabObject instanceof Object2D) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Prefab.class, new PrefabDeserializer())
                    .registerTypeAdapter(Component.class, new ComponentDeserializer())
                    .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                    .create();

            String serialized = gson.toJson(this);
            FileUtils.serializeObject(path, serialized);
        }
    }

    public static Prefab load(String path)
    {
        String deSerialized = (String) FileUtils.deSerializeObject(path);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Prefab.class, new PrefabDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                .create();
        return gson.fromJson(deSerialized, Prefab.class);
    }

    public void applyObjectsToScene()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            CommonDrawableObjectsParameters prefParams = (CommonDrawableObjectsParameters) prefabObject;
            Object2D parentObject = (Object2D) prefabObject;
            Layer layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer(prefParams.getLayerName());
            if(layer == null) {
                layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default");
            }
            SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;
            prefParams.setID(SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID);
            prefParams.setLayer(layer);
            parentObject.getChildrenObjectsID().clear();
            for (Prefab prefab : childrenObjects) {
                if (prefab.getPrefabObject() instanceof CommonDrawableObjectsParameters) {
                    CommonDrawableObjectsParameters params = (CommonDrawableObjectsParameters) prefab.getPrefabObject();
                    layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer(params.getLayerName());
                    if(layer == null) {
                        layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default");
                    }
                    SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;
                    params.setID(SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID);
                    params.setLayer(layer);
                    parentObject.getChildrenObjectsID().add(params.getID());
                    applyChildPrefabToScene(prefab);
                }
            }
        }
    }

    private void applyChildPrefabToScene(Prefab pref)
    {
        for (Prefab prefab : pref.getChildrenObjects()) {
            Object2D parentObj = (Object2D) pref.getPrefabObject();
            if (prefab.getPrefabObject() instanceof Object2D) {
                CommonDrawableObjectsParameters params = (CommonDrawableObjectsParameters) prefab.getPrefabObject();
                Layer layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer(params.getLayerName());
                if(layer == null) {
                    layer = SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default");
                }
                SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;
                params.setID(SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID);
                params.setLayer(layer);
                parentObj.getChildrenObjectsID().add(params.getID());
                applyChildPrefabToScene(prefab);
            }
        }
    }

    public Object getPrefabObject() { return prefabObject; }
    public void setPrefabObject(Object prefabObject)
    {
        this.prefabObject = prefabObject;

        childrenObjects.clear();
        if(prefabObject instanceof Object2D) {
            for(Object2D childObject : ((Object2D) prefabObject).getChildrenObjects()) {
                childrenObjects.add(new Prefab(childObject));
            }
        }
    }

    public List<Prefab> getChildrenObjects() { return childrenObjects; }
}
