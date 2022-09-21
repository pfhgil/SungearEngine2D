package SungearEngine2D.Builder;

import Core2D.Component.Components.ScriptComponent;
import Core2D.Component.Components.TextureComponent;
import Core2D.Core2D.Core2D;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;
import Core2D.Utils.WrappedObject;
import SungearEngine2D.Main.Settings;
import SungearEngine2D.Scripting.Compiler;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class Builder
{
    public static void build(String buildName)
    {
        try {
            if (ProjectsManager.getCurrentProject() != null) {
                SceneManager.saveSceneManager(ProjectsManager.getCurrentProject().getProjectPath() + "\\SceneManager.sm");

                File outDirectory = new File(ProjectsManager.getCurrentProject().getProjectPath() + "\\out");
                if (!outDirectory.exists()) {
                    FileUtils.createFolder(outDirectory.getPath());
                }

                // создаю SceneManager с выбранными сценами
                SceneManager sceneManager = new SceneManager();
                System.out.println(SceneManager.currentSceneManager.getScenes().get(0).getLayering());
                for(Scene2D scene2D : SceneManager.currentSceneManager.getScenes()) {
                    if(scene2D.inBuild) {
                        sceneManager.getScenes().add(scene2D);
                    }
                }
                SceneManager.saveSceneManager(outDirectory.getPath() + "/SceneManager.sm", sceneManager);
                SceneManager newSceneManager = SceneManager.loadSceneManagerNotAsCurrent(outDirectory.getPath() + "/SceneManager.sm");

                // упаковываю ресурсы
                packResources(outDirectory.getPath() + "\\resources", newSceneManager);

                // заново сохраняю этот SceneManager
                SceneManager.saveSceneManager(outDirectory.getPath() + "/SceneManager.sm", newSceneManager);

                File applicationStarterClassFile = new File(".\\compiler\\ApplicationStarter.class");
                File applicationStarterClassFile1 = new File(".\\compiler\\ApplicationStarter$1.class");

                // копирую файл Core2D.jar в папку sourcesDirectoryPath
                FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/Core2D.jar"), outDirectory.getPath() + "/Core2D.jar");

                // копирую файл кодировки в папку sourcesDirectoryPath
                FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/chcp.com"), outDirectory.getPath() + "/chcp.com");

                // копирую файл 7z.exe
                FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/7z.exe"), outDirectory.getPath() + "/7z.exe");

                Compiler.compileScript(".\\compiler\\ApplicationStarter.java");

                // копирую файл ApplicationStarter
                FileUtils.copyFile(applicationStarterClassFile.getCanonicalPath(), outDirectory.getPath() + "/ApplicationStarter.class", false);
                FileUtils.copyFile(applicationStarterClassFile1.getCanonicalPath(), outDirectory.getPath() + "/ApplicationStarter$1.class", false);

                // создаю файл манифеста
                String manifestFileData = "Manifest-Version: 1.0\n" +
                        "Main-Class: ApplicationStarter\n" +
                        "Class-Path: Core2D.jar\n";

                File manifestFile = FileUtils.createFile(outDirectory.getPath() + "/manifest.txt");
                FileUtils.writeToFile(manifestFile, manifestFileData, false);

                File builderBatFile = FileUtils.createFile(outDirectory.getPath() + "\\builder.bat");

                String builderBatFileData = "@echo off\n" +
                        // устанавливаю кодировку, чтобы был русский текст
                        "chcp 65001\n" +
                        // указываю путь до bin jdk
                        "set bin=\"" + Settings.getSettingsFile().jdkPath + "\"\n" +
                        // указываю путь до javac
                        "set ppath=%bin%;\"" + Settings.getSettingsFile().jdkPath + "\\javac.exe\"\n" +
                        // создаю jar с именем buildName, классом ApplicationStarter, манифестом
                        "jar cvmf manifest.txt " + buildName + ".jar ApplicationStarter.class ApplicationStarter$1.class\n" +
                        // удаляю папку core2d
                        "rd core2d\n" +
                        // создаю папку core2d
                        "md core2d\n" +
                        // перехожу в папку core2d
                        "cd core2d\n" +
                        // распаковываю в эту папку библиотеку Core2D.jar
                        "jar xf \"" + outDirectory.getPath() + "/Core2D.jar\"\n" +
                        // возвращаюсь обратно
                        "cd..\n" +
                        // помещаю все распакованные файлы в jar-файл buildName
                        "7z a " + buildName + ".jar core2d/. -ssc\n" +
                        // помещаю все ресурсы в jar-файл buildName
                        "7z a " + buildName + ".jar resources/. -ssc\n" +
                        // помещаю все ресурсы в jar-файл (сделать позже)
                        //"7z a " + buildName + ".jar " + resourcesDirectoryPath + "/. -ssc\n" +
                        "7z a " + buildName + ".jar SceneManager.sm -ssc\n" +
                        // обновляю манифест в jar
                        "jar umf manifest.txt " + buildName + ".jar\n" +
                        // удаляю все оставшиеся от билда файлы
                        "del /q ApplicationStarter.class ApplicationStarter$1.class manifest.txt openBuilder.bat chcp.com Core2D.jar 7z.exe SceneManager.sm\n" +
                        // удаляю папку core2d
                        "rd /s /q core2d\n" +
                        // удаляю папку resources
                        "rd /s /q resources\n" +
                        "echo build finished. check log.";

                FileUtils.writeToFile(builderBatFile, builderBatFileData, false);

                // создаю файл, который будет открывать builder
                String openBuilderBatFileData = "cd /d " + builderBatFile.getParentFile().getPath() + "\n" +
                        "builder.bat";

                File openBuilderBatFile = FileUtils.createFile(outDirectory.getPath() + "/openBuilder.bat");
                FileUtils.writeToFile(openBuilderBatFile, openBuilderBatFileData, false);

                // открываю файл openBuilder
                ProcessBuilder pb = new ProcessBuilder(outDirectory.getPath() + "/openBuilder.bat");
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
            }
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public static SceneManager packScenes2DInSceneManager(boolean inBuildMatters)
    {
        SceneManager sceneManager = new SceneManager();

        for(Scene2D scene2D : SceneManager.currentSceneManager.getScenes()) {
            Scene2D loadedScene2D = SceneManager.currentSceneManager.loadScene(scene2D.getScenePath());

        }

        return sceneManager;
    }

    // упаковка ресурсов
    private static void packResources(String toDir, SceneManager sceneManager)
    {
        FileUtils.createFolder(toDir);

        for(Scene2D scene2D : sceneManager.getScenes()) {
            for(Layer layer : scene2D.getLayering().getLayers()) {
                for(WrappedObject wrappedObject : layer.getRenderingObjects()) {
                    if(wrappedObject.getObject() instanceof Object2D) {
                        Object2D object2D = (Object2D) wrappedObject.getObject();
                        TextureComponent textureComponent = object2D.getComponent(TextureComponent.class);
                        // относительный путь текстуры (относительно папки проекта)
                        String textureRelativePath = FileUtils.getRelativePath(new File(textureComponent.getTexture2D().getSource()),
                                new File(ProjectsManager.getCurrentProject().getProjectPath()));
                        // новый путь до текстуры
                        File newTextureFile = new File(toDir + "\\" + textureRelativePath);
                        // создаю все папки, которых нет
                        newTextureFile.getParentFile().mkdirs();
                        System.out.println("relative path: " + textureRelativePath);
                        // копирую файл в эти папки
                        FileUtils.copyFile(textureComponent.getTexture2D().getSource(), newTextureFile.getPath(), false);
                        // устанавливаю для текстурного компонента путь в билде (относительный)
                        textureComponent.getTexture2D().setSource("/" + textureRelativePath.replace("\\", "/"));

                        // то же самое для скриптов
                        ScriptComponent scriptComponent = object2D.getComponent(ScriptComponent.class);
                        if(scriptComponent != null) {
                            String scriptRelativePath = FileUtils.getRelativePath(new File(scriptComponent.getScript().getPath()),
                                    new File(ProjectsManager.getCurrentProject().getProjectPath()));
                            File newScriptFile = new File(toDir + "\\" + scriptRelativePath);
                            newScriptFile.getParentFile().mkdirs();
                            FileUtils.copyFile(scriptComponent.getScript().getPath() + ".class", newScriptFile.getPath() + ".class", false);
                            scriptComponent.getScript().setPath("/" + scriptRelativePath.replace("\\", "/"));
                        }
                    }
                }
            }
        }
    }
}
