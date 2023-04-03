package Core2D.Pooling;

import java.util.*;

public class Pool
{
    private Queue<PoolObject> freePoolObjects = new LinkedList<>();
    private List<PoolObject> usedPoolObjects = new ArrayList<>();

    public void addPoolObject(PoolObject poolObject)
    {
        freePoolObjects.add(poolObject);
    }

    public void releaseAllUsedPoolObjects()
    {
        Iterator<PoolObject> iterator = usedPoolObjects.listIterator();
        while(iterator.hasNext()) {
            PoolObject poolObject = iterator.next();
            freePoolObjects.add(poolObject);
            poolObject.destroyFromScene();
            iterator.remove();
        }
    }

    public void releaseUsedPoolObject(PoolObject poolObject)
    {
        Iterator<PoolObject> iterator = usedPoolObjects.listIterator();
        while(iterator.hasNext()) {
            PoolObject poolObject0 = iterator.next();
            if(poolObject0 == poolObject) {
                freePoolObjects.add(poolObject);
                poolObject.destroyFromScene();
                iterator.remove();
            }
        }
    }

    public void destroyFreePoolObject(PoolObject poolObject)
    {
        if(freePoolObjects.removeIf(poolObject0 -> poolObject0 == poolObject)) {
            poolObject.destroy();
        }
    }

    //public void

    public PoolObject get()
    {
        PoolObject freePoolObject = freePoolObjects.poll();
        if(freePoolObject != null) {
            freePoolObject.restore();
            usedPoolObjects.add(freePoolObject);
        }
        return freePoolObject;
    }

    public boolean hasFree()
    {
        return freePoolObjects.size() != 0;
    }

    public Queue<PoolObject> getFreePoolObjects() { return freePoolObjects; }

    public List<PoolObject> getUsedPoolObjects() { return usedPoolObjects; }
}
