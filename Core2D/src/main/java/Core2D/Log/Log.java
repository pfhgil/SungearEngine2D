package Core2D.Log;

import Core2D.Utils.FileUtils;

import javax.swing.*;
import java.io.File;
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

    public static class Console
    {
        public static boolean willPrint = true;

        public static void println(Object obj, boolean appendCallableMethodDescription)
        {
            if(!willPrint) return;

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            StringBuilder strBuilder = new StringBuilder(dateFormat.format(date));
            if(appendCallableMethodDescription) {
                StackTraceElement[] ste = Thread.currentThread().getStackTrace();

                int depth = ste[2].getMethodName().equals("println") ? 3 : 2;

                strBuilder.append(" | Called from: ")
                        .append(ste[depth].getClassName())
                        .append("::").append(ste[depth].getMethodName())
                        .append(". Line: ")
                        .append(ste[depth].getLineNumber())
                        .append(" [#FFFFFF]");
            }

            strBuilder.append(" | ").append(obj);

            System.out.println(strBuilder);
        }

        public static void println(Object obj)
        {
            println(obj, false);
        }
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

        public static boolean willPrintToFile = true;


        // записывает в файл лога информацию (новая строка)
        // appendCallableMethodDescription - к строке будет присоединена строка описание метода, откуда был вызван метод println
        public static void println(Object obj, MessageType messageType, boolean appendCallableMethodDescription) {
            Console.println(obj, appendCallableMethodDescription);

            if(!willPrintToFile) return;

            File logFile = new File(directoryPath + File.separator + currentSessionFileName);
            if (logFile.exists()) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                StringBuilder strBuilder = new StringBuilder("[#FFFFFF] ").append(dateFormat.format(date.getTime()));
                if (appendCallableMethodDescription) {
                    StackTraceElement[] ste = Thread.currentThread().getStackTrace();

                    int depth = ste[2].getMethodName().equals("println") ? 3 : 2;

                    strBuilder.append(" | [#9932CC] Called from: ")
                            .append(ste[depth].getClassName())
                            .append("::").append(ste[depth].getMethodName())
                            .append(". Line: ")
                            .append(ste[depth].getLineNumber())
                            .append(" [#FFFFFF]");
                }

                switch (messageType) {
                    case SUCCESS -> {
                        strBuilder.append(" | [#008000] ").append(obj).append("\n");
                        successLog.append(strBuilder);
                    }
                    case INFO -> {
                        strBuilder.append(" | [#FFFFFF] ").append(obj).append("\n");
                        infoLog.append(strBuilder);
                    }
                    case WARNING -> {
                        strBuilder.append(" | [#FFFF00] ").append(obj).append("\n");
                        warningLog.append(strBuilder);
                    }
                    case ERROR -> {
                        strBuilder.append(" | [#FF0000] ").append(obj).append("\n");
                        errorLog.append(strBuilder);
                    }
                }

                allLog.append(strBuilder);

                FileUtils.writeToFile(logFile, strBuilder.toString(), true);
            }
        }

        public static void println(Object obj, MessageType messageType) {
            println(obj, messageType, false);
        }

        // создает файл лога для текущей сессии
        public static void createCurrentSession() {
            File directoryFile = new File(directoryPath);
            File currentSessionFile = new File(directoryPath + File.separator + currentSessionFileName);

            FileUtils.reCreateFolder(directoryFile);
            currentSessionFile = FileUtils.reCreateFile(currentSessionFile);

            if(currentSessionFile.exists()) {
                Console.println("Current session log file was created!");
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
