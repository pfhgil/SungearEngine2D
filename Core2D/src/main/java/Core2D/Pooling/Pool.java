package Core2D.Pooling;

import java.util.*;

public class Pool
{
    private Queue<PoolObject> freePoolObjects = new LinkedList<>();
    private List<PoolObject> usedPoolObjects = new ArrayList<>();

    public PoolObject addPoolObject(PoolObject poolObject)
    {
        PoolObject freePoolObject = getFreePoolObject();
        if(freePoolObject != null) {
            return freePoolObject;
        } else {
            freePoolObjects.add(poolObject);
            return poolObject;
        }
    }

    public void releaseUsedPoolObject(PoolObject poolObject)
    {
        if(usedPoolObjects.removeIf(poolObject0 -> poolObject0 == poolObject)) {
            freePoolObjects.add(poolObject);
            poolObject.destroyFromScene2D();
        }
    }

    public void destroyFreePoolObject(PoolObject poolObject)
    {
        if(freePoolObjects.removeIf(poolObject0 -> poolObject0 == poolObject)) {
            poolObject.destroy();
        }
    }

    //public void

    public PoolObject getFreePoolObject()
    {
        return freePoolObjects.peek();
    }
}
