package Core2D.CommonParameters;

import Core2D.Layering.Layer;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.Utils;
import Core2D.Utils.WrappedObject;
import Core2D.Utils.Tag;

public abstract class CommonDrawableObjectsParameters
{
    protected String name = "default";
    protected boolean active = true;
    protected transient boolean shouldDestroy = false;
    protected transient Layer layer;
    protected Tag tag = new Tag();
    protected transient WrappedObject wrappedObject = new WrappedObject(this);
    protected int ID = 0;

    public void createNewID()
    {
        if(SceneManager.getCurrentScene2D() != null) {
            SceneManager.getCurrentScene2D().maxObjectID++;
            ID = SceneManager.getCurrentScene2D().maxObjectID;
        } else {
            ID = Utils.getRandom(0, 1000000000);
        }

        System.out.println("object id: " + ID);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isShouldDestroy() { return shouldDestroy; }
    protected void setShouldDestroy(boolean shouldDestroy) { this.shouldDestroy = shouldDestroy; }

    public Layer getLayer() { return layer; }
    public void setLayer(Layer layer)
    {
        if(this.layer != null) {
            this.layer.getRenderingObjects().remove(wrappedObject);
        }

        this.layer = layer;

        this.layer.getRenderingObjects().remove(wrappedObject);
        this.layer.getRenderingObjects().add(wrappedObject);

        layer = null;
    }

    public Tag getTag() { return tag; }
    public void setTag(String tag)
    {
        this.tag.setName(tag);
        tag = null;
    }

    public void setID(int ID) { this.ID = ID; }
    public int getID() { return ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void update()
    {

    }

    public void deltaUpdate(float deltaTime)
    {

    }

    public void destroy()
    {

    }

    public void destroyParams()
    {
       // tag.destroy();
       // tag = null;
        if(this.layer != null) {
            this.layer.getRenderingObjects().remove(wrappedObject);
        }
        this.layer = null;
        wrappedObject.setObject(null);
        wrappedObject = null;
    }
}
