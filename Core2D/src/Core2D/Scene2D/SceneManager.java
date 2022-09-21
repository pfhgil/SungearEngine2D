package Core2D.Scene2D;

import Core2D.Camera2D.Camera2D;
import Core2D.Component.Component;
import Core2D.Core2D.Settings;
import Core2D.Deserializers.*;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Object2D.Object2D;
import Core2D.Scripting.ScriptSceneObject;
import Core2D.Utils.FileUtils;
import Core2D.Utils.WrappedObject;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector4f;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SceneManager
{
    private List<Scene2D> scenes = new ArrayList<>();

    // текущая сцена2д
    private transient Scene2D currentScene2D;

    public transient Scene2D mainScene2D;

    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(WrappedObject.class, new WrappedObjectDeserializer())
            .registerTypeAdapter(Camera2D.class, new Camera2DDeserializer())
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
            .registerTypeAdapter(ScriptSceneObject.class, new ScriptSceneObjectDeserializer())
            .registerTypeAdapter(Layer.class, new LayerDeserializer())
            .registerTypeAdapter(Layering.class, new LayeringDeserializer())
            .registerTypeAdapter(Scene2D.class, new Scene2DDeserializer())
            .create();

    public static transient SceneManager currentSceneManager = new SceneManager();

    public void drawCurrentScene2D()
    {
        if(currentScene2D != null) currentScene2D.draw();
    }

    // рисует все объекты разными цветами при выборке объектов
    public void drawCurrentScene2DPicking()
    {
        if(currentScene2D != null) currentScene2D.drawPicking();
    }

    public Object2D getPickedObject2D(Vector4f pixelColor)
    {
        if(currentScene2D != null) {
            return currentScene2D.getPickedObject2D(pixelColor);
        }

        return null;
    }

    public void updateCurrentScene2D(float deltaTime)
    {
        if(currentScene2D != null) {
            currentScene2D.deltaUpdate(deltaTime);
        }
    }

    public static void saveSceneManager(String path)
    {
        String serialized = gson.toJson(currentSceneManager);
        String newPath = new File(path).getParent() + "\\" + FilenameUtils.getBaseName(new File(path).getName()) + ".txt";
        FileUtils.createFile(newPath);
        System.out.println(newPath);
        FileUtils.writeToFile(new File(newPath), serialized, false);
        FileUtils.serializeObject(path, serialized);
    }

    public static void saveSceneManager(String path, SceneManager sceneManager)
    {
        String serialized = gson.toJson(sceneManager);
        String newPath = new File(path).getParent() + "\\" + FilenameUtils.getBaseName(new File(path).getName()) + ".txt";
        FileUtils.createFile(newPath);
        System.out.println(newPath);
        FileUtils.writeToFile(new File(newPath), serialized, false);
        FileUtils.serializeObject(path, serialized);
    }

    public static void loadSceneManager(String path)
    {
        File sceneManagerFile = new File(path);

        if(sceneManagerFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            currentSceneManager = gson.fromJson(deserialized, SceneManager.class);
            for(Scene2D scene2D : currentSceneManager.getScenes()) {
                if(scene2D.isMainScene2D) {
                    currentSceneManager.mainScene2D = scene2D;
                }
            }
        }
    }

    public static SceneManager loadSceneManagerNotAsCurrent(String path)
    {
        File sceneManagerFile = new File(path);

        if(sceneManagerFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            return gson.fromJson(deserialized, SceneManager.class);
        }

        return null;
    }

    public static void loadSceneManager(InputStream inputStream)
    {
        String deserialized = (String) FileUtils.deSerializeObject(inputStream);
        if(deserialized != null && !deserialized.equals("")) {
            currentSceneManager = gson.fromJson(deserialized, SceneManager.class);
            for(Scene2D scene2D : currentSceneManager.getScenes()) {
                if(scene2D.isMainScene2D) {
                    currentSceneManager.mainScene2D = scene2D;
                }
            }
        }
    }

    public void saveScene(Scene2D scene, String path)
    {
        scene.saveScriptsTempValues();

        scene.setScenePath(path);
        String serialized = gson.toJson(scene);

        //System.out.println(serialized);

        FileUtils.serializeObject(path, serialized);
    }

    public Scene2D loadSceneAsCurrent(String path)
    {
        File sceneFile = new File(path);

        if(sceneFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            if(!deserialized.equals("")) {
                if(currentScene2D != null) {
                    /*
                    int objectsNum = 0;
                    for(int i = 0; i < currentScene2D.getLayering().getLayers().size(); i++) {
                        for(int k = 0; k < currentScene2D.getLayering().getLayers().get(i).getRenderingObjects().size(); k++) {
                            if(currentScene2D.getLayering().getLayers().get(i).getRenderingObjects().get(k).getObject() instanceof Object2D) {
                                objectsNum++;
                            }
                        }
                    }

                     */
                    //currentScene2D.destroy();
                    //currentScene2D = null;
                }

                //System.out.println(deserialized);

                Settings.Other.Picking.currentPickingColor.x = 0.0f;
                Settings.Other.Picking.currentPickingColor.y = 0.0f;
                Settings.Other.Picking.currentPickingColor.z = 0.0f;

                Scene2D scene2D = new Scene2D();
                currentScene2D = scene2D;

                Scene2D deserializedScene2D = gson.fromJson(deserialized, Scene2D.class);
                deserializedScene2D.setPhysicsWorld(scene2D.getPhysicsWorld());
                setCurrentScene2D(deserializedScene2D);

                deserializedScene2D.setScenePath(path);

                System.gc();

                applyObject2DDependencies(currentScene2D);

                return deserializedScene2D;
            }
        }

        return null;
    }

    public Scene2D loadScene(String path)
    {
        File sceneFile = new File(path);

        if(sceneFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            if(!deserialized.equals("")) {
                if(currentScene2D != null) {
                    /*
                    int objectsNum = 0;
                    for(int i = 0; i < currentScene2D.getLayering().getLayers().size(); i++) {
                        for(int k = 0; k < currentScene2D.getLayering().getLayers().get(i).getRenderingObjects().size(); k++) {
                            if(currentScene2D.getLayering().getLayers().get(i).getRenderingObjects().get(k).getObject() instanceof Object2D) {
                                objectsNum++;
                            }
                        }
                    }

                     */
                    //currentScene2D.destroy();
                    //currentScene2D = null;
                }

                //System.out.println(deserialized);

                Settings.Other.Picking.currentPickingColor.x = 0.0f;
                Settings.Other.Picking.currentPickingColor.y = 0.0f;
                Settings.Other.Picking.currentPickingColor.z = 0.0f;

                Scene2D scene2D = new Scene2D();
                Scene2D deserializedScene2D = gson.fromJson(deserialized, Scene2D.class);
                deserializedScene2D.setPhysicsWorld(scene2D.getPhysicsWorld());

                deserializedScene2D.setScenePath(path);

                System.gc();

                applyObject2DDependencies(deserializedScene2D);

                return deserializedScene2D;
            }
        }

        return null;
    }

    private void applyObject2DDependencies(Scene2D scene2D)
    {
        for(int i = 0; i < scene2D.getLayering().getLayers().size(); i++) {
            for (int k = 0; k < scene2D.getLayering().getLayers().get(i).getRenderingObjects().size(); k++) {
                if (scene2D.getLayering().getLayers().get(i).getRenderingObjects().get(k).getObject() instanceof Object2D) {
                    Object2D object2D = (Object2D) scene2D.getLayering().getLayers().get(i).getRenderingObjects().get(k).getObject();

                    object2D.applyChildrenObjectsID();
                }
            }
        }
    }

    public List<Scene2D> getScenes() { return scenes; }

    public Scene2D getScene2D(String name)
    {
        for(Scene2D scn : scenes) {
            if(scn.getName().equals(name)) {
                return scn;
            }
        }

        return null;
    }

    public boolean isScene2DExists(String name)
    {
        return getScene2D(name) != null;
    }

    public void setCurrentScene2D(Scene2D scene2D)
    {
        if (scene2D != null) {
            scene2D.setSceneLoaded(false);
        }
        currentScene2D = scene2D;

        if(currentScene2D != null) {
            currentScene2D.load();
        }
    }
    public void setCurrentScene2D(String name)
    {
        Scene2D scene2D = null;
        for(Scene2D scn : scenes) {
            if(scn.getName().equals(name)) {
                scene2D = scn;
            }
        }

        if (currentScene2D != null) {
            currentScene2D.setSceneLoaded(false);
        }
        currentScene2D = scene2D;

        if (currentScene2D != null) {
            currentScene2D.load();
        }
    }
    public Scene2D getCurrentScene2D() { return currentScene2D; }
}
