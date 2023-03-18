package Core2D.Scripting;

public class ScriptValue
{
    public int entityID = -1;
    public int componentID = -1;
    public String name = "";
    public ScriptValueType objectType;

    public ScriptValue() { }

    public ScriptValue(int entityID, String name, ScriptValueType objectType)
    {
        this.entityID = entityID;
        this.name = name;
        this.objectType = objectType;
    }

    public ScriptValue(int entityID, int componentID, String name, ScriptValueType objectType)
    {
        this.entityID = entityID;
        this.name = name;
        this.objectType = objectType;
    }
}
