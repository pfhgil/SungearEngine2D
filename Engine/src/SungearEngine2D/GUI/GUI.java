package SungearEngine2D.GUI;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Graphics;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.GUI.Views.ResourcesView;
import SungearEngine2D.Main.Settings;
import SungearEngine2D.Project.ProjectsManager;
import imgui.ImGui;
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
        MainView.init();
        renderer.endFrame();

        GLFW.glfwSetDropCallback(Core2D.getWindow().getWindow(), new GLFWDropCallback() {
            @Override
            public void invoke(long window, int count, long names) {
                GLFW.glfwFocusWindow(window);
                if(MainView.getResourcesView().isHovered()) {
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

        /*
        line2D = new Line2D(new Vector2f(50.0f, 50.0f), new Vector2f(1000.0f, 1000.0f));
        line2D.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));

        object2D = new Object2D();
        object2D.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

         */
    }

    public static void draw()
    {
        //line2D.Draw();
        //line2D.setEnd(new Vector2f(Main.getMainCamera2D().getTransform().getPosition()).negate().add(new Vector2f(Mouse.GetMousePosition())).mul(new Vector2f(1.0f / Main.getMainCamera2D().getTransform().getScale().x, 1.0f / Main.getMainCamera2D().getTransform().getScale().y)));
        //object2D.Draw();

        renderer.startFrame();

        MainView.draw();

        Settings.drawImGUI();

        /*
        ImGui.begin("click view");

        ImGui.image(Graphics.getPickingRenderTarget().getTextureHandler(), 100.0f, 100.0f);

        ImGui.end();

         */

        renderer.endFrame();
    }
}
