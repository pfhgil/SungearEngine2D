package Core2D.Core2D;

import Core2D.Utils.Utils;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;

public class Core2DClassLoader extends ClassLoader
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


    private byte[] fetchClassFromFS(String path) throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(new File(path));

        // Get the size of the file
        long length = new File(path).length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+path);
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;

    }

    public <T> Class<T> loadNewClass(String path) throws IOException, ClassNotFoundException
    {
        byte[] classBytes = Files.readAllBytes(Path.of(path));
        Class<?> cls = this.defineClass(null, classBytes, 0, classBytes.length);
        return (Class<T>) this.loadClass(cls.getName());
    }

    public void addClass(Class<?> cls)
    {
        byte[] classBytes = Utils.serializeObject(cls);
        defineClass(cls.getName(), classBytes, 0, classBytes.length);
    }
}
