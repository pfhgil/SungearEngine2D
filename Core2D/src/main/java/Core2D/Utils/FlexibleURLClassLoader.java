package Core2D.Utils;

import Core2D.Log.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FlexibleURLClassLoader extends URLClassLoader
{
    private final Map<String, Class<?>> definedClassesPath = new HashMap<>();

    public FlexibleURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public FlexibleURLClassLoader(URL[] urls) {
        super(urls);
    }

    public Class<?> loadNewClass(String path)
    {
        if(definedClassesPath.containsKey(path)) {
            return definedClassesPath.get(path);
        }

        //Log.Console.println("path: " + path);
        if(!(new File(path).exists())) return null;
        try {
            byte[] classBytes = Files.readAllBytes(Path.of(path));
            Class<?> definedClass = this.defineClass(null, classBytes, 0, classBytes.length);
            definedClassesPath.put(path, definedClass);
            return definedClass;
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    @Override
    public void addURL(URL url)
    {
        super.addURL(url);
    }
}
