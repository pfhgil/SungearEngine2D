package SungearEngine2D.Main;

import Core2D.Core2D.Core2D;
import Core2D.Tasks.NonStoppableTask;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.GUI.Windows.DialogWindow.DialogWindow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Settings
{
    private static DialogWindow dialogWindow = new DialogWindow("Choose JDK bin path", "Cancel", "Choose");
    private static SettingsFile settingsFile = new SettingsFile();

    public static class Playmode
    {
        public static boolean active = false;
        public static boolean paused = false;
        public static boolean canEnterPlaymode = true;
    }

    public static void initCompiler()
    {
        MainView.getBottomMenuView().addTaskToList(new NonStoppableTask("Initializing compiler...  ", 1.0f, 0.0f) {
            @Override
            public void run() {
                File compilerDir = new File("./compiler");
                if (!compilerDir.exists()) {
                    compilerDir.mkdir();
                }
                File core2DFile = new File(compilerDir.getAbsolutePath() + "/Core2D.jar");
                File chcpFile = new File(compilerDir.getAbsolutePath() + "/chcp.com");
                File sevenZipFile = new File(compilerDir.getAbsolutePath() + "/7z.exe");
                File applicationStarterFile = new File(compilerDir.getAbsolutePath() + "/ApplicationStarter.java");

                if (!core2DFile.exists()) {
                    FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/Core2D.jar"), core2DFile.getPath());
                }
                if (!chcpFile.exists()) {
                    FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/chcp.com"), chcpFile.getPath());
                }
                if (!sevenZipFile.exists()) {
                    FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/7z.exe"), sevenZipFile.getPath());
                }
                if (!applicationStarterFile.exists()) {
                    FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/ApplicationStarter.java"), applicationStarterFile.getPath());
                }

                destination++;
            }
        });
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

                MainView.getBottomMenuView().addTaskToList(new StoppableTask("Finding JDKs... ", 1.0f, 0.0f) {
                    @Override
                    public void run()
                    {
                        while (true) {
                        }
                    }
                });
                /*
                while (true) {
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    System.out.println("ffsdsdgfsb");
                                }
                                String jdksPaths = CMDUtils.getCommandOutput("where /R C:\\ javac.exe");
                                //List<JavaInfo> jdks = JavaFinder.findJavas();
                                System.out.println(jdksPaths);

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
                                        }
                                        ImGui.endChild();
                                    }

                                    @Override
                                    public void onMiddleButtonClicked() {

                                    }

                                    @Override
                                    public void onLeftButtonClicked() {
                                        MainView.getBottomMenuView().finishProgressBar();
                                        dialogWindow.setActive(false);
                                        createSettingsFile();
                                        settingsFile.jdkPath = "no path";
                                        //Main.helpThread.interrupt();
                                    }

                                    @Override
                                    public void onRightButtonClicked() {
                                        MainView.getBottomMenuView().finishProgressBar();
                                        dialogWindow.setActive(false);

                                        try {
                                            boolean created = file.createNewFile();
                                            if (!created) {
                                                Log.CurrentSession.println("Error while creating settings.txt", Log.MessageType.ERROR);
                                            }
                                        } catch (IOException e) {
                                            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                                        }

                                        settingsFile = new SettingsFile();
                                        settingsFile.jdkPath = chosenJdkBinPath[0];
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        String data = gson.toJson(settingsFile);

                                        FileUtils.writeToFile(file, data, false);

                                        createSettingsFile();

                                        //Main.helpThread.interrupt();
                                    }
                                }
                                );
                 */
                System.out.println("dsdasdas11111   d");
            }
        }
    }

    public static void drawImGUI()
    {
        dialogWindow.draw();
    }

    public static SettingsFile getSettingsFile() { return settingsFile; }
}
