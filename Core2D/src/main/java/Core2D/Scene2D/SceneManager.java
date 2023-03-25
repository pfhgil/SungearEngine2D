package Core2D.Scene2D;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DWorkMode;
import Core2D.Core2D.Settings;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scripting.ScriptTempValue;
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

    // рисует все объекты разными цветами при выборке объектов
    public void drawCurrentScene2DPicking(CameraComponent cameraComponent)
    {
        if(currentScene2D != null) currentScene2D.drawPicking(cameraComponent);
    }

    public Entity getPickedObject2D(Vector4f pixelColor)
    {
        if(currentScene2D != null) {
            return currentScene2D.getPickedEntity(pixelColor);
        }

        return null;
    }

    public void updateCurrentScene2D()
    {
        if(currentScene2D != null) {
            currentScene2D.update();
        }

        ECSWorld.getCurrentECSWorld().update();
    }

    public void deltaUpdateCurrentScene2D(float deltaTime)
    {
        if(currentScene2D != null) {
            currentScene2D.deltaUpdate(deltaTime);
        }

        ECSWorld.getCurrentECSWorld().deltaUpdate(deltaTime);
    }

    public static void saveSceneManager()
    {
        if(ProjectsManager.getCurrentProject() != null && Core2D.core2DWorkMode == Core2DWorkMode.IN_ENGINE) {
            saveSceneManager(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + "SceneManager.sm");
        }
    }

    public static void saveSceneManager(String path)
    {
        String serialized = Utils.gson.toJson(currentSceneManager);
        FileUtils.createFile(path);
        FileUtils.writeToFile(path, serialized, false);
    }

    public static void saveSceneManager(String path, SceneManager sceneManager)
    {
        String serialized = Utils.gson.toJson(sceneManager);
        FileUtils.createFile(path);
        FileUtils.writeToFile(path, serialized, false);
    }

    public static void loadSceneManagerAsCurrent()
    {
        if(ProjectsManager.getCurrentProject() != null && Core2D.core2DWorkMode == Core2DWorkMode.IN_ENGINE) {
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
        try(inputStream) {
            String deserialized = FileUtils.readAllFile(inputStream);
            if (!deserialized.equals("")) {
                return Utils.gson.fromJson(deserialized, SceneManager.class);
            }
        } catch(Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return new SceneManager();
    }

    public void saveScene(Scene2D scene, String path)
    {
        scene.saveScriptsTempValues();

        for(Layer layer : scene.getLayering().getLayers()) {
            for(Entity entity : layer.getEntities()) {
                List<ScriptComponent> scriptComponents = entity.getAllComponents(ScriptComponent.class);

                for(ScriptComponent scriptComponent : scriptComponents) {
                    for(ScriptTempValue scriptTempValue : scriptComponent.script.getScriptTempValues()) {
                        Log.Console.println("entity: " + entity.name + ", scriptTempValue name: " + scriptTempValue.getFieldName() + ", value: " + scriptTempValue.getValue());
                    }
                }
            }
        }

        String serialized = Utils.gson.toJson(scene);

        FileUtils.createFile(path);
        FileUtils.writeToFile(path, serialized, false);
    }

    public void saveScene(Scene2D scene, String path, boolean saveTempValues)
    {
        if(saveTempValues) {
            scene.saveScriptsTempValues();
        }

        String serialized = Utils.gson.toJson(scene);

        FileUtils.createFile(path);
        FileUtils.writeToFile(path, serialized, false);
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
            String deserialized = FileUtils.readAllFile(path);
            return scene2DFromJson(path, deserialized);
        } else {
            try(InputStream inputStream = Core2D.class.getResourceAsStream(path)) {
                String deserialized = FileUtils.readAllFile(inputStream);
                return scene2DFromJson(path, deserialized);
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        }
        return null;
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
        boolean[] found = new boolean[] { false };
        scene2DStoredValues.forEach(scene2D -> {
            if(FilenameUtils.getBaseName(new File(scene2D.path).getName()).equals(name)) {
                found[0] = true;
            }
        });

        return found[0];
    }

    public void setCurrentScene2D(Scene2D scene2D)
    {
        if(currentScene2D != null) {
            currentScene2D.destroy();
        }

        currentScene2D = scene2D;
        if(currentScene2D != null) {
            currentScene2D.setSceneLoaded(false);
        }

        if(currentScene2D != null) {
            currentScene2D.applyEntityDependencies();
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
