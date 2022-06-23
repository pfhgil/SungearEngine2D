package SungearEngine2D.Debug;

import Core2D.Log.Log;
import Core2D.Primitives.Line2D;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DebugDraw
{
    private final int MAX_LINES = 250;
    private List<Line2D> lines = new ArrayList<>();

    public void draw()
    {
        for(Line2D line2D : lines) {
            line2D.draw();
        }
    }

    public void addLine2D(Vector2f start, Vector2f end, Vector4f color, float lineWidth)
    {
        if(lines.size() + 1 > MAX_LINES) {
            Log.CurrentSession.println("Can not add line2D to draw. Size of lines list is > MAX_LINES");
            return;
        }

        Line2D line2D = new Line2D(start, end);
        line2D.setColor(color);
        line2D.setLineWidth(lineWidth);
    }

    public void clearLines2DList()
    {
        Iterator<Line2D> iterator = lines.iterator();
        while(iterator.hasNext()) {
            Line2D line2D = iterator.next();
            line2D.destroy();
            line2D = null;
            iterator.remove();
        }

        iterator = null;
    }

    public List<Line2D> getLines() { return lines; }
}
