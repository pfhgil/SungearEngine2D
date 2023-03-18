package Core2D.ECS.Component.Components.Primitives;

import Core2D.DataClasses.LineData;
import org.joml.Vector2f;

public class LineComponent extends PrimitiveComponent
{
    public LineComponent()
    {
        data = new float[] {
                // первая точка
                0.0f, 0.0f,

                // вторая точка
                0.0f, 0.0f
        };

        linesData = new LineData[] {
                new LineData(new Vector2f(), new Vector2f(), new Vector2f(0.0f, 100.0f))
        };
    }
}
