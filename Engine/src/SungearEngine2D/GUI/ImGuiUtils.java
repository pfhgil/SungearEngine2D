package SungearEngine2D.GUI;

<<<<<<< Updated upstream
import imgui.ImGui;
import imgui.ImVec2;
=======
import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Graphics.RenderParts.Texture2D;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseCursor;
>>>>>>> Stashed changes
import imgui.flag.ImGuiStyleVar;

public class ImGuiUtils
{
    public static boolean sliderFloat(String name, float[] current, float min, float max, String toPushName, String textIn, String textNear)
    {
        boolean pressed = false;

        float cursorPos = ImGui.getCursorPosY();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 4.0f);
        ImGui.text(name);
        ImGui.sameLine();
        ImGui.setCursorPosY(cursorPos);

        ImGui.pushStyleVar(ImGuiStyleVar.Alpha, 0.0f);
        ImGui.pushID(toPushName);
        if(ImGui.sliderFloat("", current, min, max, textIn)) {
            pressed = true;
        }
        ImGui.popID();
        ImGui.popStyleVar(1);

        ImVec2 sliderSize = new ImVec2(ImGui.getItemRectSize().x, 5.0f);
        float sliderCentreY = ImGui.getItemRectMin().y + (ImGui.getItemRectMax().y - ImGui.getItemRectMin().y) / 2f;
        ImVec2 sliderMin = new ImVec2(ImGui.getItemRectMin().x, sliderCentreY - sliderSize.y / 2f);
        ImVec2 sliderMax = new ImVec2(sliderMin.x + sliderSize.x, sliderCentreY + sliderSize.y / 2f);
        ImVec2 sliderFirstPartMax = new ImVec2(sliderMin.x + sliderSize.x * current[0] / max, sliderMin.y + sliderSize.y);

        ImGui.getWindowDrawList().addRectFilled(
                sliderMin.x, sliderMin.y,
                sliderFirstPartMax.x, sliderFirstPartMax.y,
                ImGui.getColorU32(0.9f, 0.9f, 0.9f, 1.0f)
        );
        ImGui.getWindowDrawList().addRectFilled(
                sliderFirstPartMax.x, sliderMin.y,
                sliderMax.x, sliderMax.y,
                ImGui.getColorU32(0.6f, 0.6f, 0.6f, 1.0f)
        );
        ImGui.getWindowDrawList().addCircleFilled(sliderFirstPartMax.x, sliderCentreY, 6f, ImGui.getColorU32(0.4f, 0.4f, 0.4f, 1.0f));

        ImGui.sameLine();

        ImGui.text(textNear);

        return pressed;
    }

    public static boolean sliderFloat(String name, float[] current, float min, float max)
    {
        return sliderFloat(name, current, min, max, name, "", "");
    }

    public static boolean sliderFloat(String name, float[] current, float min, float max, String toPushName)
    {
        return sliderFloat(name, current, min, max, toPushName, "", "");
    }

    public static boolean sliderFloat(String name, float[] current, float min, float max, String toPushName, String textIn)
    {
        return sliderFloat(name, current, min, max, toPushName, textIn, "");
    }
}
