package SungearEngine2D.Main;

import Core2D.Core2D.Core2D;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.Utils.ResourcesUtils;
import com.google.gson.*;
import imgui.ImGui;
import imgui.ImVec2;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Settings
{
    private static DialogWindow dialogWindow = new DialogWindow("Choose JDK bin path", "Cancel", "Choose");
    private static SettingsFile settingsFile = new SettingsFile();

    public static class PlayMode
    {
        public static boolean active = false;
        public static boolean paused = false;
    }

    public static void initCompiler()
    {
        MainView.getBottomMenuView().showProgressBar = true;
        MainView.getBottomMenuView().progressBarDest = 1.0f;
        MainView.getBottomMenuView().progressBarCurrent = 0.0f;
        MainView.getBottomMenuView().progressBarText = "Initializing compiler...  ";

        File compilerDir = new File("./compiler");
        if(!compilerDir.exists()) {
            compilerDir.mkdir();
        }
        File core2DFile = new File(compilerDir.getAbsolutePath() + "/Core2D.jar");
        File chcpFile = new File(compilerDir.getAbsolutePath() + "/chcp.com");

        if(!core2DFile.exists()) {
            FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/Core2D.jar"), core2DFile.getPath());
        }
        if(!chcpFile.exists()) {
            FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/chcp.com"), chcpFile.getPath());
        }

        MainView.getBottomMenuView().progressBarCurrent++;
        MainView.getBottomMenuView().showProgressBar = false;
    }

    public static void loadSettingsFile()
    {
        File file = new File("./settings.txt");
        if(!file.exists()) {
            createSettingsFile();
        } else {
            dialogWindow.setActive(false);

            String fileString = FileUtils.readAllFile(file);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            settingsFile = gson.fromJson(fileString, SettingsFile.class);

            //Main.helpThread.interrupt();
        }
    }

    public static void createSettingsFile()
    {
        File file = new File("./settings.txt");
        if(!file.exists()) {
            if(settingsFile.getJdkPath().equals("")) {
                dialogWindow.setActive(false);

                MainView.getBottomMenuView().showProgressBar = true;
                MainView.getBottomMenuView().progressBarDest = 1.0f;
                MainView.getBottomMenuView().progressBarCurrent = 0.0f;
                MainView.getBottomMenuView().progressBarText = "Finding JDKs...  ";

                File jdksDir = FileUtils.findFile(new File("C:\\Users"), ".jdks");
                if(!jdksDir.exists()) {
                    MainView.getBottomMenuView().showProgressBar = false;
                    dialogWindow.setActive(false);
                    createSettingsFile();
                    settingsFile.setJdkPath("no path");
                    //Main.helpThread.interrupt();
                    return;
                }
                List<String> jdksBin = ResourcesUtils.findAllJdksBinPaths(jdksDir.getPath());

                MainView.getBottomMenuView().progressBarCurrent = 150.0f;

                dialogWindow.setActive(true);

                String[] chosenJdkBinPath = new String[1];

                dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                    @Override
                    public void onDraw() {
                        ImGui.setWindowFocus();

                        ImVec2 windowSize = new ImVec2();
                        windowSize = ImGui.getWindowSize();

                        ImGui.beginChild("JDKs", windowSize.x - 17.0f, windowSize.y - 75.0f, true);
                        {
                            ImGui.text("Current chosen JDK path:\n" + chosenJdkBinPath[0]);
                            ImGui.newLine();
                            ImGui.newLine();
                            for (String s : jdksBin) {
                                if (ImGui.collapsingHeader(new File(s).getParent())) {
                                    ImGui.text("JDK path:\n" + new File(s).getParent());
                                    chosenJdkBinPath[0] = new File(s).getParent();
                                }
                            }
                        }
                        ImGui.endChild();
                    }

                    @Override
                    public void onMiddleButtonClicked() {

                    }

                    @Override
                    public void onLeftButtonClicked() {
                        MainView.getBottomMenuView().showProgressBar = false;
                        dialogWindow.setActive(false);
                        createSettingsFile();
                        settingsFile.setJdkPath("no path");
                        //Main.helpThread.interrupt();
                    }

                    @Override
                    public void onRightButtonClicked() {
                        MainView.getBottomMenuView().showProgressBar = false;
                        dialogWindow.setActive(false);

                        try {
                            boolean created = file.createNewFile();
                            if(!created) {
                                Log.CurrentSession.println("Error while creating settings.txt");
                            }
                        } catch (IOException e) {
                            Log.CurrentSession.println(ExceptionsUtils.toString(e));
                        }

                        settingsFile = new SettingsFile();
                        settingsFile.setJdkPath(chosenJdkBinPath[0]);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String data = gson.toJson(settingsFile);

                        FileUtils.writeToFile(file, data, false);

                        createSettingsFile();

                        //Main.helpThread.interrupt();
                    }
                });
            }
        }
    }

    public static void drawImGUI()
    {
        dialogWindow.draw();
    }

    public static SettingsFile getSettingsFile() { return settingsFile; }
}
