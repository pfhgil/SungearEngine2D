package SungearEngine2D.GUI.Windows.FileChooserWindow;

import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTreeNodeFlags;

import java.io.File;

public class FileChooserWindow extends DialogWindow
{
    public enum FileChooserMode
    {
        CHOOSE_FILE,
        CHOOSE_DIRECTORY
    }

    private String currentChosenFilePath = "";
    private String initialDirectoryPath = "";

    private FileChooserWindowCallback fileChooserWindowCallback;

    private FileChooserMode fileChooserMode;

    private boolean needToScroll = true;

    public FileChooserWindow(FileChooserMode fileChooserMode)
    {
        super("Choose directory", "Cancel", "Choose");
        this.fileChooserMode = fileChooserMode;
        create();
    }

    public FileChooserWindow(String initialDirectoryPath, FileChooserMode fileChooserMode)
    {
        super("Choose directory", "Cancel", "Choose");
        this.initialDirectoryPath = initialDirectoryPath;
        this.fileChooserMode = fileChooserMode;
        create();
    }

    private void create()
    {
        File f = new File("");
        initialDirectoryPath = f.getAbsolutePath();

        setDialogWindowCallback(new DialogWindowCallback() {
            @Override
            public void onDraw() {
                ImGui.text("Chosen directory path: " + currentChosenFilePath);
                ImGui.beginChild("Browser", getCurrentWindowSize().x - 17, getCurrentWindowSize().y - 75, true);

                File[] disks = File.listRoots();
                for(File disk : disks) {
                    showDirectory(disk, true);
                }

                ImGui.endChild();
            }

            @Override
            public void onMiddleButtonClicked() {

            }

            @Override
            public void onLeftButtonClicked() {
                fileChooserWindowCallback.onLeftButtonClicked();
                setActive(false);
                needToScroll = true;
            }

            @Override
            public void onRightButtonClicked() {
                fileChooserWindowCallback.onRightButtonClicked(currentChosenFilePath);
                setActive(false);
                needToScroll = true;
            }
        });
    }

    private void showDirectory(File dir, boolean isDisk)
    {
        if(initialDirectoryPath.contains(dir.getPath())) {
            if(needToScroll) {
                ImGui.setNextItemOpen(true);
                ImGui.setScrollHereY();
            }
            if(initialDirectoryPath.equals(dir.getPath())) {
                needToScroll = false;
            }
        }

        boolean clicked;
        if(isDisk) {
            clicked = ImGui.treeNodeEx(dir.getPath(), ImGuiTreeNodeFlags.OpenOnDoubleClick | ImGuiTreeNodeFlags.OpenOnArrow);
        } else {
            clicked = ImGui.treeNodeEx(dir.getName(), ImGuiTreeNodeFlags.OpenOnDoubleClick | ImGuiTreeNodeFlags.OpenOnArrow);
        }
        if(ImGui.isItemClicked()) {
            if(fileChooserMode == FileChooserMode.CHOOSE_DIRECTORY) {
                currentChosenFilePath = dir.getPath();
            }
        }
        if(clicked) {
            File[] dirs = dir.listFiles();

            if(dirs != null) {
                for (File dir0 : dirs) {
                    if (dir0.isDirectory()) {
                        showDirectory(dir0, false);
                    } else if(dir0.isFile() && fileChooserMode == FileChooserMode.CHOOSE_FILE) {
                        showFile(dir0);
                    }
                }
            }

            ImGui.treePop();
        }
    }

    private void showFile(File file)
    {
        if(initialDirectoryPath.contains(file.getPath())) {
            if(needToScroll) {
                ImGui.setScrollHereY();
            }
            if (initialDirectoryPath.equals(file.getPath())) {
                needToScroll = false;
            }
        }

        ImGui.treeNodeEx(file.getName(), ImGuiTreeNodeFlags.Bullet | ImGuiTreeNodeFlags.NoTreePushOnOpen);
        boolean opened = ImGui.isItemHovered() && ImGui.isMouseClicked(ImGuiMouseButton.Left);

        if(opened) {
            currentChosenFilePath = file.getPath();
        }
    }

    public void draw()
    {
        super.draw();
    }

    public FileChooserMode getFileChooserMode() { return fileChooserMode; }
    public void setFileChooserMode(FileChooserMode fileChooserMode) { this.fileChooserMode = fileChooserMode; }

    public void setDirectoryChooserWindowCallback(FileChooserWindowCallback fileChooserWindowCallback) { this.fileChooserWindowCallback = fileChooserWindowCallback; }
}
