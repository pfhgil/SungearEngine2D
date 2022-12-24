package Core2D.Core2D;

import Core2D.Log.Log;
import Core2D.Timer.Timer;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Utils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.*;

public class Core2DClassLoader extends URLClassLoader
{
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

    /*
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

    public Class<?> findClass(URL dirURL, String s) {
        System.out.println("s: " + s);
        if (!loadedClasses.contains(s)) {
            byte[] bytes = new byte[0];
            try {
                bytes = loadClassData(dirURL, s);
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
            loadedClasses.add(s);
            System.out.println("s0: " + s);
            return defineClass(s, bytes, 0, bytes.length);
        } else {
            try {
                System.out.println("s1: " + s);
                return super.findClass(s);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
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
    */

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

    public Core2DClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Core2DClassLoader(URL[] urls)
    {
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


    @Override
    public Class<?> loadClass(String name)
    {
        List<Class<?>> classes = new ArrayList<>();
        Field classesField = null;
        try {
            System.out.println("superclass: " + this.getClass().getSuperclass().getSuperclass().getSuperclass());
            classesField = this.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("classes");
            classesField.setAccessible(true);
            classes = (ArrayList<Class<?>>) classesField.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        for(int i = 0; i < classes.size(); i++) {
            if(classes.get(i).getName().equals(name)) {
                classes.set(i, null);
            }
        }

        try {
            return super.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addClass(Class<?> cls)
    {
        byte[] classBytes = Utils.serializeObject(cls);
        defineClass(cls.getName(), classBytes, 0, classBytes.length);
    }

    @Override
    public void addURL(URL url)
    {
        super.addURL(url);
    }

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
}
