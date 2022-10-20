package SungearEngine2D.Scripting;

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
import java.util.List;

public class Compiler
{
    private static List<String> notCompiledScripts = new ArrayList<>();

    public static boolean compileScript(String path)
    {
        final boolean[] result = {false};

        File scriptFile = new File(path);
        if(scriptFile.exists()) {
            File compilerPath = new File(".\\compiler");

            ViewsManager.getBottomMenuView().addTaskToList(new StoppableTask("Compiling script " + scriptFile.getName() + "... ", 1.0f, 0.0f) {
                @Override
                public void run() {
                    try {
                        Process proc = Runtime.getRuntime().exec(compilerPath.getCanonicalPath() + "\\chcp.com 65001");

                        // жду завершения процесса
                        proc.waitFor();

                        String command = "\"" + ProjectsManager.getCurrentProject().getProjectSettings().getJdkPath() +
                                "\\bin\\javac\" -cp " +
                                "\"" + compilerPath.getCanonicalPath() + "\\Core2D.jar\" " +
                                "\"" + scriptFile.getCanonicalPath() + "\"";

                        System.out.println(command);

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
                            ViewsManager.getBottomMenuView().leftSideInfo = "Script " + scriptFile.getName() + " was successfully built!";
                            ViewsManager.getBottomMenuView().leftSideInfoColor.set(0.0f, 1.0f, 0.0f, 1.0f);
                            Log.CurrentSession.println("Script " + scriptFile.getName() + " was successfully built!", Log.MessageType.SUCCESS);
                            result[0] = true;
                            notCompiledScripts.remove(scriptFile.getName());
                        }

                        EngineSettings.Playmode.canEnterPlaymode = notCompiledScripts.size() == 0;
                    } catch (InterruptedException | IOException e) {
                        Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);

                    }

                    destination++;
                }
            });

            return result[0];
        } else {
            Log.CurrentSession.println("Error compiling script. File \"" + path + "\" does not exist", Log.MessageType.ERROR);
            return false;
        }
    }

    public static List<String> getNotCompiledScripts() { return notCompiledScripts; }
}
