package Core2D.Scene2D;

public interface Scene2DCallback
{
    void onLoad();
    void onUpdate();
    void onDeltaUpdate(float deltaTime);
}
