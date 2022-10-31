package SungearEngine2D.GUI.Views.Other;

import Core2D.Core2D.Core2D;
import Core2D.Project.ProjectsManager;
import SungearEngine2D.GUI.Views.View;
import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class EngineSettingsView extends View
{
    private String[] allBars = new String[] {
            "Graphics"
    };

    private String currentBarType = allBars[0];

    public boolean active;

    private int dockspaceID;

    public EngineSettingsView() { init(); }

    @Override
    public void init()
    {
        dockspaceID = ImGui.getID("EngineSettingsViewDockspace");
    }

    public void draw()
    {
        if(active) {
            Vector2i glfwWindowSize = Core2D.getWindow().getSize();
            Vector2f imGuiWindowSize = new Vector2f(700.0f, 425.0f);
            ImGui.setNextWindowPos(glfwWindowSize.x / 2.0f - imGuiWindowSize.x / 2.0f, glfwWindowSize.y / 2.0f - imGuiWindowSize.y / 2.0f, ImGuiCond.Once);
            ImGui.setNextWindowSize(imGuiWindowSize.x, imGuiWindowSize.y, ImGuiCond.Once);

            ImBoolean opened = new ImBoolean(true);

            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
            ImGui.begin("Engine settings##Engine", opened, ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse);

            ImGui.dockSpace(dockspaceID);

            if(!opened.get()) {
                active = false;
            }

            ImGuiWindowClass windowClass = new ImGuiWindowClass();
            windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoTabBar);
            windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoResize);

            ImGui.setNextWindowClass(windowClass);
            ImGui.begin("SideTabs##Engine", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0.0f, 0.0f);
            for(String barType : allBars) {
                if (!barType.equals(currentBarType)) {
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.0f, 0.0f, 0.0f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);
                } else {
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.5f, 0.5f, 1.0f, 1.0f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.5f, 0.5f, 1.0f, 1.0f);
                }
                ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);

                int toPop = 3;

                if(barType.equals(currentBarType)) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0.5f, 0.5f, 1.0f, 1.0f);
                    toPop++;
                }
                if(ImGui.button(barType, 150.0f, 25.0f)) {
                    currentBarType = barType;
                }
                ImGui.popStyleColor(toPop);
            }
            ImGui.popStyleVar(1);

            ImGui.end();

            ImGui.setNextWindowClass(windowClass);
            ImGui.begin("MiniMenu##Engine", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

            ImVec2 miniMenuWindowSize = ImGui.getWindowSize();
            //if(!ProjectsManager.getCurrentProject().getProjectSettings().isSaved()) {
                ImGui.setCursorPos(miniMenuWindowSize.x - 185.0f, 14.0f);
                if(ImGui.button("OK", 50.0f, 25.0f)) {
                    //ProjectsManager.getCurrentProject().getProjectSettings().saveSettings(ProjectsManager.getCurrentProject().getProjectSettingsPath());
                    active = false;
                }
                ImGui.sameLine();
                if(ImGui.button("Apply", 50.0f, 25.0f)) {
                    //ProjectsManager.getCurrentProject().getProjectSettings().saveSettings(ProjectsManager.getCurrentProject().getProjectSettingsPath());
                }
                ImGui.sameLine();
            //} else {
               // ImGui.setCursorPos(miniMenuWindowSize.x - 67.0f, 14.0f);
            //}
            if(ImGui.button("Cancel", 50.0f, 25.0f)) {
                active = false;
                //ProjectsManager.getCurrentProject().getProjectSettings().loadSettings(ProjectsManager.getCurrentProject().getProjectSettingsPath());
            }

            ImGui.setNextWindowClass(windowClass);
            ImGui.begin("MainSettingsView##Engine", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);

            ImGui.setCursorPos(9.0f, 5.0f);

            if(currentBarType.equals("Project")) {

            }

            ImGui.end();

            ImGui.end();

            ImGui.end();
            ImGui.popStyleVar(1);

            update();
        }
    }
}
