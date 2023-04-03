package Core2D.DataClasses;

import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;

import java.io.File;
import java.io.InputStream;

public abstract class Data
{
    protected String canonicalPath = "";
    protected String relativePath = "";

    public abstract Data load(String absolutePath);

    public abstract Data load(InputStream inputStream, String absolutePath);

    public void set(Data data) { }

    public void destroy() { }

    public void setNotTransientFields(Data data) { }

    public void fixAbsolutePath()
    {
        File file = new File(canonicalPath);
        if(!file.exists()) {
            if(ProjectsManager.getCurrentProject() != null) {
                file = new File(ProjectsManager.getCurrentProject().getProjectPath() + "/" + canonicalPath);

                try {
                    canonicalPath = file.exists() ? file.getCanonicalPath() : canonicalPath;
                } catch (Exception e) {
                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                }
            }
        }
    }

    public void createRelativePath()
    {
        fixAbsolutePath();

        if(ProjectsManager.getCurrentProject() != null && new File(canonicalPath).exists()) {
            this.relativePath = FileUtils.getRelativePath(
                    new File(canonicalPath),
                    new File(ProjectsManager.getCurrentProject().getProjectPath())
            );
        } else {
            this.relativePath = canonicalPath;
        }
    }

    public String getCanonicalPath() { return canonicalPath; }

    public String getRelativePath() { return relativePath; }
}
