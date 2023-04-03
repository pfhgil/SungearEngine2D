package Core2D.ECS.Component.Components;

import Core2D.AssetManager.Asset;
import Core2D.AssetManager.AssetManager;
import Core2D.DataClasses.MeshData;
import Core2D.DataClasses.Texture2DData;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.OpenGL.*;
import Core2D.Graphics.RenderParts.Material2D;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Utils.PositionsQuad;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class MeshComponent extends Component
{
    public Texture2DData texture2DData = AssetManager.getInstance().getTexture2DData("/data/textures/white_texture.png");

    public Shader shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/mesh/default_shader.glsl"));

    public Material2D material2D;

    public transient MeshData meshData = AssetManager.getInstance().getModelData("/data/models/plane.obj").getMeshData("plane");
}
