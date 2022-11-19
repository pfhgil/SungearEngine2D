package Core2D.DataClasses;

import Core2D.Utils.FileUtils;

import java.io.InputStream;

public class ShaderData extends Data
{
    private String sourceCode = "";

    @Override
    public ShaderData load(String path)
    {
        sourceCode = FileUtils.readAllFile(path);
        return this;
    }

    @Override
    public ShaderData load(InputStream inputStream)
    {
        sourceCode = FileUtils.readAllFile(inputStream);
        return this;
    }

    public String getSourceCode() { return sourceCode; }
}
