package Core2D.DataClasses;

import java.io.InputStream;

public class Data
{
    public Data load(String path)  { return this; }

    public Data load(InputStream inputStream) { return this; }

    public void set(Data data) { }

    public void setNotTransientFields(Data data) { }
}
