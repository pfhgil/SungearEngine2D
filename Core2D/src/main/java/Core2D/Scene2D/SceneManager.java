package Core2D.Scene2D;

import Core2D.Audio.AudioManager;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.Core2D.Settings;
import Core2D.GameObject.GameObject;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import Core2D.Log.Log;
import Core2D.Physics.PhysicsWorld;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector4f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SceneManager
{
    // пути до сцен
    private List<Scene2DStoredValues> scene2DStoredValues = new ArrayList<>();

    // текущая сцена2д
    private transient Scene2D currentScene2D;

    // путь до главной сцены
    public String mainScene2DPath = "";
    public transient Scene2D mainScene2D;

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

    public GameObject getPickedObject2D(Vector4f pixelColor)
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

    public static void saveSceneManager()
    {
        if(ProjectsManager.getCurrentProject() != null && Core2D.core2DMode == Core2DMode.IN_ENGINE) {
            saveSceneManager(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + "SceneManager.sm");
        }
    }

    public static void saveSceneManager(String path)
    {
        String serialized = Utils.gson.toJson(currentSceneManager);
        String newPath = new File(path).getParent() + "\\" + FilenameUtils.getBaseName(new File(path).getName()) + ".txt";
        FileUtils.createFile(newPath);
        FileUtils.writeToFile(new File(newPath), serialized, false);
        FileUtils.serializeObject(path, serialized);
    }

    public static void saveSceneManager(String path, SceneManager sceneManager)
    {
        String serialized = Utils.gson.toJson(sceneManager);
        String newPath = new File(path).getParent() + "\\" + FilenameUtils.getBaseName(new File(path).getName()) + ".txt";
        FileUtils.createFile(newPath);
        FileUtils.writeToFile(new File(newPath), serialized, false);
        FileUtils.serializeObject(path, serialized);
    }

    public static void loadSceneManagerAsCurrent()
    {
        if(ProjectsManager.getCurrentProject() != null && Core2D.core2DMode == Core2DMode.IN_ENGINE) {
            loadSceneManagerAsCurrent(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + "SceneManager.sm");
        }
    }

    public static void loadSceneManagerAsCurrent(String path)
    {
        currentSceneManager = loadSceneManager(path);
    }

    public static void loadSceneManagerAsCurrent(InputStream inputStream)
    {
        currentSceneManager = loadSceneManager(inputStream);
    }

    public static SceneManager loadSceneManager(String path)
    {
        if(new File(path).exists()) {
            try {
                return loadSceneManager(new BufferedInputStream(new FileInputStream(path)));
            } catch (FileNotFoundException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        }

        return new SceneManager();
    }

    public static SceneManager loadSceneManager(InputStream inputStream)
    {
        String deserialized = (String) FileUtils.deSerializeObject(inputStream);
        if(deserialized != null && !deserialized.equals("")) {
            Log.CurrentSession.println("scene manager code: " + deserialized, Log.MessageType.INFO);
            return Utils.gson.fromJson(deserialized, SceneManager.class);
        }

        return new SceneManager();
    }

    public void saveScene(Scene2D scene, String path)
    {
        scene.saveScriptsTempValues();

        String serialized = Utils.gson.toJson(scene);

        File f = new File(path);
        String s = f.getParentFile().getPath() + "/" + FilenameUtils.getBaseName(f.getName()) + ".txt";
        File f0 = FileUtils.createFile(s);
        FileUtils.writeToFile(f0, serialized, false);

        FileUtils.serializeObject(path, serialized);
    }

    public void saveScene(Scene2D scene, String path, boolean saveTempValues)
    {
        if(saveTempValues) {
            scene.saveScriptsTempValues();
        }

        String serialized = Utils.gson.toJson(scene);

        File f = new File(path);
        String s = f.getParentFile().getPath() + "/" + FilenameUtils.getBaseName(f.getName()) + ".txt";
        File f0 = FileUtils.createFile(s);
        FileUtils.writeToFile(f0, serialized, false);

        FileUtils.serializeObject(path, serialized);
    }

    public Scene2D loadSceneAsCurrent(String path)
    {
        Scene2D deserializedScene2D = loadScene(path);
        //deserializedScene2D.setPhysicsWorld(tmpPhysicsWorld);
        setCurrentScene2D(deserializedScene2D);

        deserializedScene2D.setScenePath(path);

        System.gc();

        return deserializedScene2D;
    }

    private Scene2D scene2DFromJson(String scene2DPath, String deserializedScene2DString)
    {
        if(!deserializedScene2DString.equals("")) {
            Settings.Other.Picking.currentPickingColor.x = 0.0f;
            Settings.Other.Picking.currentPickingColor.y = 0.0f;
            Settings.Other.Picking.currentPickingColor.z = 0.0f;

            Scene2D deserializedScene2D = Utils.gson.fromJson(deserializedScene2DString, Scene2D.class);
            deserializedScene2D.initPhysicsWorld();
            //deserializedScene2D.setPhysicsWorld(tmpPhysicsWorld);

            deserializedScene2D.setScenePath(scene2DPath);

            System.gc();

            return deserializedScene2D;
        }

        return null;
    }

    public Scene2D loadScene(String path)
    {
        File sceneFile = new File(path);

        if(sceneFile.exists()) {
            String deserialized = (String) FileUtils.deSerializeObject(path);
            return scene2DFromJson(path, deserialized);
        } else {
            InputStream inputStream = Core2D.class.getResourceAsStream(path);
            if(inputStream != null) {
                String deserialized = (String) FileUtils.deSerializeObject(inputStream);
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                }
                return scene2DFromJson(path, deserialized);
            }
        }
        return null;
    }

    // применить все зависимости к объектам
    private void applyObject2DDependencies(Scene2D scene2D)
    {
        for(int i = 0; i < scene2D.getLayering().getLayers().size(); i++) {
            for (int k = 0; k < scene2D.getLayering().getLayers().get(i).getGameObjects().size(); k++) {
                GameObject gameObject = scene2D.getLayering().getLayers().get(i).getGameObjects().get(k);
                gameObject.applyChildrenObjectsID();
            }
        }
    }

    public Scene2D getScene2D(String name)
    {
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

        AudioManager.destroyCurrentScene2DAllSources();

        if(currentScene2D != null) {
            currentScene2D.saveScriptsTempValues();
        }

        if(currentScene2D != null) {
            currentScene2D.destroy();
            currentScene2D = null;
        }

        System.gc();

        currentScene2D = scene2D;
        if(currentScene2D != null) {
            currentScene2D.setSceneLoaded(false);
        }

        if(currentScene2D != null) {
            applyObject2DDependencies(currentScene2D);
        }

        Keyboard.handleKeyboardInput();
        Mouse.handleMouseInput();

        if(currentScene2D != null) {
            if (currentScene2D.getSceneMainCamera2D() != null) {
                TransformComponent transformComponent = currentScene2D.getSceneMainCamera2D().getComponent(TransformComponent.class);
                if(transformComponent != null) {
                    transformComponent.init();
                }
            }
        }

        if(currentScene2D != null) {
            currentScene2D.load();
        }
    }
    public void setCurrentScene2D(String name)
    {
        Scene2D scene2D = getScene2D(name);

        setCurrentScene2D(scene2D);
    }
    public Scene2D getCurrentScene2D() { return currentScene2D; }

    public List<Scene2DStoredValues> getScene2DStoredValues() { return scene2DStoredValues; }
}
