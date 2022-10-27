package SungearEngine2D.GUI.Views.EditorView;

import Core2D.Camera2D.Camera2D;
import Core2D.Input.PC.Keyboard;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.Scene2DStoredValues;
import Core2D.Scene2D.SceneManager;
import SungearEngine2D.Builder.Builder;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.GUI.Windows.FileChooserWindow.FileChooserWindow;
import SungearEngine2D.GUI.Windows.FileChooserWindow.FileChooserWindowCallback;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Utils.AppData.UserSettings;
import imgui.ImGui;
import imgui.type.ImString;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class TopToolbarView
{
    private String currentAction = "";

    private ImString projectName = new ImString();
    private ImString projectPath = new ImString();

    private ImString newFileName = new ImString();

    private ImString newSceneName = new ImString();
    private ImString buildOutPath = new ImString();

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

                    if(ImGui.menuItem("Java file")) {
                        currentFileTypeNeedCreate = "Java";
                        showFileCreateDialog();
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

                                //ResourcesView.currentDirectoryPath = projectPath.get();
                                //Core2D.getSceneManager2D().loadScene(ProjectsManager.getCurrentProject().getScenesPath() + "\\lvl0.txt");
                                //Core2D.getSceneManager2D().loadScene(ProjectsManager.getCurrentProject().getScenesPath() + "\\lvl0.txt");

                                projectPath.set("");

                                dialogWindow.setActive(false);
                                currentAction = "";
                            }
                        });

                        currentAction = "File/Open/Project";
                    }
                    if (ImGui.beginMenu("Recent projects")){
                        for (var e: UserSettings.instance.lastProjects) {
                            if (ImGui.menuItem(e)){
                                ProjectsManager.loadProject(e);
                                UserSettings.instance.addLastProject(e);
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
                        // сделать
                    }

                    if (ImGui.menuItem("Build project")) {
                        dialogWindow.setWindowName("Build project");
                        dialogWindow.setRightButtonText("Build");
                        dialogWindow.setActive(true);
                        dialogWindow.setWindowSize(new Vector2f(525.0f, dialogWindow.getWindowSize().y));
                        dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                            @Override
                            public void onDraw() {
                                ImGui.inputText("##", buildOutPath); ImGui.sameLine();
                                if (ImGui.button("Browse...")){
                                    dialogWindow.setActive(false);
                                    fileChooserWindow.setOutput(buildOutPath);
                                    fileChooserWindow.setActiveWindow(dialogWindow);
                                    fileChooserWindow.setFileChooserMode(FileChooserWindow.FileChooserMode.CREATE_NEW_FILE);
                                    fileChooserWindow.setActive(true);
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
                                    Builder.startBuild("TestGame");
                                    // TODO: сделать билд
                                    dialogWindow.setActive(false);
                                    currentAction = "";
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
                        // сделать
                    }

                    if(ImGui.menuItem("Camera2D")) {
                        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                            Camera2D camera2D = new Camera2D();
                            SceneManager.currentSceneManager.getCurrentScene2D().getCameras2D().add(camera2D);
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
                    // сделать
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

                        Camera2D camera2D = new Camera2D();
                        scene2D.getCameras2D().add(camera2D);
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
