package Core2D.Primitives;

import Core2D.Graphics.Graphics;
import Core2D.Object2D.Transform;
import Core2D.Utils.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Circle2D
{
    private Transform transform = new Transform();
    private Line2D[] lines2D;
    private float radius = 10.0f;
    private int linesNum;
    private Vector4f linesColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private float linesWidth = 5;
    private int increment;

    public Circle2D(float radius, int increment, Vector4f linesColor, float linesWidth)
    {
        this.radius = radius;
        this.linesColor.set(linesColor);
        this.linesWidth = linesWidth;
        this.increment = increment;
        linesNum = 360 / increment;

        init();
    }

    public Circle2D(float radius, int increment, Vector4f linesColor)
    {
        this.radius = radius;
        this.linesColor.set(linesColor);
        this.increment = increment;
        linesNum = 360 / increment;

        init();
    }

    public Circle2D(float radius, int increment)
    {
        this.radius = radius;
        this.increment = increment;
        linesNum = 360 / increment;

        init();
    }

    public Circle2D(int increment)
    {
        this.increment = increment;
        linesNum = 360 / increment;

        init();
    }

    private void init()
    {
        lines2D = new Line2D[linesNum];

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

        int currentAngle = 0;

        Vector2f center = new Vector2f(transform.getPosition())
                .add(transform.getCentre());


        Vector2f currentPoint = new Vector2f();
        Vector2f lastPoint = new Vector2f();

        Vector2f tmp0 = new Vector2f(0, radius);
        MathUtils.rotate(tmp0, currentAngle, new Vector2f());
        lastPoint.set(tmp0.add(center));

        currentAngle += increment;

        tmp0 = new Vector2f(0, radius);
        MathUtils.rotate(tmp0, currentAngle, new Vector2f());
        currentPoint.set(tmp0.add(center));

        lines2D[0].setStart(new Vector2f(lastPoint));
        lines2D[0].setEnd(new Vector2f(currentPoint));

        currentAngle += increment;

        for(int i = 1; i < lines2D.length - 1; i++) {
            Vector2f tmp = new Vector2f(0, radius);
            MathUtils.rotate(tmp, currentAngle, new Vector2f());
            lastPoint.set(currentPoint);
            currentPoint.set(tmp.add(center));

            lines2D[i].setStart(new Vector2f(lastPoint));
            lines2D[i].setEnd(new Vector2f(currentPoint));

            currentAngle += increment;
        }
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }

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

    public Transform getTransform() { return transform; }
}
