package Core2D.Log;

import Core2D.Utils.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
    public enum MessageType
    {
        SUCCESS,
        INFO,
        WARNING,
        ERROR
    }

    public static class CurrentSession {
        // путь до папки для логов
        private static String directoryPath = "Log";
        // название файла лога для текущей сессии
        private static String currentSessionFileName = "currentSession.txt";
        // файл текущей сессии
        private static File currentSessionFile;

        private static StringBuilder successLog = new StringBuilder();
        private static StringBuilder infoLog = new StringBuilder();
        private static StringBuilder warningLog = new StringBuilder();
        private static StringBuilder errorLog = new StringBuilder();
        private static StringBuilder allLog = new StringBuilder();


        // записывает в файл лога информацию (новая строка)
        public static void println(String string, MessageType messageType) {
            if(new File(directoryPath + File.separator + currentSessionFileName).exists()) {
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(new File(directoryPath + File.separator + currentSessionFileName), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (fileWriter != null) {
                    try {
                        // получаю текущую дату, час, минуту, секунду
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        String str = "";
                        if (messageType == MessageType.SUCCESS) {
                            str = "[#FFFFFF] " + dateFormat.format(date) + " | [#008000] " + string + "\n";
                            successLog.append(str);
                        } else if (messageType == MessageType.INFO) {
                            str = "[#FFFFFF] " + dateFormat.format(date) + " | [#FFFFFF] " + string + "\n";
                            infoLog.append(str);
                        } else if (messageType == MessageType.WARNING) {
                            str = "[#FFFFFF] " + dateFormat.format(date) + " | [#FFFF00] " + string + "\n";
                            warningLog.append(str);
                        } else if (messageType == MessageType.ERROR) {
                            str = "[#FFFFFF] " + dateFormat.format(date) + " | [#FF0000] " + string + "\n";
                            errorLog.append(str);
                        }
                        allLog.append(str);
                        // записываю в файл лога информацию
                        fileWriter.write(str);

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
            }
        }

        // создает файл лога для текущей сессии
        public static void createCurrentSession() {
            File directoryFile = new File(directoryPath);
            File currentSessionFile = new File(directoryPath + File.separator + currentSessionFileName);

            FileUtils.reCreateFolder(directoryFile);
            currentSessionFile = FileUtils.reCreateFile(currentSessionFile);

            if(currentSessionFile.exists()) {
                println("Current session log file was created!", MessageType.SUCCESS);
            }
        }

        public static void clearAllLog()
        {
            successLog.setLength(0);
            infoLog.setLength(0);
            warningLog.setLength(0);
            errorLog.setLength(0);
            allLog.setLength(0);

            successLog.trimToSize();
            infoLog.trimToSize();
            warningLog.trimToSize();
            errorLog.trimToSize();
            allLog.trimToSize();
        }

        public static String getSuccessLog() { return successLog.toString(); }

        public static String getInfoLog() { return infoLog.toString(); }

        public static String getWarningLog() { return warningLog.toString(); }

        public static String getErrorLog() { return errorLog.toString(); }

        public static String getAllLog() { return allLog.toString(); }
    }

    public interface DialogCallback
    {
        void firstButtonClicked();
        void secondButtonClicked();
        void thirdButtonClicked();
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

    // показывает окно предупреждения с выбором
    public static void showWarningChooseDialog(String msg, String leftBtnText, String rightBtnText, DialogCallback dialogCallback)
    {
        Object[] options = { leftBtnText, rightBtnText };

        int n = JOptionPane.showOptionDialog(null,
                msg,
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]);

        if(dialogCallback != null) {
            if(n == 0) {
                dialogCallback.firstButtonClicked();
            } else if (n == 1) {
                dialogCallback.secondButtonClicked();
            }
        }
    }
}
