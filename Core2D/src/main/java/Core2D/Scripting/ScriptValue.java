package Core2D.Scripting;

public class ScriptValue
{
    public int ID = -1;
    public String name = "";
    public ScriptValueType objectType;

    public ScriptValue() { }

    public ScriptValue(int ID, String name, ScriptValueType objectType)
    {
        this.ID = ID;
        this.name = name;
        this.objectType = objectType;
    }
}
