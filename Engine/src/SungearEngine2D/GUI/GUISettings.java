package SungearEngine2D.GUI;

import Core2D.Core2D.Core2D;
import Core2D.Utils.Utils;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;

public class GUISettings
{
    public static void init()
    {
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        String s = Utils.inputStreamToString(Core2D.class.getResourceAsStream("/data/imgui/imgui.ini"));
        ImGui.loadIniSettingsFromMemory(s);
    }
}
