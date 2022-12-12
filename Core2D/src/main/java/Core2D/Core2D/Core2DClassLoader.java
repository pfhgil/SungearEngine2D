package Core2D.Core2D;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Vector;

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
