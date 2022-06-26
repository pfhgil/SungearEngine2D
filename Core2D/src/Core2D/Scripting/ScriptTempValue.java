package Core2D.Scripting;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

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
                if(value instanceof Double && field.getType().isAssignableFrom(float.class)) {
                    field.setFloat(script.getScriptClassInstance(), ((Double) value).floatValue());
                } else {
                    field.set(script.getScriptClassInstance(), value);
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
