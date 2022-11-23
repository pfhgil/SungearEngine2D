package Core2D.Scripting;

import Core2D.Component.Component;
import Core2D.GameObject.GameObject;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;

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
        if(script != null && fieldName != null && value != null) {
            try {
                Field field = script.getScriptClass().getField(fieldName);
                //Object fieldValue = getScriptFieldValue(script);

                //System.out.println("fieldName: " + fieldName + ", wrappedObject.getObject(): " + wrappedObject.getObject() + ", script name: " + script.getName() + ", script class: " + script.getScriptClass());

                //System.out.println(wrappedObject.getObject());

                if(value instanceof Double && field.getType().isAssignableFrom(float.class)) {
                    field.setFloat(script.getScriptClassInstance(), ((Double) value).floatValue());
                } else if(value instanceof ScriptSceneObject object) {
                    switch(object.objectType) {
                        case TYPE_GAME_OBJECT:
                            GameObject foundGameObject = SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(object.ID);
                            field.set(script.getScriptClassInstance(), foundGameObject);
                            break;
                    }
                } else if(value instanceof Component) {
                    Component component = (Component) value;
                    Core2D.GameObject.GameObject foundGameObject = SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(component.getObject2DID());
                    if(foundGameObject != null) {
                        for(Component objComponent : foundGameObject.getComponents()) {
                            if(objComponent.componentID == component.componentID &&
                            objComponent.getClass().isAssignableFrom(component.getClass())) {
                                field.set(script.getScriptClassInstance(), objComponent);
                            }
                        }
                    }
                } else {
                    if(value != null) {
                        field.set(script.getScriptClassInstance(), value);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }

            if(ProjectsManager.getCurrentProject() != null) {
                long lastModified = new File(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + script.path + ".java").lastModified();
                // установка времени  последней  модификации на скрипт
                script.setLastModified(lastModified);
            }
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
