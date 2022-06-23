package SungearEngine2D.GUI;

import Core2D.Core2D.Core2D;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import imgui.internal.ImGuiContext;

public class Renderer
{
    private ImGuiContext imGuiContext;
    private ImGuiImplGlfw imGuiImplGlfw;
    private ImGuiImplGl3 imGuiImplGl3;

    public Renderer()
    {
        imGuiContext = ImGui.createContext();
        ImGui.setCurrentContext(imGuiContext);

        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGlfw.init(Core2D.getWindow().getWindow(), true);

        Styles.applyDarkLightStyle();

        Styles.loadFont(Core2D.class.getResourceAsStream("/data/fonts/calibrib.ttf"), 12);

        imGuiImplGl3 = new ImGuiImplGl3();
        imGuiImplGl3.init("#version 330 core");
    }

    public void startFrame()
    {
        // начало отрисовки интерфейса
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame()
    {
        // конец отрисовки
        ImGui.endFrame();
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }
}
