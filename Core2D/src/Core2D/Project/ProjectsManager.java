package Core2D.Project;

import Core2D.Utils.FileUtils;

import java.io.File;

public class ProjectsManager
{
    private static Project currentProject;

    public static void createProject(String projectPath, String projectName)
    {
        Project project = new Project(projectPath, projectName);

        FileUtils.createFolder(projectPath + "\\" + projectName);
        FileUtils.createFolder(projectPath + "\\" + projectName + "\\Resources");
        FileUtils.createFolder(projectPath + "\\" + projectName + "\\Scripts");
        FileUtils.createFolder(projectPath + "\\" + projectName + "\\Scenes");
        FileUtils.createFile(projectPath + "\\" + projectName + "\\ProjectSettings.txt");

        FileUtils.serializeObject(projectPath + "\\" + projectName + "\\" + projectName + ".sgp", project);

        currentProject = project;
        currentProject.saveProject();
        //ResourcesView.currentDirectoryPath = currentProject.getProjectPath();
    }

    public static void loadProject(String projectFilePath)
    {
        currentProject = (Project) FileUtils.deSerializeObject(projectFilePath);
        currentProject.setProjectPath(new File(projectFilePath).getParent());
        currentProject.loadProject();
    }

    public static Project getCurrentProject() { return currentProject; }
}