package SungearEngine2D.Builder;

import Core2D.Core2D.Core2D;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.FileUtils;
import SungearEngine2D.Scripting.Compiler;

import java.io.File;

public class Builder
{
    public static void build(String buildName)
    {
        if(ProjectsManager.getCurrentProject() != null) {
            File outDirectory = new File(ProjectsManager.getCurrentProject().getProjectParentPath() + "\\out");
            if(!outDirectory.exists()) {
                FileUtils.createFolder(outDirectory.getPath());
            }
            // копирую файл Core2D.jar в папку sourcesDirectoryPath
            FileUtils.copyFile(Core2D.class.getResourceAsStream("./compiler/Core2D.jar"), outDirectory.getPath() + "/Core2D.jar");

            // копирую файл кодировки в папку sourcesDirectoryPath
            FileUtils.copyFile(Core2D.class.getResourceAsStream("./compiler/chcp.com"), outDirectory.getPath() + "/chcp.com");

            // копирую файл 7z.exe
            FileUtils.copyFile(Core2D.class.getResourceAsStream("./compiler/7z.exe"), outDirectory.getPath() + "/7z.exe");

            Compiler.compileScript("./compiler/ApplicationStarter.java");

            // копирую файл ApplicationStarter
            FileUtils.copyFile(Core2D.class.getResourceAsStream("./compiler/ApplicationStarter.class"), outDirectory.getPath() + "/ApplicationStarter.class");

            // создаю файл манифеста
            String manifestFileData = "Manifest-Version: 1.0\n" +
                    "Main-Class: ApplicationStarter\n" +
                    "Class-Path: Core2D.jar\n";

            File manifestFile = FileUtils.createFile(outDirectory.getPath() + "/manifest.txt");
            FileUtils.writeToFile(manifestFile, manifestFileData, false);
        }
    }
}
