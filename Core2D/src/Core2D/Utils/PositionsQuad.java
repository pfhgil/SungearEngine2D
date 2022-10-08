package Core2D.Utils;

import org.joml.Vector2f;

// имеет 4 позиции для каждой вершин
// порядок перечисления: левая нижняя, левая верхняя, правая верхняя, правая нижняя
// используется для хранения текстурных координат (ну и может для чего-то еще)
public class PositionsQuad
{
    private Vector2f leftBottom = new Vector2f();
    private Vector2f leftTop = new Vector2f();
    private Vector2f rightTop = new Vector2f();
    private Vector2f rightBottom = new Vector2f();

    private Vector2f atlasSize = new Vector2f(1.0f);

    public PositionsQuad() { }

    public PositionsQuad(
            Vector2f leftBottom,
            Vector2f leftTop,
            Vector2f rightTop,
            Vector2f rightBottom
    )
    {
        this.leftBottom = new Vector2f(leftBottom);
        this.leftTop = new Vector2f(leftTop);
        this.rightTop = new Vector2f(rightTop);
        this.rightBottom = new Vector2f(rightBottom);
    }

    public PositionsQuad(
            Vector2f leftBottom,
            Vector2f leftTop,
            Vector2f rightTop,
            Vector2f rightBottom,
            Vector2f atlasSize
    )
    {
        this.leftBottom = new Vector2f(leftBottom);
        this.leftTop = new Vector2f(leftTop);
        this.rightTop = new Vector2f(rightTop);
        this.rightBottom = new Vector2f(rightBottom);

        this.atlasSize = atlasSize;
    }

    public PositionsQuad(PositionsQuad positionsQuad)
    {
        this.leftBottom = new Vector2f(positionsQuad.getLeftBottom());
        this.leftTop = new Vector2f(positionsQuad.getLeftTop());
        this.rightTop = new Vector2f(positionsQuad.getRightTop());
        this.rightBottom = new Vector2f(positionsQuad.getRightBottom());

        this.atlasSize = new Vector2f(positionsQuad.getAtlasSize());
    }

    public void destroy()
    {
        leftBottom = null;
        leftTop = null;
        rightTop = null;
        rightBottom = null;

        atlasSize = null;
    }

    public Vector2f getLeftBottom() { return leftBottom; }
    public void setLeftBottom(Vector2f leftBottom) { this.leftBottom = leftBottom; }

    public Vector2f getLeftTop() { return leftTop; }
    public void setLeftTop(Vector2f leftTop) { this.leftTop = leftTop; }

    public Vector2f getRightTop() { return rightTop; }
    public void setRightTop(Vector2f rightTop) { this.rightTop = rightTop; }

    public Vector2f getRightBottom() { return rightBottom; }
    public void setRightBottom(Vector2f rightBottom) { this.rightBottom = rightBottom; }

    public Vector2f getAtlasSize() { return atlasSize; }
    public void setAtlasSize(Vector2f atlasSize) { this.atlasSize = atlasSize; }
}
