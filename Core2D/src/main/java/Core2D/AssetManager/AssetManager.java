package Core2D.AssetManager;

import Core2D.Audio.AudioInfo;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DMode;
import Core2D.DataClasses.Data;
import Core2D.DataClasses.ShaderData;
import Core2D.DataClasses.Texture2DData;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Shader.Shader;
import Core2D.Utils.ExceptionsUtils;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        ShaderData object2DShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(object2DShaderPath));
        ShaderData objects2DInstancingShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(objects2DInstancingShaderPath));
        ShaderData line2DShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(line2DShaderPath));
        ShaderData lines2DInstancingShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(lines2DInstancingShaderPath));

        Texture2DData whiteTextureData = new Texture2DData().load(Core2D.class.getResourceAsStream(whiteTexturePath));
        Texture2DData defaultProgressBarTextureData = new Texture2DData().load(Core2D.class.getResourceAsStream(defaultProgressBarTexturePath));

        addAsset(new Asset(object2DShaderData, object2DShaderPath));
        addAsset(new Asset(objects2DInstancingShaderData, objects2DInstancingShaderPath));
        addAsset(new Asset(line2DShaderData, line2DShaderPath));
        addAsset(new Asset(lines2DInstancingShaderData, lines2DInstancingShaderPath));

        addAsset(new Asset(whiteTextureData, whiteTexturePath));
        addAsset(new Asset(defaultProgressBarTextureData, defaultProgressBarTexturePath));
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
    public ShaderData getShaderData(String path)
    {
        return getAssetObject(path, ShaderData.class);
    }

    /**
     * @param path Name of asset.
     * @return Null if the asset is not found and texture if the asset is found.
     */
    public Texture2DData getTexture2DData(String path)
    {
        return getAssetObject(path, Texture2DData.class);
    }

    public AudioInfo getAudioInfo(String path)
    {
        return getAssetObject(path, AudioInfo.class);
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

    public <T> T getAssetObject(String path, Class<T> assetObjectClass)
    {
        T assetObj = null;
        for(Asset asset : assets) {
            if(asset.path.equals(path) && asset.assetObject.getClass().isAssignableFrom(assetObjectClass)) {
                assetObj = assetObjectClass.cast(asset.assetObject);
            }
        }

        if(assetObj == null) {
            if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                if(ProjectsManager.getCurrentProject() != null) {
                    String fullPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + path;
                    Object objToCast = null;
                    if (assetObjectClass.getSuperclass().isAssignableFrom(Data.class)) {
                        try {
                            objToCast = ((Data) assetObjectClass.newInstance()).load(fullPath);
                        } catch (InstantiationException | IllegalAccessException e) {
                            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                        }
                    }

                    if(objToCast != null) {
                        assetObj = assetObjectClass.cast(objToCast);
                    }
                }
            } else {
                Object objToCast = null;
                if (assetObjectClass.getSuperclass().isAssignableFrom(Data.class)) {
                            objToCast = ((Data) assetObjectClass.cast(new Data())).load(Core2D.class.getResourceAsStream(path));
                }

                if(objToCast != null) {
                    assetObj = assetObjectClass.cast(objToCast);
                }
            }

            assets.add(new Asset(assetObj, path));
            System.out.println("added new asset! data type: " + assetObj.getClass().getSimpleName() + ", path: " + path);
        }
        System.out.println("found asset! data type: " + assetObj.getClass().getSimpleName() + ", path: " + path);

        return assetObj;
    }

    public static AssetManager getInstance()
    {
        return instance = Objects.requireNonNullElseGet(instance, () -> new AssetManager());
    }
}
