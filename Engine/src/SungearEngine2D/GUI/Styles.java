package SungearEngine2D.GUI;

import Core2D.Utils.Utils;
import imgui.*;
import imgui.flag.ImGuiCol;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Styles
{
    public static void applyBlueStyle()
    {
        ImGuiStyle style = ImGui.getStyle();
        float[][] colors = style.getColors();

        style.setWindowRounding(2.0f);
        style.setWindowTitleAlign(0.5f, 0.84f);
        style.setChildRounding(2.0f);
        style.setFrameRounding(2.0f);
        style.setItemSpacing(5.0f, 4.0f);
        style.setScrollbarSize(13.0f);
        style.setScrollbarRounding(0.0f);
        style.setGrabMinSize(8.0f);
        style.setGrabRounding(1.0f);

        colors[ImGuiCol.FrameBg] = new float[] { 0.16f, 0.29f, 0.48f, 0.54f };
        colors[ImGuiCol.FrameBgHovered] = new float[] { 0.26f, 0.59f, 0.98f, 0.40f };
        colors[ImGuiCol.FrameBgActive] = new float[] { 0.26f, 0.59f, 0.98f, 0.67f };
        colors[ImGuiCol.TitleBg] = new float[] { 0.04f, 0.04f, 0.04f, 1.00f };
        colors[ImGuiCol.TitleBgActive] = new float[] { 0.16f, 0.29f, 0.48f, 1.00f };
        colors[ImGuiCol.TitleBgCollapsed] = new float[] { 0.00f, 0.00f, 0.00f, 0.51f };
        colors[ImGuiCol.CheckMark] = new float[] { 0.26f, 0.59f, 0.98f, 1.00f };
        colors[ImGuiCol.SliderGrab] = new float[] { 0.24f, 0.52f, 0.88f, 1.00f };
        colors[ImGuiCol.SliderGrabActive] = new float[] { 0.26f, 0.59f, 0.98f, 1.00f };
        colors[ImGuiCol.Button] = new float[] { 0.26f, 0.59f, 0.98f, 0.40f };
        colors[ImGuiCol.ButtonHovered] = new float[] { 0.26f, 0.59f, 0.98f, 1.00f };
        colors[ImGuiCol.ButtonActive] = new float[] { 0.06f, 0.53f, 0.98f, 1.00f };
        colors[ImGuiCol.Header] = new float[] { 0.26f, 0.59f, 0.98f, 0.31f };
        colors[ImGuiCol.HeaderHovered]  = new float[] { 0.26f, 0.59f, 0.98f, 0.80f };
        colors[ImGuiCol.HeaderActive] = new float[] { 0.26f, 0.59f, 0.98f, 1.00f };
        colors[ImGuiCol.Separator] = colors[ImGuiCol.Border];
        colors[ImGuiCol.SeparatorHovered] = new float[] { 0.26f, 0.59f, 0.98f, 0.78f };
        colors[ImGuiCol.SeparatorActive] = new float[] { 0.26f, 0.59f, 0.98f, 1.00f };
        colors[ImGuiCol.ResizeGrip] = new float[] { 0.26f, 0.59f, 0.98f, 0.25f };
        colors[ImGuiCol.ResizeGripHovered] = new float[] { 0.26f, 0.59f, 0.98f, 0.67f };
        colors[ImGuiCol.ResizeGripActive] = new float[] { 0.26f, 0.59f, 0.98f, 0.95f };
        colors[ImGuiCol.TextSelectedBg] = new float[] { 0.26f, 0.59f, 0.98f, 0.35f };
        colors[ImGuiCol.Text] = new float[] { 1.00f, 1.00f, 1.00f, 1.00f };
        colors[ImGuiCol.TextDisabled] = new float[] { 0.50f, 0.50f, 0.50f, 1.00f };
        colors[ImGuiCol.WindowBg] = new float[] { 0.06f, 0.06f, 0.06f, 0.94f };
        colors[ImGuiCol.ChildBg] = new float[] { 1.00f, 1.00f, 1.00f, 0.00f };
        colors[ImGuiCol.PopupBg] = new float[] { 0.08f, 0.08f, 0.08f, 0.94f };
        colors[ImGuiCol.Border] = new float[] { 0.43f, 0.43f, 0.50f, 0.50f };
        colors[ImGuiCol.BorderShadow] = new float[] { 0.00f, 0.00f, 0.00f, 0.00f };
        colors[ImGuiCol.MenuBarBg] = new float[] { 0.14f, 0.14f, 0.14f, 1.00f };
        colors[ImGuiCol.ScrollbarBg] = new float[] { 0.02f, 0.02f, 0.02f, 0.53f };
        colors[ImGuiCol.ScrollbarGrab] = new float[] { 0.31f, 0.31f, 0.31f, 1.00f };
        colors[ImGuiCol.ScrollbarGrabHovered] = new float[] { 0.41f, 0.41f, 0.41f, 1.00f };
        colors[ImGuiCol.ScrollbarGrabActive] = new float[] { 0.51f, 0.51f, 0.51f, 1.00f };
        colors[ImGuiCol.PlotLines] = new float[] { 0.61f, 0.61f, 0.61f, 1.00f };
        colors[ImGuiCol.PlotLinesHovered] = new float[] { 1.00f, 0.43f, 0.35f, 1.00f };
        colors[ImGuiCol.PlotHistogram] = new float[] { 0.90f, 0.70f, 0.00f, 1.00f };
        colors[ImGuiCol.PlotHistogramHovered] = new float[] { 1.00f, 0.60f, 0.00f, 1.00f };
        colors[ImGuiCol.ModalWindowDimBg] = new float[] { 0.80f, 0.80f, 0.80f, 0.35f };

        style.setColors(colors);
    }

    // светло-темный стиль
    public static void applyDarkLightStyle()
    {
        ImGuiStyle style = ImGui.getStyle();
        float[][] colors = style.getColors();

        style.setWindowPadding(9.0f, 5.0f);
        style.setWindowRounding(0.0f);
        style.setChildRounding(0.0f);
        style.setFramePadding(5.0f, 3.0f);
        style.setFrameRounding(0.0f);
        style.setItemSpacing(9.0f, 3.0f);
        style.setItemInnerSpacing(9.0f, 3.0f);
        style.setIndentSpacing(21.0f);
        style.setScrollbarSize(6.0f);
        style.setScrollbarRounding(0.0f);
        style.setGrabMinSize(17.0f);
        style.setGrabRounding(0.0f);
        style.setWindowTitleAlign(0.5f, 0.5f);
        style.setButtonTextAlign(0.5f, 0.5f);
        style.setTabRounding(0.0f);

        colors[ImGuiCol.Text] = new float[] { 0.90f, 0.90f, 0.90f, 1.00f };
        colors[ImGuiCol.TextDisabled] = new float[] { 1.00f, 1.00f, 1.00f, 1.00f };
        colors[ImGuiCol.WindowBg] = new float[] { 34.0f / 255.0f, 34.0f / 255.0f, 34.0f / 255.0f, 1.00f };
        colors[ImGuiCol.ChildBg] = new float[] { 34.0f / 255.0f, 34.0f / 255.0f, 34.0f / 255.0f, 1.00f };
        colors[ImGuiCol.PopupBg] = new float[] { 0.00f, 0.00f, 0.00f, 1.00f };
        colors[ImGuiCol.Border] = new float[] { 0.82f, 0.77f, 0.78f, 1.00f };
        colors[ImGuiCol.BorderShadow] = new float[] { 0.35f, 0.35f, 0.35f, 0.66f };
        colors[ImGuiCol.FrameBg] = new float[] { 1.00f, 1.00f, 1.00f, 0.28f };
        colors[ImGuiCol.FrameBgHovered] = new float[] { 0.68f, 0.68f, 0.68f, 0.67f };
        colors[ImGuiCol.FrameBgActive] = new float[] { 0.79f, 0.73f, 0.73f, 0.62f };
        colors[ImGuiCol.TitleBg] = new float[] { 24.0f / 255.0f, 24.0f / 255.0f, 24.0f / 255.0f, 1.00f };
        colors[ImGuiCol.TitleBgActive] = new float[] { 0.46f, 0.46f, 0.46f, 1.00f };
        colors[ImGuiCol.TitleBgCollapsed] = new float[] { 0.00f, 0.00f, 0.00f, 1.00f };
        colors[ImGuiCol.MenuBarBg] = new float[] { 0.00f, 0.00f, 0.00f, 0.80f };
        colors[ImGuiCol.ScrollbarBg] = new float[] { 0.00f, 0.00f, 0.00f, 0.60f };
        colors[ImGuiCol.ScrollbarGrab] = new float[] { 1.00f, 1.00f, 1.00f, 0.87f };
        colors[ImGuiCol.ScrollbarGrabHovered] = new float[] { 1.00f, 1.00f, 1.00f, 0.79f };
        colors[ImGuiCol.ScrollbarGrabActive] = new float[] { 0.80f, 0.50f, 0.50f, 0.40f };
        colors[ImGuiCol.CheckMark] = new float[] { 0.99f, 0.99f, 0.99f, 0.52f };
        colors[ImGuiCol.SliderGrab] = new float[] { 1.00f, 1.00f, 1.00f, 0.42f };
        colors[ImGuiCol.SliderGrabActive] = new float[] { 0.76f, 0.76f, 0.76f, 1.00f };
        colors[ImGuiCol.Button] = new float[] { 0.51f, 0.51f, 0.51f, 0.60f };
        colors[ImGuiCol.ButtonHovered] = new float[] { 0.68f, 0.68f, 0.68f, 1.00f };
        colors[ImGuiCol.ButtonActive] = new float[] { 0.67f, 0.67f, 0.67f, 1.00f };
        colors[ImGuiCol.Header] = new float[] { 0.72f, 0.72f, 0.72f, 0.54f };
        colors[ImGuiCol.HeaderHovered] = new float[] { 0.92f, 0.92f, 0.95f, 0.77f };
        colors[ImGuiCol.HeaderActive] = new float[] { 0.82f, 0.82f, 0.82f, 0.80f };
        colors[ImGuiCol.Separator] = new float[] { 0.73f, 0.73f, 0.73f, 1.00f };
        colors[ImGuiCol.SeparatorHovered] = new float[] { 0.81f, 0.81f, 0.81f, 1.00f };
        colors[ImGuiCol.SeparatorActive] = new float[] { 0.74f, 0.74f, 0.74f, 1.00f };
        colors[ImGuiCol.ResizeGrip] = new float[] { 0.80f, 0.80f, 0.80f, 0.30f };
        colors[ImGuiCol.ResizeGripHovered] = new float[] { 0.95f, 0.95f, 0.95f, 0.60f };
        colors[ImGuiCol.ResizeGripActive] = new float[] { 1.00f, 1.00f, 1.00f, 0.90f };
        colors[ImGuiCol.PlotLines] = new float[] { 1.00f, 1.00f, 1.00f, 1.00f };
        colors[ImGuiCol.PlotLinesHovered] = new float[] { 1.00f, 1.00f, 1.00f, 1.00f };
        colors[ImGuiCol.PlotHistogram] = new float[] { 1.00f, 1.00f, 1.00f, 1.00f };
        colors[ImGuiCol.PlotHistogramHovered] = new float[] { 1.00f, 1.00f, 1.00f, 1.00f };
        colors[ImGuiCol.TextSelectedBg] = new float[] { 1.00f, 1.00f, 1.00f, 0.35f };
        colors[ImGuiCol.ModalWindowDimBg] = new float[] { 0.88f, 0.88f, 0.88f, 0.35f };

        style.setColors(colors);
    }

    public static void loadFont(InputStream inputStream, int size)
    {
        ImGuiIO imGuiIO = ImGui.getIO();
        ImFontAtlas imFontAtlas = imGuiIO.getFonts();
        ImFontConfig imFontConfig = new ImFontConfig();

        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = Utils.resourceToByteBuffer(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(byteBuffer != null) {
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            imFontConfig.setGlyphRanges(imFontAtlas.getGlyphRangesCyrillic());
            imFontAtlas.addFontFromMemoryTTF(bytes, size, imFontConfig);

            imFontAtlas.build();

            bytes = null;
            byteBuffer.clear();

            byteBuffer = null;
        }

        imFontConfig.destroy();
    }

    public static void loadFont(String path, int size)
    {
        ImGuiIO imGuiIO = ImGui.getIO();
        ImFontAtlas imFontAtlas = imGuiIO.getFonts();
        ImFontConfig imFontConfig = new ImFontConfig();

        imFontConfig.setGlyphRanges(imFontAtlas.getGlyphRangesCyrillic());
        imFontAtlas.addFontFromFileTTF(path, size, imFontConfig);

        imFontAtlas.build();

        imFontConfig.destroy();
    }
}
