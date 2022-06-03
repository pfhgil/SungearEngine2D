package Core2D.Scene2D;

import Core2D.Component.Component;
import Core2D.Deserializers.*;
import Core2D.Layering.Layer;
import Core2D.Layering.LayerObject;
import Core2D.Layering.Layering;
import Core2D.Object2D.Object2D;
import Core2D.Utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SceneManager2D
{
    private List<Scene2D> scenes = new ArrayList<>();

    // текущая сцена2д
    private Scene2D currentScene2D;

    public SceneManager2D() {}

    public SceneManager2D(Scene2D currentScene2D)
    {
        setCurrentScene2D(currentScene2D);
    }

    public void drawCurrentScene2D()
    {
        if(currentScene2D != null) currentScene2D.draw();
    }

    // рисует все объекты разными цветами при выборке объектов
    public void drawCurrentScene2DPicking()
    {
        if(currentScene2D != null) currentScene2D.drawPicking();
    }

    public Object2D getPickedObject2D(Vector3f pixelColor)
    {
        if(currentScene2D != null) {
            return currentScene2D.getPickedObject2D(pixelColor);
        }

        return null;
    }

    public void updateCurrentScene2D(float deltaTime)
    {
        if(currentScene2D != null) currentScene2D.update(deltaTime);
    }

    public void saveScene(Scene2D scene, String path)
    {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                .registerTypeAdapter(LayerObject.class, new LayerObjectDeserializer())
                .registerTypeAdapter(Layer.class, new LayerDeserializer())
                .registerTypeAdapter(Layering.class, new LayeringDeserializer())
                .registerTypeAdapter(Scene2D.class, new Scene2DDeserializer())
                .create();

        String serialized = gson.toJson(scene);

        File sceneFile = FileUtils.createFile(path);
        FileUtils.writeToFile(sceneFile, serialized, false);
    }

    public Scene2D loadScene(String path)
    {
        String sceneString = FileUtils.readAllFile(new File(path));

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LayerObject.class, new LayerObjectDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                .registerTypeAdapter(Layer.class, new LayerDeserializer())
                .registerTypeAdapter(Layering.class, new LayeringDeserializer())
                .registerTypeAdapter(Scene2D.class, new Scene2DDeserializer())
                .create();

        if(!sceneString.equals("")) {
            Scene2D scene2D = gson.fromJson(sceneString, Scene2D.class);
            setCurrentScene2D(scene2D);

            return scene2D;
        }

        return null;
    }

    public List<Scene2D> getScenes() { return scenes; }

    public void setCurrentScene2D(Scene2D currentScene2D)
    {
        this.currentScene2D = currentScene2D;

        this.currentScene2D.load();
    }
    public Scene2D getCurrentScene2D() { return currentScene2D; }
}
