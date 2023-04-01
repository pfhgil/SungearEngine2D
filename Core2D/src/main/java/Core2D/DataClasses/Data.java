package Core2D.DataClasses;

import java.io.InputStream;

public abstract class Data
{
    protected String path = "";

    public abstract Data load(String path);

    public abstract Data load(InputStream inputStream, String path);

    public void set(Data data) { }

    public void setNotTransientFields(Data data) { }

    public String getPath() { return path; }
}
