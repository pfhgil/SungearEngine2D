package Core2D.CommonParameters;

import Core2D.Layering.Layer;
import Core2D.Layering.LayerObject;
import Core2D.Utils.Tag;

public abstract class CommonDrawableObjectsParameters
{
    protected boolean active = true;
    protected transient boolean shouldDestroy = false;
    protected transient Layer layer;
    protected Tag tag = new Tag();
    protected transient LayerObject layerObject = new LayerObject(this);

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isShouldDestroy() { return shouldDestroy; }
    protected void setShouldDestroy(boolean shouldDestroy) { this.shouldDestroy = shouldDestroy; }

    public Layer getLayer() { return layer; }
    public void setLayer(Layer layer)
    {
        if(this.layer != null) {
            this.layer.getRenderingObjects().remove(layerObject);
        }

        this.layer = layer;

        this.layer.getRenderingObjects().remove(layerObject);
        this.layer.getRenderingObjects().add(layerObject);

        layer = null;
    }

    public Tag getTag() { return tag; }
    public void setTag(String tag)
    {
        this.tag.setName(tag);
        tag = null;
    }

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
        tag.destroy();
        tag = null;
        if(this.layer != null) {
            this.layer.getRenderingObjects().remove(layerObject);
        }
        this.layer = null;
        layerObject.setObject(null);
        layerObject = null;
    }
}
