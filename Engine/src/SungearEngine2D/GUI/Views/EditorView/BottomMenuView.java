package SungearEngine2D.GUI.Views.EditorView;

import Core2D.Core2D.Core2D;
import Core2D.Input.PC.Keyboard;
import Core2D.Log.Log;
import Core2D.Tasks.StoppableTask;
import Core2D.Tasks.Task;
import Core2D.Utils.Utils;
import SungearEngine2D.Main.Resources;
import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BottomMenuView
{
    private boolean showAnotherTasksWindow = true;

    private List<Task> tasksList = new ArrayList<>();

    public String leftSideInfo = "";
    public Vector4f leftSideInfoColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private float maxProgressBarWidth = 0.0f;

    public void draw()
    {
        ImGuiWindowClass windowClass = new ImGuiWindowClass();
        windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoTabBar);
        windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoResize);
        ImGui.setNextWindowClass(windowClass);

        ImGui.begin("BottomMenu", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

        ImGui.textColored(leftSideInfoColor.x, leftSideInfoColor.y, leftSideInfoColor.z, leftSideInfoColor.w, leftSideInfo);

        tasksList.removeIf(bottomProgressBarElement -> !bottomProgressBarElement.isAlive());

        maxProgressBarWidth = 0.0f;

        //maxProgressBarWidth = 0.0f;
        if(tasksList.size() > 0) {
            showProgressBar(tasksList.get(0), new Vector2f(0.0f, -10.0f));
        }
        if(tasksList.size() > 1) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);

            ImGui.sameLine();
            Vector2f collapseIconSize = new Vector2f(Resources.Textures.Icons.collapseIcon.getWidth(),
                    Resources.Textures.Icons.collapseIcon.getHeight());
            ImGui.setCursorPos(ImGui.getWindowSizeX() - maxProgressBarWidth - collapseIconSize.x * 2.5f, ImGui.getCursorPosY() + 5.0f);
            boolean collapsed = ImGui.imageButton(Resources.Textures.Icons.collapseIcon.getHandler(), collapseIconSize.x, collapseIconSize.y, 0, 0, 1, 1, -1, 1, 1, 1, 0, 1, 1, 1, 1);

            if(collapsed) {
                showAnotherTasksWindow = !showAnotherTasksWindow;
            }

            ImGui.popStyleColor(3);
        }

        ImGui.end();

        if(tasksList.size() > 1 && showAnotherTasksWindow) {
            ImGui.begin("Tasks", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
            for (int i = 1; i < tasksList.size(); i++) {
                showProgressBar(tasksList.get(i));
            }
            ImGui.setWindowSize(maxProgressBarWidth + 18.0f, 100.0f);
            Vector2i glfwWindowSize = Core2D.getWindow().getSize();
            ImVec2 imGuiWindowSize = ImGui.getWindowSize();
            ImGui.setWindowPos(glfwWindowSize.x - maxProgressBarWidth - 18.0f, glfwWindowSize.y - imGuiWindowSize.y - 50.0f);
            ImGui.end();
        }
    }

    public void showProgressBar(Task task)
    {
        ImVec2 windowSize = ImGui.getWindowSize();
        Vector2f progressBarPos = new Vector2f(windowSize.x - 150.0f, ImGui.getCursorPosY());

        Vector2f xIconSize = new Vector2f(Resources.Textures.Icons.xIcon.getWidth() - 1,
                Resources.Textures.Icons.xIcon.getHeight() - 1);

        if(task instanceof StoppableTask) {
            progressBarPos.x -= xIconSize.x * 2;
        }

        ImGui.setCursorPos(progressBarPos.x, progressBarPos.y);
        ImGui.progressBar(task.current / task.destination, 150.0f, 10, "");

        ImVec2 textSize = new ImVec2();
        ImGui.calcTextSize(textSize, task.text);
        textSize.x += 4.0f;
        ImGui.sameLine();
        ImGui.setCursorPos(progressBarPos.x - textSize.x, ImGui.getCursorPosY() - 2);
        ImGui.text(task.text);

        float resultWidth = 150.0f + xIconSize.x + textSize.x;
        if(resultWidth > maxProgressBarWidth) {
            maxProgressBarWidth = resultWidth;
        }

        if(task instanceof StoppableTask) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);

            ImGui.sameLine();
            ImGui.setCursorPos(windowSize.x - xIconSize.x * 2, ImGui.getCursorPosY() - 3);
            ImGui.imageButton(Resources.Textures.Icons.xIcon.getHandler(), xIconSize.x, xIconSize.y, 0, 0, 1, 1, -1, 1, 1, 1, 0, 1, 1, 1, 1);
            if (ImGui.isMouseClicked(ImGuiMouseButton.Left) && ImGui.isItemHovered()) {
                task.interrupt();
                task.stop();
            }

            ImGui.popStyleColor(3);
        }
    }

    public void showProgressBar(Task task, Vector2f customOffset)
    {
        ImVec2 windowSize = ImGui.getWindowSize();
        Vector2f progressBarPos = new Vector2f(windowSize.x - 150.0f + customOffset.x, ImGui.getCursorPosY() + customOffset.y);

        Vector2f xIconSize = new Vector2f(Resources.Textures.Icons.xIcon.getWidth() - 1,
                Resources.Textures.Icons.xIcon.getHeight() - 1);

        if(task instanceof StoppableTask) {
            progressBarPos.x -= xIconSize.x * 2;
        }

        ImGui.setCursorPos(progressBarPos.x, progressBarPos.y);
        ImGui.progressBar(task.current / task.destination, 150.0f, 10, "");

        ImVec2 textSize = new ImVec2();
        ImGui.calcTextSize(textSize, task.text);
        textSize.x += 4.0f;
        ImGui.sameLine();
        ImGui.setCursorPos(progressBarPos.x - textSize.x, ImGui.getCursorPosY() - 2);
        ImGui.text(task.text);

        float resultWidth = 150.0f + xIconSize.x * 2.0f + textSize.x;
        if(resultWidth > maxProgressBarWidth) {
            maxProgressBarWidth = resultWidth;
        }

        if(task instanceof StoppableTask) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);

            ImGui.sameLine();
            ImGui.setCursorPos(windowSize.x - xIconSize.x * 2, ImGui.getCursorPosY() - 3);
            boolean xClicked = ImGui.imageButton(Resources.Textures.Icons.xIcon.getHandler(), xIconSize.x, xIconSize.y, 0, 0, 1, 1, -1, 1, 1, 1, 0, 1, 1, 1, 1);
            if (xClicked) {
                task.interrupt();
                task.stop();
            }

            ImGui.popStyleColor(3);
        }
    }

    public void addTaskToList(Task task)
    {
        tasksList.add(task);
        Log.CurrentSession.println(task.text, Log.MessageType.INFO);
        task.start();
    }

    public List<Task> getTasksList() { return tasksList; }
}
