package Core2D.Scene2D;

public interface Scene2DCallback
{
    void onLoad();
    void onDraw();
    void onUpdate(float deltaTime);
}
