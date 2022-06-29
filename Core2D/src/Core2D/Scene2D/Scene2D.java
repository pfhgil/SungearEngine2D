package Core2D.Scene2D;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Components.ScriptComponent;
import Core2D.Graphics.Graphics;
import Core2D.Layering.Layer;
import Core2D.Layering.LayerObject;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Physics.PhysicsWorld;
import Core2D.Scripting.ScriptTempValue;
import Core2D.Scripting.ScriptTempValues;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.Tag;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Scene2D
{
    private String name = "sampleScene2D";
    private String scenePath = "";

    private Layering layering = new Layering();

    private transient Scene2DCallback scene2DCallback;

    // цвет очистки экрана
    private Vector4f screenClearColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private List<Tag> tags = new ArrayList<>();

    private transient PhysicsWorld physicsWorld = new PhysicsWorld();

    private ScriptSystem scriptSystem = new ScriptSystem();

    public int objectsDestroyed = 0;

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

    public Object2D getPickedObject2D(Vector3f pixelColor)
    {
        return layering.getPickedObject2D(pixelColor);
    }

    public void update(float deltaTime)
    {
        physicsWorld.step(deltaTime, 6, 2);

        layering.update(deltaTime);

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

        int k = 0;
        if(scriptSystem.getScriptTempValuesList().size() != 0) {
            for (Layer layer : layering.getLayers()) {
                for (LayerObject layerObject : layer.getRenderingObjects()) {
                    if (layerObject.getObject() instanceof Object2D) {
                        List<ScriptComponent> scriptComponents = ((Object2D) layerObject.getObject()).getAllComponents(ScriptComponent.class);

                        for (ScriptComponent scriptComponent : scriptComponents) {
                            if (scriptComponent.getScript().getScriptClass().getFields().length != 0) {
                                scriptSystem.getScriptTempValuesList().get(k).setScript(scriptComponent.getScript());
                                k++;
                            }
                        }
                    }
                }
            }
        }
        applyScriptsTempValues();
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
                CommonDrawableObjectsParameters objParams = (CommonDrawableObjectsParameters) layering.getLayers().get(k).getRenderingObjects().get(k).getObject();
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
            for(LayerObject o : layer.getRenderingObjects()) {
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
            for(LayerObject o : layer.getRenderingObjects()) {
                if(o.getObject() instanceof Object2D && ((Object2D) o.getObject()).getTag().getName().equals(tag)) {
                    return (Object2D) o.getObject();
                }
            }
        }

        return null;
    }

    public void saveScriptsTempValues()
    {
        for(int i = 0; i < scriptSystem.getScriptTempValuesList().size(); i++) {
            scriptSystem.getScriptTempValuesList().get(i).destroy();
        }

        scriptSystem.getScriptTempValuesList().clear();

        for(Layer layer : layering.getLayers()) {
            for(int i = 0; i < layer.getRenderingObjects().size(); i++) {
                LayerObject layerObject = layer.getRenderingObjects().get(i);
                if(layerObject.getObject() instanceof Object2D && !((Object2D) layerObject.getObject()).isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = ((Object2D) layerObject.getObject()).getAllComponents(ScriptComponent.class);

                    if(scriptComponents.size() != 0) {
                        for (ScriptComponent scriptComponent : scriptComponents) {
                            ScriptTempValues scriptTempValues = new ScriptTempValues();
                            for (Field field : scriptComponent.getScript().getScriptClass().getFields()) {
                                ScriptTempValue scriptTempValue = new ScriptTempValue();

                                scriptTempValue.setValue(scriptComponent.getScript().getFieldValue(field));
                                scriptTempValue.setFieldName(field.getName());
                                scriptTempValue.setScript(scriptComponent.getScript());

                                scriptTempValues.getScriptTempValues().add(scriptTempValue);
                            }
                            scriptSystem.getScriptTempValuesList().add(scriptTempValues);
                        }
                    }
                }
            }
        }
    }

    public void applyScriptsTempValues()
    {
        scriptSystem.applyTempValues();

        for (Layer layer : layering.getLayers()) {
            for(int i = 0; i < layer.getRenderingObjects().size(); i++) {
                LayerObject layerObject = layer.getRenderingObjects().get(i);
                if (layerObject.getObject() instanceof Object2D && !((Object2D) layerObject.getObject()).isShouldDestroy()) {
                    List<ScriptComponent> scriptComponents = ((Object2D) layerObject.getObject()).getAllComponents(ScriptComponent.class);

                    if (scriptComponents.size() != 0) {
                        for (ScriptComponent scriptComponent : scriptComponents) {
                            long lastModified = new File(scriptComponent.getScript().getPath() + ".java").lastModified();
                            scriptComponent.getScript().setLastModified(lastModified);
                        }
                    }
                }
            }
        }
    }

    public void destroy()
    {
        layering.destroy();
        layering = null;
        physicsWorld = null;
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
}