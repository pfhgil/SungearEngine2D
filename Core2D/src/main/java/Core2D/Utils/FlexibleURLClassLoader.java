package Core2D.Utils;

import Core2D.Log.Log;

import java.net.URL;
import java.net.URLClassLoader;

public class FlexibleURLClassLoader extends URLClassLoader
{
    public FlexibleURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public FlexibleURLClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    public Class<?> findClass(String name)
    {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
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
