package SungearEngine2D.Scripting;

import Core2D.Core2D.Core2D;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;
import SungearEngine2D.Main.Settings;

import java.io.File;
import java.io.IOException;

public class Compiler
{
    public static boolean compileScript(String path)
    {
        boolean result = false;

        File scriptFile = new File(path);
        if(scriptFile.exists()) {
            File compilerPath = new File("./compiler");
            File compilerBat = FileUtils.createFile(scriptFile.getParent() + "/compiler.bat");
            String compilerBatCode =
                    "";
            try {
                compilerBatCode = "set compilerPath=" + compilerPath.getCanonicalPath() + "\n" +
                "%compilerPath%\\chcp.com 65001\n" +
                "set bin=" + Settings.getSettingsFile().getJdkPath() + "\\bin\n" +
                "%bin%\\javac -cp %compilerPath%\\Core2D.jar " + scriptFile.getName() + "\n" +
                "del /q openCompiler.bat";
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e));
            }

            FileUtils.writeToFile(compilerBat, compilerBatCode, false);

            // создаю файл, который будет открывать builder
            String openBuilderBatFileData = "cd /d " + compilerBat.getParentFile().getPath() + "\n" +
                    "compiler.bat";

            File openBuilderBatFile = FileUtils.createFile(scriptFile.getParent() + "/openCompiler.bat");
            FileUtils.writeToFile(openBuilderBatFile, openBuilderBatFileData, false);

            ProcessBuilder pb = new ProcessBuilder(compilerBatCode);
            try {
                Process proc = pb.start();

                String outputString = Utils.outputStreamToString(proc.getOutputStream());
                String errorString = Utils.inputStreamToString(proc.getErrorStream());
                // принт вывода и ошибок
                if(!outputString.equals("")) {
                    Log.CurrentSession.println(outputString);
                }
                if(!errorString.equals("")) {
                    Log.CurrentSession.println(errorString);
                }
                if(outputString.equals("") && errorString.equals("")) {
                    Log.CurrentSession.println("Script " + scriptFile.getName() + " was successfully built!");
                    result = true;
                }

                // жду завершения процесса
                proc.waitFor();

            } catch (InterruptedException | IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e));

                result = false;
            }

            compilerBat.delete();

            return result;
        } else {
            Log.CurrentSession.println("Error compiling script. File \"" + path + "\" does not exist");
            return false;
        }
    }
}
