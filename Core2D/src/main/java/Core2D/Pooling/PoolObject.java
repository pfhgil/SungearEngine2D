package Core2D.Pooling;

public interface PoolObject
{
    void destroy();
    void destroyFromScene2D();
    void restore();
}
