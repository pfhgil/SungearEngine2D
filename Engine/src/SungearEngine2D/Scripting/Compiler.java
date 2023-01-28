package SungearEngine2D.Scripting;

import Core2D.AssetManager.Asset;
import Core2D.AssetManager.AssetManager;
import Core2D.DataClasses.ShaderData;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Tasks.StoppableTask;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Utils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.EngineSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Compiler
{
    private static List<String> notCompiledScripts = new ArrayList<>();

    private static List<Shader> shadersToCompile = new ArrayList<>();
    public static void compileAllShaders()
    {
        Iterator<Shader> shadersIterator = shadersToCompile.iterator();
        while(shadersIterator.hasNext()) {
            Shader shader = shadersIterator.next();

            String shaderFullPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + shader.path;
            long lastModified = new File(shaderFullPath).lastModified();

            if(shader.lastModified != lastModified) {
                System.out.println("asset reloaded: " + shader.path);
                Asset reloadedShaderData = AssetManager.getInstance().reloadAsset(shader.path, ShaderData.class);

                boolean compiled = shader.compile((ShaderData) reloadedShaderData.getAssetObject());

                if (compiled) {
                    shadersIterator.remove();

                    ViewsManager.getBottomMenuView().leftSideInfo = "Shader " + shader.path + " was successfully compiled!";
                    ViewsManager.getBottomMenuView().leftSideInfoColor.set(0.0f, 1.0f, 0.0f, 1.0f);

                    Log.CurrentSession.println("Shader " + shader.path + " was successfully compiled!", Log.MessageType.SUCCESS);
                } else {
                    ViewsManager.getBottomMenuView().leftSideInfo = "Shader " + shader.path + " was not compiled. See the log for details";
                    ViewsManager.getBottomMenuView().leftSideInfoColor.set(1.0f, 0.0f, 0.0f, 1.0f);
                }

                shader.lastModified = lastModified;
            }
        }
    }

    public static void addShaderToCompile(Shader shader)
    {
        boolean[] shaderExists = new boolean[] { false };

        shadersToCompile.forEach(s -> {
            if(s.path.equals(shader.path)) {
                shaderExists[0] = true;
            }
        });

        if(!shaderExists[0]) {
            shadersToCompile.add(shader);
        }
    }

    public static boolean compileScript(String path)
    {
        boolean result = false;

        File scriptFile = new File(path);
        if(scriptFile.exists()) {
            File compilerPath = new File(".\\compiler");

            try {
                Process proc = Runtime.getRuntime().exec(compilerPath.getCanonicalPath() + "\\chcp.com 65001");

                // жду завершения процесса
                proc.waitFor();

                String command = "\"" + ProjectsManager.getCurrentProject().getProjectSettings().getJdkPath() +
                        "\\bin\\javac\" -sourcepath " +
                        "\"" + ProjectsManager.getCurrentProject().getScriptsPath() + "\" " +
                        "-classpath " +
                        "\"" + compilerPath.getCanonicalPath() + "\\Core2D.jar\" " +
                        "\"" + scriptFile.getCanonicalPath() + "\"";

                System.out.println("command:  " + command);

                proc = Runtime.getRuntime().exec(command);

                // жду завершения процесса
                proc.waitFor();

                String outputString = Utils.outputStreamToString(proc.getOutputStream());
                String errorString = Utils.inputStreamToString(proc.getErrorStream());
                // принт вывода и ошибок
                if (!outputString.equals("")) {
                    Log.CurrentSession.println(outputString, Log.MessageType.ERROR);
                }
                if (!errorString.equals("")) {
                    Log.CurrentSession.println(errorString, Log.MessageType.ERROR);
                }
                if (!errorString.equals("") || !outputString.equals("")) {
                    if (notCompiledScripts.contains(scriptFile.getName())) {
                        notCompiledScripts.add(scriptFile.getName());
                    }
                    ViewsManager.getBottomMenuView().leftSideInfo = "Script " + scriptFile.getName() + " was not compiled. Fix all errors before entering the playmode";
                    ViewsManager.getBottomMenuView().leftSideInfoColor.set(1.0f, 0.0f, 0.0f, 1.0f);
                }
                if (outputString.equals("") && errorString.equals("")) {
                    ViewsManager.getBottomMenuView().leftSideInfo = "Script " + scriptFile.getName() + " was successfully compiled!";
                    ViewsManager.getBottomMenuView().leftSideInfoColor.set(0.0f, 1.0f, 0.0f, 1.0f);
                    Log.CurrentSession.println("Script " + scriptFile.getName() + " was successfully built!", Log.MessageType.SUCCESS);
                    result = true;
                    notCompiledScripts.remove(scriptFile.getName());
                }

                EngineSettings.Playmode.canEnterPlaymode = notCompiledScripts.size() == 0;
            } catch (InterruptedException | IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
            return result;
        } else {
            Log.CurrentSession.println(new RuntimeException("Error compiling script. File \"" + path + "\" does not exist"), Log.MessageType.ERROR);
            return false;
        }
    }

    public static List<String> getNotCompiledScripts() { return notCompiledScripts; }
}
