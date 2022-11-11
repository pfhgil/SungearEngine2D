package Core2D.Scene2D;

import Core2D.Camera2D.Camera2D;
import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Components.ScriptComponent;
import Core2D.Drawable.Drawable;
import Core2D.Drawable.Object2D;
import Core2D.Graphics.Graphics;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.Physics.PhysicsWorld;
import Core2D.Project.ProjectsManager;
import Core2D.Scripting.ScriptTempValue;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.Tag;
import Core2D.Utils.WrappedObject;
import org.joml.Vector4f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Scene2D
{
    private String name = "sampleScene2D";
    private String scenePath = "";

    private Layering layering = new Layering();

    private List<Camera2D> cameras2D = new ArrayList<>();

    private Camera2D sceneMainCamera2D;

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

    public void draw()
    {
        Graphics.getMainRenderer().render(layering);

        if(scene2DCallback != null) {
            scene2DCallback.onDraw();
        }
    }

    // рисует все объекты разными цветами при выборке объектов
    public void drawPicking()
    {
        layering.drawPicking();
    }

    public Object2D getPickedObject2D(Vector4f pixelColor)
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

        for(Camera2D camera2D : cameras2D) {
            camera2D.getTransform().update(deltaTime);
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
            CamerasManager.setMainCamera2D(sceneMainCamera2D);
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
            for(int k = 0; k < layering.getLayers().get(i).getRenderingObjects().size(); k++) {
                Drawable objParams = (Drawable) layering.getLayers().get(k).getRenderingObjects().get(k).getObject();
                if(tag.getName().equals(objParams.getTag().getName())) {
                    objParams.setTag("default");
                }
            }
        }

        tags.remove(tag);
    }

    public Object2D findObject2DByName(String name)
    {
        for(Layer layer : layering.getLayers()) {
            for(WrappedObject o : layer.getRenderingObjects()) {
                if(o.getObject() instanceof Object2D && ((Object2D) o.getObject()).getName().equals(name)) {
                    return (Object2D) o.getObject();
                }
            }
        }

        return null;
    }

    public Object2D findObject2DByTag(String tag)
    {
        for(Layer layer : layering.getLayers()) {
            for(WrappedObject o : layer.getRenderingObjects()) {
                if(o.getObject() instanceof Object2D && ((Object2D) o.getObject()).getTag().getName().equals(tag)) {
                    return (Object2D) o.getObject();
                }
            }
        }

        return null;
    }

    public Object2D findObject2DByID(int ID)
    {
        for(Layer layer : layering.getLayers()) {
            for(WrappedObject o : layer.getRenderingObjects()) {
                if(o.getObject() instanceof Object2D && ((Object2D) o.getObject()).getID() == ID) {
                    return (Object2D) o.getObject();
                }
            }
        }

        return null;
    }

    public Camera2D findCamera2DByID(int ID)
    {
        for(Camera2D camera2D : cameras2D) {
            if(camera2D.getID() == ID) {
                return camera2D;
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
        layering.destroy();
        layering = null;
        physicsWorld = null;

        cameras2D.clear();
        tags.clear();

        cameras2D = null;
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

    public List<Camera2D> getCameras2D() { return cameras2D; }

    public Camera2D getSceneMainCamera2D() { return sceneMainCamera2D; }
    public void setSceneMainCamera2D(Camera2D sceneMainCamera2D)
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
}