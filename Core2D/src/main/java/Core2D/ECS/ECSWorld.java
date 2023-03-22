package Core2D.ECS;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.ECS.System.Systems.Cameras.CamerasManagerSystem;
import Core2D.Log.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ECSWorld
{
    // TODO: далее сделать загрузку ecs мира из файла
    private static ECSWorld currentECSWorld = new ECSWorld();

    private List<System> systems = new ArrayList<>();

    private List<ComponentsQuery> componentsQueries = new ArrayList<>();

    // поля для дефолтных систем (имплементируют NonRemovable, то есть удалить их не получится, но отключить можно)
    // операции с ComponentsQueries происходит через componentsManager
    public final ComponentsInitializerSystem componentsInitializerSystem = new ComponentsInitializerSystem();
    public final MeshesRendererSystem meshesRendererSystem = new MeshesRendererSystem();
    public final PrimitivesRendererSystem primitivesRendererSystem = new PrimitivesRendererSystem();
    public final CamerasManagerSystem camerasManagerSystem = new CamerasManagerSystem();
    public final TransformationsSystem transformationsSystem = new TransformationsSystem();
    public final AudioSystem audioSystem = new AudioSystem();
    // ----------------------------------

    public ECSWorld()
    {
        systems.add(componentsInitializerSystem);
        systems.add(camerasManagerSystem);
        systems.add(meshesRendererSystem);
        systems.add(primitivesRendererSystem);

        // transformations
        systems.add(transformationsSystem);

        // audio
        systems.add(audioSystem);

        Log.CurrentSession.println("ECSWorld created!", Log.MessageType.WARNING);
    }

    // добавление компонента для обработки
    public void addComponent(Component component)
    {
        if(component.entity == null) {
            Log.CurrentSession.println("Value of field 'entity' in component '" + component + "' is equals null. It will be added in блять забыл", Log.MessageType.WARNING, true);
            // общак с entityid = -1
            // тут все компоненты безмамные (без entity) будут лежать
            ComponentsQuery componentsQuery = new ComponentsQuery(-1);
            componentsQuery.getComponents().add(component);
            componentsQueries.add(componentsQuery);
            return;
        }

        componentsInitializerSystem.initComponentOnAdd(component);

        boolean foundQuery = false;
        for(ComponentsQuery componentsQuery : componentsQueries) {
            if(componentsQuery.entityID == component.entity.ID) {
                componentsQuery.getComponents().add(component);
                foundQuery = true;
            }
        }

        if(!foundQuery) {
            ComponentsQuery componentsQuery = new ComponentsQuery(component.entity.ID);
            componentsQuery.getComponents().add(component);
            componentsQueries.add(componentsQuery);
        }
    }

    // удаляет компонент из обработки
    public void removeComponent(Component component)
    {
        Iterator<ComponentsQuery> componentsQueryIterator = componentsQueries.iterator();
        while (componentsQueryIterator.hasNext()) {
            ComponentsQuery componentsQuery = componentsQueryIterator.next();

            componentsQuery.getComponents().remove(component);
            if (componentsQuery.getComponents().size() == 0) {
                componentsQueryIterator.remove();
            }
        }

        componentsInitializerSystem.destroyComponent(component);
    }

    public void addSystem(System system)
    {
        if(system instanceof NonDuplicated) {
            Optional<System> foundSystem = systems.stream().filter(s -> s.getClass().equals(system.getClass())).findAny();

            if(foundSystem.isPresent()) {
                Log.CurrentSession.println("Systems '" + system + "' already exists in ECSWorld. Systems " + system + "' is NonDuplicated", Log.MessageType.ERROR, false);
            } else {
                systems.add(system);
            }
        } else {
            systems.add(system);
        }
    }

    public ComponentsQuery findComponentsQuery(int entityID)
    {
        return componentsQueries.stream().filter(componentsQuery -> componentsQuery.entityID == entityID).findAny().orElse(null);
    }

    public void update()
    {
        for(System system : systems) {
            if(!system.active) continue;

            for (ComponentsQuery componentsQuery : componentsQueries) {
                system.update(componentsQuery);
            }
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        for(System system : systems) {
            if(!system.active) continue;

            for (ComponentsQuery componentsQuery : componentsQueries) {
                system.deltaUpdate(componentsQuery, deltaTime);
            }
        }
    }

    public <T extends System> T getSystem(Class<T> systemCls)
    {
        for(var system : systems) {
            if(systemCls.isAssignableFrom(system.getClass())) {
                return systemCls.cast(system);
            }
            // FIXME: сделать скриптинговые системы
            /*else if(ScriptComponent.class.isAssignableFrom(system.getClass()) && componentClass.isAssignableFrom(((ScriptComponent) system).script.getScriptClass())) {
                return componentClass.cast(((ScriptComponent) system).script.getScriptClassInstance());
            }*/
        }

        return null;
    }

    public static ECSWorld getCurrentECSWorld() { return currentECSWorld; }

    public List<ComponentsQuery> getComponentsQueries() { return componentsQueries; }

    public List<System> getSystems() { return systems; }
}
