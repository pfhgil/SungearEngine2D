package Core2D.Project;

import Core2D.Log.Log;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;

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

        String strToSerialize = Utils.gson.toJson(project);
        String pathToSave = projectPath + File.separator + projectName + File.separator + projectName + ".sgp";
        FileUtils.createFile(pathToSave);
        FileUtils.writeToFile(pathToSave, strToSerialize, false);
        //FileUtils.serializeObject(projectPath + File.separator + projectName + File.separator + projectName + ".sgp", project);

        currentProject = project;
        currentProject.save(); //а бл
        //ResourcesView.currentDirectoryPath = currentProject.getProjectPath();
    }

    public static void loadProject(String projectFilePath)
    {
        currentProject = Utils.gson.fromJson(FileUtils.readAllFile(projectFilePath), Project.class);
        Log.Console.println("project scripts path: " + currentProject.getScriptsPath());
        currentProject.setProjectPath(new File(projectFilePath).getParent());
        currentProject.load();
    }

    public static Project getCurrentProject() { return currentProject; }
}
