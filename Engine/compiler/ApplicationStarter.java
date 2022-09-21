import Core2D.Core2D.*;
import Core2D.Utils.*;
import Core2D.Scene2D.*;
import Core2D.Core2D.Core2DUserCallback;
import org.lwjgl.glfw.GLFW;

public class ApplicationStarter
{
    public static void main(String[] args)
    {
        Core2D.core2DMode = Core2DMode.IN_BUILD;

        Core2DUserCallback core2DUserCallback = new Core2DUserCallback() {
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
                SceneManager.currentSceneManager.drawCurrentScene2D();
            }

            @Override
            public void onDeltaUpdate(float deltaTime) {
                SceneManager.currentSceneManager.updateCurrentScene2D(deltaTime);
            }
        };

        Core2D.core2DUserCallback = core2DUserCallback;
        Core2D.start("Test game", new int[] { GLFW.GLFW_SAMPLES }, new int[] { 8 });
    }
}