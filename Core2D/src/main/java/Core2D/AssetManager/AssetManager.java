package Core2D.AssetManager;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Core2DWorkMode;
import Core2D.DataClasses.*;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * It is needed for storing all assets and for their proper use.
 */
public class AssetManager implements Serializable
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

        String entityDefaultShaderPath = "/data/shaders/mesh/default_shader.glsl";
        String entitiesInstancingShaderPath = "/data/shaders/mesh/instancing_shader.glsl";
        String entityPickingShader = "/data/shaders/mesh/picking_shader.glsl";

        String line2DShaderPath = "/data/shaders/primitives/line/shader.glsl";
        String lines2DInstancingShaderPath = "/data/shaders/primitives/line/instancing/shader.glsl";

        String postprocessingDefaultShaderPath = "/data/shaders/postprocessing/postprocessing_default_shader.glsl";
        String postprocessingTest0ShaderPath = "/data/shaders/postprocessing/postprocessing_test0_shader.glsl";
        String postprocessingTest1ShaderPath = "/data/shaders/postprocessing/postprocessing_test1_shader.glsl";

        String whiteTexturePath = "/data/textures/white_texture.png";
        String defaultProgressBarTexturePath = "/data/textures/ui/progressBar/progress_bar.png";

        // models ------------------------------
        String planeObjectPath = "/data/models/plane.obj";
        String planeNormalizedObjectPath = "/data/models/plane_normalized.obj";
        String sphereObjectPath = "/data/models/Tiger_I.obj";
        // -------------------------------------

        ShaderData entityDefaultShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(entityDefaultShaderPath), entityDefaultShaderPath);
        ShaderData entitiesInstancingShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(entitiesInstancingShaderPath), entitiesInstancingShaderPath);
        ShaderData entityPickingShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(entityPickingShader), entityPickingShader);

        ShaderData line2DShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(line2DShaderPath), line2DShaderPath);
        ShaderData lines2DInstancingShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(lines2DInstancingShaderPath), lines2DInstancingShaderPath);

        ShaderData postprocessingDefaultShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(postprocessingDefaultShaderPath), postprocessingDefaultShaderPath);
        ShaderData postprocessingTest0ShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(postprocessingTest0ShaderPath), postprocessingTest0ShaderPath);
        ShaderData postprocessingTest1ShaderData = new ShaderData().load(Core2D.class.getResourceAsStream(postprocessingTest1ShaderPath), postprocessingTest1ShaderPath);

        Texture2DData whiteTextureData = new Texture2DData().load(Core2D.class.getResourceAsStream(whiteTexturePath), whiteTexturePath);
        Texture2DData defaultProgressBarTextureData = new Texture2DData().load(Core2D.class.getResourceAsStream(defaultProgressBarTexturePath), defaultProgressBarTexturePath);

        ModelData planeModelData = new ModelData().load(Core2D.class.getResourceAsStream(planeObjectPath), planeObjectPath);
        ModelData planeNormalizedModelData = new ModelData().load(Core2D.class.getResourceAsStream(planeNormalizedObjectPath), planeNormalizedObjectPath);
        ModelData sphereModelData = new ModelData().load(Core2D.class.getResourceAsStream(sphereObjectPath), sphereObjectPath);

        addAsset(new Asset(entityDefaultShaderData, entityDefaultShaderPath));
        addAsset(new Asset(entitiesInstancingShaderData, entitiesInstancingShaderPath));
        addAsset(new Asset(entityPickingShaderData, entityPickingShader));

        addAsset(new Asset(line2DShaderData, line2DShaderPath));
        addAsset(new Asset(lines2DInstancingShaderData, lines2DInstancingShaderPath));

        addAsset(new Asset(postprocessingDefaultShaderData, postprocessingDefaultShaderPath));
        addAsset(new Asset(postprocessingTest0ShaderData, postprocessingTest0ShaderPath));
        addAsset(new Asset(postprocessingTest1ShaderData, postprocessingTest1ShaderPath));

        addAsset(new Asset(whiteTextureData, whiteTexturePath));
        addAsset(new Asset(defaultProgressBarTextureData, defaultProgressBarTexturePath));

        // models -------------------------------------------

        addAsset(new Asset(planeModelData, planeObjectPath));
        addAsset(new Asset(planeNormalizedModelData, planeNormalizedObjectPath));
        addAsset(new Asset(sphereModelData, sphereObjectPath));

        // ---------------- other graphics shaders
        String onlyColorShaderPath = "/data/shaders/common/only_color_shader.glsl";

        ShaderData onlyColorShader = new ShaderData().load(Core2D.class.getResourceAsStream(onlyColorShaderPath), onlyColorShaderPath);

        addAsset(new Asset(onlyColorShader, onlyColorShaderPath));
    }

    public void save()
    {
        if(ProjectsManager.getCurrentProject() != null && Core2D.core2DWorkMode == Core2DWorkMode.IN_ENGINE) {
            save(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + "AssetManager.am");
        }
    }

    public void save(String path)
    {
        if(Core2D.core2DWorkMode == Core2DWorkMode.IN_ENGINE) {
            String gsonObj = Utils.gson.toJson(this);
            FileUtils.reCreateFile(path);
            FileUtils.writeToFile(path, gsonObj, false);
        }
    }

    public AssetManager load()
    {
        if(ProjectsManager.getCurrentProject() != null && Core2D.core2DWorkMode == Core2DWorkMode.IN_ENGINE) {
            return load(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + "AssetManager.am");
        }
        return this;
    }

    public AssetManager load(String path)
    {
        if(Core2D.core2DWorkMode == Core2DWorkMode.IN_ENGINE && new File(path).exists()) {
            String fileText = FileUtils.readAllFile(path);
            AssetManager assetManager = Utils.gson.fromJson(fileText, AssetManager.class);
            load(assetManager);
        }
        return this;
    }

    public AssetManager load(AssetManager assetManager)
    {
        for(Asset asset : assetManager.assets) {
            //Log.Console.println("asset obj: " + asset.getAssetObject().getClass());
            Asset loadedAsset = getAsset(asset);
            if(loadedAsset != null && asset.getAssetObject() instanceof Data data) {
                data.setNotTransientFields(data);
            }
        }
        return this;
    }

    /**
     * Adds a new asset.
     * If an asset with the same name already exists, an error is output to the log,
     * in another case, it adds a new asset to the list of all assets.
     */
    private void addAsset(Asset asset)
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
     * @param path Relative path of asset.
     * @return Null if the asset is not found and texture if the asset is found.
     */
    public Texture2DData getTexture2DData(String path)
    {
        return getAssetObject(path, Texture2DData.class);
    }

    public AudioData getAudioData(String path)
    {
        return getAssetObject(path, AudioData.class);
    }

    public ScriptData getScriptData(String path)
    {
        return getAssetObject(path, ScriptData.class);
    }

    public ModelData getModelData(String path)
    {
        return getAssetObject(path, ModelData.class);
    }

    /**
     * @param path Relative path of asset.
     * @return Null if the asset is not found and asset if the asset is found.
     */
    public Asset getAsset(String path, Class<?> assetObjectClass)
    {
        Asset asset = null;
        for(Asset a : assets) {
            if(a.path.equals(path)) {
                asset = a;
            }
        }

        if(asset == null) {
            getAssetObject(path, assetObjectClass);
            asset = getAsset(path);
        }

        return asset;
    }

    public Asset getAsset(String path)
    {
        Asset asset = null;
        for(Asset a : assets) {
            if(a.path.equals(path)) {
                asset = a;
            }
        }

        if(asset == null) {
            getAssetObject(path, Data.class);
            asset = getAsset(path);
        }

        return asset;
    }

    public Asset getAsset(Asset asset)
    {
        Asset returnAsset = null;
        for(Asset a : assets) {
            if(a.path.equals(asset.path)) {
                returnAsset = a;
            }
        }

        if(returnAsset == null) {
            getAssetObject(asset.path, asset.getAssetObject().getClass());
            returnAsset = getAsset(asset.path);
        }

        return returnAsset;
    }

    public Object loadAsset(String path, Class<?> assetObjectClass)
    {
        String projectPath = ProjectsManager.getCurrentProject() != null ?
                ProjectsManager.getCurrentProject().getProjectPath() :
                "";

        String relativePath = path;
        if (new File(path).exists()) {
            relativePath = FileUtils.getRelativePath(path, projectPath);
        }

        String fullPath = projectPath + File.separator + relativePath;
        Object objToCast = null;
        if (assetObjectClass.getSuperclass().isAssignableFrom(Data.class)) {
            try {
                //Log.CurrentSession.println("trying load: " + fullPath, Log.MessageType.WARNING);

                if (new File(fullPath).exists()) {
                    //Log.CurrentSession.println("trying to load not from resources.... Path: " + fullPath, Log.MessageType.WARNING);
                    objToCast = ((Data) assetObjectClass.getConstructor().newInstance()).load(fullPath);
                } else { // если файл не существует, то пробуем загрузить файл из ресурсов
                    try (InputStream resource = Core2D.class.getResourceAsStream(path)) {
                        //Log.CurrentSession.println("trying to load from resources.... Path: " + fullPath, Log.MessageType.WARNING);
                        objToCast = ((Data) assetObjectClass.getConstructor().newInstance()).load(resource, path);
                    }
                }
            } catch (InstantiationException |
                     IllegalAccessException |
                     InvocationTargetException |
                     IOException |
                     NoSuchMethodException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
        }

        return objToCast;
    }

    public <T> T getAssetObject(String path, Class<?> assetObjectClass)
    {
        Object assetObj = null;
        for(Asset asset : assets) {
            if(asset.path.equals(path) && assetObjectClass.isAssignableFrom(asset.getAssetObject().getClass())) {
                assetObj = assetObjectClass.cast(asset.getAssetObject());

                //Log.CurrentSession.println("found asset: " + assetObj + ", " + asset.path, Log.MessageType.SUCCESS);
            }
        }

        if(assetObj == null) {
            Object foundAsset = loadAsset(path, assetObjectClass);

            if(foundAsset != null) {
                assetObj = assetObjectClass.cast(foundAsset);
            }

            String projectPath = ProjectsManager.getCurrentProject() != null ?
                    ProjectsManager.getCurrentProject().getProjectPath() :
                    "";

            String relativePath = path;
            if (new File(path).exists()) {
                relativePath = FileUtils.getRelativePath(path, projectPath);
            }

            assets.add(new Asset(assetObj, relativePath));

            if(assetObj != null) {
                Log.Console.println("added new asset! data type: " + assetObj.getClass().getSimpleName() +
                        ", path: " + path + ", asset class: " + assetObjectClass.getName() + ", asset obj: " + assetObj);
            }
        }

        return (T) assetObj;
    }

    public Asset addAsset(Object obj, String path)
    {
        Asset asset = new Asset(obj, path);
        assets.add(asset);
        return asset;
    }

    public void removeAsset(String path)
    {
        Iterator<Asset> assetsIterator = assets.iterator();
        while(assetsIterator.hasNext()) {
            Asset asset = assetsIterator.next();

            if(asset.path.equals(path)) {
                assetsIterator.remove();
                break;
            }
        }
    }

    public void removeAsset(Asset asset)
    {
        assets.remove(asset);
    }

    public Asset reloadAsset(String path, Class<?> assetObjectClass)
    {
        removeAsset(path);
        return getAsset(path, assetObjectClass);
    }

    public List<Asset> getAssets() { return assets; }

    public static AssetManager getInstance()
    {
        return instance = Objects.requireNonNullElseGet(instance, () -> new AssetManager());
    }
}
