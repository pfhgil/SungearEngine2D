package Core2D.Scripting;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.RenderMethod;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.ByteClassLoader;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FlexibleURLClassLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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
        this.loadClass(ProjectsManager.getCurrentProject().getScriptsPath(), scriptFile.getPath(), name);

        if(script != this) {
            destroyTempValues();
            scriptTempValues.addAll(script.getScriptTempValues());
        }
    }

    public void loadClass(Class<?> cls, Object scriptClassInstance)
    {
        //Thread.currentThread().setContextClassLoader(Utils.core2DClassLoader);

        scriptClass = cls;

        this.scriptClassInstance = scriptClassInstance;

        deltaUpdateMethod = getMethod("deltaUpdate", float.class);
        updateMethod = getMethod("update");
        collider2DEnterMethod = getMethod("collider2DEnter", Entity.class);
        collider2DExitMethod = getMethod("collider2DExit", Entity.class);
    }

    public void loadClass(String scriptsDirPath, String fullPath, String baseName)
    {
        loadClass(scriptsDirPath, fullPath, baseName, new FlexibleURLClassLoader(new URL[] { }));
    }

    public void loadClass(String scriptsDirPath, String fullPath, String baseName, FlexibleURLClassLoader flexibleURLClassLoader)
    {
        scriptsDirPath = scriptsDirPath.replace("\\", "/");
        //String fullPath = scriptsDirPath + "/" + baseName;

        //System.out.println("loadClass in: " + scriptsDirPath + "\n\t" + fullPath + "\n\t" + baseName);
        try {
            // если режим работы - в движке
            if (Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                File file = new File(scriptsDirPath);

                URL scriptDirURL = file.toURI().toURL();
                flexibleURLClassLoader.addURL(scriptDirURL);
                ScriptSystem.loadAllChildURLs(flexibleURLClassLoader, scriptsDirPath);
                System.out.println(scriptDirURL);

                scriptClass = flexibleURLClassLoader.loadClass(baseName);
                // если в in-build
            } else {
                ByteClassLoader byteClassLoader = new ByteClassLoader();
                scriptClass = byteClassLoader.loadClass(Core2D.class.getResourceAsStream(scriptsDirPath + "/" + baseName + ".class"),
                        baseName);
            }

            scriptClassInstance = scriptClass.getConstructor().newInstance();

            path = fullPath;
            name = baseName;

            deltaUpdateMethod = Script.getMethod(scriptClass, "deltaUpdate", float.class);
            updateMethod = Script.getMethod(scriptClass, "update");
            collider2DEnterMethod = Script.getMethod(scriptClass, "collider2DEnter", Entity.class);
            collider2DExitMethod = Script.getMethod(scriptClass, "collider2DExit", Entity.class);

            fullPath = fullPath.replace(".java", "") + ".java";

            lastModified = new File(fullPath).lastModified();
        } catch (InstantiationException | IllegalAccessException | IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public Field getField(String name)
    {
        if(scriptClass == null) return null;

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
        if(active) {
            if (updateMethod != null) {
                invokeMethod(updateMethod);
            }

            if(scriptClass != null) {
                for (Method method : scriptClass.getMethods()) {
                    if (method.isAnnotationPresent(RenderMethod.class)) {
                        invokeMethod(method);
                    }
                }
            }
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        if(active && deltaUpdateMethod != null) {
            invokeMethod(deltaUpdateMethod, deltaTime);
        }
    }

    public void collider2DEnter(Entity otherObj)
    {
        if(active && collider2DEnterMethod != null) {
            invokeMethod(collider2DEnterMethod, otherObj);
        }
    }

    public void collider2DExit(Entity otherObj)
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
                    if (value instanceof Entity entity) {
                        scriptTempValue.setValue(new ScriptValue(entity.ID, entity.name, ScriptValueType.TYPE_ENTITY));
                    } else if(value instanceof Component component) {
                        scriptTempValue.setValue(new ScriptValue(component.entity.ID, component.componentID, component.entity.name, ScriptValueType.TYPE_COMPONENT));
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
