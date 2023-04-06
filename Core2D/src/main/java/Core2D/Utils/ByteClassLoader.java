package Core2D.Utils;

import Core2D.Log.Log;

import java.net.URL;
import java.net.URLClassLoader;

public class ByteClassLoader extends URLClassLoader
{
    public ByteClassLoader()
    {
        super(new URL[]{});
    }

    public Class<?> loadClass(byte[] bytes, String className)
    {
        //byte[] bytes = Utils.getByteBufferBytes(Utils.resourceToByteBuffer(resource));
        Log.CurrentSession.println("bytes length: " + bytes.length, Log.MessageType.ERROR);

        return defineClass(className, bytes, 0, bytes.length);
    }
}
