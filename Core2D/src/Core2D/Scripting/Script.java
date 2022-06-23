package Core2D.Scripting;

import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Utils.ExceptionsUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Script
{
    private String path;
    private String name;

    private transient Method updateMethod;
    private transient Method deltaUpdateMethod;
    private transient Method collider2DEnterMethod;
    private transient Method collider2DExitMethod;

    private boolean active = true;

    private transient Class<?> scriptClass;
    private transient Object scriptClassInstance;

    private transient long lastModified = -1;

    public void set(Script script)
    {
        loadClass(new File(script.getPath()).getParent(), FilenameUtils.getBaseName(script.getName()));
    }

    public void loadClass(String dirPath, String baseName) {
        URLClassLoader urlClassLoader = null;
        try {
            File file = new File(dirPath);

            urlClassLoader = URLClassLoader.newInstance(new URL[] {
                    file.toURI().toURL()
            });

            scriptClass = urlClassLoader.loadClass(baseName);
            scriptClassInstance = scriptClass.newInstance();

            path = dirPath + "\\" + baseName;
            this.name = baseName;

            deltaUpdateMethod = getMethod("deltaUpdate", float.class);
            updateMethod = getMethod("update");
            collider2DEnterMethod = getMethod("collider2DEnter", Object2D.class);
            collider2DExitMethod = getMethod("collider2DExit", Object2D.class);
        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }
    }

    public Field getField(String name)
    {
        try {
            return scriptClass.getField(name);
        } catch (NoSuchFieldException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }

        return null;
    }

    public List<Field> getInspectorViewFields()
    {
        List<Field> fields = new ArrayList<>();

        for(Field field : scriptClass.getFields()) {
            if(field.isAnnotationPresent(InspectorView.class)) {
                fields.add(field);
            }
        }
        return fields;
    }

    public Object getFieldValue(Field field)
    {
        try {
            return field.get(scriptClassInstance);
        } catch (IllegalAccessException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }

        return null;
    }

    public void setFieldValue(Field field, Object obj)
    {
        try {
            field.set(scriptClassInstance, obj);
        } catch (IllegalAccessException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }
    }

    public Method getMethod(String name, Class<?>... parameterTypes)
    {
        try {
            return scriptClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }

        return null;
    }

    public void invokeMethod(Method method, Object... args)
    {
        try {
            method.invoke(scriptClassInstance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
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

    public void collider2DEnter(Object2D otherObj)
    {
        if(active && collider2DEnterMethod != null) {
            invokeMethod(collider2DEnterMethod, otherObj);
        }
    }

    public void collider2DExit(Object2D otherObj)
    {
        if(active && collider2DExitMethod != null) {
            invokeMethod(collider2DExitMethod, otherObj);
        }
    }

    public void destroy()
    {
        path = null;
        name = null;

        updateMethod = null;
        deltaUpdateMethod = null;

        scriptClass = null;
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
}
