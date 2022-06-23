package Core2D.Project;

import java.io.Serializable;

public class Project implements Serializable
{
    private static final long serialVersionUID = 6332323L;

    private String projectParentPath;
    private String projectName;

    private String resourcesPath;
    private String scriptsPath;

    public Project(String projectParentPath, String projectName)
    {
        this.projectParentPath = projectParentPath;
        this.projectName = projectName;

        resourcesPath = projectParentPath + "\\" + projectName + "\\Resources";
        scriptsPath = projectParentPath + "\\" + projectName + "\\Scripts";
    }

    public String getProjectParentPath() { return projectParentPath; }

    public String getProjectPath() { return projectParentPath + "\\" + projectName;  }

    public String getProjectName() { return projectName; }

    public String getResourcesPath() { return resourcesPath; }

    public String getScriptsPath() { return scriptsPath; }
}
