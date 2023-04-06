package Core2D.DataClasses;

import org.joml.Vector3f;
import org.joml.Vector4f;
public class LineData
{
    public Vector3f offset = new Vector3f();

    public Vector3f start = new Vector3f();
    public Vector3f end = new Vector3f();
    
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
        this.start.set(start);
        this.end.set(end);
        this.lineWidth = lineWidth;


        this.color.set(color);
    }

    public LineData(Vector3f offset, Vector3f start, Vector3f end)
    {
        this.offset.set(offset);
        this.start.set(start);
        this.end.set(end);
    }

    public void set(LineData lineData)
    {
        offset.set(lineData.offset);

        start.set(lineData.start);
        end.set(lineData.end);

        lineWidth = lineData.lineWidth;

        color.set(lineData.color);
    }
}
