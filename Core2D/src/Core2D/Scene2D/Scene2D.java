package Core2D.Scene2D;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Core2D.Graphics;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Utils.Tag;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Scene2D
{
    private String name = "sampleScene2D";

    private Layering layering = new Layering();

    private transient Scene2DCallback scene2DCallback;

    // цвет очистки экрана
    private Vector4f screenClearColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private List<Tag> tags = new ArrayList<>();

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
        layering.draw();

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
    }

    public void addTag(Tag tag)
    {
        Tag foundTag = getTag(tag.getName());
        if(foundTag != null) {
            Log.CurrentSession.println("Error adding new tag \"" + tag.getName() + "\" on scene \"" + name + "\". This tag already exists.");
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

    public String getName() { return name; }
    public void setName(String name)
    {
        this.name = name;
        name = null;
    }

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
}
