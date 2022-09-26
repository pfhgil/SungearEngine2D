package Core2D.Scene2D;

import Core2D.Camera2D.Camera2D;
import Core2D.Component.Component;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Settings;
import Core2D.Deserializers.*;
import Core2D.Layering.Layer;
import Core2D.Layering.LayerObject;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Scripting.ScriptSceneObject;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.WrappedObject;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector4f;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SceneManager
{
    //private List<Scene2D> scenes = new ArrayList<>();
    // пути до сцен
    private List<Scene2DStoredValues> scene2DStoredValues = new ArrayList<>();

    // текущая сцена2д
    private transient Scene2D currentScene2D;

    // путь до главной сцены
    public String mainScene2DPath = "";
    public transient Scene2D mainScene2D;

    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(WrappedObject.class, new WrappedObjectDeserializer())
            .registerTypeAdapter(Camera2D.class, new Camera2DDeserializer())
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
            .registerTypeAdapter(ScriptSceneObject.class, new ScriptSceneObjectDeserializer())
            .registerTypeAdapter(LayerObject.class, new LayerObjectDeserializer())
            .registerTypeAdapter(Layer.class, new LayerDeserializer())
            .registerTypeAdapter(Layering.class, new LayeringDeserializer())
            .registerTypeAdapter(Scene2D.class, new Scene2DDeserializer())
            .registerTypeAdapter(SceneManager.class, new SceneManagerDeserializer())
            .registerTypeAdapter(Scene2DStoredValues.class, new Scene2DStoredValuesDeserializer())
            .create();

    public static SceneManager currentSceneManager = new SceneManager();

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

    public static SceneManager loadSceneManager(String path)
    {
        File sceneManagerFile = new File(path);

        if(sceneManagerFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            return gson.fromJson(deserialized, SceneManager.class);
            /*
            for(Scene2D scene2D : currentSceneManager.getScenes()) {
                if(scene2D.isMainScene2D) {
                    currentSceneManager.mainScene2D = scene2D;
                }
            }

             */
        }

        return new SceneManager();
    }

    public static void loadSceneManagerAsCurrent(String path)
    {
        /*
        File sceneManagerFile = new File(path);

        if(sceneManagerFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            currentSceneManager = gson.fromJson(deserialized, SceneManager.class);
        }

         */
        currentSceneManager = loadSceneManager(path);
    }

    public static void loadSceneManagerAsCurrent(InputStream inputStream)
    {
        currentSceneManager = loadSceneManager(inputStream);
    }

    public static SceneManager loadSceneManager(InputStream inputStream)
    {
        String deserialized = (String) FileUtils.deSerializeObject(inputStream);
        if(deserialized != null && !deserialized.equals("")) {
            Log.CurrentSession.println("scene manager code: " + deserialized, Log.MessageType.INFO);
            //currentSceneManager = gson.fromJson(deserialized, SceneManager.class);
            return gson.fromJson(deserialized, SceneManager.class);
            /*
            for(Scene2D scene2D : currentSceneManager.getScenes()) {
                if(scene2D.isMainScene2D) {
                    currentSceneManager.mainScene2D = scene2D;
                }
            }

             */
        }
        return new SceneManager();
    }

    public void saveScene(Scene2D scene, String path)
    {
        //scene.saveScriptsTempValues();

        String serialized = gson.toJson(scene);

        System.out.println(serialized);

        File f = new File(path);
        String s = f.getParentFile().getPath() + "/" + FilenameUtils.getBaseName(f.getName()) + ".txt";
        File f0 = FileUtils.createFile(s);
        FileUtils.writeToFile(f0, serialized, false);

        FileUtils.serializeObject(path, serialized);
    }

    public Scene2D loadSceneAsCurrent(String path)
    {
        if(currentScene2D != null) {
            currentScene2D.destroy();
            currentScene2D = null;
        }
        System.gc();
        Scene2D scene2D = new Scene2D();
        currentScene2D = scene2D;

        Scene2D deserializedScene2D = loadScene(path);
        deserializedScene2D.setPhysicsWorld(scene2D.getPhysicsWorld());
        setCurrentScene2D(deserializedScene2D);

        deserializedScene2D.setScenePath(path);

        //applyObject2DDependencies(currentScene2D);

        System.gc();

        return deserializedScene2D;
    }

    private Scene2D scene2DFromJson(String scene2DPath, String deserializedScene2DString)
    {
        if(!deserializedScene2DString.equals("")) {
            Settings.Other.Picking.currentPickingColor.x = 0.0f;
            Settings.Other.Picking.currentPickingColor.y = 0.0f;
            Settings.Other.Picking.currentPickingColor.z = 0.0f;

            Scene2D scene2D = new Scene2D();
            Scene2D deserializedScene2D = gson.fromJson(deserializedScene2DString, Scene2D.class);
            deserializedScene2D.setPhysicsWorld(scene2D.getPhysicsWorld());

            deserializedScene2D.setScenePath(scene2DPath);

            System.gc();

            //applyObject2DDependencies(deserializedScene2D);

            return deserializedScene2D;
        }

        return null;
    }

    public Scene2D loadScene(String path)
    {
        File sceneFile = new File(path);

        if(sceneFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            Scene2D scene2D = scene2DFromJson(path, deserialized);
            return scene2D;
        } else {
            InputStream inputStream = Core2D.class.getResourceAsStream(path);
            String deserialized = (String) FileUtils.deSerializeObject(inputStream);
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
            Scene2D scene2D = scene2DFromJson(path, deserialized);
            return scene2D;
        }
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

    //public List<Scene2D> getScenes() { return scenes; }

    public Scene2D getScene2D(String name)
    {
        /*
        for(Scene2D scn : scenes) {
            if(scn.getName().equals(name)) {
                return scn;
            }
        }

         */
        for(Scene2DStoredValues storedValues : scene2DStoredValues) {
            File sceneFile = new File(storedValues.path);
            if(name.equals(FilenameUtils.getBaseName(sceneFile.getName()))) {
                return this.loadScene(storedValues.path);
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

        if(currentScene2D != null) {
            applyObject2DDependencies(currentScene2D);
        }
    }
    public void setCurrentScene2D(String name)
    {
        Scene2D scene2D = getScene2D(name);

        if (currentScene2D != null) {
            currentScene2D.setSceneLoaded(false);
        }
        currentScene2D = scene2D;

        if (currentScene2D != null) {
            currentScene2D.load();
        }

        if(currentScene2D != null) {
            applyObject2DDependencies(currentScene2D);
        }
    }
    public Scene2D getCurrentScene2D() { return currentScene2D; }

    public List<Scene2DStoredValues> getScene2DStoredValues() { return scene2DStoredValues; }
}
