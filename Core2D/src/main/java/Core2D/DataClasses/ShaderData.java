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
        this.canonicalPath = absolutePath;

        code = FileUtils.readAllFile(absolutePath);
        createRelativePath();

        lastModified = getShaderFileLastModified();

        return this;
    }

    @Override
    public ShaderData load(InputStream inputStream, String absolutePath)
    {                                                                       
        code = FileUtils.readAllFile(inputStream);
        this.canonicalPath = absolutePath;

        createRelativePath();

        lastModified = getShaderFileLastModified();

        return this;
    }

    public long getShaderFileLastModified()
    {
        File file = new File(getCanonicalPath());
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
