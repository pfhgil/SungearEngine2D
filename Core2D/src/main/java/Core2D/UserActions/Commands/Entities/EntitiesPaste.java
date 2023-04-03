package Core2D.UserActions.Commands.Entities;

import Core2D.Common.Interfaces.Unregistered;
import Core2D.ECS.Entity;
import Core2D.Log.Log;
import Core2D.Scene2D.SceneManager;
import Core2D.UserActions.Commands.Command;
import Core2D.UserActions.Executors.Executor;

import java.util.Iterator;

public class EntitiesPaste extends Command implements Unregistered
{
    @Override
    public void execute(Object... params)
    {
        Log.CurrentSession.println("entity: " + (Executor.bufferedObject instanceof Entity) + ", entities[]: " + (Executor.bufferedObject instanceof Entity[]) +
                ", object[]: " + (Executor.bufferedObject instanceof Object[]), Log.MessageType.WARNING);

        if(Executor.bufferedObject instanceof Object[] objects) {
            for(Object obj : objects) {
                if(obj instanceof Entity entity) {
                    entity.copy().addOnScene();

                    executedObjects.add(entity);
                }
            }
        }
    }

    @Override
    public void restore()
    {
        if(SceneManager.currentSceneManager == null || SceneManager.currentSceneManager.getCurrentScene2D() == null || executed) return;

        for(Object obj : executedObjects) {
            if(obj instanceof Entity entity) {
                entity.restore();
            }
        }

        executed = true;
    }

    @Override
    public void revert()
    {
        if(!executed) return;

        for(Object obj : executedObjects) {
            if(obj instanceof Entity entity) {
                entity.destroyFromScene();
            }
        }

        executed = false;
    }

    @Override
    public void free()
    {
        Iterator<Object> objectsIterator = executedObjects.iterator();
        while(objectsIterator.hasNext()) {
            Object obj = objectsIterator.next();

            if(obj instanceof Entity entity && !executed) {
                entity.destroy();
            }

            objectsIterator.remove();
        }
    }
}
