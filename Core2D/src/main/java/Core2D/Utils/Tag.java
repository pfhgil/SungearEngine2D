package Core2D.Utils;

public class Tag
{
    private String name = "default";

    public Tag(String name)
    {
        this.name = name;
    }

    public Tag() {}

    public void destroy()
    {
        name = null;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
