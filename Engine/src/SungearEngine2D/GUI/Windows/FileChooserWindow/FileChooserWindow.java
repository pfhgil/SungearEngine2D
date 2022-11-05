package SungearEngine2D.GUI.Windows.FileChooserWindow;

import Core2D.Log.Log;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;

import java.io.File;

public class FileChooserWindow extends DialogWindow
{
    public enum FileChooserMode
    {
        CHOOSE_FILE,
        CHOOSE_DIRECTORY,
        CREATE_NEW_FILE,
        CREATE_NEW_DIRECTORY
    }

    private String currentChosenFilePath = "";
    private String initialDirectoryPath = "";

    private ImString output = new ImString();
    private FileChooserWindowCallback fileChooserWindowCallback;

    private FileChooserMode fileChooserMode;

    private boolean needToScroll = true;
    private DialogWindow activeWindow;
    public void setActiveWindow(DialogWindow a){
        activeWindow = a;
    }
    public FileChooserWindow(FileChooserMode fileChooserMode)
    {
        super("Choose directory", "Cancel", "Choose");
        this.fileChooserMode = fileChooserMode;
        create();
    }
    public FileChooserWindow setOutput(ImString val){
        output = val; return this;
    }

    public FileChooserWindow(String initialDirectoryPath, FileChooserMode fileChooserMode)
    {
        super("Choose directory", "Cancel", "Choose");
        this.initialDirectoryPath = initialDirectoryPath;
        this.fileChooserMode = fileChooserMode;
        create();
    }
    private void close(){
        if (activeWindow != null) {
            activeWindow.setActive(true);
        }
        setActive(false);
    }

    private void create()
    {
        File f = new File("");
        initialDirectoryPath = f.getAbsolutePath();

        setDialogWindowCallback(new DialogWindowCallback() {
            @Override
            public void onDraw() {

                if (fileChooserMode == FileChooserMode.CREATE_NEW_FILE){
                    ImGui.text("Chosen directory path: " + currentChosenFilePath + File.separator + newFileName.get());
                    ImGui.text("File name:"); ImGui.sameLine();
                    ImGui.inputText("##", newFileName);
                    ImGui.beginChild("Browser", getCurrentWindowSize().x - 17, getCurrentWindowSize().y - 110, true);
                } else{
                    ImGui.text("Chosen directory path: " + currentChosenFilePath);
                    ImGui.beginChild("Browser", getCurrentWindowSize().x - 17, getCurrentWindowSize().y - 75, true);
                }


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
                if (fileChooserWindowCallback != null) {
                    fileChooserWindowCallback.onLeftButtonClicked();
                }
                close();
            }

            @Override
            public void onRightButtonClicked() {
                if (fileChooserWindowCallback != null) {
                    fileChooserWindowCallback.onRightButtonClicked(currentChosenFilePath +
                            (fileChooserMode == FileChooserMode.CREATE_NEW_FILE ? newFileName.get() : ""));
                }
                output.set(currentChosenFilePath +
                        (fileChooserMode == FileChooserMode.CREATE_NEW_FILE ? newFileName.get() : ""));
                close();
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
            if(fileChooserMode == FileChooserMode.CHOOSE_DIRECTORY || fileChooserMode == FileChooserMode.CREATE_NEW_FILE) {
                currentChosenFilePath = dir.getPath();
            }
        }
        if(clicked) {
            File[] dirs = dir.listFiles();

            if(dirs != null) {
                for (File dir0 : dirs) {
                    if (dir0.isDirectory()) {
                        showDirectory(dir0, false);
                    } else if(dir0.isFile() && (fileChooserMode == FileChooserMode.CHOOSE_FILE ||
                            fileChooserMode == FileChooserMode.CREATE_NEW_FILE)) {
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
            if (fileChooserMode == FileChooserMode.CREATE_NEW_FILE) {
                currentChosenFilePath = file.getParentFile().getParent();
                newFileName.set(file.getName());
            }else{
                currentChosenFilePath = file.getPath();
            }
        }
    }
    private ImString newFileName = new ImString();
    public void setNewFileName(String value){ newFileName.set(value); }
    public void draw()
    {
        super.draw();
    }

    public FileChooserMode getFileChooserMode() { return fileChooserMode; }
    public void setFileChooserMode(FileChooserMode fileChooserMode) { this.fileChooserMode = fileChooserMode; }

    public void setDirectoryChooserWindowCallback(FileChooserWindowCallback fileChooserWindowCallback) { this.fileChooserWindowCallback = fileChooserWindowCallback; }
}
