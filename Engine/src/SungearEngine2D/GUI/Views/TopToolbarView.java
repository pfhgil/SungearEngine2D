package SungearEngine2D.GUI.Views;

import Core2D.Camera2D.Camera2D;
import Core2D.Controllers.PC.Keyboard;
import Core2D.Core2D.Core2D;
import Core2D.Log.Log;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.GUI.Windows.FileChooserWindow.FileChooserWindow;
import SungearEngine2D.GUI.Windows.FileChooserWindow.FileChooserWindowCallback;
import SungearEngine2D.Project.ProjectsManager;
import imgui.ImGui;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class TopToolbarView
{
    private String currentAction = "";

    private ImString projectName = new ImString();
    private ImString projectPath = new ImString();

    private ImString newFileName = new ImString();

    private ImString newSceneName = new ImString();

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
        fileChooserWindow.getDialogWindow().setActive(false);
        fileChooserWindow.setDirectoryChooserWindowCallback(new FileChooserWindowCallback() {
            @Override
            public void onLeftButtonClicked() {
                dialogWindow.setActive(true);
            }

            @Override
            public void onRightButtonClicked(String chosenDirectory) {
                dialogWindow.setActive(true);
                projectPath.set(chosenDirectory);
            }
        });
    }

    public void draw()
    {
        showFileCreateDialog();

        ImGui.beginMainMenuBar();
        {
            if(ImGui.beginMenu("File")) {
                if(ImGui.beginMenu("New...")) {
                    if(ImGui.menuItem("Project")) {
                        fileChooserWindow.setFileChooserMode(FileChooserWindow.FileChooserMode.CHOOSE_DIRECTORY);

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
                                    fileChooserWindow.getDialogWindow().setActive(true);
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
                        fileChooserWindow.setFileChooserMode(FileChooserWindow.FileChooserMode.CHOOSE_FILE);

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
                                    fileChooserWindow.getDialogWindow().setActive(true);
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

                    ImGui.endMenu();
                }

                if(ImGui.menuItem("Close project")) {
                    // сделать
                }

                if(ImGui.menuItem("Project settings")) {
                    // сделать
                }

                if(ImGui.menuItem("Build project")) {
                    // сделать
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
                        if(SceneManager.getCurrentScene2D() != null) {
                            Camera2D camera2D = new Camera2D();
                            SceneManager.getCurrentScene2D().getCameras2D().add(camera2D);
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
                    if(ProjectsManager.getCurrentProject() != null && SceneManager.getCurrentScene2D() != null) {
                        SceneManager.saveScene(SceneManager.getCurrentScene2D(), SceneManager.getCurrentScene2D().getScenePath());
                    }
                }

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Engine")) {
                if(ImGui.menuItem("Settings")) {
                    // сделать
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
                if(ProjectsManager.getCurrentProject() != null) {
                    MainView.getSceneView().stopPlayMode();
                    if(SceneManager.getCurrentScene2D() != null) {
                        SceneManager.getCurrentScene2D().destroy();
                    }
                    Scene2D scene2D = new Scene2D();

                    Camera2D camera2D = new Camera2D();
                    scene2D.getCameras2D().add(camera2D);
                    scene2D.setSceneMainCamera2D(camera2D);

                    scene2D.getPhysicsWorld().simulatePhysics = false;
                    scene2D.getScriptSystem().runScripts = false;
                    scene2D.setName(newSceneName.get());
                    if (SceneManager.getScenes().size() == 0) {
                        SceneManager.setCurrentScene2D(scene2D);
                    }
                    SceneManager.getScenes().add(scene2D);
                    SceneManager.saveScene(scene2D, ResourcesView.currentDirectoryPath + "\\" + scene2D.getName() + ".sgs");
                    scene2D = null;
                } else {
                    Log.showErrorDialog("Can not create new scene2D! First create or open project.");
                }

                newSceneName.set("");

                dialogWindow.setActive(false);
                currentAction = "";
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
                    MainView.getResourcesView().createFile(currentFileTypeNeedCreate, newFileName.get());

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
