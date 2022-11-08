package Core2D.Drawable;

import Core2D.Layering.Layer;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.Tag;
import Core2D.Utils.Utils;
import Core2D.Utils.WrappedObject;

/**
 * Abstract class for all rendered objects. Contains general parameters and methods for rendered objects.
 */
public abstract class Drawable
{
    /**
     * Name of the drawable object. By default, it is "default".
     */
    protected String name = "default";
    /**
     * Is drawable object active
     */
    protected boolean active = true;

    /**
     * Whether the object will be deleted in the next call of the deltaUpdate() method in the Layer class.
     * @see Layer#deltaUpdate(float)
     */
    protected transient boolean shouldDestroy = false;
    /**
     * The layer in which is located drawable object.
     */
    protected transient Layer layer;
    /**
     * Name of the layer in which is located drawable object.
     */
    protected String layerName = "";
    /**
     * Tag (label) of the drawable object.
     */
    protected Tag tag = new Tag();
    /**
     * The wrapper object that this drawable object is wrapped in.
     * @see WrappedObject
     */
    protected transient WrappedObject wrappedObject = new WrappedObject(this);
    /**
     * Identifier of the object on the scene.
     */
    protected int ID = 0;

    /**
     * Creates a new identifier for this drawable object.
     * If there is no current scene, it sets a random ID from 0 to 1000000000,
     * in another case it sets the ID equal to the ID of the last created object on the scene + 1.
     */

    public void createNewID()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;
            ID = SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID;
        } else {
            ID = Utils.getRandom(0, 1000000000);
        }

        System.out.println("object id: " + ID);
    }

    public void render()
    {

    }

    /**
     * @return Is the drawable object active on the scene.
     */
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
        this.layerName = layer.getName();

        this.layer.getRenderingObjects().remove(wrappedObject);
        this.layer.getRenderingObjects().add(wrappedObject);
    }

    public Tag getTag() { return tag; }
    public void setTag(String tag)
    {
        this.tag.setName(tag);
    }

    public void setID(int ID) { this.ID = ID; }
    public int getID() { return ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLayerName() { return layerName; }
    public void setLayerName(String layerName) { this.layerName = layerName; }

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
        this.layer = null;
        wrappedObject.setObject(null);
        wrappedObject = null;
    }
}
