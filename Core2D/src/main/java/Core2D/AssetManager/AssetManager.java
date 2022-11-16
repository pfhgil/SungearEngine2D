package Core2D.AssetManager;

import Core2D.Audio.AudioInfo;
import Core2D.Core2D.Core2D;
import Core2D.Drawable.Font;
import Core2D.Log.Log;
import Core2D.Shader.Shader;
import Core2D.Texture2D.Texture2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * It is needed for storing all assets and for their proper use.
 */
public class AssetManager
{
    private static AssetManager instance;
    /**
     * All assets.
     */
    private List<Asset> assets = new ArrayList<>();

    /**
     * Initializes the base assets
     */
    public void init()
    {
        //String textInstancingVertexShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/Text/Instancing/vertexShader.glsl"));
        //String textInstancingFragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/Text/Instancing/fragmentShader.glsl"));

        //String progressBarFragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/ProgressBar/fragmentShader.glsl"));

        String object2DShaderPath = "/data/shaders/object2D/shader.glsl";
        String objects2DInstancingShaderPath = "/data/shaders/object2D/instancing/shader.glsl";
        String line2DShaderPath = "/data/shaders/primitives/line2D/shader.glsl";
        String lines2DInstancingShaderPath = "/data/shaders/primitives/line2D/instancing/shader.glsl";

        String whiteTexturePath = "/data/textures/white_texture.png";
        String defaultProgressBarTexturePath = "/data/textures/ui/progressBar/progress_bar.png";

        String comicSansSMFontPath = "/data/fonts/ComicSansSM/cssm";

        Shader object2DShader = Shader.loadShader(Core2D.class.getResourceAsStream(object2DShaderPath));
        Shader objects2DInstancingShader = Shader.loadShader(Core2D.class.getResourceAsStream(objects2DInstancingShaderPath));
        Shader line2DShader = Shader.loadShader(Core2D.class.getResourceAsStream(line2DShaderPath));
        Shader lines2DInstancingShader = Shader.loadShader(Core2D.class.getResourceAsStream(lines2DInstancingShaderPath));

        Texture2D whiteTexture = new Texture2D(Core2D.class.getResourceAsStream(whiteTexturePath));
        Texture2D defaultProgressBarTexture = new Texture2D(Core2D.class.getResourceAsStream(defaultProgressBarTexturePath));

        Font comicSansSM = new Font(comicSansSMFontPath, true);

        addAsset(new Asset(object2DShader, object2DShaderPath));
        addAsset(new Asset(objects2DInstancingShader, objects2DInstancingShaderPath));
        addAsset(new Asset(line2DShader, line2DShaderPath));
        addAsset(new Asset(lines2DInstancingShader, lines2DInstancingShaderPath));

        addAsset(new Asset(whiteTexture, whiteTexturePath));
        addAsset(new Asset(defaultProgressBarTexture, defaultProgressBarTexturePath));

        addAsset(new Asset(comicSansSM, comicSansSMFontPath));
    }

    /**
     * Adds a new asset.
     * If an asset with the same name already exists, an error is output to the log,
     * in another case, it adds a new asset to the list of all assets.
     */
    public void addAsset(Asset asset)
    {
        for(Asset a : assets) {
            if (a.path.equals(asset.path)) {
                Log.CurrentSession.println("Error while adding asset \"" + asset.path + "\". Asset with this path is already exists", Log.MessageType.ERROR);
                break;
            }
        }

        assets.add(asset);
    }

    /**
     * @param path Relative path of asset.
     * @return Null if the asset is not found and shader program if the asset is found.
     */
    public Shader getShaderProgram(String path)
    {
        for(Asset asset : assets) {
            if(asset.path.equals(path) && asset.assetObject instanceof Shader) {
                return (Shader) asset.assetObject;
            }
        }

        return null;
    }

    /**
     * @param path Name of asset.
     * @return Null if the asset is not found and texture if the asset is found.
     */
    public Texture2D getTexture2D(String path)
    {
        for(Asset asset : assets) {
            if(asset.path.equals(path) && asset.assetObject instanceof Texture2D) {
                return (Texture2D) asset.assetObject;
            }
        }

        return null;
    }

    /**
     * @param path Name of asset.
     * @return Null if the asset is not found and font if the asset is found.
     */
    public Font getFont(String path)
    {
        for(Asset asset : assets) {
            if(asset.path.equals(path) && asset.assetObject instanceof Font) {
                return (Font) asset.assetObject;
            }
        }

        return null;
    }

    public AudioInfo getAudioInfo(String path)
    {
        for(Asset asset : assets) {
            if(asset.path.equals(path) && asset.assetObject instanceof AudioInfo) {
                return (AudioInfo) asset.assetObject;
            }
        }

        return null;
    }

    /**
     * @param path Name of asset.
     * @return Null if the asset is not found and asset if the asset is found.
     */
    public Asset getAsset(String path)
    {
        for(Asset asset : assets) {
            if(asset.path.equals(path)) {
                return asset;
            }
        }

        return null;
    }

    public static AssetManager getInstance()
    {
        return instance = Objects.requireNonNullElseGet(instance, () -> new AssetManager());
    }
}
