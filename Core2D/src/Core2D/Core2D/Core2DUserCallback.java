package Core2D.Core2D;

public interface Core2DUserCallback
{
    void onInit();
    void onExit();
    void onDrawFrame();
    void onDeltaUpdate(float deltaTime);
}
