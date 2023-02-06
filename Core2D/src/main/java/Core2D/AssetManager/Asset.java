package Core2D.AssetManager;

/**
 * Asset class.
 */
public class Asset
{
    /**
     * Stores the asset itself.
     */
    private Object assetObject;
    /**
     * Asset path (relative)
     */
    public String path;

    private String assetObjectClassName = "";

    public Asset() { }

    public Asset(Object assetObject, String path)
    {
        this.assetObject = assetObject;
        this.path = path;

        if(assetObject != null) {
            assetObjectClassName = assetObject.getClass().getCanonicalName();
        }
    }

    public Object getAssetObject() { return assetObject; }
    public void setAssetObject(Object assetObject)
    {
        this.assetObject = assetObject;

        if(assetObject != null) {
            assetObjectClassName = assetObject.getClass().getCanonicalName();
        }
    }

   public String getAssetObjectClassName() { return assetObjectClassName; }
}
