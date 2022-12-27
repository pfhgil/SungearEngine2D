package Core2D.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class SingleClassLoader extends ClassLoader
{
    public String classPath = "";

    public SingleClassLoader() { }

    public SingleClassLoader(ClassLoader parent)
    {
        super(parent);
    }

    public <T> Class<T> loadNewClass(String path) throws IOException, ClassNotFoundException
    {
        classPath = path;

        System.out.println("class to load path: " + path);
        byte[] classBytes = Files.readAllBytes(Path.of(path));
        System.out.println("class bytes0: " + Arrays.toString(classBytes));
        Class<?> cls = this.defineClass(null, classBytes, 0, classBytes.length);
        return (Class<T>) this.loadClass(cls.getName());
    }
}
