package Core2D.Scripting;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.GameObject.GameObject;
import Core2D.Log.Log;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.ByteClassLoader;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Utils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Script
{
    public String path = "";
    private String name = "";

    private transient Method updateMethod;
    private transient Method deltaUpdateMethod;
    private transient Method collider2DEnterMethod;
    private transient Method collider2DExitMethod;

    private boolean active = true;

    private transient Class<?> scriptClass;
    private transient Object scriptClassInstance;

    private long lastModified = -1;

    private List<ScriptTempValue> scriptTempValues = new ArrayList<>();

    public void set(Script script)
    {
        File scriptFile = new File(script.path);

        String name = FilenameUtils.getBaseName(scriptFile.getName()).replace("\\\\/", ".");
        System.out.println("name: " + name);
        Script loadedScript = new Script();
        loadedScript.loadClass(scriptFile.getParent(), scriptFile.getPath(), name);

        scriptClass = loadedScript.scriptClass;
        scriptClassInstance = loadedScript.scriptClassInstance;

        path = loadedScript.path;
        this.name = loadedScript.name;

        deltaUpdateMethod = loadedScript.deltaUpdateMethod;
        updateMethod = loadedScript.updateMethod;
        collider2DEnterMethod = loadedScript.collider2DEnterMethod;
        collider2DExitMethod = loadedScript.collider2DExitMethod;

        lastModified = loadedScript.lastModified;

        destroyTempValues();
        scriptTempValues.addAll(script.getScriptTempValues());
    }

    public void loadClass(Class<?> cls, Object scriptClassInstance)
    {
        //Thread.currentThread().setContextClassLoader(Utils.core2DClassLoader);

        scriptClass = cls;

        this.scriptClassInstance = scriptClassInstance;

        deltaUpdateMethod = getMethod("deltaUpdate", float.class);
        updateMethod = getMethod("update");
        collider2DEnterMethod = getMethod("collider2DEnter", GameObject.class);
        collider2DExitMethod = getMethod("collider2DExit", GameObject.class);
    }

    public void loadClass(String dirPath, String scriptPath, String baseName)
    {
        dirPath = dirPath.replace("\\", "/");
        try {
            // если режим работы - в движке
            if (Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                File file = new File(dirPath);

                URL scriptDirURL = file.toURI().toURL();

                //Utils.core2DClassLoader = new Core2DClassLoader(new URL[] { }, Utils.core2DClassLoader);

                Utils.core2DClassLoader.addURL(scriptDirURL);
                scriptClass = Utils.core2DClassLoader.loadClass(baseName);
                // если в in-build
            } else {
                ByteClassLoader byteClassLoader = new ByteClassLoader();
                scriptClass = byteClassLoader.loadClass(Core2D.class.getResourceAsStream(dirPath + "/" + baseName + ".class"),
                        baseName);
            }

            scriptClassInstance = scriptClass.newInstance();

            path = dirPath + "\\" + baseName;
            name = baseName;

            deltaUpdateMethod = Script.getMethod(scriptClass, "deltaUpdate", float.class);
            updateMethod = Script.getMethod(scriptClass, "update");
            collider2DEnterMethod = Script.getMethod(scriptClass, "collider2DEnter", GameObject.class);
            collider2DExitMethod = Script.getMethod(scriptClass, "collider2DExit", GameObject.class);

            //System.out.println("script path: " + scriptPath);
            lastModified = new File(scriptPath + ".java").lastModified();
            System.out.println("last modified: " + lastModified + ", path: " + scriptPath + ".java");
        } catch (MalformedURLException | InstantiationException | IllegalAccessException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public Field getField(String name)
    {
        try {
            return scriptClass.getField(name);
        } catch (NoSuchFieldException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }


    public List<Field> getInspectorViewFields()
    {
        return getInspectorViewFields(scriptClass);
    }

    public static List<Field> getInspectorViewFields(Class<?> cls)
    {
        List<Field> fields = new ArrayList<>();

        for (Field field : cls.getFields()) {
            if (field.isAnnotationPresent(InspectorView.class)) {
                fields.add(field);
            }
        }
        return fields;
    }

    public Object getFieldValue(Field field)
    {
        return getFieldValue(scriptClassInstance, field);
    }

    public static Object getFieldValue(Object clsInstance, Field field)
    {
        try {
            return field.get(clsInstance);
        } catch (IllegalAccessException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    public void setFieldValue(Field field, Object obj)
    {
        setFieldValue(scriptClassInstance, field, obj);
    }

    public static void setFieldValue(Object clsInstance, Field field, Object obj)
    {
        try {
            field.set(clsInstance, obj);
        } catch (IllegalAccessException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public Method getMethod(String name, Class<?>... parameterTypes)
    {
        return getMethod(scriptClass, name, parameterTypes);
    }

    public static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes)
    {
        try {
            return cls.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    public void invokeMethod(Method method, Object... args)
    {
        if(method != null) {
            try {
                method.invoke(scriptClassInstance, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        }
    }

    public void update()
    {
        if(active && updateMethod != null) {
            invokeMethod(updateMethod);
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        if(active && deltaUpdateMethod != null) {
            invokeMethod(deltaUpdateMethod, deltaTime);
        }
    }

    public void collider2DEnter(GameObject otherObj)
    {
        if(active && collider2DEnterMethod != null) {
            invokeMethod(collider2DEnterMethod, otherObj);
        }
    }

    public void collider2DExit(GameObject otherObj)
    {
        if(active && collider2DExitMethod != null) {
            invokeMethod(collider2DExitMethod, otherObj);
        }
    }

    public void applyTempValues()
    {
        int len = scriptTempValues.size();
        if(len != 0) {
            for (int i = 0; i < len; i++) {
                scriptTempValues.get(i).applyToScript(this);
            }
        }
    }

    public void saveTempValues()
    {
        destroyTempValues();

        if(scriptClass != null) {
            for (Field field : scriptClass.getFields()) {
                if (field.isAnnotationPresent(InspectorView.class)) {
                    ScriptTempValue scriptTempValue = new ScriptTempValue();

                    Object value = getFieldValue(field);
                    if (value instanceof GameObject gameObject) {
                        scriptTempValue.setValue(new ScriptValue(gameObject.ID, gameObject.name, ScriptValueType.TYPE_GAME_OBJECT));
                    } else {
                        scriptTempValue.setValue(value);
                    }
                    scriptTempValue.setFieldName(field.getName());

                    scriptTempValues.add(scriptTempValue);
                }
            }
        }
    }

    public void destroyTempValues()
    {
        int len = scriptTempValues.size();
        if(len != 0) {
            for (int i = 0; i < len; i++) {
                scriptTempValues.get(i).destroy();
            }
        }
        scriptTempValues.clear();
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Class<?> getScriptClass() { return scriptClass; }

    public Object getScriptClassInstance() { return scriptClassInstance; }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }

    public List<ScriptTempValue> getScriptTempValues() { return scriptTempValues; }

    public Method getUpdateMethod() { return updateMethod; }

    public Method getDeltaUpdateMethod() { return deltaUpdateMethod; }

    public Method getCollider2DEnterMethod() { return collider2DEnterMethod; }

    public Method getCollider2DExitMethod() { return collider2DExitMethod; }
}
