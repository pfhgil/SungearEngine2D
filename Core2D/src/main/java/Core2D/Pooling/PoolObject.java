package Core2D.Pooling;

public interface PoolObject
{
    void destroy();
    void destroyFromScene();
    void restore();
}
