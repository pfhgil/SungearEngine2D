package SungearEngine2D.Main;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindowCallback;
import SungearEngine2D.Utils.ResourcesUtils;
import com.google.gson.*;
import imgui.ImGui;

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

            Main.settingsThread.interrupt();
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
                MainView.getBottomMenuView().progressBarText = "Finding JDK...  ";

                File jdksDir = new File("C:\\Users\\user\\.jdks");
                if(!jdksDir.exists()) {
                    MainView.getBottomMenuView().showProgressBar = false;
                    dialogWindow.setActive(false);
                    createSettingsFile();
                    settingsFile.setJdkPath("no path");
                    Main.settingsThread.interrupt();
                    return;
                }
                List<String> jdksBin = ResourcesUtils.findAllJdksBinPaths(jdksDir.getPath());

                MainView.getBottomMenuView().progressBarCurrent = 150.0f;

                dialogWindow.setActive(true);

                String[] chosenJdkBinPath = new String[1];

                dialogWindow.setDialogWindowCallback(new DialogWindowCallback() {
                    @Override
                    public void onDraw() {
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

                    @Override
                    public void onMiddleButtonClicked() {

                    }

                    @Override
                    public void onLeftButtonClicked() {
                        MainView.getBottomMenuView().showProgressBar = false;
                        dialogWindow.setActive(false);
                        createSettingsFile();
                        settingsFile.setJdkPath("no path");
                        Main.settingsThread.interrupt();
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

                        Main.settingsThread.interrupt();
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
