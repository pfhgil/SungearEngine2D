package sungear.script;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ScriptClassLoader extends ClassLoader {
    public ScriptClassLoader(String name, ClassLoader parent) {
        super(name, parent);
    }

    public Class<?> loadScript(File script) throws ClassNotFoundException, IOException {
        byte[] classBytes = Files.readAllBytes(script.toPath());
        Class<?> clazz = this.defineClass(null, classBytes, 0, classBytes.length);
        return this.loadClass(clazz.getName());
    }
}
