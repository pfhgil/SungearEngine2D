package Core2D.ECS.Component.Components.Primitives;

import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Component;
import Core2D.ShaderUtils.VertexArray;
import org.joml.Vector2f;

public class BoxComponent extends PrimitiveComponent
{
    private Vector2f size = new Vector2f(100.0f, 100.0f);

    public BoxComponent()
    {
        data = new float[] {
                // первая точка
                0.0f, 0.0f,

                // вторая точка
                0.0f, 0.0f,

                // третья точка
                0.0f, 0.0f,

                // четвертая точка
                0.0f, 0.0f
        };

        linesData = new LineData[]{
                new LineData(new Vector2f(), new Vector2f(-size.x / 2.0f, -size.y / 2.0f), new Vector2f(-size.x / 2.0f, size.y / 2.0f)),
                new LineData(new Vector2f(), new Vector2f(-size.x / 2.0f, size.y / 2.0f), new Vector2f(size.x / 2.0f, size.y / 2.0f)),
                new LineData(new Vector2f(), new Vector2f(size.x / 2.0f, size.y / 2.0f), new Vector2f(size.x / 2.0f, -size.y / 2.0f)),
                new LineData(new Vector2f(), new Vector2f(size.x / 2.0f, -size.y / 2.0f), new Vector2f(-size.x / 2.0f, -size.y / 2.0f))
        };
    }

    @Override
    public void set(Component component)
    {
        super.set(component);

        if(component instanceof BoxComponent boxComponent) {
            setSize(boxComponent.size);
        }
    }

    public VertexArray getVertexArrayObject() { return vertexArray; }

    public Vector2f getSize() { return size; }
    public void setSize(Vector2f size)
    {
        this.size = size;

        linesData[0].getVertices()[0].set(-size.x / 2.0f, -size.y / 2.0f);
        linesData[0].getVertices()[1].set(-size.x / 2.0f, size.y / 2.0f);

        linesData[1].getVertices()[0].set(-size.x / 2.0f, size.y / 2.0f);
        linesData[1].getVertices()[1].set(size.x / 2.0f, size.y / 2.0f);

        linesData[2].getVertices()[0].set(size.x / 2.0f, size.y / 2.0f);
        linesData[2].getVertices()[1].set(size.x / 2.0f, -size.y / 2.0f);

        linesData[3].getVertices()[0].set(size.x / 2.0f, -size.y / 2.0f);
        linesData[3].getVertices()[1].set(-size.x / 2.0f, -size.y / 2.0f);
    }
    public void setWidth(float width)
    {
        this.size.x = width;

        setSize(size);
    }
    public void setHeight(float height)
    {
        this.size.y = height;

        setSize(size);
    }
}
