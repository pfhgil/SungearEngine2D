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
    public ShaderData load(String path)
    {
        code = FileUtils.readAllFile(path);
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
    public ShaderData load(InputStream inputStream, String path)
    {                                                                       
        code = FileUtils.readAllFile(inputStream);
        this.path = path;

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
