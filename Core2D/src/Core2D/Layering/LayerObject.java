package Core2D.Layering;

public class LayerObject
{
    private Object object;

    public LayerObject(Object object)
    {
        this.object = object;
    }

    public Object getObject() { return object; }
    public void setObject(Object object) { this.object = object; }
}
