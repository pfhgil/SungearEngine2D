package Core2D.Core2D;

import Core2D.Utils.Utils;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Core2DClassLoader extends URLClassLoader
{
    public Core2DClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Core2DClassLoader(URL[] urls) {
        super(urls);
    }
    /*
    public Class loadClass(URL url, String name) throws ClassNotFoundException {
        try {
            String urlStr = url.toString() + File.separator + name.replace(".", File.separator) + ".class";
            System.out.println(urlStr);
            URL newURL = new URL(urlStr);
            URLConnection connection = newURL.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while(data != -1){
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            return defineClass(name,
                    classData, 0, classData.length);

        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

     */

    //private List<String> loadedClasses = new ArrayList<>();

    public byte[] loadClassData(URL dirURL, String name) throws ClassNotFoundException, IOException {
        String url = dirURL.toString() + File.separator + name.replace(".", File.separator) + ".class";
        URL myUrl = new URL(url);
        URLConnection connection = myUrl.openConnection();
        InputStream input = connection.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int data = input.read();

        while (data != -1) {
            buffer.write(data);
            data = input.read();
        }

        input.close();

        return buffer.toByteArray();
    }
    public void addClass(Class<?> cls)
    {
        byte[] classBytes = Utils.serializeObject(cls);
        defineClass(cls.getName(), classBytes, 0, classBytes.length);
    }

    public void addURL(URL url)
    {
        super.addURL(url);
    }

    /*
    public boolean isURLExists(URL url)
    {
        for(URL url1 : getURLs()) {
            if(url.equals(url1)) {
                return true;
            }
        }

        return false;
    }

    public void updateURL(URL url)
    {
        for(int i = 0; i < getURLs().length; i++) {
            if(url.equals(getURLs()[i])) {
                getURLs()[i] = url;
            }
        }
    }

     */

    /*
    public Class loadClass(URL url, String name) throws ClassNotFoundException {
        try {
            String urlStr = url.toString() + File.separator + name.replace(".", File.separator) + ".class";
            System.out.println(urlStr);
            URL newURL = new URL(urlStr);
            URLConnection connection = newURL.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();
            while(data != -1){
                buffer.write(data);
                data = input.read();
            }
            input.close();
            byte[] classData = buffer.toByteArray();
            return defineClass(name,
                    classData, 0, classData.length);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
        return null;
    }
     */

    public <T> Class<T> loadNewClass(String path) throws IOException, ClassNotFoundException
    {
        System.out.println("class to load path: " + path);
        byte[] classBytes = Files.readAllBytes(Path.of(path));
        System.out.println("class bytes0: " + Arrays.toString(classBytes));
        Class<?> cls = this.defineClass(null, classBytes, 0, classBytes.length);
        return (Class<T>) this.loadClass(cls.getName());
    }
}
