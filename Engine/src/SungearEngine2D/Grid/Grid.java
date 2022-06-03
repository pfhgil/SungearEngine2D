package SungearEngine2D.Grid;

import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Graphics;
import Core2D.Instancing.Primitives.LinesInstancing;
import Core2D.Object2D.Object2D;
import Core2D.Primitives.Line2D;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Grid
{
    private static Line2D[] lines;
    private static LinesInstancing linesInstancing;

    private static Vector4f lineColor = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);

    public static void init(Vector2f startPosition, int cellsNum, float cellSize)
    {
        lines = new Line2D[cellsNum + cellsNum + 2];

        Vector2f currentPosition = new Vector2f(startPosition);
        for(int i = 0; i < lines.length - 2; i++) {
            if(i == cellsNum) {
                currentPosition = new Vector2f(startPosition.x, startPosition.y + cellSize * (cellsNum ));
            }
            if(i > cellsNum - 1) {
                lines[i] = new Line2D(currentPosition, new Vector2f(currentPosition.x, currentPosition.y - cellSize * cellsNum));
                lines[i].setColor(lineColor);
                if(i == (cellsNum + cellsNum / 2) - 1) {
                    lines[i].setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
                }
                currentPosition.x += cellSize;
            } else {
                lines[i] = new Line2D(currentPosition, new Vector2f(currentPosition.x + cellSize * (cellsNum ), currentPosition.y));
                lines[i].setColor(lineColor);
                if(i == (cellsNum) / 2 - 1) {
                    lines[i].setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                }
                currentPosition.y += cellSize;
            }
        }

        currentPosition = new Vector2f(startPosition);

        lines[lines.length - 1] = new Line2D(new Vector2f(currentPosition.x, currentPosition.y + cellSize * cellsNum),
                new Vector2f(currentPosition.x + cellSize * cellsNum, currentPosition.y + cellSize * cellsNum));
        lines[lines.length - 1].setColor(lineColor);
        lines[lines.length - 2] = new Line2D(new Vector2f(currentPosition.x + cellSize * cellsNum, currentPosition.y + cellSize * cellsNum),
                new Vector2f(currentPosition.x + cellSize * cellsNum, currentPosition.y));
        lines[lines.length - 2].setColor(lineColor);

        linesInstancing = new LinesInstancing(lines, false);
        linesInstancing.setLinesWidth(3.0f);
    }

    public static void draw()
    {
        linesInstancing.draw();
    }
}
