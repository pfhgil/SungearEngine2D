package SungearEngine2D.Debug;

import Core2D.Graphics.Graphics;
import Core2D.Log.Log;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DebugDraw
{
    private final int MAX_LINES = 250;
    //private List<Line2D> lines = new ArrayList<>(); //#FIXME

    public void draw()
    {
        /*for(Line2D line2D : lines) {
            Graphics.getMainRenderer().render(line2D);
        }*/ //#FIXME
    }

    public void addLine2D(Vector2f start, Vector2f end, Vector4f color, float lineWidth)
    {
        /*if(lines.size() + 1 > MAX_LINES) {
            Log.CurrentSession.println("Can not add line2D to draw. Size of lines list is > MAX_LINES", Log.MessageType.ERROR);
            return;
        }

        Line2D line2D = new Line2D();
        line2D.setStart(start);
        line2D.setEnd(end);
        line2D.setColor(color);
        line2D.setLineWidth(lineWidth);*/ //#FIXME
    }

    public void clearLines2DList()
    {
        /*Iterator<Line2D> iterator = lines.iterator();
        while(iterator.hasNext()) {
            Line2D line2D = iterator.next();
            line2D.destroy();
            line2D = null;
            iterator.remove();
        }

        iterator = null;*/ //#FIXME
    }

    //#FIXME public List<Line2D> getLines() { return lines; }
}
