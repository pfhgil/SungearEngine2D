package Core2D.Scripting;

import Core2D.ECS.Entity;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.lang.reflect.Field;

// само одно временное значение в скрипте (хранятся только в рантайме)
public class ScriptTempValue
{
    private String fieldName;
    private Object value;

    /*
    public Object getScriptFieldValue(Script script)
    {
        Object val = null;
        if(script != null && fieldName != null && value != null) {
            if (value instanceof LinkedTreeMap) {
                JsonObject jsonObject = Utils.gson.toJsonTree(value).getAsJsonObject();
                val = Utils.gson.fromJson(jsonObject.toString(), GameObject.class);
            } else {
                System.out.println(value.getClass().getName());
            }
        }

        return val;
    }

     */

    public void applyToScript(Script script)
    {
        Log.Console.println("script: +" + script + ", field name: " + fieldName + ", value: " + value);

        if(script != null && fieldName != null && value != null) {
            try {
                Field field = script.getScriptClass().getField(fieldName);

                Object resValue = value;
                if(value instanceof LinkedTreeMap) {
                    JsonObject jsonObject = Utils.gson.toJsonTree(value).getAsJsonObject();
                    resValue = Utils.gson.fromJson(jsonObject.toString(), ScriptValue.class);
                }

                if(resValue instanceof Double && field.getType().isAssignableFrom(float.class)) {
                    field.setFloat(script.getScriptClassInstance(), ((Double) resValue).floatValue());
                } else if(resValue instanceof ScriptValue object) {
                    Entity foundEntity = SceneManager.currentSceneManager.getCurrentScene2D().findEntityByID(object.entityID);
                    switch(object.objectType) {
                        case TYPE_ENTITY:
                            if(foundEntity != null) {
                                field.set(script.getScriptClassInstance(), foundEntity);
                            }
                            break;

                        case TYPE_COMPONENT:
                            if(foundEntity != null) {
                                field.set(script.getScriptClassInstance(), foundEntity.findComponentByID(object.componentID));
                            }
                            break;
                    }
                } else {
                    if(resValue != null) {
                        field.set(script.getScriptClassInstance(), resValue);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }

            /*
            if(ProjectsManager.getCurrentProject() != null) {
                long lastModified = new File(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + script.path + ".java").lastModified();
                // установка времени  последней  модификации на скрипт
                script.setLastModified(lastModified);
            }

             */
        }
    }

    public void destroy()
    {
        fieldName = null;
        value = null;
    }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
}
