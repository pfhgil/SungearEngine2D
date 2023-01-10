package Core2D.ECS.Component.Components.Primitives;

import Core2D.AssetManager.AssetManager;
import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.ShaderUtils.VertexArray;

public class PrimitiveComponent extends Component
{
    public transient Shader shader;

    public transient VertexArray vertexArray;

    protected LineData[] linesData;

    @Override
    public void init()
    {
        shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/primitives/line2D/shader.glsl"));
    }

    public LineData[] getLinesData() { return linesData; }
}
