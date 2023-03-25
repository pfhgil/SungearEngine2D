package Core2D.ECS.Component.Components.Primitives;

import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Component;
import Core2D.Utils.MathUtils;
import org.joml.Vector2f;

public class CircleComponent extends PrimitiveComponent
{
    private float radius = 10.0f;

    private int linesNum;

    private int angleIncrement = 10;

    public CircleComponent()
    {
        setAngleIncrement(angleIncrement);
    }

    @Override
    public void set(Component component)
    {
        destroy();
        super.set(component);

        if(component instanceof CircleComponent circleComponent) {
            setAngleIncrement(circleComponent.getAngleIncrement());
        }
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius)
    {
        this.radius = radius;

        int currentAngle = 0;

        Vector2f currentPoint = new Vector2f(0, radius);
        MathUtils.rotate(currentPoint, -angleIncrement, new Vector2f());
        Vector2f lastPoint = new Vector2f();

        for(int i = 0; i < linesData.length; i++) {
            Vector2f tmp = new Vector2f(0, radius);
            MathUtils.rotate(tmp, currentAngle, new Vector2f());
            lastPoint.set(currentPoint);
            currentPoint.set(tmp);

            linesData[i].getVertices()[0].set(lastPoint, linesData[i].getVertices()[0].z);
            linesData[i].getVertices()[1].set(currentPoint, linesData[i].getVertices()[1].z);

            currentAngle += angleIncrement;
        }
    }

    public int getAngleIncrement() { return angleIncrement; }

    public void setAngleIncrement(int angleIncrement)
    {
        this.angleIncrement = angleIncrement;

        linesNum = 360 / angleIncrement;

        data = new float[linesNum * 2];

        loadVAO();

        linesData = new LineData[linesNum];
        for(int i = 0; i < linesData.length; i++) {
            linesData[i] = new LineData();
            linesData[i].color.set(color);
            linesData[i].offset.set(offset);
            linesData[i].lineWidth = linesWidth;
        }

        setRadius(radius);
    }
}
