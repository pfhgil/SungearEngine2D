package Core2D.ECS.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.DataClasses.ModelData;
import Core2D.DataClasses.Texture2DData;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.RenderParts.Material2D;
import Core2D.Graphics.RenderParts.Shader;

public class MeshComponent extends Component
{
    public Texture2DData texture2DData = AssetManager.getInstance().getTexture2DData("/data/textures/white_texture.png");

    public Shader shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/mesh/default_shader.glsl"));

    public Material2D material2D;

    public transient ModelData modelData = AssetManager.getInstance().getModelData("/data/models/plane.obj");
}
