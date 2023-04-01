package Core2D.ECS.Component.Components.Primitives;

import Core2D.DataClasses.LineData;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.OpenGL.VertexArray;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BoxComponent extends PrimitiveComponent
{
    private Vector2f size = new Vector2f(100.0f, 100.0f);

    public BoxComponent()
    {
        // 8 значений для точек примитивов (xy)
        data = new float[8];

        linesData = new LineData[]{
                new LineData(new Vector3f(), new Vector3f(-size.x / 2.0f, -size.y / 2.0f, 0f), new Vector3f(-size.x / 2.0f, size.y / 2.0f, 0f)),
                new LineData(new Vector3f(), new Vector3f(-size.x / 2.0f, size.y / 2.0f, 0f), new Vector3f(size.x / 2.0f, size.y / 2.0f, 0f)),
                new LineData(new Vector3f(), new Vector3f(size.x / 2.0f, size.y / 2.0f, 0f), new Vector3f(size.x / 2.0f, -size.y / 2.0f, 0f)),
                new LineData(new Vector3f(), new Vector3f(size.x / 2.0f, -size.y / 2.0f, 0f), new Vector3f(-size.x / 2.0f, -size.y / 2.0f, 0f))
        };
    }

    @Override
    public void set(Component component)
    {
        destroy();
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

        linesData[0].getVertices()[0].set(-size.x / 2.0f, -size.y / 2.0f, linesData[0].getVertices()[0].z);
        linesData[0].getVertices()[1].set(-size.x / 2.0f, size.y / 2.0f, linesData[0].getVertices()[0].z);

        linesData[1].getVertices()[0].set(-size.x / 2.0f, size.y / 2.0f, linesData[1].getVertices()[0].z);
        linesData[1].getVertices()[1].set(size.x / 2.0f, size.y / 2.0f, linesData[1].getVertices()[1].z);

        linesData[2].getVertices()[0].set(size.x / 2.0f, size.y / 2.0f, linesData[2].getVertices()[0].z);
        linesData[2].getVertices()[1].set(size.x / 2.0f, -size.y / 2.0f, linesData[2].getVertices()[1].z);

        linesData[3].getVertices()[0].set(size.x / 2.0f, -size.y / 2.0f, linesData[3].getVertices()[0].z);
        linesData[3].getVertices()[1].set(-size.x / 2.0f, -size.y / 2.0f, linesData[3].getVertices()[1].z);
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
