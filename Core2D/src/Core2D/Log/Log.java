package Core2D.Log;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
    public static class CurrentSession {
        // путь до папки для логов
        private static String directoryPath = "Log";
        // название файла лога для текущей сессии
        private static String currentSessionFileName = "currentSession.txt";
        // файл текущей сессии
        private static File currentSessionFile;

        private static StringBuilder log = new StringBuilder();

        // записывает в файл лога информацию (новая строка)
        public static void println(String string) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(currentSessionFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fileWriter != null) {
                try {
                    // получаю текущую дату, час, минуту, секунду
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    String str = dateFormat.format(date) + "|" + string + "\n";
                    log.append(str);
                    // записываю в файл лога информацию
                    fileWriter.write(str);

                    dateFormat = null;
                    date = null;

                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    showWarningDialog("LOG: String was not logged!");
                }
            } else {
                showWarningDialog("LOG: String was not logged!");
            }

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    showWarningDialog("LOG: File writer thread was not closed!");
                }
            }

            string = null;
            fileWriter = null;
        }

        // создает файл лога для текущей сессии
        public static void createCurrentSession() {
            File directoryFile = new File(directoryPath);
            File _currentSessionFile = new File(directoryPath + "/" + currentSessionFileName);

            // проверяю, существует ли папка для логов
            if (!directoryFile.exists()) {
                // если папка не существует, создаю её
                if (directoryFile.mkdir()) {
                    if (_currentSessionFile.exists()) {
                        if (!_currentSessionFile.delete()) {
                            showWarningDialog("LOG: Current session log file was not deleted!");
                        }
                    }
                    try {
                        if (_currentSessionFile.createNewFile()) {
                            currentSessionFile = _currentSessionFile;

                            println("Log directory was not founded! It was created.");
                            println("Current session log file was created!");
                        } else {
                            showWarningDialog("LOG: Current session log file was not created!");
                        }
                    } catch (IOException e) {
                        showWarningDialog("LOG: Current session log file was not created!");
                    }
                } else {
                    showWarningDialog("LOG: Log directory was not created!");
                }
            } else {
                if (_currentSessionFile.exists()) {
                    if (!_currentSessionFile.delete()) {
                        showWarningDialog("LOG: Current session log file was not deleted!");
                    }
                }
                try {
                    if (_currentSessionFile.createNewFile()) {
                        currentSessionFile = _currentSessionFile;

                        println("Log directory was founded!");
                        println("Current session log file was created!");
                    } else {
                        showWarningDialog("LOG: Current session log file was not created!");
                    }
                } catch (IOException e) {
                    showWarningDialog("LOG: Current session log file was not created!");
                }
            }
        }

        public static String getLog() { return log.toString(); }
    }

    // показывает окно предупреждения
    public static void showWarningDialog(String msg)
    {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    // показывает окно ошибки
    public static void showErrorDialog(String msg)
    {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // показывает окно информации
    public static void showInfoDialog(String msg)
    {
        JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
