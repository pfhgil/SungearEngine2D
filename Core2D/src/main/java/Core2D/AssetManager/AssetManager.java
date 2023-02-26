package Core2D.AssetManager;

import Core2D.Audio.AudioInfo;
import Core2D.Core2D.Core2D;
import Core2D.Drawable.UI.Text.Font;
import Core2D.Log.Log;
import Core2D.Shader.Shader;
import Core2D.Shader.ShaderProgram;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;

/**
 * It is needed for storing all assets and for their proper use.
 */
public class AssetManager
{
    /**
     * All assets.
     */
    private static List<Asset> assets = new ArrayList<>();

    /**
     * Initializes the base assets
     */
    public static void init()
    {
        String line2DVertexShaderText  = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Primitives/Line2D/vertexShader.glsl"));
        String line2DFragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Primitives/Line2D/fragmentShader.glsl"));

        String object2DVertexShaderText  = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Object2D/vertexShader.glsl"));
        String object2DFragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Object2D/fragmentShader.glsl"));

        String lines2DInstancingVertexShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Primitives/Line2D/Instancing/vertexShader.glsl"));
        String lines2DInstancingFragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Primitives/Line2D/Instancing/fragmentShader.glsl"));

        String objects2DInstancingVertexShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Object2D/Instancing/vertexShader.glsl"));
        String objects2DInstancingFragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Object2D/Instancing/fragmentShader.glsl"));

        String textInstancingVertexShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/Text/Instancing/vertexShader.glsl"));
        String textInstancingFragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/Text/Instancing/fragmentShader.glsl"));

        String progressBarFragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/ProgressBar/fragmentShader.glsl"));

        ShaderProgram line2DProgram = new ShaderProgram(
                new Shader(line2DVertexShaderText, GL_VERTEX_SHADER),
                new Shader(line2DFragmentShaderText, GL_FRAGMENT_SHADER)
        );

        ShaderProgram object2DProgram = new ShaderProgram(
                new Shader(object2DVertexShaderText, GL_VERTEX_SHADER),
                new Shader(object2DFragmentShaderText, GL_FRAGMENT_SHADER)
        );

        ShaderProgram lines2DInstancingProgram = new ShaderProgram(
                new Shader(lines2DInstancingVertexShaderText, GL_VERTEX_SHADER),
                new Shader(lines2DInstancingFragmentShaderText, GL_FRAGMENT_SHADER)
        );

        ShaderProgram objects2DInstancingProgram = new ShaderProgram(
                new Shader(objects2DInstancingVertexShaderText, GL_VERTEX_SHADER),
                new Shader(objects2DInstancingFragmentShaderText, GL_FRAGMENT_SHADER)
        );

        ShaderProgram textInstancingProgram = new ShaderProgram(
                new Shader(textInstancingVertexShaderText, GL_VERTEX_SHADER),
                new Shader(textInstancingFragmentShaderText, GL_FRAGMENT_SHADER)
        );

        ShaderProgram progressBarProgram = new ShaderProgram(
                new Shader(object2DVertexShaderText, GL_VERTEX_SHADER),
                new Shader(progressBarFragmentShaderText, GL_FRAGMENT_SHADER)
        );

        Texture2D whiteTexture = new Texture2D(Core2D.class.getResourceAsStream("/data/Textures/white_texture.png"));
        Texture2D defaultProgressBarTexture = new Texture2D(Core2D.class.getResourceAsStream("/data/Textures/UI/ProgressBar/progress_bar.png"));

        Font comicSansSM = new Font(Core2D.class.getResourceAsStream("/data/Fonts/ComicSansSM/cssm.fnt"), Core2D.class.getResourceAsStream("/data/Fonts/ComicSansSM/cssm.png"));

        addAsset(new Asset(line2DProgram, "line2DProgram"));
        addAsset(new Asset(object2DProgram, "object2DProgram"));
        addAsset(new Asset(lines2DInstancingProgram, "lines2DInstancingProgram"));
        addAsset(new Asset(objects2DInstancingProgram, "objects2DInstancingProgram"));
        addAsset(new Asset(textInstancingProgram, "textInstancingProgram"));
        addAsset(new Asset(progressBarProgram, "progressBarProgram"));

        addAsset(new Asset(whiteTexture, "whiteTexture"));
        addAsset(new Asset(defaultProgressBarTexture, "defaultProgressBarTexture"));

<<<<<<< Updated upstream
        addAsset(new Asset(comicSansSM, "comicSansSM"));
=======
        addAsset(new Asset(onlyColorShader, onlyColorShaderPath));
    }

    public void save()
    {
        if(ProjectsManager.getCurrentProject() != null && Core2D.core2DMode == Core2DMode.IN_ENGINE) {
            save(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + "AssetManager.txt");
        }
    }

    public void save(String path)
    {
        if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
            String gsonObj = Utils.gson.toJson(this);
            //Log.CurrentSession.println(gsonObj, Log.MessageType.WARNING);
            FileUtils.reCreateFile(path);
            FileUtils.writeToFile(path, gsonObj, false);
        }
    }

    public AssetManager load()
    {
        if(ProjectsManager.getCurrentProject() != null && Core2D.core2DMode == Core2DMode.IN_ENGINE) {
            return load(ProjectsManager.getCurrentProject().getProjectPath() + File.separator + "AssetManager.txt");
        }
        return this;
    }

    public AssetManager load(String path)
    {
        if(Core2D.core2DMode == Core2DMode.IN_ENGINE && new File(path).exists()) {
            String fileText = FileUtils.readAllFile(path);
            AssetManager assetManager = Utils.gson.fromJson(fileText, AssetManager.class);
            load(assetManager);
        }
        return this;
    }

    public AssetManager load(AssetManager assetManager)
    {
        for(Asset asset : assetManager.assets) {
            Log.Console.println("asset obj: " + asset.getAssetObject().getClass());
            Asset loadedAsset = getAsset(asset);
            if(loadedAsset != null && asset.getAssetObject() instanceof Data data) {
                data.setNotTransientFields(data);
            }
        }
        return this;
>>>>>>> Stashed changes
    }

    /**
     * Adds a new asset.
     * If an asset with the same name already exists, an error is output to the log,
     * in another case, it adds a new asset to the list of all assets.
     */
    public static void addAsset(Asset asset)
    {
        for(Asset a : assets) {
            if (a.name.equals(asset.name)) {
                Log.CurrentSession.println("Error while adding shader program \"" + asset.name + "\". Shader program with this name is already exists", Log.MessageType.ERROR);
                break;
            }
        }

        assets.add(asset);
    }

    /**
     * @param name Name of asset.
     * @return Null if the asset is not found and shader program if the asset is found.
     */
    public static ShaderProgram getShaderProgram(String name)
    {
        for(Asset asset : assets) {
            if(asset.name.equals(name) && asset.assetObject instanceof ShaderProgram) {
                return (ShaderProgram) asset.assetObject;
            }
        }

        return null;
    }

    /**
     * @param name Name of asset.
     * @return Null if the asset is not found and texture if the asset is found.
     */
    public static Texture2D getTexture2D(String name)
    {
        for(Asset asset : assets) {
            if(asset.name.equals(name) && asset.assetObject instanceof Texture2D) {
                return (Texture2D) asset.assetObject;
            }
        }

        return null;
    }

    /**
     * @param name Name of asset.
     * @return Null if the asset is not found and font if the asset is found.
     */
    public static Font getFont(String name)
    {
        for(Asset asset : assets) {
            if(asset.name.equals(name) && asset.assetObject instanceof Font) {
                return (Font) asset.assetObject;
            }
        }

        return null;
    }

    public static AudioInfo getAudioInfo(String name)
    {
        for(Asset asset : assets) {
            if(asset.name.equals(name) && asset.assetObject instanceof AudioInfo) {
                return (AudioInfo) asset.assetObject;
            }
        }

        return null;
    }

    /**
     * @param name Name of asset.
     * @return Null if the asset is not found and asset if the asset is found.
     */
    public static Asset getAsset(String name)
    {
        for(Asset asset : assets) {
            if(asset.name.equals(name)) {
                return asset;
            }
        }

<<<<<<< Updated upstream
        return null;
=======
        if(assetObj == null) {
            if(Core2D.core2DMode == Core2DMode.IN_ENGINE) {
                if(ProjectsManager.getCurrentProject() != null) {
                    String resPath = path;
                    if(new File(path).exists()) {
                        resPath = FileUtils.getRelativePath(path, ProjectsManager.getCurrentProject().getProjectPath());
                    }

                    String fullPath = ProjectsManager.getCurrentProject().getProjectPath() + File.separator + resPath;
                    Object objToCast = null;
                    if (assetObjectClass.getSuperclass().isAssignableFrom(Data.class)) {
                        try {
                            //System.out.println("assignable from: " + assetObjectClass.getName());
                            objToCast = ((Data) assetObjectClass.getConstructor().newInstance()).load(fullPath);
                        } catch (InstantiationException |
                                 IllegalAccessException |
                                 InvocationTargetException |
                                 NoSuchMethodException e) {
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
                    try(InputStream resource = Core2D.class.getResourceAsStream(path)) {
                        objToCast = ((Data) assetObjectClass.getConstructor().newInstance()).load(resource, path);
                    } catch (InstantiationException |
                             IllegalAccessException |
                             InvocationTargetException |
                             NoSuchMethodException |
                             IOException e) {
                        Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                    }
                }

                if(objToCast != null) {
                    assetObj = assetObjectClass.cast(objToCast);
                }
            }

            assets.add(new Asset(assetObj, path));
            if(assetObj != null) {
                Log.Console.println("added new asset! data type: " + assetObj.getClass().getSimpleName() +
                        ", path: " + path + ", asset class: " + assetObjectClass.getName());
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

    public static AssetManager getInstance()
    {
        return instance = Objects.requireNonNullElseGet(instance, () -> new AssetManager());
>>>>>>> Stashed changes
    }
}
