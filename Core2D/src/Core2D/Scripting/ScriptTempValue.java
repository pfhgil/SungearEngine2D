package Core2D.Scripting;

import Core2D.Component.Component;
import Core2D.Deserializers.ComponentDeserializer;
import Core2D.Deserializers.Object2DDeserializer;
import Core2D.Deserializers.WrappedObjectDeserializer;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.WrappedObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;

// само одно значение в скрипте script
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
                if(value instanceof LinkedTreeMap) {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .registerTypeAdapter(Component.class, new ComponentDeserializer())
                            .registerTypeAdapter(WrappedObject.class, new WrappedObjectDeserializer())
                            .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                            .create();

                    JsonObject jsonObject = gson.toJsonTree(value).getAsJsonObject();
                    WrappedObject wrappedObject = gson.fromJson(jsonObject.toString(), WrappedObject.class);

                    if(wrappedObject.getObject() instanceof Double && field.getType().isAssignableFrom(float.class)) {
                        field.setFloat(script.getScriptClassInstance(), ((Double) value).floatValue());
                    } else if(wrappedObject.getObject() instanceof Object2D) {
                        Object2D foundObject2D = SceneManager.getCurrentScene2D().findObject2DByID(((Object2D) wrappedObject.getObject()).getID());
                        field.set(script.getScriptClassInstance(), foundObject2D);
                        ((Object2D) wrappedObject.getObject()).destroy();
                    } else {
                        field.set(script.getScriptClassInstance(), wrappedObject.getObject());
                    }

                    wrappedObject.setObject(null);
                    wrappedObject = null;
                }
                /*
                if(value instanceof Double && field.getType().isAssignableFrom(float.class)) {
                    field.setFloat(script.getScriptClassInstance(), ((Double) value).floatValue());
                } else {
                        Gson gson = new GsonBuilder()
                                .setPrettyPrinting()
                                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                                //.registerTypeAdapter(WrappedObject.class, new WrappedObjectDeserializer())
                                .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                                .create();
                        JsonObject jsonObject = gson.toJsonTree(value).getAsJsonObject();
                        Object outObj = gson.fromJson(jsonObject.toString(), Object.class);
                        if(outObj instanceof Object2D) {
                            System.out.println("dsdssdfsd");
                            // сделать
                            //Object2D foundObject2D = SceneManager.getCurrentScene2D().findObject2DByID();

                            //field.set(script.getScriptClassInstance(), foundObject2D);
                        }


                    } else {
                        field.set(script.getScriptClassInstance(), value);
                    }
                }

                 */
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
