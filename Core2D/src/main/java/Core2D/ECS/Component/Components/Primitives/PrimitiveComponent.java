package Core2D.ECS.Component.Components.Primitives;

import Core2D.ECS.Component.Component;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.ShaderUtils.VertexArray;

public class PrimitiveComponent extends Component
{
    public transient Shader shader;

    public transient VertexArray vertexArray;
}
