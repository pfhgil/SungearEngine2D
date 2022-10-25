package Core2D.Project;

import Core2D.Log.Log;
import Core2D.Utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.Serializable;

public class ProjectSettings implements Serializable
{
    private String jdkPath = "No JDK";
    private transient Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private transient boolean saved = false;

    public void saveSettings(String path)
    {

        File projectSettingsFile = new File(path);
        if (!projectSettingsFile.exists()){
            FileUtils.createFile(path);
        }

        String settingsString = gson.toJson(this);
        FileUtils.writeToFile(projectSettingsFile, settingsString, false);
        saved = true;
    }

    public void loadSettings(String path)
    {
        File projectSettingsFile = new File(path);
        if(projectSettingsFile.exists()) {
            String settingsString = FileUtils.readAllFile(new File(path));
            ProjectSettings projectSettings = gson.fromJson(settingsString, ProjectSettings.class);
            set(projectSettings);
        } else {
            Log.CurrentSession.println("Can not load project settings file! File by path \"" + path + "\" does not exist!", Log.MessageType.ERROR);
        }
    }

    public void set(ProjectSettings projectSettings)
    {
        this.jdkPath = projectSettings.jdkPath;
    }

    public void setJdkPath(String jdkPath)
    {
        this.jdkPath = jdkPath;
        saved = false;
    }
    public String getJdkPath() { return jdkPath; }

    public boolean isSaved() { return saved; }
}
