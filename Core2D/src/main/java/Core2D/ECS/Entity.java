package Core2D.ECS;

import Core2D.Core2D.Settings;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
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
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.Tag;
import Core2D.Utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
    //protected List<System> systems = new ArrayList<>();

    public boolean isUIElement = false;

    // цвет
    public Vector4f color = new Vector4f();
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

    @Deprecated
    // копировать объект
    public Entity(Entity entity)
    {
        /*
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

         */
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
        setParentEntity(entity.getParentEntity());
        addChildrenEntities(entity.getChildrenEntities());
    }

    public static Entity createAsObject2D()
    {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent());
        entity.addComponent(new MeshComponent());
        //entity.addSystem(new MeshRendererSystem());

        return entity;
    }

    public static Entity createAsCamera2D()
    {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent());
        entity.addComponent(new Camera2DComponent());

        return entity;
    }

    public static Entity createAsLine()
    {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent());
        entity.addComponent(new LineComponent());
        //entity.addSystem(new PrimitivesRendererSystem());

        return entity;
    }

    public static Entity createAsBox()
    {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent());
        entity.addComponent(new BoxComponent());
        //entity.addSystem(new PrimitivesRendererSystem());

        return entity;
    }

    public static Entity createAsCircle()
    {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent());
        entity.addComponent(new CircleComponent());
        //entity.addSystem(new PrimitivesRendererSystem());

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
            for(Component component : components) {
                component.update();
            }

            /*
            for(System system : systems) {
                system.update();
            }

             */
        }
    }

    // FIXME
    public void deltaUpdate(float deltaTime)
    {
        if(active && !shouldDestroy) {
            for(Component component : components) {
                component.deltaUpdate(deltaTime);
            }

            /*
            for(System system : systems) {
                system.deltaUpdate(deltaTime);
            }

             */
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
    public void destroyFromScene2D()
    {
        shouldDestroy = true;

        active = false;
    }

    @Override
    public void restore()
    {
        shouldDestroy = false;

        active = true;
    }

    // Components
    public <T extends Component> T addComponent (T component)
    {
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
             /* else if(scriptComponent.script.getScriptClassInstance() instanceof System systemScriptInstance) {
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

    // Systems
    /*
    public <T extends System> T addSystem(T system)
    {
        for(var currentSystem : systems) {
            if(currentSystem.getClass().equals(system.getClass()) && currentSystem instanceof NonDuplicated) {
                Log.showErrorDialog("System " + system.getClass().getName() + " already exists");
                Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("System " + system.getClass().getName() + " already exists")), Log.MessageType.ERROR);
            }
        }

        systems.add(system);
        system.entity = this;
        if(system instanceof ScriptableSystem scriptableSystem) {
            scriptableSystem.script.setFieldValue(scriptableSystem.script.getField("entity"), this);
        }
        system.initSystem();
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
            if(systemClass.isAssignableFrom(system.getClass())) {
                return systemClass.cast(system);
            } else if(ScriptableSystem.class.isAssignableFrom(system.getClass()) && systemClass.isAssignableFrom(((ScriptableSystem) system).script.getScriptClass())) {
                return systemClass.cast(((ScriptableSystem) system).script.getScriptClassInstance());
            }
        }

        return null;
    }

    public <T extends System> List<T> getAllSystems(Class<T> systemClass)
    {
        List<T> systemsFound = new ArrayList<>();
        for(var system : systems) {
            if(systemClass.isAssignableFrom(system.getClass())){
                systemsFound.add(systemClass.cast(system));
            } else if(ScriptableSystem.class.isAssignableFrom(system.getClass()) && systemClass.isAssignableFrom(((ScriptableSystem) system).script.getScriptClass())) {
                systemsFound.add(systemClass.cast(((ScriptableSystem) system).script.getScriptClassInstance()));
            }
        }

        return systemsFound;
    }

    public void removeSystem(System system)
    {
        if(system instanceof NonRemovable) {
            Log.showErrorDialog("System " + system.getClass().getName() + " is non-removable");

            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("System " + system.getClass().getName() + " is non-removable")), Log.MessageType.ERROR);
        } else {
            boolean removed = systems.remove(system);
            if (removed) {
                system.destroy();
            } else { // попытка получить все скриптабельные системы и попробовать на основе скриптов удалить систему из списка
                List<ScriptableSystem> scriptableSystems = getAllSystems(ScriptableSystem.class);
                for(var scriptableSystem : scriptableSystems) {
                    if(system == scriptableSystem.script.getScriptClassInstance()) {
                        removed = systems.remove(scriptableSystem);
                        if(removed) {
                            system.destroy();
                        } else {
                            Log.CurrentSession.println("System " + system + " was not found for deletion!", Log.MessageType.ERROR);
                        }

                        return;
                    }
                }
            }
        }
    }

    public void removeFirstSystem(Class<? extends System> systemClass)
    {
        System system = getSystem(systemClass);

        if(system instanceof NonRemovable) {
            Log.showErrorDialog("System " + system.getClass().getName() + " is non-removable");

            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("System " + system.getClass().getName() + " is non-removable")), Log.MessageType.ERROR);
        } else {
            if(system != null) {
                boolean removed = systems.remove(system);
                if (removed) {
                    system.destroy();
                } else {
                    List<ScriptableSystem> scriptableSystems = getAllSystems(ScriptableSystem.class);
                    for (var scriptableSystem : scriptableSystems) {
                        if (systemClass.isAssignableFrom(scriptableSystem.script.getScriptClass())) {
                            removed = systems.remove(scriptableSystem);
                            if (removed) {
                                scriptableSystem.destroy();
                            } else {
                                Log.CurrentSession.println("System " + systemClass + " was not found for deletion!", Log.MessageType.ERROR);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    public void removeAllSystems(Class<? extends System> systemClass)
    {
        Iterator<? extends System> systemsIterator = systems.listIterator();

        while(systemsIterator.hasNext()) {
            System system = systemsIterator.next();

            boolean assignable = systemClass.isAssignableFrom(system.getClass()) ||
                    (ScriptableSystem.class.isAssignableFrom(system.getClass()) && systemClass.isAssignableFrom(((ScriptableSystem) system).script.getScriptClass()));
            if(assignable && system instanceof NonRemovable) {
                Log.showErrorDialog("System " + system.getClass().getName() + " is non-removable");

                Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("System " + system.getClass().getName() + " is non-removable")), Log.MessageType.ERROR);
            } else if(assignable) {
                system.destroy();
                systemsIterator.remove();
            }
        }
    }

     */

    public boolean isShouldDestroy() { return shouldDestroy; }

    public List<Component> getComponents() { return components; }

    //public List<System> getSystems() { return systems; }

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
                transformComponent.position.set(new Vector2f(transformComponent.position)
                        .mul(MatrixUtils.getScale(parentTransformComponent.modelMatrix))
                        .add(MatrixUtils.getPosition(parentTransformComponent.modelMatrix)));
                transformComponent.scale.set(new Vector2f(transformComponent.scale).mul(MatrixUtils.getScale(parentTransformComponent.modelMatrix)));

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
            transformComponent.position.set(new Vector2f(transformComponent.position).add(MatrixUtils.getPosition(parentTransformComponent.modelMatrix).negate()));
            transformComponent.scale.set(new Vector2f(transformComponent.scale).div(MatrixUtils.getScale(parentTransformComponent.modelMatrix)));
        } else {
            this.parentEntityID = -1;
            getComponent(TransformComponent.class).parentTransformComponent = null;
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
    }
}