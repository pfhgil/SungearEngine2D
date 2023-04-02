package Core2D.DataClasses;

import Core2D.Project.ProjectsManager;
import Core2D.Utils.FileUtils;

import java.io.File;
import java.io.InputStream;

public class ShaderData extends Data
{
    public transient String code = "";
    public long lastModified = -1;

    @Override
    public ShaderData load(String absolutePath)
    {
        this.absolutePath = absolutePath;

        code = FileUtils.readAllFile(absolutePath);
        createRelativePath();

        lastModified = getScriptFileLastModified();

        return this;
    }

    @Override
    public ShaderData load(InputStream inputStream, String absolutePath)
    {                                                                       
        code = FileUtils.readAllFile(inputStream);
        this.absolutePath = absolutePath;

        createRelativePath();

        lastModified = getScriptFileLastModified();

        return this;
    }

    public long getScriptFileLastModified()
    {
        File file = new File(getAbsolutePath());
        if(file.exists()) {
            return file.lastModified();
        }

        return -1;
    }

    @Override
    public String getAbsolutePath()
    {
        File file = new File(absolutePath);
        if(file.exists()) {
            return absolutePath;
        } else {
            if(ProjectsManager.getCurrentProject() != null) {
                file = new File(ProjectsManager.getCurrentProject().getProjectPath() + "/" + absolutePath);
                //Log.CurrentSession.println("script path: " + file.getPath(), Log.MessageType.INFO);

                return file.exists() ? file.getPath() : "";
            }
        }

        return "";
    }
}
