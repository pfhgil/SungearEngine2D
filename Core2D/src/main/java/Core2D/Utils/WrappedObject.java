package Core2D.Utils;

public class WrappedObject
{
    private Object object;

    public WrappedObject(Object object)
    {
        this.object = object;
    }

    public Object getObject() { return object; }
    public void setObject(Object object) { this.object = object; }
}
