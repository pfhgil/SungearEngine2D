package sungear.script;

import sungear.attribute.SceneObjectAttribute;
import sungear.exception.SungearEngineError;
import sungear.exception.SungearEngineException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScriptProvider {
    private ScriptClassLoaderFactory scriptClassLoaderFactory;
    private CompileService compileService;

    public ScriptProvider(ScriptClassLoaderFactory scriptClassLoaderFactory, CompileService compileService) {
        this.scriptClassLoaderFactory = scriptClassLoaderFactory;
        this.compileService = compileService;
    }

    public Class<?> loadScript(File scriptPath) throws ClassNotFoundException, IOException {
        ScriptClassLoader scriptClassLoader = scriptClassLoaderFactory.getClassLoader();
        return scriptClassLoader.loadScript(scriptPath);
    }

    private void register(Class<?> clazz) {
        if (SceneObjectAttribute.class.equals(clazz)) {
            // TODO: add to index for UI search and core initialization
        }
    }

    public void loadScripts(List<File> scriptPath) {
//        scriptPath
    }

    public void loadScripts(String dirPath) throws SungearEngineException, IOException, ClassNotFoundException {
        File scriptsDir = new File(dirPath);
        if (!scriptsDir.isDirectory()) {
            throw new SungearEngineException(SungearEngineError.SCRIPT_DIR_DOES_NOT_EXIST, dirPath);
        }
        for (File file : scriptsDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                Class<?> clazz = loadScript(file);
                register(clazz);
            }
        }
    }

}
