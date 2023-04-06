package Core2D.UserActions.Commands.Entities;

import Core2D.ECS.Entity;
import Core2D.Scene2D.SceneManager;
import Core2D.UserActions.Commands.Command;

import java.util.Iterator;

public class EntityAddOnScene extends Command {
    @Override
    public void execute(Object... params)
    {
        if(SceneManager.currentSceneManager == null || SceneManager.currentSceneManager.getCurrentScene2D() == null) return;

        for(Object obj : params) {
            if(obj instanceof Entity entity) {
                entity.addOnScene();

                executedObjects.add(entity);
            }
        }

        executed = true;
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
