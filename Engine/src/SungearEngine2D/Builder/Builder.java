package SungearEngine2D.Builder;

import Core2D.Audio.AudioInfo;
import Core2D.Component.Components.AudioComponent;
import Core2D.Component.Components.ScriptComponent;
import Core2D.Component.Components.TextureComponent;
import Core2D.Core2D.Core2D;
import Core2D.Drawable.Object2D;
import Core2D.Layering.Layer;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.Scene2DStoredValues;
import Core2D.Scene2D.SceneManager;
import Core2D.Tasks.StoppableTask;
import Core2D.Tasks.Task;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;
import Core2D.Utils.WrappedObject;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Scripting.Compiler;
import SungearEngine2D.Utils.AppData.AppDataManager;
import SungearEngine2D.exception.SungearEngineError;
import SungearEngine2D.exception.SungearEngineException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Builder {
    public static String outDirectoryPath = "";

    public static void startBuild() {
        startBuild(AppDataManager.getSettings().lastBuildName);
    }

    public static void startBuild(String buildName) {
        Log.CurrentSession.println("Attention! Builder uses a separate thread to build the game, so all OpenGL commands won't work.", Log.MessageType.WARNING);
        AppDataManager.getSettings().lastBuildName = buildName;
        build();
    }

    private static void build() {
        if (ProjectsManager.getCurrentProject() != null) {
            // создаю SceneManager с выбранными сценами
            SceneManager sceneManager = new SceneManager();
            ViewsManager.getBottomMenuView().addTaskToList(new StoppableTask("Creating the necessary files...", 3.0f, 0.0f) {
                @Override
                public void run() {
                    SceneManager.saveSceneManager(ProjectsManager.getCurrentProject().getProjectPath() + "\\SceneManager.sm");
                    current++;
                    File outDirectory = new File(AppDataManager.getSettings().lastBuildOutPath);
                    current++;
                    for (Scene2DStoredValues storedValues : SceneManager.currentSceneManager.getScene2DStoredValues()) {
                        if (storedValues.inBuild) {
                            Scene2DStoredValues newStoredValues = new Scene2DStoredValues();
                            newStoredValues.path = storedValues.path;
                            newStoredValues.inBuild = true;
                            newStoredValues.isMainScene2D = storedValues.isMainScene2D;

                            sceneManager.getScene2DStoredValues().add(newStoredValues);
                        }
                    }
                    current++;

                    text = "Preparing resources for packaging...";
                    current = 0.0f;
                    destination = sceneManager.getScene2DStoredValues().size();

                    //SceneManager.currentSceneManager.setCurrentScene2D(new Scene2D());
                    prepareResources(outDirectory.getPath() + "\\resources", sceneManager, this);
                    SceneManager.saveSceneManager(outDirectory.getPath() + "/SceneManager.sm", sceneManager);

                    text = "Copying necessary files...";
                    destination = 5.0f;
                    current = 0.0f;

                    File applicationStarterClassFile = new File(".\\compiler\\ApplicationStarter.class");
                    File applicationStarterClassFile1 = new File(".\\compiler\\ApplicationStarter$1.class");
                    // копирую файл Core2D.jar в папку sourcesDirectoryPath
                    FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/Core2D.jar"), outDirectory.getPath() + "/Core2D.jar");
                    current++;
                    // копирую файл кодировки в папку sourcesDirectoryPath
                    FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/chcp.com"), outDirectory.getPath() + "/chcp.com");
                    current++;
                    // копирую файл 7z.exe
                    FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/7z.exe"), outDirectory.getPath() + "/7z.exe");
                    current++;
                    Compiler.compileScript(".\\compiler\\ApplicationStarter.java");
                    // копирую файл ApplicationStarter
                    try {
                        FileUtils.copyFile(applicationStarterClassFile.getCanonicalPath(), outDirectory.getPath() + "/ApplicationStarter.class", false);
                        current++;
                        FileUtils.copyFile(applicationStarterClassFile1.getCanonicalPath(), outDirectory.getPath() + "/ApplicationStarter$1.class", false);
                        current++;
                    } catch (IOException e) {
                        Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                        current += 2.0f;
                    }

                    text = "Creating manifest file...";
                    destination = 1.0f;
                    current = 0.0f;

                    // создаю файл манифеста
                    String manifestFileData = "Manifest-Version: 1.0\n" +
                            "Main-Class: ApplicationStarter\n" +
                            "Class-Path: Core2D.jar\n";
                    File manifestFile = FileUtils.createFile(outDirectory.getPath() + "/manifest.txt");
                    FileUtils.writeToFile(manifestFile, manifestFileData, false);
                    current++;

                    text = "Creating builder.bat file...";
                    destination = 2.0f;
                    current = 0.0f;

                    File builderBatFile = FileUtils.createFile(outDirectory.getPath() + "\\builder.bat");
                    current++;
                    String builderBatFileData = "@echo off\n" +
                            // устанавливаю кодировку, чтобы был русский текст
                            "chcp 65001\n" +
                            // указываю путь до bin jdk
                            "set bin=\"" + ProjectsManager.getCurrentProject().getProjectSettings().getJdkPath() + "\"\n" +
                            // указываю путь до javac
                            "set ppath=%bin%;\"" + ProjectsManager.getCurrentProject().getProjectSettings().getJdkPath() + "\\javac.exe\"\n" +
                            // создаю jar с именем buildName, классом ApplicationStarter, манифестом
                            "jar cvmf manifest.txt " + AppDataManager.getSettings().lastBuildName + ".jar ApplicationStarter.class ApplicationStarter$1.class\n" +
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
                            "7z a " + AppDataManager.getSettings().lastBuildName + ".jar core2d/. -ssc\n" +
                            // помещаю все ресурсы в jar-файл buildName
                            "7z a " + AppDataManager.getSettings().lastBuildName + ".jar resources/. -ssc\n" +
                            // помещаю все ресурсы в jar-файл (сделать позже)
                            //"7z a " + buildName + ".jar " + resourcesDirectoryPath + "/. -ssc\n" +
                            "7z a " + AppDataManager.getSettings().lastBuildName + ".jar SceneManager.sm -ssc\n" +
                            // обновляю манифест в jar
                            "jar umf manifest.txt " + AppDataManager.getSettings().lastBuildName + ".jar\n" +
                            // удаляю все оставшиеся от билда файлы
                            "del /q ApplicationStarter.class ApplicationStarter$1.class manifest.txt openBuilder.bat chcp.com 7z.exe SceneManager.sm\n" +
                            // удаляю папку core2d
                            "rd /s /q core2d\n" +
                            // удаляю папку resources
                            "rd /s /q resources\n" +
                            "echo build finished. check log.";
                    FileUtils.writeToFile(builderBatFile, builderBatFileData, false);
                    current++;
                    // создаю файл, который будет открывать builder
                    String openBuilderBatFileData = "cd /d " + builderBatFile.getParentFile().getPath() + "\n" +
                            "builder.bat";

                    text = "Creating openBuilder.bat file...";
                    destination = 2.0f;
                    current = 0.0f;

                    File openBuilderBatFile = FileUtils.createFile(outDirectory.getPath() + "/openBuilder.bat");
                    current++;
                    FileUtils.writeToFile(openBuilderBatFile, openBuilderBatFileData, false);
                    current++;

                    text = "Starting openBuilder.bat...";
                    destination = 3.0f;
                    current = 0.0f;

                    // открываю файл openBuilder
                    ProcessBuilder pb = new ProcessBuilder(outDirectory.getPath() + "/openBuilder.bat");
                    destination++;
                    try {
                        Process toJarBuildProc = pb.start();
                        destination++;
                        // принт вывода и ошибок
                        Log.CurrentSession.println(Utils.outputStreamToString(toJarBuildProc.getOutputStream()), Log.MessageType.INFO);
                        Log.CurrentSession.println(Utils.inputStreamToString(toJarBuildProc.getErrorStream()), Log.MessageType.ERROR);
                        // жду завершения процесса
                        toJarBuildProc.waitFor();

                        text = "Translating jar to exe...";
                        destination = 3.0f;
                        current = 0.0f;

                        URL resource = Builder.class.getClassLoader().getResource("utils/win/bat2exe.bat");
                        String[] classPathLib = new String[] { "Core2D.jar" };
                        File javaPath = new File(ProjectsManager.getCurrentProject().getProjectSettings().getJdkPath() + "\\bin\\java.exe");
                        File jarSourceFolder = new File(outDirectory.getPath());
                        File exeTargetFolder = new File(outDirectory.getPath());
                        File bat2ExeUtilPath = new File(resource.getPath().replace("%20", " "));
                        Builder b = new Builder().validateFileSystemState(jarSourceFolder, exeTargetFolder, bat2ExeUtilPath);
                        destination++;
                        b.createBat(javaPath, AppDataManager.getSettings().lastBuildName, classPathLib, jarSourceFolder);
                        destination++;
                        Process toExeBuildProc = b.createExe(jarSourceFolder, exeTargetFolder, bat2ExeUtilPath);
                        destination++;

                        // принт вывода и ошибок
                        Log.CurrentSession.println(Utils.outputStreamToString(toExeBuildProc.getOutputStream()), Log.MessageType.INFO);
                        Log.CurrentSession.println(Utils.inputStreamToString(toExeBuildProc.getErrorStream()), Log.MessageType.ERROR);

                        toExeBuildProc.waitFor();
                    } catch (InterruptedException | IOException | SungearEngineException e) {
                        Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                    }
                    destination++;
                    // удаление оставшихся файлов
                    builderBatFile.delete();
                    File core2DFile = new File(builderBatFile.getParent() + "\\Core2D.jar");
                    File gameJarFile = new File(builderBatFile.getParent() + "\\" + AppDataManager.getSettings().lastBuildName + ".jar");
                    File gameBatFile = new File(builderBatFile.getParent() + "\\" + AppDataManager.getSettings().lastBuildName + ".bat");
                    if(core2DFile.exists()) {
                        core2DFile.delete();
                    }
                    if(gameJarFile.exists()) {
                        gameJarFile.delete();
                    }
                    if(gameBatFile.exists()) {
                        gameBatFile.delete();
                    }

                    ViewsManager.getBottomMenuView().leftSideInfo = "File " + AppDataManager.getSettings().lastBuildName + ".jar was successfully built!";
                    ViewsManager.getBottomMenuView().leftSideInfoColor.set(0.0f, 1.0f, 0.0f, 1.0f);
                }
            });
        }
    }

    // подготовка ресурсов
    private static void prepareResources(String toDir, SceneManager sceneManager, Task task) {
        FileUtils.createFolder(toDir);
        // лист всех сцен, которые нужно сохранить для билда
        List<Scene2D> scenes2DToSaveInBuild = new ArrayList<>();
        // сначала загружаю все сцены
        for (Scene2DStoredValues storedValues : sceneManager.getScene2DStoredValues()) {
            Scene2D scene2D = sceneManager.loadScene(storedValues.path);
            if (scene2D != null) {
                //scene2D.applyScriptsTempValues();
                scenes2DToSaveInBuild.add(scene2D);
            }
        }
        // далее пробегаюсь по всем сценам и изменяю путь ресурсов у кажого объекта на относительный
        for (Scene2D scene2D : scenes2DToSaveInBuild) {
            for (Layer layer : scene2D.getLayering().getLayers()) {
                for (WrappedObject wrappedObject : layer.getRenderingObjects()) {
                    if (wrappedObject.getObject() instanceof Object2D) {
                        Object2D object2D = (Object2D) wrappedObject.getObject();

                        TextureComponent textureComponent = object2D.getComponent(TextureComponent.class);
                        if(textureComponent != null) {
                            // новый путь до текстуры
                            File newFile = new File(toDir + "\\" + textureComponent.getTexture2D().path);
                            // создаю все папки, которых нет
                            newFile.getParentFile().mkdirs();
                            // копирую файл в эти папки
                            FileUtils.copyFile(ProjectsManager.getCurrentProject().getProjectPath() +
                                            File.separator +
                                            textureComponent.getTexture2D().path,
                                    newFile.getPath(), false);
                            // устанавливаю для текстурного компонента путь в билде (относительный)
                            textureComponent.getTexture2D().path = "/" + textureComponent.getTexture2D().path.replace("\\", "/");
                        }

                        // то же самое для скриптов
                        List<ScriptComponent> scriptComponents = object2D.getAllComponents(ScriptComponent.class);
                        for(ScriptComponent scriptComponent : scriptComponents) {
                            File newFile = new File(toDir + "\\" + scriptComponent.getScript().path);
                            newFile.getParentFile().mkdirs();
                            FileUtils.copyFile(ProjectsManager.getCurrentProject().getProjectPath() +
                                            File.separator +
                                            scriptComponent.getScript().path + ".class",
                                    newFile.getPath() + ".class", false);
                            scriptComponent.getScript().path = "/" + scriptComponent.getScript().path.replace("\\", "/");
                        }

                        // то же самое для аудио
                        List<AudioComponent> audioComponents = object2D.getAllComponents(AudioComponent.class);
                        for(AudioComponent audioComponent : audioComponents) {
                            File newFile = new File(toDir + "\\" + audioComponent.audio.path);
                            newFile.getParentFile().mkdirs();
                            FileUtils.copyFile(ProjectsManager.getCurrentProject().getProjectPath() +
                                            File.separator +
                                            audioComponent.audio.path,
                                    newFile.getPath(), false);
                            audioComponent.audio.path = "/" + audioComponent.audio.path.replace("\\", "/");
                        }
                    }
                }
            }
            // относительный путь сцены (относительно папки проекта)
            String scene2DRelativePath = FileUtils.getRelativePath(new File(scene2D.getScenePath()), new File(ProjectsManager.getCurrentProject().getProjectPath()));
            System.out.println("scene2d relative path: " + scene2DRelativePath);
            // новый путь до сцены
            File newScene2DFile = new File(toDir + "\\" + scene2DRelativePath);
            // создаю все папки, которых нет
            newScene2DFile.getParentFile().mkdirs();
            String newScene2DPath = "/" + scene2DRelativePath.replace("\\", "/");
            // сохраняю сцену
            sceneManager.saveScene(scene2D, newScene2DFile.getPath(), false);
            // копирую сцену в эти папки
            //FileUtils.copyFile(scene2D.getScenePath(), newScene2DFile.getPath(), false);
            // нахожу нужные сохраняемые данные и изменяю путь до сцены
            sceneManager.getScene2DStoredValues()
                    .stream()
                    .filter(p -> p.path.equals(scene2D.getScenePath()))
                    .findFirst()
                    .get().path = newScene2DPath;
            scene2D.setScenePath(newScene2DPath);
        }
        task.current++;
    }

//    public static void main(String[] args) throws IOException, SungearEngineException {
//        URL resource = Builder.class.getClassLoader().getResource("utils/win/bat2exe.exe");
//        String gameName = "MyGame";
//        String[] classPathLib = new String[] {"Core2D.jar"};
//        File javaPath = new File("java");
//        File jarSourceFolder = new File("Q:\\download\\test\\source");
//        File exeTargetFolder = new File("Q:\\download\\test\\target");
//        File bat2ExeUtilPath = new File(resource.getPath());
//        new Builder().validateFileSystemState(jarSourceFolder, exeTargetFolder, bat2ExeUtilPath)
//                .createBat(javaPath, gameName, classPathLib, jarSourceFolder)
//                .createExe(jarSourceFolder, exeTargetFolder, bat2ExeUtilPath);
//    }

    public Builder createBat(File javaPath, String gameName, String[] classPathLibs, File jarSourceFolder) throws IOException {
        File batFile = new File(String.format("%s/%s.bat", jarSourceFolder.getAbsolutePath(), gameName));
        batFile.createNewFile();
        Files.writeString(batFile.toPath(), String.format("%s -cp \"%s\" -jar \"%s.jar\"", javaPath.getAbsolutePath(), String.join(";", classPathLibs), gameName));
        return this;
    }

    public Process createExe(File jarSourceFolder, File exeTargetFolder, File bat2ExeUtilPath) throws SungearEngineException, IOException {
        return Runtime.getRuntime().exec(String.format("%s /source:%s /target:%s /s /y", bat2ExeUtilPath.getAbsolutePath(), jarSourceFolder.getAbsolutePath(), exeTargetFolder.getAbsolutePath()));
    }

    private Builder validateFileSystemState(File jarBuildFolder, File exeBuildFolder, File batToExeUtilPath) throws SungearEngineException {
        if (!jarBuildFolder.canRead()) {
            throw new SungearEngineException(SungearEngineError.THERE_ARE_NO_PERMISSIONS, String.format("read file or dir %s", batToExeUtilPath.getAbsolutePath()));
        }
        if (!batToExeUtilPath.canExecute()) {
            throw new SungearEngineException(SungearEngineError.THERE_ARE_NO_PERMISSIONS, String.format("execute file or dir %s", batToExeUtilPath.getAbsolutePath()));
        }
        if (!batToExeUtilPath.canRead()) {
            throw new SungearEngineException(SungearEngineError.THERE_ARE_NO_PERMISSIONS, String.format("read file or dir %s", batToExeUtilPath.getAbsolutePath()));
        }
        exeBuildFolder.mkdirs();
        if (!exeBuildFolder.canRead()) {
            throw new SungearEngineException(SungearEngineError.THERE_ARE_NO_PERMISSIONS, String.format("read file or dir %s", batToExeUtilPath.getAbsolutePath()));
        }
        if (!exeBuildFolder.canWrite()) {
            throw new SungearEngineException(SungearEngineError.THERE_ARE_NO_PERMISSIONS, String.format("write file or dir %s", batToExeUtilPath.getAbsolutePath()));
        }
        return this;
    }

}
