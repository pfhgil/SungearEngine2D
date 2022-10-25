package Core2D.Core2D;

/**
 * Custom callback for the Core2D.
 */
public interface Core2DUserCallback
{
    /**
     * Called when the Core2D is started.
     */
    void onInit();

    /**
     * Called when the Core2D exits.
     */
    void onExit();

    /**
     * Called every time the frame is drawn.
     */
    void onDrawFrame();

    /**
     * Called every time the frame is drawn, but it also takes the time difference between the last frame processing and the current one.
     */
    void onDeltaUpdate(float deltaTime);
}
