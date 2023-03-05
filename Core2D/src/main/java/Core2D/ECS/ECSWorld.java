package Core2D.ECS;

import Core2D.ECS.Component.Component;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
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
    // ----------------------------------

    public ECSWorld()
    {
        systems.add(componentsInitializerSystem);
        systems.add(camerasManagerSystem);
        systems.add(meshesRendererSystem);
        systems.add(primitivesRendererSystem);

        // transformations
        systems.add(transformationsSystem);

        Log.CurrentSession.println("ECSWorld created!", Log.MessageType.WARNING);
    }

    // добавление компонента для обработки
    public void addComponent(Component component)
    {
        if(component.entity == null) {
            Log.CurrentSession.println("Value of field 'entity' in component '" + component + "' is equals null", Log.MessageType.ERROR, true);
            return;
        }

        componentsInitializerSystem.initComponentOnAdd(component);

        boolean foundQuery = false;
        for(ComponentsQuery componentsQuery : componentsQueries) {
            if(componentsQuery.entityID == component.entity.ID) {
                //Log.CurrentSession.println("found query for component: " + component + ", ename: " + component.entity.name + ", eid: " + component.entity.ID, Log.MessageType.ERROR);
                if(component instanceof NonDuplicated) {
                    Optional<Component> foundComponent = componentsQuery.getComponents().stream().filter(c -> c.getClass().equals(component.getClass())).findAny();
                    if(foundComponent.isPresent()) {
                        Log.CurrentSession.println("Component '" + component + "' already exists in ComponentsQuery with 'entityID' == "
                                + componentsQuery.entityID + ". Component '" + component + "' is NonDuplicated", Log.MessageType.ERROR, true);
                        return;
                    } else {
                        componentsQuery.getComponents().add(component);
                        foundQuery = true;
                        /*
                        Log.CurrentSession.println("added non-duplicated component: " + component + ", entity: " + component.entity.name + ", eid: " + component.entity.ID,
                                Log.MessageType.WARNING);

                         */
                    }
                } else {
                    componentsQuery.getComponents().add(component);
                    foundQuery = true;
                    /*
                    Log.CurrentSession.println("added simple component: " + component + ", entity: " + component.entity.name + ", eid: " + component.entity.ID,
                            Log.MessageType.WARNING);

                     */
                }
            }
        }

        if(!foundQuery) {
            ComponentsQuery componentsQuery = new ComponentsQuery(component.entity.ID);
            componentsQuery.getComponents().add(component);
            componentsQueries.add(componentsQuery);

            /*
            Log.CurrentSession.println("added component (and created query): " + component + ", entity: " + component.entity.name + ", eid: " + component.entity.ID,
                    Log.MessageType.WARNING);

             */
        }

        //Log.CurrentSession.println("added component: " + component + ", entity: " + component.entity.name, Log.MessageType.WARNING);

        //Log.CurrentSession.println("components queries num: " + componentsQueries.size() + ", component entity id: " + component.entity.ID, Log.MessageType.WARNING);
    }

    // удаляет компонент из обработки
    public void removeComponent(Component component)
    {
        Iterator<ComponentsQuery> componentsQueryIterator = componentsQueries.iterator();
        while(componentsQueryIterator.hasNext()) {
            ComponentsQuery componentsQuery = componentsQueryIterator.next();

            if(componentsQuery.entityID == component.entity.ID) {
                componentsQuery.getComponents().remove(component);

                if(componentsQuery.getComponents().size() == 0) {
                    componentsQueryIterator.remove();
                }
            }
        }

        componentsInitializerSystem.destroyComponent(component);
    }

    public ComponentsQuery findComponentsQuery(int entityID)
    {
        return componentsQueries.stream().filter(componentsQuery -> componentsQuery.entityID == entityID).findAny().orElse(null);
    }

    public void update()
    {
        for(System system : systems) {
            for (ComponentsQuery componentsQuery : componentsQueries) {
                system.update(componentsQuery);
            }
        }
    }

    public void deltaUpdate(float deltaTime)
    {
        for(System system : systems) {
            for (ComponentsQuery componentsQuery : componentsQueries) {
                system.deltaUpdate(componentsQuery, deltaTime);
            }
        }
    }

    public static ECSWorld getCurrentECSWorld() { return currentECSWorld; }

    public List<ComponentsQuery> getComponentsQueries() { return componentsQueries; }
}
