package Core2D.DataClasses;

import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.ExceptionsUtils;
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
    public ScriptData load(String absolutePath)
    {
        this.canonicalPath = absolutePath;

        try {
            data = Files.readAllBytes(Path.of(absolutePath));
            //Log.CurrentSession.println("script data modified", Log.MessageType.SUCCESS);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        createRelativePath();

        lastModified = getScriptFileLastModified();

        return this;
    }

    @Override
    public ScriptData load(InputStream inputStream, String absolutePath)
    {
        try {
            data = IOUtils.toByteArray(inputStream);
            //Log.CurrentSession.println("script data modified", Log.MessageType.SUCCESS);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
        this.canonicalPath = absolutePath;

        createRelativePath();

        lastModified = getScriptFileLastModified();

        return this;
    }

    public long getScriptFileLastModified()
    {
        File file = new File(getCanonicalPath().replaceAll(".class", ".java"));
        if(file.exists()) {
            return file.lastModified();
        }

        return -1;
    }

    @Override
    public String getCanonicalPath()
    {
        File file = new File(canonicalPath);
        if(file.exists()) {
            return canonicalPath;
        } else {
            if(ProjectsManager.getCurrentProject() != null) {
                file = new File(ProjectsManager.getCurrentProject().getProjectPath() + "/" + canonicalPath);

                return file.exists() ? file.getPath() : "";
            }
        }

        return "";
    }
}
