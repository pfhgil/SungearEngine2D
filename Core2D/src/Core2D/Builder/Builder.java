package Core2D.Builder;

import Core2D.Core2D.Core2D;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Builder
{
    private static String compilingJavaFiles = "";
    private static String buildingClassFiles = "";
    // включаемые библиотеки для указания в classpath
    private static String includingLibraries = "";

    public static void buildProject(String sourcesDirectoryPath, String resourcesDirectoryPath, String mainClass, String buildName)
    {
        // копирую файл Core2D.jar в папку sourcesDirectoryPath
        FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/Builder/Core2D.jar"), sourcesDirectoryPath + "/Core2D.jar");

        // копирую файл кодировки в папку sourcesDirectoryPath
        FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/Builder/chcp.com"), sourcesDirectoryPath + "/chcp.com");

        // копирую файл 7z.exe
        FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/Builder/7z.exe"), sourcesDirectoryPath + "/7z.exe");

        // создаю файл манифеста
        String manifestFileData = "Manifest-Version: 1.0\n" +
                "Main-Class: " + mainClass + "\n" +
                "Class-Path: Core2D.jar\n";

        File manifestFile = FileUtils.createFile(sourcesDirectoryPath + "/manifest.txt");
        FileUtils.writeToFile(manifestFile, manifestFileData, false);

        // получаю относительный путь java файлов, которые нужно скомпилировать
        AppendJavaAndClassPath(new File(sourcesDirectoryPath), sourcesDirectoryPath);

        System.out.println(compilingJavaFiles);

        // создаю файл для билда
        File builderBatFile = FileUtils.createFile(sourcesDirectoryPath + "/builder.bat");

        // в будущем нужно заменить del /q Build.jar на del /q кастомное_имя_файла
        String builderBatFileData = "@echo off\n" +
                // устанавливаю кодировку, чтобы был русский текст
                "chcp 65001\n" +
                // указываю путь до bin jdk
                "set bin=C:\\Users\\Админ\\.jdks\\corretto-1.8.0_302\\bin\n" +
                // указываю путь до javac
                "set ppath=%bin%;C:/Users/Админ/.jdks/corretto-1.8.0_302/bin/javac.exe\n" +
                // компилирую все java файлы
                "javac " +  "-cp Core2D.jar " + compilingJavaFiles + "\n" +
                // создаю jar с именем buildName, классами java, манифестом
                "jar cvmf manifest.txt " + buildName + ".jar " + buildingClassFiles + "\n" +
                // удаляю папку core2d
                "rd core2d\n" +
                // создаю папку core2d
                "md core2d\n" +
                // перехожу в папку core2d
                "cd core2d\n" +
                // распаковываю в эту папку библиотеку Core2D.jar
                "jar xf " + sourcesDirectoryPath + "/Core2D.jar\n" +
                // возвращаюсь обратно
                "cd..\n" +
                // помещаю все распакованные файлы в jar-файл buildName
                "7z a " + buildName + ".jar core2d/. -ssc\n" +
                // помещаю все ресурсы в jar-файл
                "7z a " + buildName + ".jar " + resourcesDirectoryPath + "/. -ssc\n" +
                // обновляю манифест в jar
                "jar umf manifest.txt " + buildName + ".jar\n" +
                // удаляю все оставшиеся от билда файлы
                "del /q " + buildingClassFiles + " manifest.txt openBuilder.bat chcp.com Core2D.jar 7z.exe\n" +
                // удаляю папку core2d
                "rd /s /q core2d\n" +
                "echo build finished. check log.";

        FileUtils.writeToFile(builderBatFile, builderBatFileData, false);

        // создаю файл, который будет открывать builder
        String openBuilderBatFileData = "cd /d " + builderBatFile.getParentFile().getPath() + "\n" +
                "builder.bat";

        File openBuilderBatFile = FileUtils.createFile(sourcesDirectoryPath + "/openBuilder.bat");
        FileUtils.writeToFile(openBuilderBatFile, openBuilderBatFileData, false);

        // открываю файл openBuilder
        ProcessBuilder pb = new ProcessBuilder(sourcesDirectoryPath + "/openBuilder.bat");
        try {
            Process proc = pb.start();

            // принт вывода и ошибок
            Log.CurrentSession.println(Utils.outputStreamToString(proc.getOutputStream()), Log.MessageType.INFO);
            Log.CurrentSession.println(Utils.inputStreamToString(proc.getErrorStream()), Log.MessageType.ERROR);

            // жду завершения процесса
            proc.waitFor();
        } catch (InterruptedException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        // удаляю bat файл для билда
        builderBatFile.delete();
        compilingJavaFiles = "";
        buildingClassFiles = "";
    }

    public static void buildProject(String sourcesDirectoryPath, String resourcesDirectoryPath, String librariesDirectoryPath, String mainClass, String buildName)
    {
        // копирую файл Core2D.jar в папку sourcesDirectoryPath
        FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/Builder/Core2D.jar"), sourcesDirectoryPath + "/Core2D.jar");

        // копирую файл кодировки в папку sourcesDirectoryPath
        FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/Builder/chcp.com"), sourcesDirectoryPath + "/chcp.com");

        // копирую файл 7z.exe
        FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/Builder/7z.exe"), sourcesDirectoryPath + "/7z.exe");

        // создаю файл манифеста
        String manifestFileData = "Manifest-Version: 1.0\n" +
                "Main-Class: " + mainClass + "\n" +
                "Class-Path: Core2D.jar\n";

        File manifestFile = FileUtils.createFile(sourcesDirectoryPath + "/manifest.txt");
        FileUtils.writeToFile(manifestFile, manifestFileData, false);

        // получаю относительный путь java файлов, которые нужно скомпилировать
        AppendJavaAndClassPath(new File(sourcesDirectoryPath), sourcesDirectoryPath);

        // получаю строку путей всех сторонних библиотек
        AppendLibraries(new File(librariesDirectoryPath));

        System.out.println(compilingJavaFiles);

        // создаю файл для билда
        File builderBatFile = FileUtils.createFile(sourcesDirectoryPath + "/builder.bat");

        // в будущем нужно заменить del /q Build.jar на del /q кастомное_имя_файла
        String builderBatFileData = "@echo off\n" +
                // устанавливаю кодировку, чтобы был русский текст
                "chcp 65001\n" +
                // указываю путь до bin jdk
                "set bin=C:\\Users\\Админ\\.jdks\\corretto-1.8.0_302\\bin\n" +
                // указываю путь до javac
                "set ppath=%bin%;C:/Users/Админ/.jdks/corretto-1.8.0_302/bin/javac.exe\n" +
                // компилирую все java файлы
                "javac " +  "-cp " + includingLibraries + "Core2D.jar " + compilingJavaFiles + "\n" +
                // создаю jar с именем buildName, классами java, манифестом
                "jar cvmf manifest.txt " + buildName + ".jar " + buildingClassFiles + "\n" +
                // создаю папку core2d
                "md core2d\n" +
                // перехожу в папку core2d
                "cd core2d\n" +
                // распаковываю в эту папку библиотеку Core2D.jar
                "jar xf " + sourcesDirectoryPath + "/Core2D.jar\n" +
                // возвращаюсь обратно
                "cd..\n" +
                // помещаю все распакованные файлы в jar-файл buildName
                "7z a " + buildName + ".jar core2d/. -ssc\n" +
                // помещаю все ресурсы в jar-файл
                "7z a " + buildName + ".jar " + resourcesDirectoryPath + "/. -ssc\n" +
                // создаю папку для сторонних библиотек
                "md lib\n" +
                // распаковываю все библиотеки в папку
                "7z x " + librariesDirectoryPath + " -ao\"lib/.\" -ssc\n" +
                // помещаю всю папку в jar-файл
                "7z a TileBreaker.jar lib/.\n" +
                // обновляю манифест в jar
                "jar umf manifest.txt " + buildName + ".jar\n" +
                // удаляю все оставшиеся от билда файлы
                "del /q " + buildingClassFiles + " manifest.txt openBuilder.bat chcp.com Core2D.jar 7z.exe\n" +
                // удаляю папку core2d
                "rd /s /q core2d\n" +
                // удаляю папку lib
                "rd /s /q lib\n" +
                "echo build finished. check log.";

        FileUtils.writeToFile(builderBatFile, builderBatFileData, false);

        // создаю файл, который будет открывать builder
        String openBuilderBatFileData = "cd /d " + builderBatFile.getParentFile().getPath() + "\n" +
                "builder.bat";

        File openBuilderBatFile = FileUtils.createFile(sourcesDirectoryPath + "/openBuilder.bat");
        FileUtils.writeToFile(openBuilderBatFile, openBuilderBatFileData, false);

        // открываю файл openBuilder
        ProcessBuilder pb = new ProcessBuilder(sourcesDirectoryPath + "/openBuilder.bat");
        try {
            Process proc = pb.start();

            // принт вывода и ошибок
            Log.CurrentSession.println(Utils.outputStreamToString(proc.getOutputStream()), Log.MessageType.INFO);
            Log.CurrentSession.println(Utils.inputStreamToString(proc.getErrorStream()), Log.MessageType.ERROR);

            // жду завершения процесса
            proc.waitFor();
        } catch (InterruptedException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        // удаляю bat файл для билда
        builderBatFile.delete();
        compilingJavaFiles = "";
        buildingClassFiles = "";
        includingLibraries = "";
    }

    public static void buildProject(String sourcesDirectoryPath, String mainClass, String buildName)
    {
        // копирую файл Core2D.jar в папку sourcesDirectoryPath
        FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/Builder/Core2D.jar"), sourcesDirectoryPath + "/Core2D.jar");

        // копирую файл кодировки в папку sourcesDirectoryPath
        FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/Builder/chcp.com"), sourcesDirectoryPath + "/chcp.com");

        // создаю файл манифеста
        String manifestFileData = "Manifest-Version: 1.0\n" +
                "Main-Class: " + mainClass + "\n" +
                "Class-Path: Core2D.jar\n";

        File manifestFile = FileUtils.createFile(sourcesDirectoryPath + "/manifest.txt");
        FileUtils.writeToFile(manifestFile, manifestFileData, false);

        // получаю относительный путь java файлов, которые нужно скомпилировать
        AppendJavaAndClassPath(new File(sourcesDirectoryPath), sourcesDirectoryPath);

        System.out.println(compilingJavaFiles);

        // создаю файл для билда
        File builderBatFile = FileUtils.createFile(sourcesDirectoryPath + "/builder.bat");

        // в будущем нужно заменить del /q Build.jar на del /q кастомное_имя_файла
        String builderBatFileData = "@echo off\n" +
                // устанавливаю кодировку, чтобы был русский текст
                "chcp 65001\n" +
                // указываю путь до bin jdk
                "set bin=C:\\Users\\Админ\\.jdks\\corretto-1.8.0_302\\bin\n" +
                // указываю путь до javac
                "set ppath=%bin%;C:/Users/Админ/.jdks/corretto-1.8.0_302/bin/javac.exe\n" +
                // компилирую все java файлы
                "javac " +  "-cp Core2D.jar " + compilingJavaFiles + "\n" +
                // создаю jar с именем buildName, классами java, манифестом
                "jar cvmf manifest.txt " + buildName + ".jar " + buildingClassFiles + "\n" +
                // создаю папку core2d
                "md core2d\n" +
                // перехожу в папку core2d
                "cd core2d\n" +
                // распаковываю в эту папку библиотеку Core2D.jar
                "jar xf " + sourcesDirectoryPath + "/Core2D.jar\n" +
                // возвращаюсь обратно
                "cd..\n" +
                // помещаю все распакованные файлы в jar-файл buildName
                "jar uf " + buildName + ".jar -C core2d .\n" +
                // обновляю манифест в jar
                "jar umf manifest.txt " + buildName + ".jar\n" +
                // удаляю все оставшиеся от билда файлы
                "del /q " + buildingClassFiles + " manifest.txt openBuilder.bat chcp.com Core2D.jar\n" +
                // удаляю папку core2d
                "rd /s /q core2d\n" +
                "echo build finished. check log.";

        FileUtils.writeToFile(builderBatFile, builderBatFileData, false);

        // создаю файл, который будет открывать builder
        String openBuilderBatFileData = "cd /d " + builderBatFile.getParentFile().getPath() + "\n" +
                "builder.bat";

        File openBuilderBatFile = FileUtils.createFile(sourcesDirectoryPath + "/openBuilder.bat");
        FileUtils.writeToFile(openBuilderBatFile, openBuilderBatFileData, false);

        // открываю файл openBuilder
        ProcessBuilder pb = new ProcessBuilder(sourcesDirectoryPath + "/openBuilder.bat");
        try {
            Process proc = pb.start();

            // принт вывода и ошибок
            Log.CurrentSession.println(Utils.outputStreamToString(proc.getOutputStream()), Log.MessageType.INFO);
            Log.CurrentSession.println(Utils.inputStreamToString(proc.getErrorStream()), Log.MessageType.ERROR);

            // жду завершения процесса
            proc.waitFor();
        } catch (InterruptedException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        // удаляю bat файл для билда
        builderBatFile.delete();
        compilingJavaFiles = "";
        buildingClassFiles = "";
    }

    private static void AppendLibraries(File currentDir)
    {
        File[] dirs = currentDir.listFiles();

        boolean hasNeededFiles = false;
        for(int i = 0; i < dirs.length; i++) {
            if(!dirs[i].isDirectory()) {
                String e = FilenameUtils.getExtension(dirs[i].getPath());
                String e1 = FilenameUtils.getExtension(".jar");

                if(e.equals(e1)) {
                    hasNeededFiles = true;
                }
            }
        }

        if(hasNeededFiles) {
            includingLibraries += currentDir + "/*;";
        }

        for(int i = 0; i < dirs.length; i++) {
            if(dirs[i].isDirectory()) {
                AppendLibraries(dirs[i]);
            }
        }
    }

    private static void AppendJavaAndClassPath(File currentDir, String parentPath)
    {
        File[] dirs = currentDir.listFiles();

        boolean hasNeededFiles = false;
        for(int i = 0; i < dirs.length; i++) {
            if(!dirs[i].isDirectory()) {
                String e = FilenameUtils.getExtension(dirs[i].getPath());
                String e1 = FilenameUtils.getExtension(".java");

                if(e.equals(e1)) {
                    hasNeededFiles = true;
                }
            }
        }

        Path parentFolder = Paths.get(parentPath);
        Path currentDirPath = Paths.get(currentDir.getPath());

        Path relativePath = parentFolder.relativize(currentDirPath);

        if(hasNeededFiles) {
            if (!relativePath.toString().equals("")) {
                compilingJavaFiles += relativePath + "/*.java ";
                buildingClassFiles += relativePath + "\\*.class ";
            } else {
                compilingJavaFiles += "*.java ";
                buildingClassFiles += "*.class ";
            }
        }

        for(int i = 0; i < dirs.length; i++) {
            if(dirs[i].isDirectory()) {
                AppendJavaAndClassPath(dirs[i], parentPath);
            }
        }
    }
}
