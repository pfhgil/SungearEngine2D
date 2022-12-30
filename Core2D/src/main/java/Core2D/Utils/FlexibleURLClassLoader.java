package Core2D.Utils;

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
    public void addURL(URL url)
    {
        super.addURL(url);
    }
}
