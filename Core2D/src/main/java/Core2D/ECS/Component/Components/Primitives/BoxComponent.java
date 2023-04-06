package Core2D.ECS.Component.Components.Primitives;

import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.OpenGL.VertexArray;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BoxComponent extends PrimitiveComponent
{
    public Vector2f size = new Vector2f(100.0f, 100.0f);
    public transient Vector2f lastSize = new Vector2f();
}
