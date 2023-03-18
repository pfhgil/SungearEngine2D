package Core2D.DataClasses;

import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScriptData extends Data
{
    public transient byte[] data = new byte[0];
    public long lastModified;

    @Override
    public ScriptData load(String path)
    {
        try {
            data = Files.readAllBytes(Path.of(path));
            //Log.CurrentSession.println("script data modified", Log.MessageType.SUCCESS);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        if(ProjectsManager.getCurrentProject() != null) {
            this.path = FileUtils.getRelativePath(
                    new File(path),
                    new File(ProjectsManager.getCurrentProject().getProjectPath())
            );
        } else {
            this.path = path;
        }

        lastModified = getScriptFileLastModified();

        return this;
    }

    @Override
    public ScriptData load(InputStream inputStream, String path)
    {
        try {
            data = IOUtils.toByteArray(inputStream);
            //Log.CurrentSession.println("script data modified", Log.MessageType.SUCCESS);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
        this.path = path;

        lastModified = getScriptFileLastModified();

        return this;
    }

    public long getScriptFileLastModified()
    {
        File file = new File(getAbsolutePath().replace(".class", ".java"));
        if(file.exists()) {
            return file.lastModified();
        }

        return -1;
    }

    public String getAbsolutePath()
    {
        File file = new File(path);
        if(file.exists()) {
            return path;
        } else {
            if(ProjectsManager.getCurrentProject() != null) {
                file = new File(ProjectsManager.getCurrentProject().getProjectPath() + "/" + path);
                //Log.CurrentSession.println("script path: " + file.getPath(), Log.MessageType.INFO);

                return file.exists() ? file.getPath() : "";
            }
        }

        return "";
    }
}
