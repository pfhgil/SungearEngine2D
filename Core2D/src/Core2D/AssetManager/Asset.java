package Core2D.AssetManager;

/**
 * Asset class.
 */
public class Asset
{
    /**
     * Stores the asset itself.
     */
    public Object assetObject;
    /**
     * Asset name
     */
    public String name;

    public Asset() { }

    public Asset(Object assetObject, String name)
    {
        this.assetObject = assetObject;
        this.name = name;
    }
}
