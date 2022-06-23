package SungearEngine2D.GUI.Views;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;

public class BottomMenuView
{
    public boolean showProgressBar = false;
    public String progressBarText = "";
    public float progressBarCurrent = 0.0f;
    public float progressBarDest = 0.0f;

    public void draw()
    {
        ImGuiWindowClass windowClass = new ImGuiWindowClass();
        windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoTabBar);
        windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoResize);
        ImGui.setNextWindowClass(windowClass);

        ImGui.begin("BottomMenu", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

        if(showProgressBar) {
            ImVec2 windowSize = ImGui.getWindowSize();

            ImGui.setCursorPos(windowSize.x - 150.0f, 10.0f);
            ImGui.progressBar(progressBarCurrent, 150.0f, 10, "");

            ImVec2 textSize = new ImVec2();
            ImGui.calcTextSize(textSize, progressBarText);
            ImGui.sameLine();
            ImGui.setCursorPos(windowSize.x - 150.0f - textSize.x, ImGui.getCursorPosY() - 2);
            ImGui.text(progressBarText);
        }

        ImGui.end();
    }
}
