package SungearEngine2D.Utils;

import SungearEngine2D.Main.Resources;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResourcesUtils
{
    public static int getIconHandler(File file)
    {
        String fileExtension = FilenameUtils.getExtension(file.getPath());

        if(file.isFile()) {
            return switch (fileExtension) {
                case "png", "jpg" -> Resources.Textures.Icons.imageFileIcon.getTextureHandler();
                case "java" -> Resources.Textures.Icons.javaFileIcon.getTextureHandler();
                case "txt" -> Resources.Textures.Icons.textFileIcon.getTextureHandler();
                default -> Resources.Textures.Icons.unknownFileIcon.getTextureHandler();
            };
        } else {
            return Resources.Textures.Icons.directoryIcon.getTextureHandler();
        }
    }

    public static boolean isFileImage(File file)
    {
        String fileExtension = FilenameUtils.getExtension(file.getPath());
        return switch (fileExtension) {
            case "png", "jpg" -> true;
            default -> false;
        };
    }

    public static boolean isFilePrefab(File file)
    {
        String fileExtension = FilenameUtils.getExtension(file.getPath());
        return switch (fileExtension) {
            case "sgopref", "sgcpref" -> true;
            default -> false;
        };
    }

    public static int getNumOfAllSystemFiles()
    {
        int num = 0;

        File[] disks = File.listRoots();
        for(File disk : disks) {
            num += getNumOfFilesInDir(disk);
        }

        return num;
    }

    private static int getNumOfFilesInDir(File dir)
    {
        int num = 0;
        File[] files = dir.listFiles();
        if(files != null) {
            num += files.length;

            for(File f : files) {
                if(f.isDirectory()) {
                    num += getNumOfFilesInDir(f);
                }
            }
        }

        return num;
    }

    public static List<String> findOnlyOneJdkBinPath()
    {
        List<String> foundPath = new ArrayList<>();

        File[] disks = File.listRoots();
        for(File disk : disks) {
            foundPath.addAll(inspectDirectory(disk));
        }

        return foundPath;
    }

    public static List<String> findAllJdksBinPaths()
    {
        List<String> foundPath = new ArrayList<>();

        File[] disks = File.listRoots();
        for(File disk : disks) {
            foundPath.addAll(inspectDirectory(disk));
        }

        return foundPath;
    }

    public static List<String> findAllJdksBinPaths(String fromPath)
    {
        List<String> foundPath = new ArrayList<>();

        File startFile = new File(fromPath);
        foundPath.addAll(inspectDirectory(startFile));

        return foundPath;
    }

    private static List<String> inspectDirectory(File dir)
    {
        File[] listFiles = dir.listFiles();
        List<String> foundPath = new ArrayList<>();

        if(listFiles != null) {
            for (File f : listFiles) {
                if(f.isDirectory()) {
                    foundPath.addAll(inspectDirectory(f));
                } else {
                    if (f.getName().equals("javac.exe")) {
                        foundPath.add(f.getParent());
                        break;
                    }
                }
            }
        }

        return foundPath;
    }
}
