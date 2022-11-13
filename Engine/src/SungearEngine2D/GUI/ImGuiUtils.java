package SungearEngine2D.GUI;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;

public class ImGuiUtils
{
    public static boolean sliderFloat(String name, float[] current, float min, float max, String text, String toPushName)
    {
        boolean pressed = false;

        ImGui.pushStyleVar(ImGuiStyleVar.Alpha, 0.0f);
        ImGui.pushID(toPushName);
        if(ImGui.sliderFloat(name, current, min, max, text)) {
            pressed = true;
        }
        ImGui.popID();
        ImGui.popStyleVar(1);

        ImGuiStyle style = ImGui.getStyle();

        ImVec2 sliderSize = new ImVec2(ImGui.getItemRectSize().x, 5.0f);
        float sliderCentreY = ImGui.getItemRectMin().y + (ImGui.getItemRectMax().y - ImGui.getItemRectMin().y) / 2f;
        ImVec2 sliderMin = new ImVec2(ImGui.getItemRectMin().x, sliderCentreY - sliderSize.y / 2f);
        ImVec2 sliderMax = new ImVec2(sliderMin.x + sliderSize.x, sliderCentreY + sliderSize.y / 2f);
        float coeff = current[0] / max;
        ImVec2 sliderFirstPartMax = new ImVec2(sliderMin.x + sliderSize.x * coeff, sliderMin.y + sliderSize.y);

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

        return pressed;
    }

    public static boolean sliderFloat(String name, float[] current, float min, float max, String text)
    {
        boolean pressed = false;

        ImGui.pushStyleVar(ImGuiStyleVar.Alpha, 0.0f);
        if(ImGui.sliderFloat(name, current, min, max, text)) {
            pressed = true;
        }
        ImGui.popStyleVar(1);

        ImVec2 sliderSize = new ImVec2(ImGui.getItemRectSize().x, 5.0f);
        float sliderCentreY = ImGui.getItemRectMin().y + (ImGui.getItemRectMax().y - ImGui.getItemRectMin().y) / 2f;
        ImVec2 sliderMin = new ImVec2(ImGui.getItemRectMin().x, sliderCentreY - sliderSize.y / 2f);
        ImVec2 sliderMax = new ImVec2(sliderMin.x + sliderSize.x, sliderCentreY + sliderSize.y / 2f);
        float coeff = current[0] / max;
        ImVec2 sliderFirstPartMax = new ImVec2(sliderMin.x + sliderSize.x * coeff, sliderMin.y + sliderSize.y);

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

        return pressed;
    }
}
