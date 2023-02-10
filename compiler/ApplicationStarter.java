import Core2D.Core2D.*;
import Core2D.Scene2D.*;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DUserCallback;
import org.lwjgl.glfw.GLFW;

// доделать, чтобы этот класс запускал игру и её главную сцену
public class ApplicationStarter
{
    private static Core2DUserCallback core2DUserCallback;

    public static void main(String[] args)
    {
        core2DUserCallback = new Core2DUserCallback() {
            @Override
            public void onInit() {
                SceneManager.loadSceneManager(Core2D.class.getResourceAsStream("/SceneManager.sm"));
                if(SceneManager.currentSceneManager != null) {
                    SceneManager.currentSceneManager.setCurrentScene2D(SceneManager.currentSceneManager.mainScene2D);
                }
            }

            @Override
            public void onExit() {

            }

            @Override
            public void onDrawFrame() {

            }

            @Override
            public void onDeltaUpdate(float v) {

            }
        };

        Core2D.core2DUserCallback = core2DUserCallback;
        Core2D.start("Test game", new int[] { GLFW.GLFW_SAMPLES }, new int[] { 8 });
    }
}