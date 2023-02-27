package Core2D.Scripting;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
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
import java.util.ArrayList;
import java.util.List;

public class Script
{
    public String path = "";
    private String name = "";

    private boolean active = true;

    private transient Class<?> scriptClass;
    private transient Object scriptClassInstance;

    private long lastModified = -1;

    private List<ScriptTempValue> scriptTempValues = new ArrayList<>();

    public void set(Script script)
    {
        File scriptFile = new File(script.path);

        String name = FilenameUtils.getBaseName(scriptFile.getName()).replace("\\\\/", ".");
        //System.out.println("name: " + name);
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
    }

    public void loadClass(String scriptsDirPath, String fullPath, String baseName)
    {
        loadClass(scriptsDirPath, fullPath, baseName, new FlexibleURLClassLoader(new URL[] { }));
    }

    public void loadClass(String scriptsDirPath, String fullPath, String baseName, FlexibleURLClassLoader flexibleURLClassLoader)
    {
        scriptsDirPath = scriptsDirPath.replace("\\", "/");
        fullPath = fullPath.replace(".java", "") + ".java";
        //String fullPath = scriptsDirPath + "/" + baseName;

        //System.out.println("loadClass in: " + scriptsDirPath + "\n\t" + fullPath + "\n\t" + baseName);
        try {
            // если режим работы - в движке
            if (Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                File file = new File(scriptsDirPath);

                 URL scriptDirURL = file.toURI().toURL();
                flexibleURLClassLoader.addURL(scriptDirURL);
                // ЕСЛИ БУДУТ БАГИ, ТО РАСКОММЕНТИРОВАТЬ
                //ScriptSystem.loadAllChildURLs(flexibleURLClassLoader, scriptsDirPath);
                //System.out.println(scriptDirURL);

                scriptClass = flexibleURLClassLoader.loadNewClass(fullPath.replace(".java", ".class"));
                // если в in-build
            } else {
                ByteClassLoader byteClassLoader = new ByteClassLoader();
                scriptClass = byteClassLoader.loadClass(Core2D.class.getResourceAsStream(scriptsDirPath + "/" + baseName + ".class"),
                        baseName);
            }

            scriptClassInstance = scriptClass.getConstructor().newInstance();

            path = fullPath;
            name = baseName;

            lastModified = new File(fullPath).lastModified();
        } catch (InstantiationException | IllegalAccessException | IOException | InvocationTargetException | NoSuchMethodException e) {
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
            if(scriptClassInstance instanceof Component component) {
                component.update();
            }
            if(scriptClassInstance instanceof System system) {
                system.update();
            }
        }
    }

    public void render(Camera2DComponent camera2DComponent)
    {
        if(active) {
            if(scriptClassInstance instanceof Component component) {
                component.render(camera2DComponent);
            }
            /*
            if(scriptClassInstance instanceof System system) {
                system.renderEntity(camera2DComponent);
            }

             */
        }
    }

    public void render(Camera2DComponent camera2DComponent, Shader shader)
    {
        if(active) {
            if(scriptClassInstance instanceof Component component) {
                component.render(camera2DComponent, shader);
            }
            // FIXME:
            /*
            if(scriptClassInstance instanceof System system) {
                system.renderEntity(camera2DComponent, shader);
            }

             */
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        if(scriptClassInstance instanceof Component component) {
            component.deltaUpdate(deltaTime);
        }
        if(scriptClassInstance instanceof System system) {
            system.deltaUpdate(deltaTime);
        }
    }

    public void collider2DEnter(Entity otherObj)
    {
        if(scriptClassInstance instanceof Component component) {
            component.collider2DEnter(otherObj);
        }
        if(scriptClassInstance instanceof System system) {
            system.collider2DEnter(otherObj);
        }
    }

    public void collider2DExit(Entity otherObj)
    {
        if(scriptClassInstance instanceof Component component) {
            component.collider2DExit(otherObj);
        }
        if(scriptClassInstance instanceof System system) {
            system.collider2DExit(otherObj);
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
                        scriptTempValue.setValue(new ScriptValue(component.entity.ID, component.ID, component.entity.name, ScriptValueType.TYPE_COMPONENT));
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
}
