package SungearEngine2D.GUI.Views;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class SceneTreeView extends View
{
    public void draw()
    {
        ImGui.begin("Scene2D tree", ImGuiWindowFlags.NoMove);
        {
            update();
            // сделать
        }
        ImGui.end();
    }
}
