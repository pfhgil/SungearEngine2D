package Core2D.Utils;

import Core2D.Log.Log;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class FileUtils
{
    public static File findFile(File dir, String parentName, String name) {
        File parentFile = findFile(dir, parentName);
        if(parentFile != null) {
            return new File(parentFile.getPath() + "\\" + name);
        }
        return null;
    }

    public static String getRelativePath(File file, File folder)
    {
        String filePath = file.getAbsolutePath();
        String folderPath = folder.getAbsolutePath();
        if (filePath.startsWith(folderPath)) {
            return filePath.substring(folderPath.length() + 1);
        } else {
            return null;
        }
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

    public static Object deSerializeObject(String path) { return deSerializeObject(new File(path)); }
    public static Object deSerializeObject(File file)
    {
        Object obj = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectOutputStream = new ObjectInputStream(fileInputStream);

            obj = objectOutputStream.readObject();

            objectOutputStream.close();
            fileInputStream.close();
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
        } catch (IOException | ClassNotFoundException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return obj;
    }

    public static File reCreateFolder(String path){ return reCreateFolder(new File(path)); }
    public static File reCreateFolder(File folder){
        if (folder.exists()) {
            folder.delete();
        }
        return createFolder(folder);
    }

    public static File reCreateFile(String path){ return reCreateFile(new File(path)); }
    public static File reCreateFile(File file){
        if (file.exists()) {
            file.delete();
        }
        return createFile(file);
    }

    public static void serializeObject(String path, Object obj) { serializeObject(new File(path), obj); }
    public static void serializeObject(File file, Object obj)
    {
        reCreateFile(file);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);

            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    // чтение всего файла
    public static String readAllFile(String path) { return readAllFile(new File(path)); }
    public static String readAllFile(File file)
    {
        StringBuilder stringBuilder = new StringBuilder(); // для операций со строками

        try {
            Scanner scanner = new Scanner(file).useDelimiter("\\A"); // использую разделитель \n
            stringBuilder = new StringBuilder();

            // выполняю пока есть следующая линия
            while(scanner.hasNext()) {
                stringBuilder.append(scanner.next()); // соединяю fileText с scanner.next(). scanner.next - следующая линия
            }
            scanner.close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);;
        }

        return stringBuilder.toString();
    }

    public static String readAllFile(InputStream inputStream)
    {
        StringBuilder stringBuilder = new StringBuilder(); // для операций со строками

        try {
            stringBuilder = new StringBuilder();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream, false,
                    ByteOrderMark.UTF_8,
                    ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE,
                    ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE)));

            String curLine = "";

            // выполняю пока есть следующая линия
            while((curLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(curLine).append("\n");
            }

            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return stringBuilder.toString().replace("\uFEFF", "");
    }

    // записывает данные в файл
    public static void writeToFile(String path, String data, boolean append) { writeToFile(new File(path), data, append); }
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
    // записывает данные в файл
    public static void writeToFile(String path, byte[] data, boolean append) { writeToFile(new File(path), data, append); }
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

    // создание папки
    public static File createFolder(String path) {return createFolder(new File(path)); }
    public static File createFolder(File folder)
    {
        // если папки не существует, то создаю папку
        if(!folder.exists()) {
            if(!folder.mkdir()) {
                Log.CurrentSession.println("Folder by path '" + folder.getAbsolutePath() + "' was not created!", Log.MessageType.ERROR);
            }
        } else {
            Log.CurrentSession.println("Folder by path '" + folder.getAbsolutePath() + "' already exists", Log.MessageType.ERROR);
        }

        return folder;
    }

    // создание файла
    public static File createFile(String path)
    {
        // файл с путем path
        return createFile(new File(path));
    }
    public static File createFile(File file)
    {
        // если файла не существует, то создаю файл
        if(!file.exists()) {
            try {
                if(!file.createNewFile()) {
                    Log.CurrentSession.println("File by path '" + file.getPath() + "' was not created!", Log.MessageType.ERROR);
                }
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        } else {
            Log.CurrentSession.println("File by path '" + file.getPath() + "' already exists", Log.MessageType.ERROR);
        }

        return file;
    }

    // копирует файл из input stream в файл по пути toPath
    public static void copyFile(InputStream from, String toPath)
    {
        try {
            Files.copy(from, Paths.get(toPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    // копирует файл из файла по пути fromPath в файл по пути toPath
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
