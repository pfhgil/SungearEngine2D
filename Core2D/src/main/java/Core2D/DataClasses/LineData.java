package Core2D.DataClasses;

import org.joml.Vector2f;
import org.joml.Vector4f;
public class LineData
{
    public Vector2f offset = new Vector2f();
    private Vector2f[] vertices = new Vector2f[] {
            new Vector2f(),
            new Vector2f()
    };
    public float lineWidth = 3.0f;

    public Vector4f color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    public LineData() { }

    public LineData(LineData lineData)
    {
        set(lineData);
    }

    public LineData(Vector2f offset, Vector2f start, Vector2f end, float lineWidth, Vector4f color)
    {
        this.offset.set(offset);
        this.vertices[0].set(start);
        this.vertices[1].set(end);
        this.lineWidth = lineWidth;


        this.color.set(color);
    }

    public LineData(Vector2f offset, Vector2f start, Vector2f end)
    {
        this.offset.set(offset);
        this.vertices[0].set(start);
        this.vertices[1].set(end);
    }

    public void set(LineData lineData)
    {
        offset.set(lineData.offset);

        vertices[0].set(lineData.vertices[0]);
        vertices[1].set(lineData.vertices[1]);

        lineWidth = lineData.lineWidth;

        color.set(lineData.color);
    }

    public Vector2f[] getVertices() { return vertices; }
}
