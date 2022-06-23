package SungearEngine2D.GUI.Views;

import Core2D.Log.Log;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

public class LogView extends View
{
    private ImString log = new ImString();

    public void draw()
    {
        ImGui.begin("Log", ImGuiWindowFlags.NoMove);
        {
            log.set(Log.CurrentSession.getLog(), true);

            ImGui.pushID("LogText");
            ImGui.inputTextMultiline("", log, ImGui.getWindowWidth(), ImGui.getContentRegionAvailY(), ImGuiInputTextFlags.ReadOnly);
            ImGui.popID();

            update();
        }
        ImGui.end();
    }
}
