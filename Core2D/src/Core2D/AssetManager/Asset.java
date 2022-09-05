package Core2D.AssetManager;

public class Asset
{
    private Object asset;
    private String name;

    public Asset(Object asset, String name)
    {
        this.asset = asset;
        this.name = name;
    }

    public Object getAsset() { return asset; }
    public void setAsset(Object asset) { this.asset = asset; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
