package Core2D.Scripting;

import Core2D.Camera2D.Camera2D;
import Core2D.Component.Component;
import Core2D.Drawable.Object2D;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Utils;
import Core2D.Utils.WrappedObject;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.lang.reflect.Field;

// само одно временное значение в скрипте (хранятся только в рантайме)
public class ScriptTempValue
{
    private String fieldName;
    private Object value;

    public WrappedObject getScriptFieldValue(Script script)
    {
        WrappedObject wrappedObject = new WrappedObject(null);
        if(script != null && fieldName != null && value != null) {
            try {
                Field field = script.getScriptClass().getField(fieldName);
                wrappedObject = new WrappedObject(null);
                if (value instanceof LinkedTreeMap) {
                    JsonObject jsonObject = Utils.gson.toJsonTree(value).getAsJsonObject();
                    wrappedObject = Utils.gson.fromJson(jsonObject.toString(), WrappedObject.class);
                } else if (value instanceof WrappedObject) {
                    wrappedObject = (WrappedObject) value;
                }
            } catch (NoSuchFieldException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        }

        return wrappedObject;
    }

    public void applyToScript(Script script)
    {
        if(script != null && fieldName != null && value != null) {
            try {
                Field field = script.getScriptClass().getField(fieldName);
                WrappedObject wrappedObject = getScriptFieldValue(script);

                //System.out.println("fieldName: " + fieldName + ", wrappedObject.getObject(): " + wrappedObject.getObject() + ", script name: " + script.getName() + ", script class: " + script.getScriptClass());

                //System.out.println(wrappedObject.getObject());

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
                } else if(wrappedObject.getObject() instanceof Component) {
                    Component component = (Component) wrappedObject.getObject();
                    Object2D foundObject2D = SceneManager.currentSceneManager.getCurrentScene2D().findObject2DByID(component.getObject2DID());
                    if(foundObject2D != null) {
                        for(Component objComponent : foundObject2D.getComponents()) {
                            if(objComponent.componentID == component.componentID &&
                            objComponent.getClass().isAssignableFrom(component.getClass())) {
                                field.set(script.getScriptClassInstance(), objComponent);
                            }
                        }
                    }
                } else {
                    if(wrappedObject.getObject() != null) {
                        field.set(script.getScriptClassInstance(), wrappedObject.getObject());
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
