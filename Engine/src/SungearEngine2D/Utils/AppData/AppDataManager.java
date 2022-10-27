package SungearEngine2D.Utils.AppData;

import Core2D.Log.Log;

import java.io.File;
import java.io.IOException;

public class AppDataManager {
    private static AppDataManager istance = new AppDataManager();

    public static File getAppDataDirectory() {
        return AppDataDirectory;
    }
    public static File getRoamingDirectory() {
        return RoamingDirectory;
    }
    public static File getLocalDirectory() {
        return LocalDirectory;
    }
    private static File AppDataDirectory = new File(System.getenv("APPDATA"));
    private static File RoamingDirectory = new File(AppDataDirectory.getAbsolutePath() + File.separator + "Roaming");
    private static File LocalDirectory = new File(AppDataDirectory.getAbsolutePath()+File.separator + "Local");

    public static UserSettings getSettings() { return settings; }
    private static UserSettings settings = UserSettings.getUserSettings();

    public static File createRoamingDirectory(){
        try {
            if (!getRoamingDirectory().exists())
                getRoamingDirectory().createNewFile();
        } catch (IOException e){
            Log.CurrentSession.println("Can't create " + getRoamingDirectory(), Log.MessageType.ERROR);
            return null;
        }
        return RoamingDirectory;
    }
    public static File createLocalDirectory(){
        try {
            if (!getLocalDirectory().exists())
                getLocalDirectory().createNewFile();
        } catch (IOException e){
            Log.CurrentSession.println("Can't create " + getLocalDirectory(), Log.MessageType.ERROR);
            return null;
        }
        return  LocalDirectory;
    }
}

