package Core2D.DataClasses;

import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ShaderData extends Data
{
    private transient String sourceCode = "";

    @Override
    public ShaderData load(String path)
    {
        sourceCode = FileUtils.readAllFile(path);
        if(ProjectsManager.getCurrentProject() != null) {
            this.path = FileUtils.getRelativePath(
                    new File(path),
                    new File(ProjectsManager.getCurrentProject().getProjectPath())
            );
        } else {
            this.path = path;
        }
        return this;
    }

    @Override
    public ShaderData load(InputStream inputStream, String path)
    {
        sourceCode = FileUtils.readAllFile(inputStream);
        this.path = path;
        return this;
    }

    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
}
