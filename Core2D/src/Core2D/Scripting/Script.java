package Core2D.Scripting;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Script
{
    private String path;
    private String name;

    private Method updateMethod;
    private Method deltaUpdateMethod;

    private boolean active = true;

    private Class<?> scriptClass;

    public void loadClass(String dirPath, String name) {
        URLClassLoader urlClassLoader = null;
        try {
            urlClassLoader = URLClassLoader.newInstance(new URL[] {
                    new URL(path)
            });

            scriptClass = urlClassLoader.loadClass(name);

            path = dirPath + "\\" + name;
            this.name = name;

            deltaUpdateMethod = getMethod("deltaUpdate", float.class);
            updateMethod = getMethod("update");
        } catch (MalformedURLException | ClassNotFoundException e) {
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

    public void setField(Field field, Object obj)
    {
        try {
            field.set(scriptClass, obj);
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
            method.invoke(scriptClass, args);
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

    public boolean isActive() {  return active;  }
    public void setActive(boolean active) {  this.active = active;  }
}
