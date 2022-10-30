package SungearEngine2D.GUI.Views.EditorView;

import Core2D.Camera2D.CamerasManager;
import Core2D.Drawable.Object2D;
import Core2D.Log.Log;
import Core2D.Prefab.Prefab;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.Scene2DStoredValues;
import Core2D.Scene2D.SceneManager;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.WrappedObject;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.EngineSettings;
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
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static Core2D.Scene2D.SceneManager.currentSceneManager;
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

    private boolean canOpenScene2D = true;
    private Timer openScene2DTimer = new Timer(new TimerCallback() {
        @Override
        public void deltaUpdate(float v) {

        }

        @Override
        public void update() {
            System.out.println("ddsd");
            canOpenScene2D = true;
        }
    }, 3.0f, true);

    public ResourcesView()
    {
        init();
    }

    @Override
    public void init()
    {
        openScene2DTimer.start();
    }

    public void draw()
    {
        if(!canOpenScene2D) {
            openScene2DTimer.startFrame();
        }
        
        ImGui.begin("Resources", ImGuiWindowFlags.NoMove);
        {
            ImVec2 windowSize = ImGui.getWindowSize();
            ImGui.invisibleButton("ResourcesDropTarget", windowSize.x, windowSize.y);
            ImGui.setItemAllowOverlap();

            if(ImGui.beginDragDropTarget()) {
                Object object = ImGui.acceptDragDropPayload("SceneWrappedObject");
                if(object instanceof WrappedObject && ((WrappedObject) object).getObject() instanceof Object2D) {
                    Object2D object2D = (Object2D) ((WrappedObject) object).getObject();
                    Prefab prefab = new Prefab(object2D);
                    prefab.save(currentDirectoryPath + "\\" + object2D.getName() + ".sgopref");

                    /*
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .registerTypeAdapter(WrappedObject.class, new WrappedObjectDeserializer())
                            .registerTypeAdapter(Component.class, new ComponentDeserializer())
                            .registerTypeAdapter(Object2D.class, new Object2DDeserializer())
                            .create();
                    String serializedObject2D = gson.toJson(object2D);
                    FileUtils.serializeObject(currentDirectoryPath + "\\" + object2D.getName() + ".sgopref", serializedObject2D);

                     */
                }
                ImGui.endDragDropTarget();
            }


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
                            ViewsManager.getTopToolbarView().setCurrentFileTypeNeedCreate("Directory");
                        }
                        ImGui.separator();
                        if(ImGui.menuItem("Java file")) {
                            ViewsManager.getTopToolbarView().setCurrentFileTypeNeedCreate("Java");
                        }
                        if(ImGui.menuItem("Text file")) {
                            ViewsManager.getTopToolbarView().setCurrentFileTypeNeedCreate("Text");
                        }
                        ImGui.separator();
                        if(ImGui.menuItem("Scene2D")) {
                            ViewsManager.getTopToolbarView().showCreateScene2DDialog();
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
                        Log.CurrentSession.println("File " + files[i].getPath() + " can not be renamed to " + renamedFile.getPath(), Log.MessageType.ERROR);
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
            ViewsManager.getInspectorView().droppingFile = files[id];
            currentMovingFile = files[id];

            ImGui.image(textureID, iconImageSize.x / 2.0f, iconImageSize.y / 2.0f, 0, 0, 1, 1, 1.0f, 1.0f, 1.0f, 0.5f);

            ImGui.endDragDropSource();
        }

        beginDragAndDropTarget(files[id]);

        if(ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left) && ImGui.isItemHovered()) {
            if(FilenameUtils.getExtension(files[id].getName()).equals("sgs")) {
                if(canOpenScene2D) {
                    System.out.println("dfdfsdfsd");
                    Scene2D scene2D = currentSceneManager.loadSceneAsCurrent(files[id].getPath());
                    if(scene2D != null) {
                        canOpenScene2D = false;
                        EngineSettings.Playmode.active = false;
                        EngineSettings.Playmode.paused = false;
                        CamerasManager.setMainCamera2D(Main.getMainCamera2D());
                        ViewsManager.getInspectorView().setCurrentInspectingObject(null);
                        if (currentSceneManager != null && currentSceneManager.getCurrentScene2D() != null) {
                            currentSceneManager.getCurrentScene2D().getPhysicsWorld().simulatePhysics = false;
                            currentSceneManager.getCurrentScene2D().getScriptSystem().runScripts = false;
                        }
                        if (!currentSceneManager.isScene2DExists(scene2D.getName())) {
                            Log.showWarningChooseDialog("Scene2D " + scene2D.getName() + " not found in SceneManager!\nWould you like to add this scene2D in SceneManager?",
                                    "Yes",
                                    "No",
                                    new Log.DialogCallback() {
                                        @Override
                                        public void firstButtonClicked() {
                                            Scene2DStoredValues storedValues = new Scene2DStoredValues();
                                            storedValues.path = files[id].getPath();
                                            currentSceneManager.getScene2DStoredValues().add(storedValues);
                                            //scene2D.setScenePath(files[id].getPath());
                                        }

                                        @Override
                                        public void secondButtonClicked() {

                                        }

                                        @Override
                                        public void thirdButtonClicked() {

                                        }
                                    });
                        }
                        //currentSceneManager.setCurrentScene2D(scene2D.getName());
                        currentSceneManager.getCurrentScene2D().setScenePath(files[id].getPath());

                        scene2D.getPhysicsWorld().simulatePhysics = false;
                        scene2D.getScriptSystem().runScripts = false;

                        CamerasManager.setMainCamera2D((Main.getMainCamera2D()));
                    }
                }
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
                String fileExtension = FilenameUtils.getExtension(files[id].getName());
                String fileBaseName = FilenameUtils.getBaseName(files[id].getName());
                if(ImGui.menuItem("Rename")) {
                    enableEditingFilename(files, id);
                }

                ImGui.separator();

                if(ImGui.menuItem("Delete")) {
                    try {
                        if(!isDirectory) {
                            if(fileExtension.equals("sgs")) {
                                if(currentSceneManager.getCurrentScene2D() != null && currentSceneManager.getCurrentScene2D().getScenePath().equals(files[id].getPath())) {
                                    currentSceneManager.getCurrentScene2D().destroy();
                                    SceneManager.currentSceneManager.setCurrentScene2D((Scene2D) null);
                                }
                                currentSceneManager.getScene2DStoredValues().removeIf(p -> p.path.equals(files[id].getPath()));
                                ProjectsManager.getCurrentProject().saveProject();
                            }
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
                        "import Core2D.Object2D.*;\n" +
                        "\n" +
                        "public class " + name + "\n" +
                        "{\n" +
                        "    public void update()\n" +
                        "    {\n" +
                        "        \n" +
                        "    }\n" +
                        "    \n" +
                        "    public void deltaUpdate(float deltaTime)\n" +
                        "    {\n" +
                        "        \n" +
                        "    }\n" +
                        "    \n" +
                        "    public void collider2DEnter(Object2D otherObj)\n" +
                        "    {\n" +
                        "        \n" +
                        "    }\n" +
                        "    \n" +
                        "    public void collider2DExit(Object2D otherObj)\n" +
                        "    {\n" +
                        "        \n" +
                        "    }\n" +
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
                            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
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
