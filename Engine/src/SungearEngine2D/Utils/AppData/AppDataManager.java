package SungearEngine2D.Utils.AppData;

import Core2D.Log.Log;
import Core2D.Utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class AppDataManager {

    private static final File AppDataDirectory = new File(System.getenv("APPDATA")); public static File getAppDataDirectory() { return AppDataDirectory; }
    private static final File RoamingDirectory = new File(AppDataDirectory.getAbsolutePath() + File.separator + "Roaming"); public static File getRoamingDirectory() { return RoamingDirectory; }
    private static final File LocalDirectory = new File(AppDataDirectory.getAbsolutePath()+File.separator + "Local"); public static File getLocalDirectory() { return LocalDirectory; }

    private static UserSettings settings; public static UserSettings getSettings() { return settings; }

    public static void init(){
        settings = UserSettings.getUserSettings();
    }
    public static File createRoamingDirectory(){
        if (!RoamingDirectory.exists()){
            FileUtils.createFolder(RoamingDirectory);
        }
        return RoamingDirectory;
    }
    public static File createLocalDirectory(){
        if (!LocalDirectory.exists()){
            FileUtils.createFolder(LocalDirectory);
        }
        return LocalDirectory;
    }
}

