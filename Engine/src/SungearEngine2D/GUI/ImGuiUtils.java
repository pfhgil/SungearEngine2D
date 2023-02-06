package SungearEngine2D.GUI;

import Core2D.Graphics.OpenGL;
import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.ImGuiWindow;
import imgui.type.ImString;
import org.lwjgl.opengl.GL46C;

import java.util.*;
import java.util.function.Function;

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

    // первый boolean для обозначения, нужно ли показывать меню, а второй - наведена ли мышка хотя бы на одну кнопка (arrow button состоит из двух кнопок - просто кнопка с текстом и кнопка со стрелкой)
    // string - идентификатор
    // это кортеж
    private static List<Map.Entry<String, Map.Entry<Boolean, Boolean>>> arrowButtons = new ArrayList<>();

    // последний параметр - требуется ли удержание состояния кнопка (то есть была нажата или нет)
    public static boolean arrowButton(String text, String ID, boolean retentionRequired)
    {
        Map.Entry<String, Map.Entry<Boolean, Boolean>> arrowButton = null;
        Optional<Map.Entry<String, Map.Entry<Boolean, Boolean>>> arrowButtonFound = arrowButtons.stream().filter(entry -> entry.getKey().equals(ID)).findFirst();
        if(arrowButtonFound.isEmpty()) {
            arrowButton = new AbstractMap.SimpleEntry<>(ID, new AbstractMap.SimpleEntry<>(false, false));
            arrowButtons.add(arrowButton);
        } else {
            arrowButton = arrowButtonFound.get();
        }

        boolean newRetention = arrowButton.getValue().getValue();
        boolean newIsHovered = arrowButton.getValue().getKey();

        if(!retentionRequired) {
            newRetention = false;
        }

        ImVec4 buttonCol = new ImVec4();
        ImVec4 hoveredButtonCol = new ImVec4();
        ImGui.getStyleColorVec4(ImGuiCol.Button, buttonCol);
        ImGui.getStyleColorVec4(ImGuiCol.ButtonHovered, hoveredButtonCol);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 3.0f);

        ImGui.pushID(ID);

        if(newIsHovered) {
            ImGui.pushStyleColor(ImGuiCol.Button, hoveredButtonCol.x, hoveredButtonCol.y, hoveredButtonCol.z, hoveredButtonCol.w);
        }
        boolean firstClicked = ImGui.button(text);
        if(newIsHovered) {
            ImGui.popStyleColor(1);
        }
        boolean firstHovered = ImGui.isItemHovered();

        ImGui.popID();

        ImGui.sameLine();

        if(newIsHovered) {
            ImGui.pushStyleColor(ImGuiCol.Button, hoveredButtonCol.x, hoveredButtonCol.y, hoveredButtonCol.z, hoveredButtonCol.w);
        }
        boolean secondClicked = ImGui.arrowButton(ID, 1);
        if(newIsHovered) {
            ImGui.popStyleColor(1);
        }
        boolean secondHovered = ImGui.isItemHovered();

        ImGui.popStyleVar(1);

        if(firstClicked || secondClicked) {
            if(retentionRequired) {
                // устанавливаю, что кнопка была нажата и удержана (каждый раз обратное значение)
                newRetention = !newRetention;
            } else {
                return true;
            }
        }

        // устанавливаю, что хотя бы одна из кнопок была перекрыта
        newIsHovered = firstHovered || secondHovered;

        arrowButton.setValue(new AbstractMap.SimpleEntry<>(newIsHovered, newRetention));

        if(retentionRequired) {
            return newRetention;
        } else {
            return false;
        }
    }

    public static void setArrowButtonRetention(String ID, boolean retention)
    {
        Optional<Map.Entry<String, Map.Entry<Boolean, Boolean>>> arrowButtonFound = arrowButtons.stream().filter(entry -> entry.getKey().equals(ID)).findFirst();
        arrowButtonFound.ifPresent(entryStringEntry -> entryStringEntry.getValue().setValue(retention));
    }

    public static boolean inputText(String label, ImString text, int flags)
    {
        ImGui.pushStyleColor(ImGuiCol.Border, 10.0f / 255.0f, 10 / 255.0f, 10.0f / 255.0f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.BorderShadow, 10.0f / 255.0f, 10 / 255.0f, 10.0f / 255.0f, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 2);

        boolean edited = ImGui.inputText(label, text, flags);

        ImGui.popStyleVar();
        ImGui.popStyleColor(2);

        return edited;
    }

    public static boolean imCallWBorder(Function<Object[], ?> func)
    {
        ImGui.pushStyleColor(ImGuiCol.Border, 10.0f / 255.0f, 10 / 255.0f, 10.0f / 255.0f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.BorderShadow, 10.0f / 255.0f, 10 / 255.0f, 10.0f / 255.0f, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0.2f);

        Object obj = func.apply(null);

        ImGui.popStyleVar();
        ImGui.popStyleColor(2);

        return (boolean) obj;
    }

    public static boolean menuItemWithImage(String text, String ID, int textureID, float iWidth, float iHeight)
    {
        ImVec4 buttonCol = new ImVec4();
        ImVec4 hoveredButtonCol = new ImVec4();
        ImGui.getStyleColorVec4(ImGuiCol.Button, buttonCol);
        ImGui.getStyleColorVec4(ImGuiCol.ButtonHovered, hoveredButtonCol);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 3.0f);

        ImGui.pushID(ID);

        ImVec2 cursorPos = ImGui.getCursorPos();
        ImGui.setCursorPosY(cursorPos.y - 1);
        if(OpenGL.glCall(params -> GL46C.glIsTexture(textureID), Boolean.class)) {
            ImGui.image(textureID, iWidth, iHeight);
        } else {
            ImGui.image(textureID, iWidth, iHeight, 0, 0, 1, 1, 0.0f, 0.0f, 0.0f, 0.0f);
        }
        boolean firstHovered = ImGui.isItemHovered();

        ImGui.popID();

        ImGui.sameLine();

        ImGui.setCursorPosY(cursorPos.y);

        ImVec2 cursorScreenPos = ImGui.getCursorScreenPos();
        ImVec2 windowSize = ImGui.getWindowSize();

        ImGui.setCursorPos(ImGui.getCursorPos().x + 10.0f, cursorPos.y);

        ImGui.pushStyleColor(ImGuiCol.Header, hoveredButtonCol.x, hoveredButtonCol.y, hoveredButtonCol.z, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, hoveredButtonCol.x, hoveredButtonCol.y, hoveredButtonCol.z, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, hoveredButtonCol.x, hoveredButtonCol.y, hoveredButtonCol.z, 0.0f);

        boolean secondHovered = ImGui.menuItem(text);

        ImVec2 min = new ImVec2(cursorScreenPos.x - 20.0f, cursorScreenPos.y - 0);
        ImVec2 max = new ImVec2(cursorScreenPos.x + windowSize.x - 25.0f, cursorScreenPos.y + 15.0f);

        ImGui.popStyleColor(3);

        boolean hovered = ImGui.isMouseHoveringRect(min.x, min.y, max.x, max.y, false);
        if(hovered) {
            ImGui.getForegroundDrawList().addRectFilled(cursorScreenPos.x - 15.0f, cursorScreenPos.y - 2, cursorScreenPos.x + windowSize.x - 32.0f, cursorScreenPos.y + 15.0f, imgui.internal.ImGui.getColorU32(0.5f, 0.5f, 0.5f, 0.5f));
        }

        ImGui.popStyleVar(1);

        return secondHovered;
    }

    public static boolean menuItemWithImage(String text, int textureID, float iWidth, float iHeight)
    {
        return menuItemWithImage(text, text, textureID, iWidth, iHeight);
    }

    public static boolean beginMenuWithImage(String text, String ID, int textureID, float iWidth, float iHeight)
    {
        ImVec4 buttonCol = new ImVec4();
        ImVec4 hoveredButtonCol = new ImVec4();
        ImGui.getStyleColorVec4(ImGuiCol.Button, buttonCol);
        ImGui.getStyleColorVec4(ImGuiCol.ButtonHovered, hoveredButtonCol);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 3.0f);

        ImGui.pushID(ID);

        ImVec2 cursorPos = ImGui.getCursorPos();
        ImGui.setCursorPosY(cursorPos.y - 1);
        if(OpenGL.glCall(params -> GL46C.glIsTexture(textureID), Boolean.class)) {
            ImGui.image(textureID, iWidth, iHeight);
        } else {
            ImGui.image(textureID, iWidth, iHeight, 0, 0, 1, 1, 0.0f, 0.0f, 0.0f, 0.0f);
        }
        boolean firstHovered = ImGui.isItemHovered();

        ImGui.popID();

        ImGui.sameLine();

        ImGui.setCursorPosY(cursorPos.y);

        ImVec2 cursorScreenPos = ImGui.getCursorScreenPos();
        ImVec2 windowSize = ImGui.getWindowSize();

        ImGui.setCursorPos(ImGui.getCursorPos().x + 10.0f, cursorPos.y);

        ImGui.pushStyleColor(ImGuiCol.Header, hoveredButtonCol.x, hoveredButtonCol.y, hoveredButtonCol.z, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, hoveredButtonCol.x, hoveredButtonCol.y, hoveredButtonCol.z, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, hoveredButtonCol.x, hoveredButtonCol.y, hoveredButtonCol.z, 0.0f);

        boolean secondHovered = firstHovered ? ImGui.beginMenu(text, true) : ImGui.beginMenu(text);

        ImVec2 min = new ImVec2(cursorScreenPos.x - 20.0f, cursorScreenPos.y - 0);
        ImVec2 max = new ImVec2(cursorScreenPos.x + windowSize.x - 25.0f, cursorScreenPos.y + 15.0f);

        ImGui.popStyleColor(3);

        boolean hovered = ImGui.isMouseHoveringRect(min.x, min.y, max.x, max.y, false);
        if(hovered) {
            ImGui.getForegroundDrawList().addRectFilled(cursorScreenPos.x - 15.0f, cursorScreenPos.y - 2, cursorScreenPos.x + windowSize.x - 32.0f, cursorScreenPos.y + 15.0f, imgui.internal.ImGui.getColorU32(0.5f, 0.5f, 0.5f, 0.5f));
        }

        ImGui.popStyleVar(1);

        return secondHovered;
    }

    public static boolean beginMenuWithImage(String text, int textureID, float iWidth, float iHeight)
    {
        return beginMenuWithImage(text, text, textureID, iWidth, iHeight);
    }

    public static boolean leftSideInputText(String text, String ID, ImString str, int flags)
    {
        ImGui.pushID(ID);
        boolean edited = ImGui.inputText(text, str, flags);
        ImGui.popID();

        return edited;
    }

    public static boolean leftSideInputText(String text, ImString str, int flags)
    {
        return leftSideInputText(text, text, str, flags);
    }
}
