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
     * Asset path (relative)
     */
    public String path;

    public Asset() { }

    public Asset(Object assetObject, String path)
    {
        this.assetObject = assetObject;
        this.path = path;
    }
}
