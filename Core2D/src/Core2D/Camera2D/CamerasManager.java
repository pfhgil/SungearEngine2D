package Core2D.Camera2D;

/**
 * Cameras manager.
 */
public class CamerasManager
{
    private static Camera2D mainCamera2D;

    public static Camera2D getMainCamera2D() { return mainCamera2D; }
    public static void setMainCamera2D(Camera2D mainCamera2D) { CamerasManager.mainCamera2D = mainCamera2D; }
}
