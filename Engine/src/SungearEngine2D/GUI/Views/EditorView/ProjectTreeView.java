package SungearEngine2D.GUI.Views.EditorView;

import Core2D.Project.Project;
import Core2D.Project.ProjectsManager;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Resources;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

import java.io.File;

import static SungearEngine2D.Utils.ResourcesUtils.getIconHandler;

public class ProjectTreeView extends View
{
    private Vector2f iconImageSize = new Vector2f(125.0f, 125.0f);

    public void draw()
    {
        ImGui.begin("Project tree", ImGuiWindowFlags.NoMove);
        {
            Project currentProject = ProjectsManager.getCurrentProject();

            if(currentProject != null) {
                showDirectory(new File(currentProject.getProjectPath()));
            }

            update();
        }
        ImGui.end();
    }

    private void showDirectory(File dir)
    {
        Vector2f lastCursorPos = new Vector2f(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() - 4.5f);
        ImGui.image(getIconHandler(dir), 20, 20);
        ImGui.sameLine();

        if(ResourcesView.currentDirectoryPath.equals(dir.getPath())) {
            ImGui.setNextItemOpen(true, ImGuiCond.Once);
        }
        ImGui.sameLine();
        ImGui.setCursorPos(lastCursorPos.x, lastCursorPos.y);

        ImGui.indent(ImGui.getTreeNodeToLabelSpacing());
        boolean opened = ImGui.treeNodeEx(dir.getName(), ImGuiTreeNodeFlags.OpenOnArrow);
        ImGui.unindent(ImGui.getTreeNodeToLabelSpacing());

        if(ImGui.isMouseClicked(ImGuiMouseButton.Left) && ImGui.isItemHovered()) {
            ResourcesView.currentDirectoryPath = dir.getPath();
        }

        if(ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("File", dir);

            ImGui.image(Resources.Textures.Icons.directoryIcon.getHandler(), iconImageSize.x / 2.0f, iconImageSize.y / 2.0f, 0, 0, 1, 1, 1.0f, 1.0f, 1.0f, 0.5f);

            ImGui.endDragDropSource();
        }

        ViewsManager.getResourcesView().beginDragAndDropTarget(dir);

        if(opened) {
            File[] files = dir.listFiles();

            if(files != null) {
                for(File file : files) {
                    if(file.isDirectory()) {
                        showDirectory(file);
                    } else {
                        showFile(file);
                    }
                }
            }

            ImGui.treePop();
        }
    }

    private void showFile(File file)
    {
        Vector2f lastCursorPos = new Vector2f(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() - 4.5f);
        ImGui.image(getIconHandler(file), 20, 20);

        ImGui.sameLine();
        ImGui.setCursorPos(lastCursorPos.x, lastCursorPos.y);

        ImGui.indent(ImGui.getTreeNodeToLabelSpacing());
        boolean opened = ImGui.treeNodeEx(file.getName(), ImGuiTreeNodeFlags.OpenOnDoubleClick | ImGuiTreeNodeFlags.Bullet);
        ImGui.unindent(ImGui.getTreeNodeToLabelSpacing());

        if(ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("File", file);

            ImGui.image(getIconHandler(file), iconImageSize.x / 2.0f, iconImageSize.y / 2.0f, 0, 0, 1, 1, 1.0f, 1.0f, 1.0f, 0.5f);

            ImGui.endDragDropSource();
        }

        if(opened) {
            ImGui.treePop();
        }
    }
}
