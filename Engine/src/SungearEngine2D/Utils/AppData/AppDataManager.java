package SungearEngine2D.Utils.AppData;

import Core2D.Utils.FileUtils;

import java.io.File;

public class AppDataManager {

    private static final File appDataDirectory = new File(System.getenv("APPDATA")); public static File getAppDataDirectory() { return appDataDirectory; }
    private static final File roamingDirectory = new File(appDataDirectory.getAbsolutePath() + File.separator + "Sungear Engine 2D"); public static File getRoamingDirectory() { return roamingDirectory; }
    private static final File localDirectory = new File(appDataDirectory.getAbsolutePath() + File.separator + "Sungear Engine 2D"); public static File getLocalDirectory() { return localDirectory; }

    private static UserSettings settings; public static UserSettings getSettings() { return settings; }

    public static void init(){
        settings = UserSettings.getUserSettings();
    }
    public static File createRoamingDirectory()
    {
        if (!roamingDirectory.exists()){
            FileUtils.createFolder(roamingDirectory);
        }
        return roamingDirectory;
    }
    public static File createLocalDirectory()
    {
        if (!localDirectory.exists()){
            FileUtils.createFolder(localDirectory);
        }
        return localDirectory;
    }
}

