package Core2D.Utils;

public class Tag
{
    private String name = "default";

    public Tag(String name)
    {
        this.name = name;
        name = null;
    }

    public Tag() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
