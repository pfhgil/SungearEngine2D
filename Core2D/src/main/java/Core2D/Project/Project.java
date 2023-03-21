package Core2D.Project;

import Core2D.AssetManager.AssetManager;
import Core2D.Scene2D.SceneManager;

import java.io.File;
import java.io.Serializable;

public class Project implements Serializable
{
    private static final long serialVersionUID = 6332323L;

    private String projectParentPath;
    private String projectPath;
    private String projectName;

    private String resourcesPath = "";
    private String scriptsPath = "";
    private String scenesPath = "";

    private ProjectSettings projectSettings = new ProjectSettings();
    private String projectSettingsPath;

    public Project(String projectParentPath, String projectName)
    {
        this.projectParentPath = projectParentPath;
        this.projectName = projectName;
        this.projectPath = projectParentPath + File.separator + projectName;

        resourcesPath = this.projectPath + File.separator + "Resources";
        scriptsPath = this.projectPath + File.separator + "ECSOrientedScripts";
        scenesPath = this.projectPath + File.separator + "Scenes";
        projectSettingsPath = this.projectPath + File.separator + "ProjectSettings.sgps";

        projectSettings = new ProjectSettings();
    }

    public void save()
    {
        SceneManager.saveSceneManager();
        AssetManager.getInstance().save();
        getProjectSettings().saveSettings(projectSettingsPath);
    }

    public void load()
    {
        SceneManager.loadSceneManagerAsCurrent();
        AssetManager.getInstance().load();
        getProjectSettings().loadSettings(projectSettingsPath);
    }


    public String getProjectParentPath() { return projectParentPath; }

    public void setProjectParentPath(String projectParentPath) { this.projectParentPath = projectParentPath; }

    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath)
    {
        this.projectPath = projectPath;
        resourcesPath = projectPath + File.separator + "Resources";
        scriptsPath = projectPath + File.separator + "ECSOrientedScripts";
        scenesPath = projectPath + File.separator + "Scenes";
        projectSettingsPath = projectPath + File.separator + "ProjectSettings.sgps";
        projectParentPath = new File(projectParentPath).getParent();
    }

    public String getProjectName() { return projectName; }

    public String getResourcesPath() { return resourcesPath; }

    public String getScriptsPath() { return scriptsPath; }

    public String getScenesPath() { return scenesPath; }

    public ProjectSettings getProjectSettings()
    {
        if(projectSettings == null) {
            projectSettings = new ProjectSettings();
        }
        return projectSettings;
    }

    public String getProjectSettingsPath() { return projectSettingsPath; }
}
