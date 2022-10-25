package Core2D.Scripting;

import Core2D.Scene2D.SceneObjectType;

// это класс, хранящий в себе id, имя и тип объекта сцены (это может быть Object2D, Camera2D и т.д.)
public class ScriptSceneObject
{
    public int ID = -1;
    public String name = "";
    public SceneObjectType objectType;

    public ScriptSceneObject() { }

    public ScriptSceneObject(int ID, String name, SceneObjectType objectType)
    {
        this.ID = ID;
        this.name = name;
        this.objectType = objectType;
    }
}
