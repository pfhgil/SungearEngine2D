package Core2D.Utils;

import Core2D.Log.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class FileUtils
{
    public static File findFile(File dir, String parentName, String name) {
        File result = null; // no need to store result as String, you're returning File anyway
        File[] dirlist  = dir.listFiles();

        if(dirlist != null) {
            for (int i = 0; i < dirlist.length; i++) {
                if (dirlist[i].isDirectory()) {
                    result = findFile(dirlist[i], name);
                    if (result != null) break; // recursive call found the file; terminate the loop
                } if (dirlist[i].getName().matches(name) && dirlist[i].getParentFile().getName().matches(parentName)) {
                    return dirlist[i]; // found the file; return it
                }
            }
        }
        return result; // will return null if we didn't find anything
    }

    public static File findFile(File dir, String name) {
        File result = null; // no need to store result as String, you're returning File anyway
        File[] dirlist  = dir.listFiles();

        if(dirlist != null) {
            for (int i = 0; i < dirlist.length; i++) {
                if (dirlist[i].isDirectory()) {
                    result = findFile(dirlist[i], name);
                    if (result != null) break; // recursive call found the file; terminate the loop
                } if (dirlist[i].getName().matches(name)) {
                    return dirlist[i]; // found the file; return it
                }
            }
        }
        return result; // will return null if we didn't find anything
    }

    public static Object deSerializeObject(String path)
    {
        Object obj = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectOutputStream = new ObjectInputStream(fileInputStream);

            obj = objectOutputStream.readObject();

            objectOutputStream.close();
            fileInputStream.close();

            objectOutputStream = null;
            fileInputStream = null;
        } catch (IOException | ClassNotFoundException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return obj;
    }

    public static Object deSerializeObject(InputStream inputStream)
    {
        Object obj = null;

        ObjectInputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectInputStream(inputStream);

            obj = objectOutputStream.readObject();

            objectOutputStream.close();
            objectOutputStream = null;
        } catch (IOException | ClassNotFoundException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return obj;
    }

    public synchronized static void serializeObject(String path, Object obj)
    {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);

            oos.close();
            fos.close();

            oos = null;
            fos = null;
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    // ???????????? ?????????? ??????????
    public static String readAllFile(File file)
    {
        StringBuilder stringBuilder = null; // ?????? ???????????????? ???? ????????????????

        try {
            Scanner scanner = new Scanner(file).useDelimiter("\\A"); // ?????????????????? ?????????????????????? \n
            stringBuilder = new StringBuilder();

            // ???????????????? ???????? ???????? ?????????????????? ??????????
            while(scanner.hasNext()) {
                stringBuilder.append(scanner.next()); // ???????????????? fileText ?? scanner.next(). scanner.next - ?????????????????? ??????????
            }
            scanner.close();
            scanner = null;
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            //System.out.println("[FILE_OPERATIONS_READ_ALL_LINES] Error while reading all file (name: " + file.getName() + ") lines: " + e.toString());
        }

        return stringBuilder.toString();
    }

    public static String readAllFile(InputStream inputStream)
    {
        StringBuilder stringBuilder = null; // ?????? ???????????????? ???? ????????????????

        try {
            stringBuilder = new StringBuilder();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String curLine = "";

            // ???????????????? ???????? ???????? ?????????????????? ??????????
            while((curLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(curLine).append("\n");
            }

            bufferedReader.close();
            inputStream.close();

            bufferedReader = null;
            inputStream = null;
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            //System.out.println("[FILE_OPERATIONS_READ_ALL_LINES] Error while reading all file (name: " + inputStream.toString() + ") lines: " + e.toString());
        }

        return stringBuilder.toString();
    }

    // ???????????????????? ???????????? ?? ????????
    public static void writeToFile(File file, String data, boolean append)
    {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);

            fileWriter.write(data);

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    // ???????????????????? ???????????? ?? ????????
    public static void writeToFile(File file, byte[] data, boolean append)
    {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, append);

            outputStream.write(data);

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    // ???????????????? ??????????
    public static File createFolder(String path)
    {
        // ???????? ?? ?????????? path
        File folder = new File(path);
        // ???????? ?????????? ???? ????????????????????, ???? ???????????? ??????????
        if(!folder.exists()) {
            if(!folder.mkdir()) Log.CurrentSession.println("Folder by path '" + path + "' was not created!", Log.MessageType.ERROR);
        } else {
            Log.CurrentSession.println("Folder by path '" + path + "' already exists", Log.MessageType.ERROR);
        }

        return folder;
    }

    // ???????????????? ??????????
    public static File createFile(String path)
    {
        // ???????? ?? ?????????? path
        File file = new File(path);
        // ???????? ?????????? ???? ????????????????????, ???? ???????????? ????????
        if(!file.exists()) {
            try {
                if(!file.createNewFile()) Log.CurrentSession.println("File by path '" + path + "' was not created!", Log.MessageType.ERROR);
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        } else {
            Log.CurrentSession.println("File by path '" + path + "' already exists", Log.MessageType.ERROR);
        }

        return file;
    }

    // ???????????????? ???????? ???? input stream ?? ???????? ???? ???????? toPath
    public static void copyFile(InputStream from, String toPath)
    {
        try {
            Files.copy(from, Paths.get(toPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    // ???????????????? ???????? ???? ?????????? ???? ???????? fromPath ?? ???????? ???? ???????? toPath
    public static void copyFile(String fromPath, String toPath, boolean isDirectory)
    {
        try {
            if(!isDirectory) {
                Files.copy(Paths.get(fromPath), Paths.get(toPath), StandardCopyOption.REPLACE_EXISTING);
            } else {
                org.apache.commons.io.FileUtils.copyDirectory(new File(fromPath), new File(toPath));
            }
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }
}
