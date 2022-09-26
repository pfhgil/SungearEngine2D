package Core2D.Scripting;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Utils.ByteClassLoader;
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
        scriptClass = null;
        scriptClassInstance = null;

        path = null;
        name = null;

        deltaUpdateMethod = null;
        updateMethod = null;
        collider2DEnterMethod = null;
        collider2DExitMethod = null;

        loadClass(new File(script.getPath()).getParent(), FilenameUtils.getBaseName(script.getName()));
    }

    public void loadClass(String dirPath, String baseName) {
        dirPath = dirPath.replace("\\", "/");
        System.out.println("dirPath: " + dirPath);
        Log.CurrentSession.println("Script dirPath: " + dirPath + ", baseName: " + baseName, Log.MessageType.INFO);

        URLClassLoader urlClassLoader = null;
        try {
            if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                File file = new File(dirPath);

                Log.CurrentSession.println("firth", Log.MessageType.INFO);

                urlClassLoader = URLClassLoader.newInstance(new URL[]{
                        file.toURI().toURL()
                });

                scriptClass = urlClassLoader.loadClass(baseName);
            } else {
                Log.CurrentSession.println("second", Log.MessageType.INFO);
                //TODO: доделать

                ByteClassLoader byteClassLoader = new ByteClassLoader();
                scriptClass = byteClassLoader.loadClass(Core2D.class.getResourceAsStream(dirPath + "/" + baseName + ".class"), baseName);
                Log.CurrentSession.println("path: " + dirPath + "/" + baseName + ".class", Log.MessageType.INFO);
                //Log.CurrentSession.println("path: " + dirPath + "/" + baseName + ".class" + " , s: " + s, Log.MessageType.INFO);

                /*
                URL url = Core2D.class.getResource(dirPath);
                urlClassLoader = URLClassLoader.newInstance(new URL[]{
                        url
                });
                Log.CurrentSession.println("url: " + url.toString(), Log.MessageType.INFO);

                 */
            }

            //scriptClass = urlClassLoader.loadClass(baseName);
            scriptClassInstance = scriptClass.newInstance();

            path = dirPath + "\\" + baseName;
            name = baseName;

            deltaUpdateMethod = getMethod("deltaUpdate", float.class);
            updateMethod = getMethod("update");
            collider2DEnterMethod = getMethod("collider2DEnter", Object2D.class);
            collider2DExitMethod = getMethod("collider2DExit", Object2D.class);
        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
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
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    public void setFieldValue(Field field, Object obj)
    {
        try {
            field.set(scriptClassInstance, obj);
        } catch (IllegalAccessException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public Method getMethod(String name, Class<?>... parameterTypes)
    {
        try {
            return scriptClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    public void invokeMethod(Method method, Object... args)
    {
        try {
            method.invoke(scriptClassInstance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
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
        collider2DEnterMethod = null;
        collider2DExitMethod = null;

        scriptClass = null;
        scriptClassInstance = null;
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
