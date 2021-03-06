package SungearEngine2D.Project;

import java.io.Serial;
import java.io.Serializable;

public class Project implements Serializable
{
    @Serial
    private static final long serialVersionUID = 6332323L;

    private String projectParentPath;
    private String projectPath;
    private String projectName;

    private String resourcesPath;
    private String scriptsPath;
    private String scenesPath;

    public Project(String projectParentPath, String projectName)
    {
        this.projectParentPath = projectParentPath;
        this.projectName = projectName;
        this.projectPath = projectParentPath + "\\" + projectName;

        resourcesPath = projectParentPath + "\\" + projectName + "\\Resources";
        scriptsPath = projectParentPath + "\\" + projectName + "\\Scripts";
        scenesPath = projectParentPath + "\\" + projectName + "\\Scenes";
    }

    public String getProjectParentPath() { return projectParentPath; }

    public void setProjectParentPath(String projectParentPath) { this.projectParentPath = projectParentPath; }

    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath)
    {
        this.projectPath = projectPath;
        resourcesPath = projectPath + "\\Resources";
        scriptsPath = projectPath + "\\Scripts";
        scenesPath = projectPath + "\\Scenes";
    }

    public String getProjectName() { return projectName; }

    public String getResourcesPath() { return resourcesPath; }

    public String getScriptsPath() { return scriptsPath; }

    public String getScenesPath() { return scenesPath; }
}
