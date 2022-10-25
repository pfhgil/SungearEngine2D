package Core2D.Project;

import Core2D.Scene2D.SceneManager;

import java.io.File;
import java.io.Serializable;

public class Project implements Serializable
{
    private static final long serialVersionUID = 6332323L;

    private String projectParentPath;
    private String projectPath;
    private String projectName;

    private String resourcesPath;
    private String scriptsPath;
    private String scenesPath;

    private ProjectSettings projectSettings = new ProjectSettings();
    private String projectSettingsPath;

    public Project(String projectParentPath, String projectName)
    {
        this.projectParentPath = projectParentPath;
        this.projectName = projectName;
        this.projectPath = projectParentPath + File.pathSeparator + projectName;

        resourcesPath = this.projectPath + File.pathSeparator + "Resources";
        scriptsPath = this.projectPath + File.pathSeparator + "Scripts";
        scenesPath = this.projectPath + File.pathSeparator + "Scenes";
        projectSettingsPath = this.projectPath + File.pathSeparator + "ProjectSettings.txt";

        projectSettings = new ProjectSettings();
        getProjectSettings().saveSettings(projectSettingsPath);
    }

    public void saveProject()
    {
        SceneManager.saveSceneManager(projectPath + File.pathSeparator + "SceneManager.sm");
        getProjectSettings().saveSettings(projectSettingsPath);
    }

    public void loadProject()
    {
        SceneManager.loadSceneManagerAsCurrent(projectPath + File.pathSeparator + "SceneManager.sm");
        getProjectSettings().loadSettings(projectSettingsPath);
    }


    public String getProjectParentPath() { return projectParentPath; }

    public void setProjectParentPath(String projectParentPath) { this.projectParentPath = projectParentPath; }

    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath)
    {
        this.projectPath = projectPath;
        resourcesPath = projectPath + File.pathSeparator + "Resources";
        scriptsPath = projectPath + File.pathSeparator + "Scripts";
        scenesPath = projectPath + File.pathSeparator + "Scenes";
        projectSettingsPath = projectPath + File.pathSeparator + "ProjectSettings.txt";
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
