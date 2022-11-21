package Core2D.AssetManager;

import Core2D.DataClasses.Data;

/**
 * Asset class.
 */
public class Asset
{
    /**
     * Stores the asset itself.
     */
    private Data assetObject;
    /**
     * Asset path (relative)
     */
    public String path;

    private String className = "";

    public Asset() { }

    public Asset(Data assetObject, String path)
    {
        this.assetObject = assetObject;
        this.path = path;

        className = assetObject.getClass().getCanonicalName();
    }

    public Data getAssetObject() { return assetObject; }
    public void setAssetObject(Data assetObject)
    {
        this.assetObject = assetObject;

        className = assetObject.getClass().getCanonicalName();
    }

    public String getClassName() { return className; }
}
