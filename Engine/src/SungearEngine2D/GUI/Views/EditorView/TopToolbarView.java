package SungearEngine2D.GUI.Views.EditorView;

import Core2D.ECS.Entity;
import Core2D.Input.PC.Keyboard;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.Scene2DStoredValues;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import SungearEngine2D.Builder.Builder;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.GUI.Windows.FileChooserWindow.FileChooserWindow;
import SungearEngine2D.GUI.Windows.FileChooserWindow.FileChooserWindowCallback;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Utils.AppData.AppDataManager;
import SungearEngine2D.Utils.AppData.UserSettings;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.Game;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class TopToolbarView
{
    private String currentAction = "";

    private ImString projectName = new ImString();
    private ImString projectPath = new ImString();

    private ImString newFileName = new ImString();

    private ImString newSceneName = new ImString();

    private ImString buildOutPath = new ImString(AppDataManager.getSettings().lastBuildOutPath);

    // текущий тип файла, который нужно создать
    private String currentFileTypeNeedCreate = "";

    private FileChooserWindow fileChooserWindow;

    private DialogWindow dialogWindow;

    public TopToolbarView()
    {
        init();
    }

    public void init()
    {
        dialogWindow = new DialogWindow("", "", "");
        dialogWindow.setLeftButtonText("Close");

        fileChooserWindow = new FileChooserWindow(FileChooserWindow.FileChooserMode.CHOOSE_DIRECTORY);
        fileChooserWindow.setActive(false);
    }

    public void draw()
    {
        showFileCreateDialog();

        ImGui.beginMainMenuBar();
        {
            if(ImGui.beginMenu("File")) {
                if(ImGui.beginMenu("New...")) {
                    if(ImGui.menuItem("Project")) {
                        dialogWindow.setWindowName("New project");
                        dialogWindow.setRightButtonText("Create");
                        dialogWindow.setActive(true);
                        dialogWindow.setWindowSize(new Vector2f(525.0f, dialogWindow.getWindowSize().y));
                        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                            @Override
                            public void onDraw() {
                                ImGui.inputText("Project name", projectName);
                                ImGui.inputText("Project path", projectPath);
                                ImGui.sameLine();
                                if(ImGui.button("Browse...")) {
                                    dialogWindow.setActive(false);
                                    fileChooserWindow.setFileChooserMode(FileChooserWindow.FileChooserMode.CHOOSE_DIRECTORY);
                                    fileChooserWindow.setOutput(projectPath);
                                    fileChooserWindow.setActiveWindow(dialogWindow);
                                    fileChooserWindow.setActive(true);
                                }
                            }

                            @Override
                            public void onMiddleButtonClicked() {

                            }

                            @Override
                            public void onLeftButtonClicked() {
                                projectName.set("");
                                projectPath.set("");

                                dialogWindow.setActive(false);
                                currentAction = "";
                            }

                            @Override
                            public void onRightButtonClicked() {
                                ProjectsManager.createProject(projectPath.get(), projectName.get());

                                projectName.set("");
                                projectPath.set("");

                                dialogWindow.setActive(false);
                                currentAction = "";
                            }
                        });

                        currentAction = "File/New/Project";
                    }

                    if(ImGui.menuItem("Directory")) {
                        currentFileTypeNeedCreate = "Directory";
                        showFileCreateDialog();
                    }

                    ImGui.separator();

                    if(ImGui.beginMenu("Java file")) {
                        if(ImGui.menuItem("Component")) {
                            currentFileTypeNeedCreate = "Java.Component";
                            showFileCreateDialog();
                        }
                        if(ImGui.menuItem("System")) {
                            currentFileTypeNeedCreate = "Java.System";
                            showFileCreateDialog();
                        }
                        ImGui.endMenu();
                    }

                    if(ImGui.menuItem("Text file")) {
                        currentFileTypeNeedCreate = "Text";
                        showFileCreateDialog();
                    }

                    ImGui.endMenu();
                }

                if(ImGui.beginMenu("Open")) {
                    if(ImGui.menuItem("Project")) {
                        dialogWindow.setWindowName("Open project");
                        dialogWindow.setRightButtonText("Open");
                        dialogWindow.setActive(true);
                        dialogWindow.setWindowSize(new Vector2f(525.0f, dialogWindow.getWindowSize().y));
                        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                            @Override
                            public void onDraw() {
                                ImGui.inputText("Project file path", projectPath);
                                if(!ImGui.isItemActive() && Keyboard.keyReleased(GLFW.GLFW_KEY_ENTER)) {
                                    onRightButtonClicked();
                                }
                                ImGui.sameLine();
                                if(ImGui.button("Browse...")) {
                                    dialogWindow.setActive(false);
                                    fileChooserWindow.setFileChooserMode(FileChooserWindow.FileChooserMode.CHOOSE_FILE);
                                    fileChooserWindow.setOutput(projectPath);
                                    fileChooserWindow.setActiveWindow(dialogWindow);
                                    fileChooserWindow.setActive(true);
                                }
                            }

                            @Override
                            public void onMiddleButtonClicked() {

                            }

                            @Override
                            public void onLeftButtonClicked() {
                                projectPath.set("");

                                dialogWindow.setActive(false);
                                currentAction = "";
                            }

                            @Override
                            public void onRightButtonClicked() {
                                ProjectsManager.loadProject(projectPath.get());
                                UserSettings.instance.addLastProject(projectPath.get());

                                projectPath.set("");

                                dialogWindow.setActive(false);
                                currentAction = "";
                            }
                        });

                        currentAction = "File/Open/Project";
                    }
                    if (ImGui.beginMenu("Last projects")) {
                        int size = UserSettings.instance.lastProjects.size();
                         for(int i = 0; i < size; i++) {
                             String projectPath = UserSettings.instance.lastProjects.get(i);
                             if (ImGui.menuItem(projectPath)) {
                                 if(!new File(projectPath).exists()) {
                                     Log.CurrentSession.println("It is not possible to load the project on the path \"" + projectPath + "\". Such a file does not exist.", Log.MessageType.ERROR);
                                     Log.showErrorDialog("It is not possible to load the project. See log for more info.");
                                     UserSettings.instance.lastProjects.remove(projectPath);
                                 } else {
                                     ProjectsManager.loadProject(projectPath);
                                     UserSettings.instance.addLastProject(projectPath);
                                 }
                             }
                        }
                        ImGui.endMenu();
                    }


                    ImGui.endMenu();
                }

                if(ProjectsManager.getCurrentProject() != null) {
                    if (ImGui.menuItem("Close project")) {
                        // сделать
                    }

                    if (ImGui.menuItem("Project settings")) {
                        ViewsManager.getProjectSettingsView().active = true;
                    }

                    if (ImGui.menuItem("Build project")) {
                        dialogWindow.setWindowName("Build project");
                        dialogWindow.setRightButtonText("Build");
                        dialogWindow.setActive(true);
                        dialogWindow.setWindowSize(new Vector2f(525.0f, dialogWindow.getWindowSize().y));
                        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                            @Override
                            public void onDraw() {
                                ImGui.inputText("##", buildOutPath);
                                AppDataManager.getSettings().lastBuildOutPath = buildOutPath.get();

                                ImGui.sameLine();
                                if (ImGui.button("Browse...")){
                                    dialogWindow.setActive(false);
                                    fileChooserWindow.setOutput(buildOutPath);
                                    fileChooserWindow.setActiveWindow(dialogWindow);
                                    fileChooserWindow.setFileChooserMode(FileChooserWindow.FileChooserMode.CHOOSE_DIRECTORY);
                                    fileChooserWindow.setActive(true);
                                }

                                ImString buildNameString = new ImString(AppDataManager.getSettings().lastBuildName, 256);
                                if(ImGui.inputText("Build name", buildNameString)) {
                                    AppDataManager.getSettings().lastBuildName = buildNameString.get();
                                }

                                ImGui.text("Scenes to build:");
                                ImGui.beginChild("ChooseScenes2DToAdd", dialogWindow.getWindowSize().x, dialogWindow.getWindowSize().y / 3.0f);

                                /*
                                for(Scene2D scene2D : SceneManager.currentSceneManager.getScenes()) {
                                    if(ImGui.checkbox(scene2D.getName(), scene2D.inBuild)) {
                                        scene2D.inBuild = !scene2D.inBuild;
                                    }
                                }

                                 */
                                for (Scene2DStoredValues storedValues : SceneManager.currentSceneManager.getScene2DStoredValues()) {
                                    if (ImGui.checkbox(FilenameUtils.getBaseName(new File(storedValues.path).getName()), storedValues.inBuild)) {
                                        storedValues.inBuild = !storedValues.inBuild;
                                    }
                                }

                                ImGui.endChild();

                            }

                            @Override
                            public void onMiddleButtonClicked() {

                            }

                            @Override
                            public void onLeftButtonClicked() {
                                dialogWindow.setActive(false);
                                currentAction = "";
                            }

                            @Override
                            public void onRightButtonClicked() {
                                boolean hasMainScene2D = SceneManager.currentSceneManager.getScene2DStoredValues()
                                        .stream()
                                        .anyMatch(s -> s.isMainScene2D);
                                if (hasMainScene2D) {
                                    AppDataManager.getSettings().save();
                                    Log.showWarningChooseDialog("Warning! The directory will be completely cleared.",
                                            "Continue",
                                            "Cancel",
                                            new Log.DialogCallback() {
                                                @Override
                                                public void firstButtonClicked() {
                                                    File lastBuildOutFile = new File(AppDataManager.getSettings().lastBuildOutPath);
                                                    if(lastBuildOutFile.exists()) {
                                                        try {
                                                            org.apache.commons.io.FileUtils.cleanDirectory(new File(AppDataManager.getSettings().lastBuildOutPath));
                                                        } catch (IOException e) {
                                                            Log.CurrentSession.println(ExceptionsUtils.toString(e) + "\n\tCaused by: " + e.getCause(), Log.MessageType.ERROR);
                                                        }
                                                        Builder.startBuild();
                                                        dialogWindow.setActive(false);
                                                        currentAction = "";
                                                    } else {
                                                        Log.showWarningChooseDialog("The directory does not exist. Do you want to create it and continue build?",
                                                                "Yes",
                                                                "No",
                                                                new Log.DialogCallback() {
                                                                    @Override
                                                                    public void firstButtonClicked() {
                                                                        FileUtils.createFolder(AppDataManager.getSettings().lastBuildOutPath);
                                                                        try {
                                                                            org.apache.commons.io.FileUtils.cleanDirectory(new File(AppDataManager.getSettings().lastBuildOutPath));
                                                                        } catch (IOException e) {
                                                                            Log.CurrentSession.println(ExceptionsUtils.toString(e) + "\n\tCaused by: " + e.getCause(), Log.MessageType.ERROR);
                                                                        }
                                                                        Builder.startBuild();
                                                                        dialogWindow.setActive(false);
                                                                        currentAction = "";
                                                                    }

                                                                    @Override
                                                                    public void secondButtonClicked() {
                                                                        dialogWindow.setActive(false);
                                                                        currentAction = "";
                                                                    }

                                                                    @Override
                                                                    public void thirdButtonClicked() {

                                                                    }
                                                                });
                                                    }
                                                }

                                                @Override
                                                public void secondButtonClicked() {
                                                    dialogWindow.setActive(false);
                                                    currentAction = "";
                                                }

                                                @Override
                                                public void thirdButtonClicked() {

                                                }
                                            });
                                } else {
                                    Log.showErrorDialog("The main Scene2D was not selected!");
                                }
                            }
                        });

                        currentAction = "Project/Build";
                    }
                }

                if(ImGui.menuItem("Exit")) {
                    // сделать
                }

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Objects")) {
                if(ImGui.beginMenu("New...")) {
                    if(ImGui.menuItem("Object2D")) {
                        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                            new Entity().setLayer(SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default"));
                        }
                    }

                    if(ImGui.menuItem("Camera2D")) {
                        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                            Entity camera2D = Entity.createAsCamera2D();
                            camera2D.setLayer(SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default"));
                        }
                    }

                    if(ImGui.menuItem("Instancing object")) {
                        // сделать
                    }

                    if(ImGui.menuItem("AtlasDrawing object")) {
                        // сделать
                    }

                    ImGui.endMenu();
                }

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("UI")) {
                if(ImGui.beginMenu("New...")) {
                    if(ImGui.menuItem("Text")) {
                        // сделать
                    }

                    if(ImGui.menuItem("Progress Bar")) {
                        // сделать
                    }

                    if(ImGui.menuItem("Button")) {
                        // сделать
                    }

                    ImGui.endMenu();
                }

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Scene")) {
                if(ImGui.menuItem("Create")) {
                    showCreateScene2DDialog();
                }

                if(ImGui.menuItem("Save current")) {
                    if(ProjectsManager.getCurrentProject() != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                        SceneManager.currentSceneManager.saveScene(SceneManager.currentSceneManager.getCurrentScene2D(), SceneManager.currentSceneManager.getCurrentScene2D().getScenePath());
                    }
                }

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Engine")) {
                if(ImGui.menuItem("Reload resources")) {
                    //Resources.load();
                }

                if(ImGui.menuItem("Settings")) {
                    ViewsManager.getEngineSettingsView().active = true;
                }

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Scene Manager")) {
                if(ImGui.beginMenu("Main Scene2D")) {
                    if(SceneManager.currentSceneManager != null) {
                        for (int i = 0; i < SceneManager.currentSceneManager.getScene2DStoredValues().size(); i++) {
                            Scene2DStoredValues storedValues = SceneManager.currentSceneManager.getScene2DStoredValues().get(i);
                            System.out.println(storedValues.path);
                            boolean clicked = ImGui.menuItem(FilenameUtils.getBaseName(new File(storedValues.path).getName()));
                            if (storedValues.isMainScene2D) {
                                ImGui.sameLine();
                                ImGui.image(Resources.Textures.Icons.checkMarkIcon.getTextureHandler(), 12, 12);
                            }
                            if (clicked) {
                                // убираю текущую сцену
                                for (int k = 0; k < SceneManager.currentSceneManager.getScene2DStoredValues().size(); k++) {
                                    SceneManager.currentSceneManager.getScene2DStoredValues().get(k).isMainScene2D = false;
                                }
                                storedValues.isMainScene2D = !storedValues.isMainScene2D;
                            }
                        }
                    }
                    ImGui.endMenu();
                }

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Views")) {
                if(ImGui.menuItem("Debugger")) {
                    ViewsManager.getDebuggerView().active = true;
                }
                ImGui.endMenu();
            }
        }
        ImGui.endMainMenuBar();

        drawAction();
    }

    private void drawAction()
    {
        if(!currentAction.equals("")) {
            dialogWindow.draw();
            fileChooserWindow.draw();
        }
    }

    public void showCreateScene2DDialog()
    {
        dialogWindow.setWindowName("Create scene");
        dialogWindow.setRightButtonText("Create");
        dialogWindow.setActive(true);
        dialogWindow.setWindowSize(new Vector2f(525.0f, dialogWindow.getWindowSize().y));
        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
            @Override
            public void onDraw() {
                ImGui.inputText("Scene name", newSceneName);
                if(!ImGui.isItemActive() && Keyboard.keyReleased(GLFW.GLFW_KEY_ENTER)) {
                    onRightButtonClicked();
                }
            }

            @Override
            public void onMiddleButtonClicked() {

            }

            @Override
            public void onLeftButtonClicked() {
                newSceneName.set("");

                dialogWindow.setActive(false);
                currentAction = "";
            }

            @Override
            public void onRightButtonClicked() {
                if (!SceneManager.currentSceneManager.isScene2DExists(newSceneName.get())) {
                    if (ProjectsManager.getCurrentProject() != null) {
                        ViewsManager.getSceneView().stopPlayMode();
                        if (SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                            //SceneManager.currentSceneManager.getCurrentScene2D().destroy();
                        }
                        Scene2D scene2D = new Scene2D();
                        scene2D.setName(newSceneName.get());

                        String scenePath = ResourcesView.currentDirectoryPath + "\\" + scene2D.getName() + ".sgs";

                        Entity camera2D = Entity.createAsObject2D();
                        camera2D.setLayer(scene2D.getLayering().getLayer("default"));
                        scene2D.setSceneMainCamera2D(camera2D);

                        scene2D.getPhysicsWorld().simulatePhysics = false;
                        scene2D.getScriptSystem().runScripts = false;
                        scene2D.setScenePath(scenePath);
                        if (SceneManager.currentSceneManager.getScene2DStoredValues().size() == 0) {
                            SceneManager.currentSceneManager.setCurrentScene2D(scene2D);
                        }

                        Scene2DStoredValues storedValues = new Scene2DStoredValues();
                        storedValues.path = scene2D.getScenePath();
                        System.out.println(storedValues.path);
                        SceneManager.currentSceneManager.getScene2DStoredValues().add(storedValues);
                        SceneManager.currentSceneManager.saveScene(scene2D, scenePath);
                    } else {
                        Log.showErrorDialog("Can not create new scene2D! First create or open project.");
                    }

                    newSceneName.set("");

                    dialogWindow.setActive(false);
                    currentAction = "";
                } else {
                    Log.showErrorDialog("Can not create new scene2D! Scene2D with the same name already exists.");
                }
            }
        });

        currentAction = "Scene/Create";
        // сделать
    }

    // устанавливает тип файла, который нужно создать
    public void setCurrentFileTypeNeedCreate(String fileType)
    {
        currentFileTypeNeedCreate = fileType;
    }

    // показывает диалог создания файла
    private void showFileCreateDialog() {
        if (!currentFileTypeNeedCreate.equals("")) {
            dialogWindow.setWindowName(currentFileTypeNeedCreate.equals("Directory") ? "New " + currentFileTypeNeedCreate : "New " + currentFileTypeNeedCreate + " file");
            dialogWindow.setRightButtonText("Create");
            dialogWindow.setActive(true);
            dialogWindow.setWindowSize(new Vector2f(525.0f, dialogWindow.getWindowSize().y));
            dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                @Override
                public void onDraw() {
                    ImGui.inputText("File name", newFileName);
                    if(!ImGui.isItemActive() && Keyboard.keyReleased(GLFW.GLFW_KEY_ENTER)) {
                        onRightButtonClicked();
                    }
                }

                @Override
                public void onMiddleButtonClicked() {

                }

                @Override
                public void onLeftButtonClicked() {
                    newFileName.set("");

                    dialogWindow.setActive(false);
                    currentAction = "";
                    currentFileTypeNeedCreate = "";
                }

                @Override
                public void onRightButtonClicked() {
                    ViewsManager.getResourcesView().createFile(currentFileTypeNeedCreate, newFileName.get());

                    newFileName.set("");
                    currentFileTypeNeedCreate = "";

                    dialogWindow.setActive(false);
                    currentAction = "";
                }
            });

            if(!currentFileTypeNeedCreate.equals("Directory")) {
                currentAction = "File/New/" + currentFileTypeNeedCreate + "File";
            } else {
                currentAction = "File/New/" + currentFileTypeNeedCreate;
            }
        }
    }
}
