package Core2D.Scene2D;

import Core2D.Camera2D.Camera2D;
import Core2D.Component.Component;
import Core2D.Core2D.Settings;
import Core2D.Deserializers.*;
import Core2D.Layering.Layer;
import Core2D.Utils.WrappedObject;
import Core2D.Layering.Layering;
import Core2D.Object2D.Object2D;
import Core2D.Utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SceneManager
{
    private static List<Scene2D> scenes = new ArrayList<>();

    // текущая сцена2д
    private static Scene2D currentScene2D;

    public static void drawCurrentScene2D()
    {
        if(currentScene2D != null) currentScene2D.draw();
    }

    // рисует все объекты разными цветами при выборке объектов
    public static void drawCurrentScene2DPicking()
    {
        if(currentScene2D != null) currentScene2D.drawPicking();
    }

    public static Object2D getPickedObject2D(Vector3f pixelColor)
    {
        if(currentScene2D != null) {
            return currentScene2D.getPickedObject2D(pixelColor);
        }

        return null;
    }

    public static void updateCurrentScene2D(float deltaTime)
    {
        if(currentScene2D != null) {
            currentScene2D.update(deltaTime);
        }
    }

    public static void saveScene(Scene2D scene, String path)
    {
        scene.saveScriptsTempValues();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(Camera2D.class, new Camera2DDeserializer())
                .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                .registerTypeAdapter(WrappedObject.class, new WrappedObjectDeserializer())
                .registerTypeAdapter(Layer.class, new LayerDeserializer())
                .registerTypeAdapter(Layering.class, new LayeringDeserializer())
                .registerTypeAdapter(Scene2D.class, new Scene2DDeserializer())
                .create();

        scene.setScenePath(path);
        String serialized = gson.toJson(scene);

        //System.out.println(serialized);

        FileUtils.serializeObject(path, serialized);
    }

    public static Scene2D loadScene(String path)
    {
        File sceneFile = new File(path);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(WrappedObject.class, new WrappedObjectDeserializer())
                .registerTypeAdapter(Camera2D.class, new Camera2DDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                .registerTypeAdapter(Layer.class, new LayerDeserializer())
                .registerTypeAdapter(Layering.class, new LayeringDeserializer())
                .registerTypeAdapter(Scene2D.class, new Scene2DDeserializer())
                .create();

        if(sceneFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            if(!deserialized.equals("")) {
                if(currentScene2D != null) {
                    int objectsNum = 0;
                    for(int i = 0; i < currentScene2D.getLayering().getLayers().size(); i++) {
                        for(int k = 0; k < currentScene2D.getLayering().getLayers().get(i).getRenderingObjects().size(); k++) {
                            if(currentScene2D.getLayering().getLayers().get(i).getRenderingObjects().get(k).getObject() instanceof Object2D) {
                                objectsNum++;
                            }
                        }
                    }
                    System.out.println("Objects num: " + objectsNum);
                    currentScene2D.destroy();
                    System.out.println("Objects destroyed: " + currentScene2D.objectsDestroyed);
                    currentScene2D = null;
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

                return deserializedScene2D;
            }
        }

        return null;
    }

    public static List<Scene2D> getScenes() { return scenes; }

    public static void setCurrentScene2D(Scene2D scene2D)
    {
        /*
        if(currentScene2D != null) {
            currentScene2D.destroy();
            currentScene2D = null;
        }

         */

        if (currentScene2D != null) {
            currentScene2D.setSceneLoaded(false);
        }
        currentScene2D = scene2D;

        currentScene2D.load();
    }
    public static Scene2D getCurrentScene2D() { return currentScene2D; }
}
