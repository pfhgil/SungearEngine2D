package Core2D.ECS;

import Core2D.ECS.Component.Component;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.CamerasUpdater;
import Core2D.ECS.System.Systems.MeshesRenderer;
import Core2D.ECS.System.Systems.PrimitivesRenderer;
import Core2D.Utils.Utils;

import java.lang.reflect.Method;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ECSWorld
{
    // TODO: далее сделать загрузку ecs мира из файла
    private static ECSWorld currentECSWorld = new ECSWorld();

    private List<System> systems = new ArrayList<>();

    // поля для дефолтных систем (имплементируют NonRemovable, то есть удалить их не получится, но отключить можно)
    public final MeshesRenderer meshesRenderer = new MeshesRenderer();
    public final PrimitivesRenderer primitivesRenderer = new PrimitivesRenderer();
    public final CamerasUpdater camerasUpdater = new CamerasUpdater();
    // ----------------------------------

    public ECSWorld()
    {
        systems.add(camerasUpdater);
        systems.add(meshesRenderer);
        systems.add(primitivesRenderer);
    }

    public void update()
    {
        for(System system : systems) {
            system.update();
        }
    }

    /*
    public void init()
    {

    }

     */

    public static ECSWorld getCurrentECSWorld() { return currentECSWorld; }

    public void updateSystems()
    {
        systems.forEach(System::update);
    }

    public void deltaUpdateSystems(float deltaTime)
    {
        systems.forEach(system -> system.deltaUpdate(deltaTime));
    }


    public void addComponent(Component component)
    {
        systems.forEach(system -> system.addComponent(component));
    }

    public void removeComponent(Component component)
    {
        systems.forEach(system -> system.removeComponent(component));
    }
}
