package Core2D.Drawable;

import Core2D.AssetManager.AssetManager;
import Core2D.Component.Component;
import Core2D.Component.Components.MeshRendererComponent;
import Core2D.Component.Components.Rigidbody2DComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Component.NonDuplicated;
import Core2D.Component.NonRemovable;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Settings;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.Texture2D.Texture2D;
import Core2D.Transform.Transform;
import Core2D.Utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class Object2D extends Drawable implements Serializable
{
    // лист компонентов
    private List<Component> components = new ArrayList<>();
    public transient Matrix4f mvpMatrix;

    // цвет
    private Vector4f color = new Vector4f();
    private transient Vector3f pickColor = new Vector3f();

    public transient Object2D parentObject2D;
    private int parentObject2DID = -1;

    private transient List<Object2D> childrenObjects = new ArrayList<>();
    private List<Integer> childrenObjectsID = new ArrayList<>();

    public Object2D()
    {
        addComponent(new TransformComponent());
        addComponent(new MeshRendererComponent());
        //addComponent(new MeshRendererComponent()).texture = new Texture2D(AssetManager.getInstance().getTexture2DData("/data/textures/white_texture.png"));

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

        pickColor.set(Settings.Other.Picking.currentPickingColor);

        createNewID();
    }

    // копировать объект
    public Object2D(Object2D object2D)
    {
        destroy();

        setColor(new Vector4f(object2D.getColor().x, object2D.getColor().y, object2D.getColor().z, object2D.getColor().w));

        Transform objectTransform = object2D.getComponent(TransformComponent.class).getTransform();
        addComponent(new TransformComponent(objectTransform));



        if(object2D.getComponent(Rigidbody2DComponent.class) != null) {
            Rigidbody2DComponent rigidbody2DComponent = new Rigidbody2DComponent();
            rigidbody2DComponent.set(object2D.getComponent(Rigidbody2DComponent.class));
            addComponent(rigidbody2DComponent);
        }

        active = object2D.isActive();

        tag = object2D.getTag();

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

        pickColor.set(Settings.Other.Picking.currentPickingColor);

        createNewID();
    }

    //@return New Object2D on current scene
    public static Object2D instantiate()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            Object2D object2D = new Object2D();
            object2D.setLayer(SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default"));

            return object2D;
        }

        return null;
    }

    @Override
    public void update()
    {
        if(active && !shouldDestroy) {
            for(Component component : components) {
                component.update();
            }
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        if(active && !shouldDestroy) {
            for(Component component : components) {
                component.deltaUpdate(deltaTime);
            }
        }
    }



    @Override
    public void destroy()
    {
        shouldDestroy = true;

        for (var i : components) {
            i.destroy();
        }

        System.out.println("Object2D " + name + " destroyed");

        //destroyParams();
    }

    public <T extends Component> T addComponent (T component)
    {
        for(Component currentComponent : components) {
            if(currentComponent.getClass().equals(component.getClass()) && currentComponent instanceof NonDuplicated) {
                Log.showErrorDialog("Component " + component.getClass().getName() + " already exists");
                throw new RuntimeException("Component " + component.getClass().getName() + " already exists");
            }
        }

        if(components.size() > 0) {
            component.componentID = components.stream().max(Comparator.comparingInt(c0 -> c0.componentID)).get().componentID + 1;
        }
        components.add(component);
        component.object2D = this;
        component.init();
        return component;
    }


    public <T extends Component> List<T> getAllComponents(Class<T> componentClass)
    {
        List<T> componentsFound = new ArrayList<>();
        for(Component component : components) {
            if(component.getClass().isAssignableFrom(componentClass)) {
                componentsFound.add(componentClass.cast(component));
            }
        }

        return componentsFound;
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for(Component component : components) {
            if(component.getClass().isAssignableFrom(componentClass)) {
                return componentClass.cast(component);
            }
        }

        return null;
    }

    public void removeComponent(Component component)
    {
        if(component instanceof NonRemovable) {
            Log.showErrorDialog("Component " + component.getClass().getName() + " is non-removable");

            throw new RuntimeException("Component " + component.getClass().getName() + " is non-removable");
        } else {
            component.destroy();
            components.remove(component);
        }
    }

    public List<Component> getComponents() { return components; }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color) { this.color = new Vector4f(color); }

    public Matrix4f getMvpMatrix() { return mvpMatrix; }

    public Vector3f getPickColor() { return pickColor; }

    public Object2D getParentObject2D() { return parentObject2D; }
    public void setParentObject2D(Object2D parentObject2D)
    {
        if(this.parentObject2D != null) {
            // если у этого объекта больше нет родителя
            if(parentObject2D == null) {
                // выполняю некоторые преобразования, чтобы этот зависимый объект встал на свою глобальную позицию обратно
                Transform transform = getComponent(TransformComponent.class).getTransform();
                Transform parentTransform = this.parentObject2D.getComponent(TransformComponent.class).getTransform();
                transform.setParentTransform(null);
                transform.setPosition(new Vector2f(transform.getPosition())
                        .mul(MatrixUtils.getScale(parentTransform.getResultModelMatrix()))
                        .add(MatrixUtils.getPosition(parentTransform.getResultModelMatrix())));
                transform.setScale(new Vector2f(transform.getScale()).mul(MatrixUtils.getScale(parentTransform.getResultModelMatrix())));

                this.parentObject2D.removeChild(this);
            }
        }
        this.parentObject2D = parentObject2D;
        if(parentObject2D != null) {
            // выполняю некоторые преобразования, чтобы этот объект встал на нужную локальную позицию
            this.parentObject2DID = parentObject2D.getID();
            Transform transform = getComponent(TransformComponent.class).getTransform();
            Transform parentTransform = this.parentObject2D.getComponent(TransformComponent.class).getTransform();
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
            setParentObject2D(SceneManager.currentSceneManager.getCurrentScene2D().findObject2DByID(parentObject2DID));
        }
    }

    public List<Object2D> getChildrenObjects() { return childrenObjects; }
    public void addChildObject(Object2D object2D)
    {
        childrenObjects.add(object2D);
        childrenObjectsID.add(object2D.getID());
        object2D.setParentObject2D(this);
    }
    public void addChildrenObjects(List<Object2D> objects2D)
    {
        childrenObjects.addAll(objects2D);
        for(int i = 0; i < objects2D.size(); i++) {
            childrenObjectsID.add(objects2D.get(i).getID());
            objects2D.get(i).setParentObject2D(this);
        }
    }
    public void addChildObjectByID(int object2DID)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            Object2D object2D = SceneManager.currentSceneManager.getCurrentScene2D().findObject2DByID(object2DID);
            if(object2D != null) {
                childrenObjects.add(object2D);
                childrenObjectsID.add(object2D.getID());
                object2D.setParentObject2D(this);
            }
        }
    }
    public void addChildrenObjectsByID(List<Integer> objects2DID)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for (int i = 0; i < objects2DID.size(); i++) {
                Object2D object2D = SceneManager.currentSceneManager.getCurrentScene2D().findObject2DByID(objects2DID.get(i));
                if(object2D != null) {
                    childrenObjects.add(object2D);
                    childrenObjectsID.add(object2D.getID());
                    object2D.setParentObject2D(this);
                }
            }
        }
    }
    public void removeChild(Object2D child)
    {
        childrenObjects.remove(child);
        childrenObjectsID.remove((Integer) child.getID());
    }

    public List<Integer> getChildrenObjectsID() { return childrenObjectsID; }

    public void applyChildrenObjectsID()
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for (int i = 0; i < childrenObjectsID.size(); i++) {
                Object2D object2D = SceneManager.currentSceneManager.getCurrentScene2D().findObject2DByID(childrenObjectsID.get(i));
                if(object2D != null) {
                    childrenObjects.add(object2D);
                    object2D.setParentObject2D(this);
                }
            }
        }
    }

    @Override
    protected synchronized void finalize()
    {
        System.out.println("Object destroyed: " + name);
    }
}