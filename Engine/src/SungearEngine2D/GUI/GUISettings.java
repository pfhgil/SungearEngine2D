package SungearEngine2D.GUI;

import Core2D.Core2D.Core2D;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;

import java.io.File;

public class GUISettings
{
    public static void init()
    {
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        //String s = Utils.inputStreamToString(Core2D.class.getResourceAsStream("/data/imgui/imgui2.ini"));
        String s = "";
        File iniFile = new File("imgui.ini");
        if(!iniFile.exists()) {
            // дефолтное расположение
            s = Utils.inputStreamToString(Core2D.class.getResourceAsStream("/data/imgui/imgui2.ini"));
        } else {
            s = FileUtils.readAllFile("imgui.ini");
        }
        ImGui.loadIniSettingsFromMemory(s);
    }
}
