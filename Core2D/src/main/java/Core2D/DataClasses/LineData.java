package Core2D.DataClasses;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
public class LineData
{
    public Vector3f offset = new Vector3f();
    private Vector3f[] vertices = new Vector3f[] {
            new Vector3f(),
            new Vector3f()
    };
    public float lineWidth = 3.0f;

    public Vector4f color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    public LineData() { }

    public LineData(LineData lineData)
    {
        set(lineData);
    }

    public LineData(Vector3f offset, Vector3f start, Vector3f end, float lineWidth, Vector4f color)
    {
        this.offset.set(offset);
        this.vertices[0].set(start);
        this.vertices[1].set(end);
        this.lineWidth = lineWidth;


        this.color.set(color);
    }

    public LineData(Vector3f offset, Vector3f start, Vector3f end)
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

    public Vector3f[] getVertices() { return vertices; }
}
