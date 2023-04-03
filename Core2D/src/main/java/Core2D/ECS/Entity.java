package Core2D.ECS;

import Core2D.Common.Interfaces.NonDuplicated;
import Core2D.Common.Interfaces.NonRemovable;
import Core2D.Core2D.Settings;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Primitives.BoxComponent;
import Core2D.ECS.Component.Components.Primitives.CircleComponent;
import Core2D.ECS.Component.Components.Primitives.LineComponent;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Pooling.PoolObject;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.*;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;
import java.util.*;

public class Entity implements Serializable, PoolObject
{
    public String name = "default";

    public boolean active = true;

    protected transient boolean shouldDestroy = false;

    protected transient Layer layer;

    public String layerName = "default";

    public Tag tag = new Tag();

    public int ID = 0;

    // лист компонентов
    protected List<Component> components = new ArrayList<>();
    //protected List<Systems> systems = new ArrayList<>();

    public boolean isUIElement = false;

    // цвет
    public Vector4f color = new Vector4f(1f);
    protected transient Vector3f pickColor = new Vector3f();

    public transient Entity parentEntity;
    protected int parentEntityID = -1;

    protected transient List<Entity> childrenEntities = new ArrayList<>();
    protected List<Integer> childrenEntitiesID = new ArrayList<>();

    public Entity()
    {
        pickColor.set(createPickColor());

        createNewID();
    }

    public Entity copy()
    {
        Entity entity = new Entity();

        entity.createNewID();
        entity.createPickColor();

        entity.name = name + "_" + entity.ID;

        entity.active = active;

        entity.layerName = layerName;

        entity.tag.setName(tag.getName());

        entity.isUIElement = isUIElement;

        entity.color.set(color);

        entity.setParentEntity(parentEntity);

        for(Component component : components) {
            entity.addComponent(ECSUtils.copyComponent(component));
        }

        for(Entity child : childrenEntities) {
            entity.addChildEntity(child.copy());
        }

        return entity;
    }

    public void addOnScene()
    {
        addOnScene(SceneManager.currentSceneManager.getCurrentScene2D());
    }

    public void addOnScene(Scene2D scene2D)
    {
        if(scene2D != null) {
            setLayer(scene2D.getLayering().getLayer(layerName));
        }
    }

    public static Entity createAsObject()
    {
        return createAsObject(new Vector3f(), new Vector3f(), new Vector3f(1f));
    }

    public static Entity createAsObject(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        Entity entity = new Entity();

        TransformComponent transformComponent = new TransformComponent();

        transformComponent.position.set(position);
        transformComponent.rotation.set(rotation);
        transformComponent.scale.set(scale);

        entity.addComponent(transformComponent);
        entity.addComponent(new MeshComponent());

        return entity;
    }

    public static Entity createAsCamera(Vector3f position, Vector3f rotation, Vector3f scale, CameraComponent.ViewMode viewMode)
    {
        Entity entity = new Entity();

        CameraComponent cameraComponent = new CameraComponent();

        cameraComponent.position.set(position);
        cameraComponent.rotation.set(rotation);
        cameraComponent.scale.set(scale);

        cameraComponent.viewMode = viewMode;

        entity.addComponent(cameraComponent);

        return entity;
    }

    public static Entity createAsCamera2D()
    {
        return createAsCamera(new Vector3f(), new Vector3f(), new Vector3f(1f), CameraComponent.ViewMode.VIEW_MODE_2D);
    }

    public static Entity createAsCamera3D()
    {
        return createAsCamera(new Vector3f(), new Vector3f(), new Vector3f(1f), CameraComponent.ViewMode.VIEW_MODE_3D);
    }

    public static Entity createAsLine()
    {
        return createAsLine(new Vector3f(), new Vector3f(), new Vector3f(1f));
    }

    public static Entity createAsLine(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        Entity entity = new Entity();

        TransformComponent transformComponent = new TransformComponent();

        transformComponent.position.set(position);
        transformComponent.rotation.set(rotation);
        transformComponent.scale.set(scale);

        entity.addComponent(transformComponent);
        entity.addComponent(new LineComponent());

        return entity;
    }

    public static Entity createAsBox()
    {
        return createAsBox(new Vector3f(), new Vector3f(), new Vector3f(1f));
    }

    public static Entity createAsBox(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        Entity entity = new Entity();

        TransformComponent transformComponent = new TransformComponent();

        transformComponent.position.set(position);
        transformComponent.rotation.set(rotation);
        transformComponent.scale.set(scale);

        entity.addComponent(transformComponent);
        entity.addComponent(new BoxComponent());

        return entity;
    }

    public static Entity createAsCircle()
    {
        return createAsCircle(new Vector3f(), new Vector3f(), new Vector3f(1f));
    }

    public static Entity createAsCircle(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        Entity entity = new Entity();

        TransformComponent transformComponent = new TransformComponent();

        transformComponent.position.set(position);
        transformComponent.rotation.set(rotation);
        transformComponent.scale.set(scale);

        entity.addComponent(transformComponent);

        entity.addComponent(new CircleComponent());

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
    }

    // FIXME
    public void update()
    {
        if(active && !shouldDestroy) {
            int sz = components.size();
            for(int i = 0; i < sz; i++) {
                components.get(i).update();
            }
        }
    }

    // FIXME
    public void deltaUpdate(float deltaTime)
    {
        if(active && !shouldDestroy) {
            int sz = components.size();
            for(int i = 0; i < sz; i++) {
                components.get(i).deltaUpdate(deltaTime);
            }
        }
    }

    public void destroy()
    {
        shouldDestroy = true;

        if (parentEntity != null) {
            parentEntity.removeChildEntity(this);
            parentEntity = null;
        }

        Iterator<Component> componentsIterator = components.iterator();
        while (componentsIterator.hasNext()) {
            Component component = componentsIterator.next();
            ECSWorld.getCurrentECSWorld().removeComponent(component);
            component.destroy();
            componentsIterator.remove();
        }

        //int size = childrenEntities.size();
        for (int i = 0; i < childrenEntities.size(); i++) {
            childrenEntities.get(i).destroy();
            //childrenIterator.remove();
        }
        layer = null;
    }

    @Override
    public void destroyFromScene()
    {
        if(layer != null) {
            layer.getEntities().remove(this);
        }

        active = false;
    }

    @Override
    public void restore()
    {
        if(layer != null) {
            layer.getEntities().add(this);
        }

        active = true;
    }

    // Components
    public <T extends Component> T addComponent (T component)
    {
        if(component == null) return null;

        if(component instanceof NonDuplicated) {
            Optional<Component> foundComponent = components.stream().filter(c -> c.getClass().equals(component.getClass())).findAny();
            if(foundComponent.isPresent()) {
                // FIXME: убрать вывод диалогового окна и сделать просто принт в лог и чтобы слева внизу в редакторе показывалась ошибка
                Log.showErrorDialog("Component " + component.getClass().getName() + " already exists");
                Log.CurrentSession.println("Component " + component.getClass().getName() + " already exists. Component '" + component + "' is NonDuplicated", Log.MessageType.ERROR, true);

                return component;
            }
        }

        if(components.size() > 0) {
            component.ID = components.stream().max(Comparator.comparingInt(c0 -> c0.ID)).get().ID + 1;
        }
        components.add(component);
        component.entity = this;

        // добавление компонента в ECS мир
        ECSWorld.getCurrentECSWorld().addComponent(component);
        //Log.CurrentSession.println("попытался добавить компонент: " + component + ", " + component.entity.name, Log.MessageType.ERROR);

        if(component instanceof ScriptComponent scriptComponent) {
            if(scriptComponent.script.getScriptClassInstance() instanceof Component componentScriptInstance) {
                componentScriptInstance.entity = this;
                scriptComponent.script.setFieldValue(scriptComponent.script.getField("entity"), this);
            } // FIXME:
             /* else if(scriptComponent.script.getScriptClassInstance() instanceof Systems systemScriptInstance) {
                systemScriptInstance.entity = this;
            }
            */
        }
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
            if(componentClass.isAssignableFrom(component.getClass())) {
                return componentClass.cast(component);
            } else if(ScriptComponent.class.isAssignableFrom(component.getClass()) && componentClass.isAssignableFrom(((ScriptComponent) component).script.getScriptClass())) {
                return componentClass.cast(((ScriptComponent) component).script.getScriptClassInstance());
            }
        }

        return null;
    }

    public <T extends Component> List<T> getAllComponents(Class<T> componentClass)
    {
        List<T> componentsFound = new ArrayList<>();
        for(var component : components) {
            if(componentClass.isAssignableFrom(component.getClass())){
                componentsFound.add(componentClass.cast(component));
            } else if(ScriptComponent.class.isAssignableFrom(component.getClass()) && componentClass.isAssignableFrom(((ScriptComponent) component).script.getScriptClass())) {
                componentsFound.add(componentClass.cast(((ScriptComponent) component).script.getScriptClassInstance()));
            }
        }

        return componentsFound;
    }

    public void removeComponent(Component component)
    {
        if(component instanceof NonRemovable) {
            Log.showErrorDialog("Component " + component.getClass().getName() + " is non-removable");
            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("Component " + component.getClass().getName() + " is non-removable")), Log.MessageType.ERROR);
        } else {
            boolean removed = components.remove(component);
            if (removed) {
                component.destroy();
            } else { // попытка получить все скрипт компоненты и попробовать на основе скриптов удалить компонент из списка
                List<ScriptComponent> scriptComponents = getAllComponents(ScriptComponent.class);
                for(var scriptComponent : scriptComponents) {
                    if(component == scriptComponent.script.getScriptClassInstance()) {
                        removed = components.remove(scriptComponent);
                        if(removed) {
                            component.destroy();
                        } else {
                            Log.CurrentSession.println("Component " + component + " was not found for deletion!", Log.MessageType.ERROR);
                        }

                        return;
                    }
                }
            }
        }

        ECSWorld.getCurrentECSWorld().removeComponent(component);
    }

    public void removeFirstComponent(Class<? extends Component> componentClass)
    {
        Component component = getComponent(componentClass);

        if(component instanceof NonRemovable) {
            Log.showErrorDialog("Component " + component.getClass().getName() + " is non-removable");

            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("Component " + component.getClass().getName() + " is non-removable")), Log.MessageType.ERROR);
        } else {
            if(component != null) {
                boolean removed = components.remove(component);
                if (removed) {
                    component.destroy();
                } else {
                    List<ScriptComponent> scriptComponents = getAllComponents(ScriptComponent.class);
                    for(var scriptComponent : scriptComponents) {
                        if(componentClass.isAssignableFrom(scriptComponent.script.getScriptClass())) {
                            removed = components.remove(scriptComponent);
                            if(removed) {
                                component.destroy();
                            } else {
                                Log.CurrentSession.println("Component " + component + " was not found for deletion!", Log.MessageType.ERROR);
                            }
                            return;
                        }
                     }
                }
            }
        }
    }

    public Component findComponentByID(int ID)
    {
        for(Component component : components) {
            if(component.ID == ID) {
                return component;
            }
        }

        return null;
    }

    public void removeAllComponents(Class<? extends Component> componentClass)
    {
        Iterator<? extends Component> componentsIterator = components.listIterator();

        while(componentsIterator.hasNext()) {
            Component component = componentsIterator.next();

            boolean assignable = componentClass.isAssignableFrom(component.getClass()) ||
                    (ScriptComponent.class.isAssignableFrom(component.getClass()) && componentClass.isAssignableFrom(((ScriptComponent) component).script.getScriptClass()));
            if(assignable && component instanceof NonRemovable) {
                Log.showErrorDialog("Component " + component.getClass().getName() + " is non-removable");

                Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("Component " + component.getClass().getName() + " is non-removable")), Log.MessageType.ERROR);
            } else if(assignable) {
                component.destroy();
                componentsIterator.remove();
            }
        }
    }

    public boolean isShouldDestroy() { return shouldDestroy; }

    public List<Component> getComponents() { return components; }

    //public List<Systems> getSystems() { return systems; }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color) { this.color = new Vector4f(color); }

    public Vector3f getPickColor() { return pickColor; }

    public Entity getParentEntity() { return parentEntity; }
    public void setParentEntity(Entity parentEntity)
    {
        if(this.parentEntity != null) {
            // если у этого объекта больше нет родителя
            if(parentEntity == null) {
                // выполняю некоторые преобразования, чтобы этот зависимый объект встал на свою глобальную позицию обратно
                TransformComponent transformComponent = getComponent(TransformComponent.class);
                TransformComponent parentTransformComponent = this.parentEntity.getComponent(TransformComponent.class);

                transformComponent.parentTransformComponent = parentTransformComponent;
                transformComponent.position.set(new Vector3f(transformComponent.position)
                        .mul(MatrixUtils.getScale(parentTransformComponent.modelMatrix))
                        .add(MatrixUtils.getPosition(parentTransformComponent.modelMatrix)));
                transformComponent.scale.set(new Vector3f(transformComponent.scale).mul(MatrixUtils.getScale(parentTransformComponent.modelMatrix)));

                this.parentEntity.removeChildEntity(this);
            }
        }
        this.parentEntity = parentEntity;
        if(parentEntity != null) {
            // выполняю некоторые преобразования, чтобы этот объект встал на нужную локальную позицию
            this.parentEntityID = parentEntity.ID;
            TransformComponent transformComponent = getComponent(TransformComponent.class);
            TransformComponent parentTransformComponent = this.parentEntity.getComponent(TransformComponent.class);
            transformComponent.parentTransformComponent = parentTransformComponent;
            java.lang.System.out.println("attached parent transformComponent: " + parentTransformComponent);
            transformComponent.position.set(new Vector3f(transformComponent.position).add(MatrixUtils.getPosition(parentTransformComponent.modelMatrix).negate()));
            transformComponent.scale.set(new Vector3f(transformComponent.scale).div(MatrixUtils.getScale(parentTransformComponent.modelMatrix)));
        } else {
            this.parentEntityID = -1;
            TransformComponent transformComponent = getComponent(TransformComponent.class);

            if(transformComponent != null) {
                transformComponent.parentTransformComponent = null;
            }
        }
    }

    public int getParentEntityID() { return parentEntityID; }
    public void setParentEntityID(int parentObject2DID)
    {
        this.parentEntityID = parentObject2DID;

        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            setParentEntity(SceneManager.currentSceneManager.getCurrentScene2D().findEntityByID(parentObject2DID));
        }
    }

    public List<Entity> getChildrenEntities() { return childrenEntities; }
    public void addChildEntity(Entity entity)
    {
        childrenEntities.add(entity);
        childrenEntitiesID.add(entity.ID);
        entity.setParentEntity(this);
    }
    public void addChildrenEntities(List<Entity> objects2D)
    {
        childrenEntities.addAll(objects2D);
        for(int i = 0; i < objects2D.size(); i++) {
            childrenEntitiesID.add(objects2D.get(i).ID);
            objects2D.get(i).setParentEntity(this);
        }
    }
    public void addChildEntityByID(int object2DID)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            Entity entity = SceneManager.currentSceneManager.getCurrentScene2D().findEntityByID(object2DID);
            if(entity != null) {
                childrenEntities.add(entity);
                childrenEntitiesID.add(entity.ID);
                entity.setParentEntity(this);
            }
        }
    }
    public void addChildrenEntitiesByID(List<Integer> objects2DID)
    {
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            for (int i = 0; i < objects2DID.size(); i++) {
                Entity entity = SceneManager.currentSceneManager.getCurrentScene2D().findEntityByID(objects2DID.get(i));
                if(entity != null) {
                    childrenEntities.add(entity);
                    childrenEntitiesID.add(entity.ID);
                    entity.setParentEntity(this);
                }
            }
        }
    }
    public void removeChildEntity(Entity child)
    {
        childrenEntities.remove(child);
        childrenEntitiesID.remove((Integer) child.ID);
    }

    public List<Integer> getChildrenEntitiesID() { return childrenEntitiesID; }

    public void applyChildrenEntitiesID(Scene2D scene2D)
    {
        java.lang.System.out.println("applying: " + childrenEntitiesID.size());
        for (int i = 0; i < childrenEntitiesID.size(); i++) {
            java.lang.System.out.println("id to apply: " + childrenEntitiesID.get(i));
            Entity entity = scene2D.findEntityByID(childrenEntitiesID.get(i));
            if (entity != null) {
                childrenEntities.add(entity);
                //childrenEntitiesID.add(entity.ID);
                entity.setParentEntity(this);
                java.lang.System.out.println("parent " + name + " applied to entity " + entity.name);
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

        Log.CurrentSession.println("entity added: " + name + ", lname: " + layerName, Log.MessageType.WARNING);

        TransformComponent transformComponent = getComponent(TransformComponent.class);
        if(transformComponent != null) {
            transformComponent.position.z = -layer.getID();
        }
    }
}