package Core2D.DataClasses;

import java.io.InputStream;

public class Data
{
    protected String path = "";

    public Data load(String path) { return this; }

    public Data load(InputStream inputStream, String path) { return this; }

    public void set(Data data) { }

    public void setNotTransientFields(Data data) { }

    public String getPath() { return path; }
}
