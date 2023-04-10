package Core2D.Scripting;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DWorkMode;
import Core2D.DataClasses.ScriptData;
import Core2D.ECS.Component;
import Core2D.ECS.Camera.CameraComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System;
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

    private List<ScriptTempValue> scriptTempValues = new ArrayList<>();

    public void set(Script script)
    {
        File scriptFile = new File(script.path);

        String name = FilenameUtils.getBaseName(scriptFile.getName()).replace("\\\\/", ".");
        //Systems.out.println("name: " + name);
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

    public void loadClass(String scriptsDirPath, String path, String baseName, FlexibleURLClassLoader flexibleURLClassLoader)
    {
        scriptsDirPath = scriptsDirPath.replace("\\", "/");

        // fixes
        path = path.replaceAll(".java", "");
        path = path.replaceAll(".class", "") + ".class";

        try {
            // если режим работы - в движке
            if (Core2D.core2DWorkMode == Core2DWorkMode.IN_ENGINE) {
                File file = new File(scriptsDirPath);

                URL scriptDirURL = file.toURI().toURL();
                flexibleURLClassLoader.addURL(scriptDirURL);

                //Log.CurrentSession.println("script path: " + path, Log.MessageType.SUCCESS);

                ScriptData scriptData = AssetManager.getInstance().getScriptData(path);

                scriptClass = flexibleURLClassLoader.loadNewClass(path, scriptData.data);

                this.path = scriptData.getCanonicalPath();
            } else {  // если в in-build
                ScriptData scriptData = AssetManager.getInstance().getScriptData(scriptsDirPath + "/" + baseName + ".class");

                try(ByteClassLoader byteClassLoader = new ByteClassLoader()) {
                    scriptClass = byteClassLoader.loadClass(scriptData.data,
                            baseName);
                } catch (Exception e) {
                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                }

                this.path = scriptData.getCanonicalPath();
            }

            scriptClassInstance = scriptClass.getConstructor().newInstance();

            name = baseName;

            //lastModified = new File(fullPath).lastModified();
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
            // FIXME
            /*
            if(scriptClassInstance instanceof Systems system) {
                system.update();
            }

             */
        }
    }

    public void render(CameraComponent cameraComponent)
    {
        if(active) {
            if(scriptClassInstance instanceof Component component) {
                component.render(cameraComponent);
            }
            /*
            if(scriptClassInstance instanceof Systems system) {
                system.renderEntity(camera2DComponent);
            }

             */
        }
    }

    public void render(CameraComponent cameraComponent, Shader shader)
    {
        if(active) {
            if(scriptClassInstance instanceof Component component) {
                component.render(cameraComponent, shader);
            }
            // FIXME:
            /*
            if(scriptClassInstance instanceof Systems system) {
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
        // FIXME
        /*
        if(scriptClassInstance instanceof Systems system) {
            system.deltaUpdate(deltaTime);
        }

         */
    }

    public void collider2DEnter(Entity otherObj)
    {
        if(scriptClassInstance instanceof Component component) {
            component.collider2DEnter(otherObj);
        }
        if(scriptClassInstance instanceof System system) {
            //system.collider2DEnter(otherObj);
        }
    }

    public void collider2DExit(Entity otherObj)
    {
        if(scriptClassInstance instanceof Component component) {
            component.collider2DExit(otherObj);
        }
        if(scriptClassInstance instanceof System system) {
            //system.collider2DExit(otherObj);
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Class<?> getScriptClass() { return scriptClass; }

    public Object getScriptClassInstance() { return scriptClassInstance; }

    public List<ScriptTempValue> getScriptTempValues() { return scriptTempValues; }
}
