package Core2D.Drawable.Primitives;

import Core2D.Graphics.Graphics;
import Core2D.Transform.Transform;
import Core2D.Utils.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Box2D
{
    private Transform transform = new Transform();
    private Line2D[] lines2D = new Line2D[4];
    private float linesWidth = 4.0f;
    private Vector4f linesColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public Box2D()
    {
        init();
    }

    public Box2D(float linesWidth)
    {
        this.linesWidth = linesWidth;

        init();
    }

    public Box2D(float linesWidth, Vector4f linesColor)
    {
        this.linesWidth = linesWidth;
        this.linesColor.set(linesColor);

        init();
    }

    private void init()
    {
        for(int i = 0; i < lines2D.length; i++) {
            lines2D[i] = new Line2D();
            lines2D[i].setColor(linesColor);
            lines2D[i].setLineWidth(linesWidth);
        }
    }

    public void draw()
    {
        for (Line2D line2D : lines2D) {
            Graphics.getMainRenderer().render(line2D);
        }

        Vector2f center = new Vector2f(transform.getPosition())
                .add(transform.getCentre());
        Vector2f halfSize = new Vector2f(100.0f * transform.getScale().x, 100.0f * transform.getScale().y);

        Vector2f min = new Vector2f(center).sub(new Vector2f(halfSize).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(halfSize).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if(transform.getRotation() != 0.0f) {
            for (Vector2f vert : vertices) {
                MathUtils.rotate(vert, transform.getRotation(), center);
            }
        }

        lines2D[0].setStart(new Vector2f(vertices[0].x, vertices[0].y));
        lines2D[0].setEnd(new Vector2f(vertices[1].x, vertices[1].y));

        lines2D[1].setStart(new Vector2f(vertices[1].x, vertices[1].y));
        lines2D[1].setEnd(new Vector2f(vertices[2].x, vertices[2].y));

        lines2D[2].setStart(new Vector2f(vertices[2].x, vertices[2].y));
        lines2D[2].setEnd(new Vector2f(vertices[3].x, vertices[3].y));

        lines2D[3].setStart(new Vector2f(vertices[3].x, vertices[3].y));
        lines2D[3].setEnd(new Vector2f(vertices[0].x, vertices[0].y));
    }

    public Transform getTransform() { return transform; }

    public float getLinesWidth() {
        return linesWidth;
    }
    public void setLinesWidth(float linesWidth)
    {
        this.linesWidth = linesWidth;

        for(int i = 0; i < lines2D.length; i++) {
            lines2D[i].setLineWidth(linesWidth);
        }
    }

    public Vector4f getLinesColor() {
        return linesColor;
    }
    public void setLinesColor(Vector4f linesColor)
    {
        this.linesColor = linesColor;

        for(int i = 0; i < lines2D.length; i++) {
            lines2D[i].setColor(linesColor);
        }
    }
}
