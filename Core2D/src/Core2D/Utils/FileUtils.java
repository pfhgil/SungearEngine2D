package Core2D.Utils;

import Core2D.Log.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class FileUtils
{
    public static Object deSerializeObject(String path)
    {
        Object obj = null;

        ObjectInputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectInputStream(new FileInputStream(path));

            obj = objectOutputStream.readObject();

            objectOutputStream.close();
            objectOutputStream = null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return obj;
    }

    public static void serializeObject(String path, Object obj)
    {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (objectOutputStream != null) {
                objectOutputStream.writeObject(obj);
                objectOutputStream.close();

                objectOutputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // чтение всего файла
    public static String readAllFile(File file)
    {
        StringBuilder stringBuilder = null; // для операций со строками

        try {
            Scanner scanner = new Scanner(file).useDelimiter("\\A"); // использую разделитель \n
            stringBuilder = new StringBuilder();

            // выполняю пока есть следующая линия
            while(scanner.hasNext()) {
                stringBuilder.append(scanner.next()); // соединяю fileText с scanner.next(). scanner.next - следующая линия
            }
            scanner.close();
            scanner = null;
        } catch (Exception e) {
            System.out.println("[FILE_OPERATIONS_READ_ALL_LINES] Error while reading all file (name: " + file.getName() + ") lines: " + e.toString());
        }

        return stringBuilder.toString();
    }

    public static String readAllFile(InputStream inputStream)
    {
        StringBuilder stringBuilder = null; // для операций со строками

        try {
            stringBuilder = new StringBuilder();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String curLine = "";

            // выполняю пока есть следующая линия
            while((curLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(curLine).append("\n");
            }

            bufferedReader.close();
            inputStream.close();

            bufferedReader = null;
            inputStream = null;
        } catch (Exception e) {
            System.out.println("[FILE_OPERATIONS_READ_ALL_LINES] Error while reading all file (name: " + inputStream.toString() + ") lines: " + e.toString());
        }

        return stringBuilder.toString();
    }

    // записывает данные в файл
    public static void writeToFile(File file, String data, boolean append)
    {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);

            fileWriter.write(data);

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }
    }

    // записывает данные в файл
    public static void writeToFile(File file, byte[] data, boolean append)
    {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, append);

            outputStream.write(data);

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }
    }

    // создание папки
    public static File createFolder(String path)
    {
        // файл с путем path
        File folder = new File(path);
        // если папки не существует, то создаю папку
        if(!folder.exists()) {
            if(!folder.mkdir()) Log.CurrentSession.println("Folder by path '" + path + "' was not created!");
        } else {
            Log.CurrentSession.println("Folder by path '" + path + "' already exists");
        }

        return folder;
    }

    // создание файла
    public static File createFile(String path)
    {
        // файл с путем path
        File file = new File(path);
        // если файла не существует, то создаю файл
        if(!file.exists()) {
            try {
                if(!file.createNewFile()) Log.CurrentSession.println("File by path '" + path + "' was not created!");
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e));
            }
        } else {
            Log.CurrentSession.println("File by path '" + path + "' already exists");
        }

        return file;
    }

    // копирует файл из input stream в файл по пути toPath
    public static void copyFile(InputStream from, String toPath)
    {
        try {
            Files.copy(from, Paths.get(toPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
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
            Log.CurrentSession.println(ExceptionsUtils.toString(e));
        }
    }
}
