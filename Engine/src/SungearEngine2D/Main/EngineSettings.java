package SungearEngine2D.Main;

import Core2D.Core2D.Core2D;
import Core2D.Tasks.NonStoppableTask;
import Core2D.Utils.FileUtils;
import SungearEngine2D.GUI.Views.ViewsManager;

import java.io.File;

public class EngineSettings
{
    public static class Playmode
    {
        public static boolean active = false;
        public static boolean paused = false;
        public static boolean canEnterPlaymode = true;
    }

    public static void initCompiler()
    {
        ViewsManager.getBottomMenuView().addTaskToList(new NonStoppableTask("Initializing compiler...  ", 1.0f, 0.0f) {
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
}
