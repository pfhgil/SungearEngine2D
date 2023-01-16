package sungear.script;

public class ScriptClassLoaderFactoryImpl implements ScriptClassLoaderFactory {
    private static final String CLASS_LOADER_NAME = "ScriptClassLoader";

    public ScriptClassLoader getClassLoader() {
        return new ScriptClassLoader(CLASS_LOADER_NAME, this.getClass().getClassLoader());
    }

}
