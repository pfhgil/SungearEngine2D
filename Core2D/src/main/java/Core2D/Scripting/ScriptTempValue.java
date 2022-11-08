package Core2D.Scripting;

import Core2D.Camera2D.Camera2D;
import Core2D.Drawable.Object2D;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Utils;
import Core2D.Utils.WrappedObject;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;

// само одно временное значение в скрипте (хранятся только в рантайме)
public class ScriptTempValue
{
    private transient Script script;
    private String fieldName;
    private Object value;

    public void applyToScript()
    {
        if(script != null && fieldName != null && value != null) {
            try {
                Field field = script.getScriptClass().getField(fieldName);
                WrappedObject wrappedObject = new WrappedObject(null);
                if(value instanceof LinkedTreeMap) {
                    JsonObject jsonObject = Utils.gson.toJsonTree(value).getAsJsonObject();
                    wrappedObject = Utils.gson.fromJson(jsonObject.toString(), WrappedObject.class);
                } else if(value instanceof WrappedObject) {
                    wrappedObject = (WrappedObject) value;
                }

                if(wrappedObject.getObject() instanceof Double && field.getType().isAssignableFrom(float.class)) {
                    field.setFloat(script.getScriptClassInstance(), ((Double) value).floatValue());
                } else if(wrappedObject.getObject() instanceof ScriptSceneObject) {
                    ScriptSceneObject object = (ScriptSceneObject) wrappedObject.getObject();
                    switch(object.objectType) {
                        case TYPE_OBJECT2D:
                            Object2D foundObject2D = SceneManager.currentSceneManager.getCurrentScene2D().findObject2DByID(object.ID);
                            field.set(script.getScriptClassInstance(), foundObject2D);
                            break;
                        case TYPE_CAMERA2D:
                            Camera2D foundCamera2D = SceneManager.currentSceneManager.getCurrentScene2D().findCamera2DByID(object.ID);
                            field.set(script.getScriptClassInstance(), foundCamera2D);
                            break;
                    }
                } else {
                    if(wrappedObject.getObject() != null) {
                        field.set(script.getScriptClassInstance(), wrappedObject.getObject());
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        }
    }

    public void destroy()
    {
        script = null;
        fieldName = null;
        value = null;
    }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }

    public Script getScript() { return script; }
    public void setScript(Script script) { this.script = script; }
}
