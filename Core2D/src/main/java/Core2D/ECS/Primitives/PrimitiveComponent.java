package Core2D.ECS.Primitives;

import Core2D.AssetManager.AssetManager;
import Core2D.DataClasses.LineData;
import Core2D.ECS.Component;
import Core2D.Graphics.RenderParts.Shader;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PrimitiveComponent extends Component
{
    public transient Shader shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/primitives/line/shader.glsl"));

    public transient LineData[] linesData = new LineData[0];

    public boolean scaleWithEntity = false;


    public Vector3f offset = new Vector3f();
    protected transient Vector3f lastOffset = new Vector3f();


    public Vector4f color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    protected transient Vector4f lastColor = new Vector4f();

    public float linesWidth = 3.0f;
    protected transient float lastLinesWidth = 0f;
}
