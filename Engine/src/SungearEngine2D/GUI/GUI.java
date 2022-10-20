package SungearEngine2D.GUI;

import Core2D.Core2D.Core2D;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Views.EditorView.ResourcesView;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

import java.io.File;

public class GUI
{
    private static Renderer renderer;
    //private static Line2D line2D;
    //private static Object2D object2D;

    public static void init()
    {
        renderer = new Renderer();

        GUISettings.init();

        renderer.startFrame();
        ViewsManager.init();
        renderer.endFrame();

        GLFW.glfwSetDropCallback(Core2D.getWindow().getWindow(), new GLFWDropCallback() {
            @Override
            public void invoke(long window, int count, long names) {
                GLFW.glfwFocusWindow(window);
                if(ViewsManager.getResourcesView().isHovered()) {
                    String[] paths = new String[count];

                    for (int i = 0; i < paths.length; i++) {
                        paths[i] = GLFWDropCallback.getName(names, i);

                        if(ProjectsManager.getCurrentProject() != null) {
                            File currentFile = new File(paths[i]);
                            boolean isDirectory = currentFile.isDirectory();
                            FileUtils.copyFile(paths[i], ResourcesView.currentDirectoryPath + "\\" + FilenameUtils.getName(paths[i]), isDirectory);
                        }
                    }
                }
            }
        });
    }

    public static void draw()
    {
        renderer.startFrame();

        ViewsManager.draw();

        renderer.endFrame();
    }
}
