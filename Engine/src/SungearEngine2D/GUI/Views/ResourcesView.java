package SungearEngine2D.GUI.Views;

import Core2D.Core2D.Core2D;
import Core2D.Log.Log;
import Core2D.Utils.FileUtils;
import Core2D.Utils.ExceptionsUtils;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Project.ProjectsManager;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.io.File;
import java.io.IOException;
import java.nio.charset.CoderResult;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static SungearEngine2D.Utils.ResourcesUtils.getIconHandler;

public class ResourcesView extends View
{
    public static String currentDirectoryPath = "";

    private Vector2f iconImageSize = new Vector2f(75.0f, 75.0f);
    private Vector2f iconImageOffset = new Vector2f(90.0f, 140.0f);

    // id файла, на которую сейчас наведена мышка
    private int hoveredFileID = -1;
    // цвет файла, на которую сейчас наведена мышка
    private Vector4f hoveredFileImageColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // последний id файла, на который была нажат мышь
    private int lastRightClickedFileID = -1;

    // id текущего изменяемого имени файла
    private int currentEditableFileNameID = -1;
    // текущее изменяемое имя файла
    private ImString currentEditableFileName = new ImString();
    // сейчас имя файла изменяется?
    private boolean filenameEditing = false;

    // текущий перемещаемый файл (drag and drop)
    private File currentMovingFile;

    // нажала ли мышка по панели ресурсов (true, если нажала по панели, но не по файлу)
    private boolean mouseRightClickedOnResourcesView = false;

    // максимальная длина имени файла на строке
    private int maxFileNameInLineLength = 8;
    // максимальная длина имени файла + "..."
    private int maxFileNameLength = 27;

    public void draw()
    {
        ImGui.begin("Resources", ImGuiWindowFlags.NoMove);
        {
            showDirectoryResources();

            if(ImGui.isMouseClicked(ImGuiMouseButton.Right) && ImGui.isWindowHovered() && lastRightClickedFileID == -1) {
                mouseRightClickedOnResourcesView = true;
            }

            if(ImGui.isMouseClicked(ImGuiMouseButton.Left) && ImGui.isWindowHovered()) {
                mouseRightClickedOnResourcesView = false;
            }

            if(mouseRightClickedOnResourcesView) {
                if(ImGui.beginPopupContextWindow("File")) {
                    if(ImGui.beginMenu("Create")) {
                        if(ImGui.menuItem("Directory")) {
                            MainView.getTopToolbarView().setCurrentFileTypeNeedCreate("Directory");
                        }
                        ImGui.separator();
                        if(ImGui.menuItem("Java file")) {
                            MainView.getTopToolbarView().setCurrentFileTypeNeedCreate("Java");
                        }
                        if(ImGui.menuItem("Text file")) {
                            MainView.getTopToolbarView().setCurrentFileTypeNeedCreate("Text");
                        }
                        ImGui.endMenu();
                    }
                    ImGui.endPopup();
                }
            }

            update();
        }
        ImGui.end();
    }

    private void showDirectoryResources()
    {
        ImVec2 windowSize = ImGui.getWindowSize();
        Vector2f currentPosition = new Vector2f(5.0f, 25.0f);

        File currentDir = new File(currentDirectoryPath);

        File[] files = currentDir.listFiles();

        if(ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
            lastRightClickedFileID = -1;
        }

        if(files != null) {
            for(int i = 0; i < files.length; i++) {
                // форматированное имя файла
                String formattedFileName = getFormattedFilename(files[i].getName());

                ImVec2 textSize = new ImVec2();
                ImGui.calcTextSize(textSize, formattedFileName);

                // если id текущего редактируемого файла совпадает с текущим id
                if(currentEditableFileNameID == i) {
                    ImGui.setCursorPos(currentPosition.x + iconImageSize.x / 2.0f - 75 / 2.0f, currentPosition.y + iconImageSize.y);
                    // чтобы убрать label
                    ImGui.pushItemWidth(75);
                    ImGui.pushID("File name");
                    ImGui.inputText("", currentEditableFileName);
                    ImGui.popID();
                    ImGui.popItemWidth();
                } else {
                    ImGui.setCursorPos((currentPosition.x + iconImageSize.x / 2.0f) - (textSize.x / 2.0f), currentPosition.y + iconImageSize.y);
                    ImGui.text(formattedFileName);
                }

                // если inputText изменен
                if(ImGui.isItemDeactivatedAfterEdit() || ImGui.isItemDeactivated()) {
                    // переименовываю файл
                    File renamedFile = new File(files[i].getParentFile() + "\\" + currentEditableFileName.get());
                    boolean renamed = files[i].renameTo(renamedFile);
                    if(!renamed) {
                        Log.showErrorDialog("File " + files[i].getPath() + " can not be renamed to " + renamedFile.getPath());
                        Log.CurrentSession.println("File " + files[i].getPath() + " can not be renamed to " + renamedFile.getPath());
                    }

                    disableEditingFilename();
                }

                // если пользователь кликнул мимо при редактировании имени файл
                if(ImGui.isMouseClicked(ImGuiMouseButton.Left) && !ImGui.isItemHovered() && filenameEditing && currentEditableFileNameID == i) {
                    disableEditingFilename();
                }

                // мышка нажала по имени файла два раза, то текущее id = текущая итерация (становится редактируемым)
                if(ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left) && ImGui.isItemHovered()) {
                    enableEditingFilename(files, i);
                }

                ImGui.setCursorPos(currentPosition.x, currentPosition.y);

                drawItem(files, i, getIconHandler(files[i]), files[i].isDirectory());

                if(currentPosition.x + iconImageSize.x + iconImageOffset.x > windowSize.x) {
                    currentPosition.x = 5.0f;
                    currentPosition.y += iconImageOffset.y;
                } else {
                    currentPosition.x += iconImageOffset.x;
                }
            }
        }
    }

    private void drawItem(File[] files, int id, int textureID, boolean isDirectory)
    {
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        // image button принимает:
        // id текстуры, размер.x, размер.y, текстурные координаты (4), frame_padding = -1, цвет фона (4), обычный цвет (4)
        ImGui.pushID("Icon " + id);
        if(hoveredFileID == id) {
            ImGui.imageButton(textureID, iconImageSize.x, iconImageSize.y, 0, 0, 1, 1, -1, 1, 1, 1, 0, hoveredFileImageColor.x, hoveredFileImageColor.y, hoveredFileImageColor.z, hoveredFileImageColor.w);
        } else {
            ImGui.imageButton(textureID, iconImageSize.x, iconImageSize.y, 0, 0, 1, 1, -1, 1, 1, 1, 0, 1, 1, 1, 1);
        }
        ImGui.popID();
        ImGui.popStyleColor(3);

        if(ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("File", files[id]);
            currentMovingFile = files[id];

            ImGui.image(textureID, iconImageSize.x / 2.0f, iconImageSize.y / 2.0f, 0, 0, 1, 1, 1.0f, 1.0f, 1.0f, 0.5f);

            ImGui.endDragDropSource();
        }

        beginDragAndDropTarget(files[id]);

        if(ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left) && ImGui.isItemHovered()) {
            if(FilenameUtils.getExtension(files[id].getName()).equals("sgs")) {
                Core2D.getSceneManager2D().loadScene(files[id].getPath());
                MainView.getInspectorView().setCurrentInspectingObject(null);
            }
        }

        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left) && ImGui.isItemHovered() && hoveredFileID == id && isDirectory && !filenameEditing) {
            currentDirectoryPath = files[id].getPath();
        }

        if(ImGui.isMouseClicked(ImGuiMouseButton.Right) && ImGui.isItemHovered() && hoveredFileID == id && !filenameEditing) {
            lastRightClickedFileID = id;
        }

        if(lastRightClickedFileID == id && !filenameEditing) {
            if(ImGui.beginPopupContextWindow("File")) {
                if(ImGui.menuItem("Rename")) {
                    enableEditingFilename(files, id);
                }

                ImGui.separator();

                if(ImGui.menuItem("Delete")) {
                    try {
                        if(!isDirectory) {
                            org.apache.commons.io.FileUtils.delete(files[id]);
                        } else {
                            org.apache.commons.io.FileUtils.deleteDirectory(files[id]);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ImGui.endPopup();
            }
        }

        if(ImGui.isItemHovered() && !filenameEditing) {
            hoveredFileID = id;
            hoveredFileImageColor.x = 0.6f;
            hoveredFileImageColor.y = 0.6f;
            hoveredFileImageColor.z = 0.6f;
        } else if(!ImGui.isItemHovered() && hoveredFileID == id && !filenameEditing) {
            hoveredFileImageColor.x = 1;
            hoveredFileImageColor.y = 1;
            hoveredFileImageColor.z = 1;
        }
    }

    // начать редактирование имени файла по id
    private void enableEditingFilename(File[] files, int id)
    {
        filenameEditing = true;
        currentEditableFileNameID = id;
        currentEditableFileName.set(files[id].getName());
    }

    // закончить редактирование имени файла по id
    private void disableEditingFilename()
    {
        filenameEditing = false;
        currentEditableFileNameID = -1;
    }

    // создает файл опираясь на тип создаваемого файла
    public void createFile(String fileType, String name)
    {
        if(ProjectsManager.getCurrentProject() != null) {
            File newFile = switch (fileType) {
                case "Java" -> FileUtils.createFile(ResourcesView.currentDirectoryPath + "\\" + name + ".java");
                case "Text" -> FileUtils.createFile(ResourcesView.currentDirectoryPath + "\\" + name + ".txt");
                case "Directory" -> FileUtils.createFolder(ResourcesView.currentDirectoryPath + "\\" + name);
                default -> null;
            };
            if(newFile == null) return;
            // если тип файл - java, то создаю этот файл с заранее подготовленным кодом
            if(newFile.exists() && fileType.equals("Java")) {
                String javaFileCode =
                        "public class " + name + "\n" +
                        "{\n" +
                        "   public void update()\n" +
                        "   {\n" +
                        "       \n" +
                        "   }\n" +
                        "   \n" +
                        "   public void deltaUpdate(float deltaTime)\n" +
                        "   {\n" +
                        "       \n" +
                        "   }\n" +
                        "}";
                // создаю файл с уже заранее подготовленным кодом
                FileUtils.writeToFile(
                        newFile,
                        javaFileCode,
                        false
                );
            } else if(fileType.equals("Text")) { // если тип файла - текст, то создаю файл с надписью hello! (по приколу)
                FileUtils.writeToFile(
                        newFile,
                        "Hello!",
                        false
                );
            }
        }
    }

    public void beginDragAndDropTarget(File dest)
    {
        if(ImGui.beginDragDropTarget()) {
            if(dest.isDirectory()) {
                Object droppedObject = ImGui.acceptDragDropPayload("File", ImGuiDragDropFlags.AcceptNoDrawDefaultRect);
                if (droppedObject instanceof File) {
                    File droppedFile = (File) droppedObject;

                    if (!droppedFile.equals(dest) && !droppedFile.getParentFile().getPath().equals(dest.getPath())) {
                        try {
                            if (droppedFile.isFile()) {
                                org.apache.commons.io.FileUtils.moveFile(droppedFile, new File(dest.getPath() + "\\" + droppedFile.getName()));
                            } else {
                                org.apache.commons.io.FileUtils.moveDirectory(droppedFile, new File(dest.getPath() + "\\" + droppedFile.getName()));
                            }
                        } catch (IOException e) {
                            Log.showErrorDialog("File " + droppedFile.getPath() + " can not be moved to directory " + dest.getPath() + "! Check log file.");
                            Log.CurrentSession.println(ExceptionsUtils.toString(e));
                        }
                    }
                }
            }
            ImGui.endDragDropTarget();
        }
    }

    private String getFormattedFilename(String filename)
    {
        List<String> result = Pattern.compile(".{1," + maxFileNameInLineLength + "}")
                .matcher(filename)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());

        StringBuilder formattedFileName = new StringBuilder();
        int currentCharsNum = 0;
        for(int k = 0; k < result.size(); k++) {
            currentCharsNum += result.get(k).length();
            if(currentCharsNum > maxFileNameLength - 3) {
                formattedFileName.append("...");
                break;
            }
            formattedFileName.append(result.get(k)).append("\n");
        }

        return formattedFileName.toString();
    }

    public File getCurrentMovingFile() { return currentMovingFile; }
    public void setCurrentMovingFile(File currentMovingFile) { this.currentMovingFile = currentMovingFile; }
}
