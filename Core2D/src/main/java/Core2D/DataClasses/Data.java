package Core2D.DataClasses;

import Core2D.Project.ProjectsManager;
import Core2D.Utils.FileUtils;

import java.io.File;
import java.io.InputStream;

public abstract class Data
{
    protected String absolutePath = "";
    protected String relativePath = "";

    public abstract Data load(String absolutePath);

    public abstract Data load(InputStream inputStream, String absolutePath);

    public void set(Data data) { }

    public void destroy() { }

    public void setNotTransientFields(Data data) { }

    public void createRelativePath()
    {
        if(ProjectsManager.getCurrentProject() != null) {
            this.relativePath = FileUtils.getRelativePath(
                    new File(absolutePath),
                    new File(ProjectsManager.getCurrentProject().getProjectPath())
            );
        } else {
            this.relativePath = absolutePath;
        }
    }

    public String getAbsolutePath() { return absolutePath; }

    public String getRelativePath() { return relativePath; }
}
