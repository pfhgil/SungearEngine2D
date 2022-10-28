package SungearEngine2D.DebugDraw;

import Core2D.Camera2D.CamerasManager;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Drawable.Object2D;
import Core2D.Graphics.Graphics;
import Core2D.ShaderUtils.ShaderUtils;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.Resources;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11C.GL_REPEAT;

public class Grid
{
    private static Object2D grid;

    private static int level = 1;

    public static void init(Vector2f scale)
    {
        grid = new Object2D();

        grid.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(1f, 1f));
        grid.getComponent(TextureComponent.class).setUV(new float[] {
                0.0f, 0.0f,
                0.0f, 261,
                261, 261,
                261, 0.0f
        });
        grid.setUIElement(true);
        //grid.getComponent(TextureComponent.class).getTexture2D().param = GL_REPEAT;
        //grid.setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));
        //grid.setShaderProgram(null);
        grid.setShaderProgram(Resources.Shaders.Grid.gridShaderProgram);
        //grid.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Icons.object2DFileIcon);
    }

    public static void draw()
    {
        int newLevel = (int) ((1.0 / Main.getMainCamera2D().getTransform().getScale().x) / 100.0f);
        if(newLevel < 1) newLevel = 1;
        level = newLevel;

        //System.out.println("lvl: " + level + ", " + newLevel);

        Vector2f cameraScale = Main.getMainCamera2D().getTransform().getScale();
        grid.getComponent(TextureComponent.class).setUV(new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f / cameraScale.y,
                1.0f / cameraScale.x, 1.0f / cameraScale.y,
                1.0f / cameraScale.x, 0.0f
        });

        /*
        grid.getShaderProgram().bind();

        ShaderUtils.setUniform(
                grid.getShaderProgram().getHandler(),
                "cameraScale",
                Main.getMainCamera2D().getTransform().getScale());

        ShaderUtils.setUniform(
                grid.getShaderProgram().getHandler(),
                "level",
                newLevel);

        grid.getShaderProgram().unBind();

         */

        //System.out.println(1.0f / Main.getMainCamera2D().getTransform().getScale().x + ", " + 1.0f / Main.getMainCamera2D().getTransform().getScale().y + ", " + newLevel);
        //Graphics.getMainRenderer().render(grid);
        grid.getComponent(TransformComponent.class).getTransform().update(0.0f);
    }
}
