package Core2D.Scene2D;

import Core2D.CamerasManager.CamerasManager;
import Core2D.Component.Components.BoxCollider2DComponent;
import Core2D.Component.Components.CircleCollider2DComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.GameObject.GameObject;
import Core2D.Graphics.Graphics;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.Physics.PhysicsWorld;
import Core2D.Physics.Rigidbody2D;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.Tag;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Scene2D
{
    private String name = "sampleScene2D";
    private String scenePath = "";

    private Layering layering = new Layering();

    private transient GameObject sceneMainCamera2D;

    private transient Scene2DCallback scene2DCallback;

    // цвет очистки экрана
    private Vector4f screenClearColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private List<Tag> tags = new ArrayList<>();

    private transient PhysicsWorld physicsWorld = new PhysicsWorld();

    private ScriptSystem scriptSystem = new ScriptSystem();

    public transient int objectsDestroyed = 0;

    private transient boolean sceneLoaded = false;

    // максимальный id объекта
    public int maxObjectID = 0;

    public boolean inBuild = false;

    public boolean isMainScene2D = false;

    private transient boolean shouldDestroy = false;

    private transient boolean running = false;

    public Scene2D()
    {
        layering.addLayer(new Layer(0, "default"));
        tags.add(new Tag("default"));
    }

    public Scene2D(String name)
    {
        layering.addLayer(new Layer(0, "default"));
        tags.add(new Tag("default"));

        this.name = name;
    }

    public Scene2D(Scene2DCallback scene2DCallback)
    {
        layering.addLayer(new Layer(0, "default"));
        tags.add(new Tag("default"));

        this.scene2DCallback = scene2DCallback;
    }

    public Scene2D(String name, Scene2DCallback scene2DCallback)
    {
        layering.addLayer(new Layer(0, "default"));
        tags.add(new Tag("default"));

        this.name = name;
        this.scene2DCallback = scene2DCallback;
    }


    public void initPhysicsWorld()
    {
        for(Layer layer : layering.getLayers()) {
            for(GameObject gameObject : layer.getGameObjects()) {
                Rigidbody2D rigidbody2D = physicsWorld.addRigidbody2D(gameObject, this);
                TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
                if(transformComponent != null && rigidbody2D != null) {
                    Vector2f position = transformComponent.getTransform().getPosition();
                    rigidbody2D.getBody().setTransform(
                            new Vec2(position.x / PhysicsWorld.RATIO, position.y / PhysicsWorld.RATIO),
                            (float) Math.toRadians(transformComponent.getTransform().getRotation())
                    );
                }
            }
        }
    }

    public void draw()
    {
        if(!shouldDestroy) {
            Graphics.getMainRenderer().render(layering);

            if (scene2DCallback != null) {
                scene2DCallback.onDraw();
            }
        }
    }

    // рисует все объекты разными цветами при выборке объектов
    public void drawPicking()
    {
        if(!shouldDestroy) {
            layering.drawPicking();
        }
    }

    public GameObject getPickedObject2D(Vector4f pixelColor)
    {
        return layering.getPickedObject2D(pixelColor);
    }

    public void deltaUpdate(float deltaTime)
    {
        physicsWorld.step(deltaTime, 6, 2);

        layering.deltaUpdate(deltaTime);

        if(scene2DCallback != null) {
            scene2DCallback.onUpdate(deltaTime);
        }
    }

    public void load()
    {
        if(scene2DCallback != null) {
            scene2DCallback.onLoad();
        }

        Graphics.setScreenClearColor(screenClearColor);

        applyScriptsTempValues();

        if(sceneMainCamera2D != null) {
            CamerasManager.mainCamera2D = sceneMainCamera2D;
        }

        physicsWorld.simulatePhysics = true;

        sceneLoaded = true;
    }

    public void addTag(Tag tag)
    {
        Tag foundTag = getTag(tag.getName());
        if(foundTag != null) {
            Log.CurrentSession.println("Error adding new tag \"" + tag.getName() + "\" on scene \"" + name + "\". This tag already exists.", Log.MessageType.ERROR);
            Log.showErrorDialog("Error adding new tag \"" + tag.getName() + "\" on scene \"" + name + "\". This tag already exists.");
            return;
        }

        tags.add(tag);
    }

    public Tag getTag(String tagName)
    {
        for(int i = 0; i < tags.size(); i++) {
            if(tags.get(i).getName().equals(tagName)) {
                return tags.get(i);
            }
        }

        return null;
    }

    public void deleteTag(Tag tag)
    {
        for(int i = 0; i < layering.getLayers().size(); i++) {
            for(int k = 0; k < layering.getLayers().get(i).getGameObjects().size(); k++) {
                GameObject gameObject = layering.getLayers().get(k).getGameObjects().get(k);
                if(tag.getName().equals(gameObject.tag.getName())) {
                    gameObject.tag.setName("default");
                }
            }
        }

        tags.remove(tag);
    }

    public GameObject findObject2DByName(String name)
    {
        for(Layer layer : layering.getLayers()) {
            for(GameObject go : layer.getGameObjects()) {
                if(go.name.equals(name)) {
                    return go;
                }
            }
        }

        return null;
    }

    public GameObject findObject2DByTag(String tag)
    {
        for(Layer layer : layering.getLayers()) {
            for(GameObject go : layer.getGameObjects()) {
                if(go.tag.getName().equals(tag)) {
                    return go;
                }
            }
        }

        return null;
    }

    public GameObject findGameObjectByID(int ID)
    {
        for(Layer layer : layering.getLayers()) {
            for(GameObject go : layer.getGameObjects()) {
                if(go.ID == ID) {
                    return go;
                }
            }
        }

        return null;
    }

    public void saveScriptsTempValues()
    {
        ScriptSystem.saveScriptsTempValues(this);
    }

    // применяет временные значения скриптов объектов, хранящиеся только при выполнении программы
    public void applyScriptsTempValues()
    {
        ScriptSystem.applyScriptsTempValues(this);
    }

    public void destroy()
    {
        shouldDestroy = true;

        layering.destroy();
        layering = null;
        physicsWorld = null;

        tags.clear();

        tags = null;

        sceneMainCamera2D = null;
    }

    public String getName() { return name; }
    public void setName(String name)
    {
        this.name = name;
        name = null;
    }

    public String getScenePath() { return scenePath; }
    public void setScenePath(String scenePath) { this.scenePath = scenePath; }

    public Layering getLayering() { return layering; }
    public void setLayering(Layering layering) { this.layering = layering; }

    public GameObject getSceneMainCamera2D() { return sceneMainCamera2D; }
    public void setSceneMainCamera2D(GameObject sceneMainCamera2D)
    {
        this.sceneMainCamera2D = sceneMainCamera2D;
    }

    public Scene2DCallback getScene2DCallback() { return scene2DCallback; }
    public void setScene2DCallback(Scene2DCallback scene2DCallback)
    {
        this.scene2DCallback = scene2DCallback;
        scene2DCallback = null;
    }

    public Vector4f getScreenClearColor() { return screenClearColor; }
    public void setScreenClearColor(Vector4f screenClearColor)
    {
        this.screenClearColor = screenClearColor;

        Graphics.screenCleared = false;
        Graphics.setScreenClearColor(this.screenClearColor);

        screenClearColor = null;
    }

    public List<Tag> getTags() { return tags; }

    public PhysicsWorld getPhysicsWorld() { return physicsWorld; }
    public void setPhysicsWorld(PhysicsWorld physicsWorld) { this.physicsWorld = physicsWorld; }

    public ScriptSystem getScriptSystem() { return scriptSystem; }
    public void setScriptSystem(ScriptSystem scriptSystem) { this.scriptSystem = scriptSystem; }

    public boolean isSceneLoaded() { return sceneLoaded; }
    public void setSceneLoaded(boolean sceneLoaded) { this.sceneLoaded = sceneLoaded; }

    public boolean isMainScene2D() { return isMainScene2D; }
    public void setMainScene2D(boolean mainScene2D)
    {
        if(SceneManager.currentSceneManager.mainScene2D != null && (mainScene2D || isMainScene2D)){
            SceneManager.currentSceneManager.mainScene2D.isMainScene2D = false;
            SceneManager.currentSceneManager.mainScene2D = null;
        }
        isMainScene2D = mainScene2D;
        if(isMainScene2D) {
            SceneManager.currentSceneManager.mainScene2D = this;
            SceneManager.currentSceneManager.mainScene2DPath = scenePath;
        }
    }

    public boolean isShouldDestroy() { return shouldDestroy; }

    public boolean isRunning() { return running; }

    public void setRunning(boolean running)
    {
        this.running = running;

        physicsWorld.simulatePhysics = running;
        scriptSystem.runScripts = running;
    }
}