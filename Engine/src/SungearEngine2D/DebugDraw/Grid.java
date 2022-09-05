package SungearEngine2D.DebugDraw;

import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Graphics.Graphics;
import Core2D.Object2D.Object2D;
import SungearEngine2D.Main.Resources;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Grid
{
    private static Object2D grid;

    public static void init(Vector2f scale)
    {
        grid = new Object2D();

        grid.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(scale.x, scale.y));
        grid.getComponent(TextureComponent.class).setUV(new float[] {
                0.0f, 0.0f,
                0.0f, scale.y,
                scale.x, scale.y,
                scale.x, 0.0f
        });
        grid.setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));
        grid.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Grid.gridChunkTexture);
    }

    public static void draw()
    {
        Graphics.getMainRenderer().render(grid);
        grid.getComponent(TransformComponent.class).getTransform().update(0.0f);
    }
}
