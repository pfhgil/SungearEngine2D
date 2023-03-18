package SungearEngine2D.Utils.AppData;

import Core2D.Core2D.Settings;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserSettings implements Serializable {
    public List<String> lastProjects;
    private int destinationFPS = Settings.Core2D.destinationFPS;
    public String lastBuildName = "";
    public String lastBuildOutPath = "";

    private transient boolean saved = true;

    private static final long serialVersionUID = 1L;
    public static String fileName = "UserSettings.dat";
    public static UserSettings instance;

    public void init()
    {
        lastProjects = new ArrayList<>();
    }

    public UserSettings()
    {
        if (instance != null) {
            throw new RuntimeException("multipleInstanceError: UserSettings already exist");
        }
        instance = this;
    }

    public void addLastProject(String path)
    {
        if (UserSettings.instance.lastProjects.contains(path)) {
            UserSettings.instance.lastProjects.remove(path);
        }
        UserSettings.instance.lastProjects.add(path);
        if (UserSettings.instance.lastProjects.size() > 10) {
            UserSettings.instance.lastProjects.remove(10);
        }
        UserSettings.instance.save();
    }

    public UserSettings load()
    {
        var usFile = new File(AppDataManager.getRoamingDirectory().getAbsolutePath() + File.separator + fileName);
        if (usFile.exists()) {
            UserSettings us = (UserSettings) FileUtils.deSerializeObject(usFile);
            Settings.Core2D.destinationFPS = us.destinationFPS;
            return us;
        }
        return null;
    }

    // Получает текущие настройки
    public static UserSettings getUserSettings()
    {
        var usFile = new File(AppDataManager.getRoamingDirectory().getAbsolutePath() + File.separator + fileName);
        if (usFile.exists()) {
            instance = (UserSettings) FileUtils.deSerializeObject(usFile);
            Settings.Core2D.destinationFPS = instance.destinationFPS;
            instance.lastProjects.removeIf(lastProjectPath -> !new File(lastProjectPath).exists());
        } else {
            instance = new UserSettings();
            instance.init();
        }
        return instance;
    }

    //Сохраняет текущие настройки
    public void save()
    {
        var usFile = new File(AppDataManager.createRoamingDirectory() + File.separator + fileName);
        usFile.delete();
        try {
            usFile.createNewFile();
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        FileUtils.serializeObject(usFile, this);
        saved = true;
    }

    public int getDestinationFPS() { return destinationFPS; }
    public void setDestinationFPS(int destinationFPS)
    {
        this.destinationFPS = destinationFPS;
        Settings.Core2D.destinationFPS = destinationFPS;
        saved = false;
    }

    public boolean isSaved() { return saved; }

    public List<String> getLastProjects() { return lastProjects; }

    public void setLastProjects(List<String> lastProjects) { this.lastProjects = lastProjects; }
}
