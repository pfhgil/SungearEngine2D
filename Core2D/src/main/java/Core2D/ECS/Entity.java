package Core2D.ECS;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.Core2D.Settings;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.MeshRendererSystem;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Pooling.PoolObject;
import Core2D.Scene2D.SceneManager;
import Core2D.Transform.Transform;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.Tag;
import Core2D.Utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Entity implements Serializable, PoolObject
{
    public String name = "default";

    public boolean active = true;

    protected transient boolean shouldDestroy = false;

    protected transient Layer layer;

    public String layerName = "";

    public Tag tag = new Tag();

    public int ID = 0;

    // лист компонентов
    protected List<Component> components = new ArrayList<>();
    protected List<System> systems = new ArrayList<>();

    public boolean isUIElement = false;

    // цвет
    public Vector4f color = new Vector4f();
    protected transient Vector3f pickColor = new Vector3f();

    public transient Entity parentEntity;
    protected int parentObject2DID = -1;

    protected transient List<Entity> childrenObjects = new ArrayList<>();
    protected List<Integer> childrenObjectsID = new ArrayList<>();

    public Entity()
    {
        pickColor.set(createPickColor());

        createNewID();
    }

    @Deprecated
    // копировать объект
    public Entity(Entity entity)
    {
        destroy();

        setColor(new Vector4f(entity.getColor().x, entity.getColor().y, entity.getColor().z, entity.getColor().w));

        Transform objectTransform = entity.getComponent(TransformComponent.class).getTransform();
        addComponent(new TransformComponent(objectTransform));

        if(entity.getComponent(Rigidbody2DComponent.class) != null) {
            Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
            rigidbody2DComponent.set(entity.getComponent(Rigidbody2DComponent.class));
            addComponent(rigidbody2DComponent);
        }

        active = entity.active;

        tag = entity.tag;

        pickColor.set(createPickColor());

        createNewID();
    }

    public void set(Entity entity)
    {
        for(var component : entity.getComponents()) {
            Component existingComponent = getComponent(component.getClass());
            if(existingComponent != null) {
                existingComponent.set(component);
            } else {
                Component newComponent = null;
                try {
                    newComponent = component.getClass().getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                }
                addComponent(newComponent);
                newComponent.set(component);
            }
        }

        name = entity.name;
        //setLayer(gameObject.getLayer());
        tag.set(entity.tag);

        isUIElement = entity.isUIElement;
        setColor(entity.getColor());
        setParentObject2D(entity.getParentObject2D());
        addChildrenObjects(entity.getChildrenObjects());
    }

    public static Entity createObject2D()
    {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent());
        entity.addComponent(new MeshComponent());
        entity.addSystem(new MeshRendererSystem());

        return entity;
    }

    public static Entity createCamera2D()
    {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent());
        entity.addComponent(new Camera2DComponent());

        return entity;
    }

    private Vector3f createPickColor()
    {
        if(Settings.Other.Picking.currentPickingColor.x < 255.0f) {
            Settings.Other.Picking.currentPickingColor.x++;
        } else {
            if (Settings.Other.Picking.currentPickingColor.y < 255.0f) {
                Settings.Other.Picking.currentPickingColor.x = 0.0f;
                Settings.Other.Picking.currentPickingColor.y++;
            } else if(Settings.Other.Picking.currentPickingColor.x == 255.0f && Settings.Other.Picking.currentPickingColor.y == 255.0f) {
                Settings.Other.Picking.currentPickingColor.x = 0.0f;
                Settings.Other.Picking.currentPickingColor.y = 0.0f;
                Settings.Other.Picking.currentPickingColor.z++;
            }
        }

        return Settings.Other.Picking.currentPickingColor;
    }

    public void createNewID()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;
            ID = SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID;
        } else {
            ID = Utils.getRandom(0, 1000000000);
        }

        java.lang.System.out.println("object id: " + ID);
    }

    public void update()
    {
        if(active && !shouldDestroy) {
            for(Component component : components) {
                if(component.getClass().isAssignableFrom(ScriptComponent.class) || component instanceof ScriptComponent) {
                     ScriptComponent sc = (ScriptComponent) component;
                     sc.callMethod((params) -> sc.update());
                } else {
                    component.update();
                }
            }
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        if(active && !shouldDestroy) {
            for(Component component : components) {
                if(component.getClass().isAssignableFrom(ScriptComponent.class) || component instanceof ScriptComponent) {
                    ScriptComponent sc = (ScriptComponent) component;
                    sc.callMethod((params) -> sc.deltaUpdate(deltaTime));
                } else {
                    component.deltaUpdate(deltaTime);
                }
            }
        }
    }

    public void destroy()
    {
        shouldDestroy = true;

        if (parentEntity != null) {
            parentEntity.removeChild(this);
            parentEntity = null;
        }

        Iterator<Component> componentsIterator = components.iterator();
        while (componentsIterator.hasNext()) {
            Component component = componentsIterator.next();
            component.destroy();
            componentsIterator.remove();
        }

        Iterator<Entity> childrenIterator = childrenObjects.iterator();
        while (childrenIterator.hasNext()) {
            Entity child = childrenIterator.next();
            child.destroy();
            childrenIterator.remove();
        }
        layer = null;
    }

    @Override
    public void destroyFromScene2D()
    {
        shouldDestroy = true;

        for (var component : components) {
            component.setActive(false);
        }
    }

    @Override
    public void restore()
    {
        shouldDestroy = false;

        for (var component : components) {
            component.setActive(true);
        }
    }

    // Components
    public <T extends Component> T addComponent (T component)
    {
        for(var currentComponent : components) {
            if(currentComponent.getClass().equals(component.getClass()) && currentComponent instanceof NonDuplicated) {
                Log.showErrorDialog("Component " + component.getClass().getName() + " already exists");
                throw new RuntimeException("Component " + component.getClass().getName() + " already exists");
            }
        }

        if(components.size() > 0) {
            component.componentID = components.stream().max(Comparator.comparingInt(c0 -> c0.componentID)).get().componentID + 1;
        }
        components.add(component);
        component.entity = this;
        component.init();
        return component;
    }

    public void addAllComponents(List<? extends Component> components)
    {
        for(Component component : components) {
            addComponent(component);
        }
    }


    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for(var component : components) {
            if(component.getClass().isAssignableFrom(componentClass) || component.getClass().getSuperclass() == componentClass) {
                return componentClass.cast(component);
            }
        }

        return null;
    }

    public <T extends Component> List<T> getAllComponents(Class<T> componentClass)
    {
        List<T> componentsFound = new ArrayList<>();
        for(var component : components) {
            if(component.getClass().isAssignableFrom(componentClass) || component.getClass().getSuperclass() == componentClass){
                componentsFound.add(componentClass.cast(component));
            }
        }

        return componentsFound;
    }

    public void removeComponent(Class<? extends Component> componentClass)
    {
        Component component = getComponent(componentClass);

        if(component instanceof NonRemovable) {
            Log.showErrorDialog("Component " + component.getClass().getName() + " is non-removable");

            throw new RuntimeException("Component " + component.getClass().getName() + " is non-removable");
        } else {
            if(component != null) {
                component.destroy();
                components.remove(component);
            }
        }
    }

    public void removeAllComponents(Class<? extends Component> componentClass)
    {
        Iterator<? extends Component> componentsIterator = components.listIterator();

        while(componentsIterator.hasNext()) {
            Component component = componentsIterator.next();
            if(component.getClass().isAssignableFrom(componentClass) && component instanceof NonRemovable) {
                Log.showErrorDialog("Component " + component.getClass().getName() + " is non-removable");

                throw new RuntimeException("Component " + component.getClass().getName() + " is non-removable");
            } else {
                component.destroy();
                componentsIterator.remove();
            }
        }
    }

    // Systems
    public <T extends System> T addSystem(T system)
    {
        for(var currentSystem : systems) {
            if(currentSystem.getClass().equals(system.getClass()) && currentSystem instanceof NonDuplicated) {
                Log.showErrorDialog("System " + system.getClass().getName() + " already exists");
                throw new RuntimeException("System " + system.getClass().getName() + " already exists");
            }
        }

        /*
        if(components.size() > 0) {
            system.componentID = components.stream().max(Comparator.comparingInt(c0 -> c0.componentID)).get().componentID + 1;
        }

         */
        systems.add(system);
        system.entity = this;
        system.init();
        return system;
    }

    public void addAllSystems(List<? extends System> systems)
    {
        for(System system : systems) {
            addSystem(system);
        }
    }


    public <T extends System> T getSystem(Class<T> systemClass)
    {
        for(var system : systems) {
            if(system.getClass().isAssignableFrom(systemClass) || system.getClass().getSuperclass() == systemClass) {
                return systemClass.cast(system);
            }
        }

        return null;
    }

    public <T extends System> List<T> getAllSystems(Class<T> systemClass)
    {
        List<T> componentsFound = new ArrayList<>();
        for(var system : systems) {
            if(system.getClass().isAssignableFrom(systemClass) || system.getClass().getSuperclass() == systemClass){
                componentsFound.add(systemClass.cast(system));
            }
        }

        return componentsFound;
    }

    public void removeSystem(Class<? extends System> systemClass)
    {
        System system = getSystem(systemClass);

        if(system instanceof NonRemovable) {
            Log.showErrorDialog("System " + system.getClass().getName() + " is non-removable");

            throw new RuntimeException("System " + system.getClass().getName() + " is non-removable");
        } else {
            if(system != null) {
                system.destroy();
                systems.remove(system);
            }
        }
    }

    public void removeAllSystems(Class<? extends System> systemClass)
    {
        Iterator<? extends System> systemsIterator = systems.listIterator();

        while(systemsIterator.hasNext()) {
            System system = systemsIterator.next();
            if(system.getClass().isAssignableFrom(systemClass) && system instanceof NonRemovable) {
                Log.showErrorDialog("System " + system.getClass().getName() + " is non-removable");

                throw new RuntimeException("System " + system.getClass().getName() + " is non-removable");
            } else {
                system.destroy();
                systemsIterator.remove();
            }
        }
    }

    public boolean isShouldDestroy() { return shouldDestroy; }

    public List<Component> getComponents() { return components; }

    public List<System> getSystems() { return systems; }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color) { this.color = new Vector4f(color); }

    public Vector3f getPickColor() { return pickColor; }

    public Entity getParentObject2D() { return parentEntity; }
    public void setParentObject2D(Entity parentEntity)
    {
        if(this.parentEntity != null) {
            // если у этого объекта больше нет родителя
            if(parentEntity == null) {
                // выполняю некоторые преобразования, чтобы этот зависимый объект встал на свою глобальную позицию обратно
                Transform transform = getComponent(TransformComponent.class).getTransform();
                Transform parentTransform = this.parentEntity.getComponent(TransformComponent.class).getTransform();
                transform.setParentTransform(null);
                transform.setPosition(new Vector2f(transform.getPosition())
                        .mul(MatrixUtils.getScale(parentTransform.getResultModelMatrix()))
                        .add(MatrixUtils.getPosition(parentTransform.getResultModelMatrix())));
                transform.setScale(new Vector2f(transform.getScale()).mul(MatrixUtils.getScale(parentTransform.getResultModelMatrix())));

                this.parentEntity.removeChild(this);
            }
        }
        this.parentEntity = parentEntity;
        if(parentEntity != null) {
            // выполняю некоторые преобразования, чтобы этот объект встал на нужную локальную позицию
            this.parentObject2DID = parentEntity.ID;
            Transform transform = getComponent(TransformComponent.class).getTransform();
            Transform parentTransform = this.parentEntity.getComponent(TransformComponent.class).getTransform();
            transform.setParentTransform(parentTransform);
            transform.setPosition(new Vector2f(transform.getPosition()).add(MatrixUtils.getPosition(parentTransform.getResultModelMatrix()).negate()));
            transform.setScale(new Vector2f(transform.getScale()).div(MatrixUtils.getScale(parentTransform.getResultModelMatrix())));
        } else {
            this.parentObject2DID = -1;
            getComponent(TransformComponent.class).getTransform().setParentTransform(null);
        }
    }

    public int getParentObject2DID() { return parentObject2DID; }
    public void setParentObject2DID(int parentObject2DID)
    {
        this.parentObject2DID = parentObject2DID;

        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            setParentObject2D(SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(parentObject2DID));
        }
    }

    public List<Entity> getChildrenObjects() { return childrenObjects; }
    public void addChildObject(Entity entity)
    {
        childrenObjects.add(entity);
        childrenObjectsID.add(entity.ID);
        entity.setParentObject2D(this);
    }
    public void addChildrenObjects(List<Entity> objects2D)
    {
        childrenObjects.addAll(objects2D);
        for(int i = 0; i < objects2D.size(); i++) {
            childrenObjectsID.add(objects2D.get(i).ID);
            objects2D.get(i).setParentObject2D(this);
        }
    }
    public void addChildObjectByID(int object2DID)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            Entity entity = SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(object2DID);
            if(entity != null) {
                childrenObjects.add(entity);
                childrenObjectsID.add(entity.ID);
                entity.setParentObject2D(this);
            }
        }
    }
    public void addChildrenObjectsByID(List<Integer> objects2DID)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for (int i = 0; i < objects2DID.size(); i++) {
                Entity entity = SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(objects2DID.get(i));
                if(entity != null) {
                    childrenObjects.add(entity);
                    childrenObjectsID.add(entity.ID);
                    entity.setParentObject2D(this);
                }
            }
        }
    }
    public void removeChild(Entity child)
    {
        childrenObjects.remove(child);
        childrenObjectsID.remove((Integer) child.ID);
    }

    public List<Integer> getChildrenObjectsID() { return childrenObjectsID; }

    public void applyChildrenObjectsID()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for (int i = 0; i < childrenObjectsID.size(); i++) {
                Entity entity = SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(childrenObjectsID.get(i));
                if(entity != null) {
                    childrenObjects.add(entity);
                    entity.setParentObject2D(this);
                }
            }
        }
    }

    public Layer getLayer() { return layer; }
    public void setLayer(Layer layer)
    {
        if(this.layer != null) {
            this.layer.getEntities().remove(this);
        }

        this.layer = layer;
        this.layerName = layer.getName();

        this.layer.getEntities().remove(this);
        this.layer.getEntities().add(this);
    }

    @Override
    protected synchronized void finalize()
    {
        java.lang.System.out.println("Object destroyed: " + name);
    }
}