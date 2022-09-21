package Core2D.Utils;

import Core2D.Log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class ByteClassLoader extends URLClassLoader
{
    public ByteClassLoader()
    {
        super(new URL[]{});
    }

    public Class<?> loadClass(InputStream resource, String className)
    {
        try {
            byte[] bytes = Utils.getByteBufferBytes(Utils.resourceToByteBuffer(resource));
            Log.CurrentSession.println("bytes length: " + bytes.length, Log.MessageType.ERROR);
            if(bytes.length != 0) {
                return defineClass(className, bytes, 0, bytes.length);
            }
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }
}
