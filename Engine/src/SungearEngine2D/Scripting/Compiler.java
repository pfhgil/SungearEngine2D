package SungearEngine2D.Scripting;

import Core2D.Core2D.Core2D;
import Core2D.Log.Log;
import Core2D.Utils.FileUtils;
import SungearEngine2D.Main.Settings;

import java.io.File;

public class Compiler
{
    public static void compileScript(String path)
    {
        File scriptFile = new File(path);
        if(scriptFile.exists()) {
            FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/Core2D.jar"), scriptFile.getParent());
            FileUtils.copyFile(Core2D.class.getResourceAsStream("/data/other/chcp.com"), scriptFile.getParent());

            File compilerBat = FileUtils.createFile(scriptFile.getParent() + "/compiler.bat");
            String compilerBatCode =
                    "chcp 65001\n" +
                    "set bin=" + Settings.getSettingsFile().getJdkPath() + "\\bin";
        } else {
            Log.CurrentSession.println("Error compiling script. File \"" + path + "\" does not exist");
        }
    }
}
