import Core2D.Core2D.*;
import Core2D.Utils.*;
import Core2D.Scene2D.*;
import Core2D.Core2D.Core2DUserCallback;
import org.lwjgl.glfw.GLFW;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import Core2D.Log.Log;

public class ApplicationStarter
{
    public static void main(String[] args)
    {
        Core2D.core2DMode = Core2DMode.IN_BUILD;

        Core2DUserCallback core2DUserCallback = new Core2DUserCallback() {
            @Override
            public void onInit() {
                SceneManager.loadSceneManagerAsCurrent(Core2D.class.getResourceAsStream("/SceneManager.sm"));
                if(SceneManager.currentSceneManager != null) {
                    String scene2DName = "";
                    for(Scene2DStoredValues storedValues : SceneManager.currentSceneManager.getScene2DStoredValues()) {
                        if(storedValues.isMainScene2D) {
                            scene2DName = FilenameUtils.getBaseName(new File(storedValues.path).getName());
                            //break;
                        }
                    }
                    SceneManager.currentSceneManager.setCurrentScene2D(SceneManager.currentSceneManager.getScene2D(scene2DName));
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