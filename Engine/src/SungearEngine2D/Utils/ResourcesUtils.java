package SungearEngine2D.Utils;

import SungearEngine2D.Main.Resources;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

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
}
