package Core2D.GameObject;

import Core2D.Component.Component;
import Core2D.Component.Components.Camera2DComponent;
import Core2D.Component.Components.MeshRendererComponent;
import Core2D.Component.Components.Rigidbody2DComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Component.NonDuplicated;
import Core2D.Component.NonRemovable;
import Core2D.Core2D.Settings;
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

public class GameObject implements Serializable, PoolObject
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

    public boolean isUIElement = false;

    // цвет
    public Vector4f color = new Vector4f();
    protected transient Vector3f pickColor = new Vector3f();

    public transient GameObject parentGameObject;
    protected int parentObject2DID = -1;

    protected transient List<GameObject> childrenObjects = new ArrayList<>();
    protected List<Integer> childrenObjectsID = new ArrayList<>();

    public GameObject()
    {
        pickColor.set(createPickColor());

        createNewID();
    }

    @Deprecated
    // копировать объект
    public GameObject(GameObject gameObject)
    {
        destroy();

        setColor(new Vector4f(gameObject.getColor().x, gameObject.getColor().y, gameObject.getColor().z, gameObject.getColor().w));

        Transform objectTransform = gameObject.getComponent(TransformComponent.class).getTransform();
        addComponent(new TransformComponent(objectTransform));

        if(gameObject.getComponent(Rigidbody2DComponent.class) != null) {
            Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
            rigidbody2DComponent.set(gameObject.getComponent(Rigidbody2DComponent.class));
            addComponent(rigidbody2DComponent);
        }

        active = gameObject.active;

        tag = gameObject.tag;

        pickColor.set(createPickColor());

        createNewID();
    }

    public void set(GameObject gameObject)
    {
        for(var component : gameObject.getComponents()) {
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

                System.out.println("component: " + newComponent);
            }
        }

        name = gameObject.name;
        //setLayer(gameObject.getLayer());
        tag.set(gameObject.tag);

        isUIElement = gameObject.isUIElement;
        setColor(gameObject.getColor());
        setParentObject2D(gameObject.getParentObject2D());
        addChildrenObjects(gameObject.getChildrenObjects());
    }

    public static GameObject createObject2D()
    {
        GameObject gameObject = new GameObject();
        gameObject.addComponent(new TransformComponent());
        gameObject.addComponent(new MeshRendererComponent());

        return gameObject;
    }

    public static GameObject createCamera2D()
    {
        GameObject gameObject = new GameObject();
        gameObject.addComponent(new TransformComponent());
        gameObject.addComponent(new Camera2DComponent());

        return gameObject;
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

        System.out.println("object id: " + ID);
    }

    public void update()
    {
        if(active && !shouldDestroy) {
            for(Component component : components) {
                component.update();
            }
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        if(active && !shouldDestroy) {
            for(Component component : components) {
                component.deltaUpdate(deltaTime);
            }
        }
    }

    public void destroy()
    {
        shouldDestroy = true;

        if (parentGameObject != null) {
            parentGameObject.removeChild(this);
            parentGameObject = null;
        }

        Iterator<Component> componentsIterator = components.iterator();
        while (componentsIterator.hasNext()) {
            Component component = componentsIterator.next();
            component.destroy();
            componentsIterator.remove();
        }

        Iterator<Core2D.GameObject.GameObject> childrenIterator = childrenObjects.iterator();
        while (childrenIterator.hasNext()) {
            Core2D.GameObject.GameObject child = childrenIterator.next();
            child.destroy();
            childrenIterator.remove();
        }
        layer = null;

        System.out.println("Object2D " + name + " destroyed");
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
        component.gameObject = this;
        component.init();
        return component;
    }


    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for(var component : components) {
            if(component.getClass().isAssignableFrom(componentClass)) {
                return componentClass.cast(component);
            }
        }

        return null;
    }

    public <T extends Component> List<T> getAllComponents(Class<T> componentClass)
    {
        List<T> componentsFound = new ArrayList<>();
        for(var component : components) {
            if(component.getClass().isAssignableFrom(componentClass)) {
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

    public boolean isShouldDestroy() { return shouldDestroy; }

    public List<Component> getComponents() { return components; }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color) { this.color = new Vector4f(color); }

    public Vector3f getPickColor() { return pickColor; }

    public GameObject getParentObject2D() { return parentGameObject; }
    public void setParentObject2D(GameObject parentGameObject)
    {
        if(this.parentGameObject != null) {
            // если у этого объекта больше нет родителя
            if(parentGameObject == null) {
                // выполняю некоторые преобразования, чтобы этот зависимый объект встал на свою глобальную позицию обратно
                Transform transform = getComponent(TransformComponent.class).getTransform();
                Transform parentTransform = this.parentGameObject.getComponent(TransformComponent.class).getTransform();
                transform.setParentTransform(null);
                transform.setPosition(new Vector2f(transform.getPosition())
                        .mul(MatrixUtils.getScale(parentTransform.getResultModelMatrix()))
                        .add(MatrixUtils.getPosition(parentTransform.getResultModelMatrix())));
                transform.setScale(new Vector2f(transform.getScale()).mul(MatrixUtils.getScale(parentTransform.getResultModelMatrix())));

                this.parentGameObject.removeChild(this);
            }
        }
        this.parentGameObject = parentGameObject;
        if(parentGameObject != null) {
            // выполняю некоторые преобразования, чтобы этот объект встал на нужную локальную позицию
            this.parentObject2DID = parentGameObject.ID;
            Transform transform = getComponent(TransformComponent.class).getTransform();
            Transform parentTransform = this.parentGameObject.getComponent(TransformComponent.class).getTransform();
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

    public List<GameObject> getChildrenObjects() { return childrenObjects; }
    public void addChildObject(GameObject gameObject)
    {
        childrenObjects.add(gameObject);
        childrenObjectsID.add(gameObject.ID);
        gameObject.setParentObject2D(this);
    }
    public void addChildrenObjects(List<GameObject> objects2D)
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
            GameObject gameObject = SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(object2DID);
            if(gameObject != null) {
                childrenObjects.add(gameObject);
                childrenObjectsID.add(gameObject.ID);
                gameObject.setParentObject2D(this);
            }
        }
    }
    public void addChildrenObjectsByID(List<Integer> objects2DID)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for (int i = 0; i < objects2DID.size(); i++) {
                GameObject gameObject = SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(objects2DID.get(i));
                if(gameObject != null) {
                    childrenObjects.add(gameObject);
                    childrenObjectsID.add(gameObject.ID);
                    gameObject.setParentObject2D(this);
                }
            }
        }
    }
    public void removeChild(GameObject child)
    {
        childrenObjects.remove(child);
        childrenObjectsID.remove((Integer) child.ID);
    }

    public List<Integer> getChildrenObjectsID() { return childrenObjectsID; }

    public void applyChildrenObjectsID()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for (int i = 0; i < childrenObjectsID.size(); i++) {
                GameObject gameObject = SceneManager.currentSceneManager.getCurrentScene2D().findGameObjectByID(childrenObjectsID.get(i));
                if(gameObject != null) {
                    childrenObjects.add(gameObject);
                    gameObject.setParentObject2D(this);
                }
            }
        }
    }

    public Layer getLayer() { return layer; }
    public void setLayer(Layer layer)
    {
        if(this.layer != null) {
            this.layer.getGameObjects().remove(this);
        }

        this.layer = layer;
        this.layerName = layer.getName();

        this.layer.getGameObjects().remove(this);
        this.layer.getGameObjects().add(this);
    }

    @Override
    protected synchronized void finalize()
    {
        System.out.println("Object destroyed: " + name);
    }
}