package Core2D.Project;

import Core2D.Utils.FileUtils;

import java.io.File;

public class ProjectsManager
{
    private static Project currentProject;

    public static void createProject(String projectPath, String projectName)
    {
        Project project = new Project(projectPath, projectName);

        FileUtils.createFolder(project.getProjectPath());
        FileUtils.createFolder(project.getResourcesPath());
        FileUtils.createFolder(project.getScriptsPath());
        FileUtils.createFolder(project.getScenesPath());
        FileUtils.createFile(project.getProjectSettingsPath());

        FileUtils.serializeObject(projectPath + File.separator + projectName + File.separator + projectName + ".sgp", project);

        currentProject = project;
        currentProject.saveProject();//а бл
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
