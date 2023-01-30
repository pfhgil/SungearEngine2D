package SungearEngine2D.GUI.Views.DebuggerView;

import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.Utils.Debugger;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class DebuggerView extends View
{
    public boolean active = false;

    private int dockspaceID;

    public DebuggerView()
    {
        init();
    }

    @Override
    public void init()
    {
        dockspaceID = ImGui.getID("DebuggerViewDockspace");
    }

    public void draw()
    {
        if(active) {
            ImBoolean opened = new ImBoolean(true);

            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0F);

            ImGui.begin("Debugger", opened);
            ImGui.popStyleVar(3);

            ImGui.dockSpace(dockspaceID);

            if(!opened.get()) {
                active = false;
            }

            ImGui.begin("CPU");
            ImGui.end();

            ImGui.begin("GPU");
            ImGui.text("GPU total memory: " + (Debugger.GPU.getGPUTotalMemInKB() / 1024.0f) + " MB (" + Debugger.GPU.getGPUTotalMemInKB() + " KB)");
            ImGui.text("GPU available memory: " + (Debugger.GPU.getGPUCurrentMemAvailableInKB() / 1024.0f) + " MB (" + Debugger.GPU.getGPUCurrentMemAvailableInKB() + " KB)");
            ImGui.text("GPU usage: " + Debugger.GPU.getGPUUsagePercentage() + "% (" + (Debugger.GPU.getGPUUsageInKB() / 1024.0f) + " MB (" + Debugger.GPU.getGPUUsageInKB() + " KB))");
            ImGui.end();

            ImGui.begin("Memory");

            ImGui.text("Current heap size: " + Debugger.Memory.getCurrentHeapSizeInMB() + " MB");
            ImGui.text("Max heap size: " + Debugger.Memory.getMaxHeapSizeInMB() + " MB");
            ImGui.text("Free heap size: " + Debugger.Memory.getFreeHeapSizeInMB() + " MB");
            ImGui.text("Heap memory usage: " + Debugger.Memory.getHeapUsageInMB() + " MB");

            ImGui.end();

            ImGui.end();
        }
    }
}
