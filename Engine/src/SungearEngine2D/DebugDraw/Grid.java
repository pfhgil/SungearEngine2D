package SungearEngine2D.DebugDraw;

import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.Utils.ECSUtils;
import SungearEngine2D.Main.Resources;
import org.joml.Vector2f;

public class Grid
{
    private static Entity grid;

    private static int level = 1;

    public static void init(Vector2f scale)
    {
        grid = Entity.createAsObject();

        grid.getComponent(TransformComponent.class).scale.set(1f);
        MeshComponent c = grid.getComponent(MeshComponent.class);
        /*
        c.setUV(new float[] {
                0.0f, 0.0f,
                0.0f, 261,
                261, 261,
                261, 0.0f
        });0

         */
        grid.isUIElement = true;
        //grid.getComponent(TextureComponent.class).getTexture2D().param = GL_REPEAT;
        //grid.setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));
        //grid.setShaderProgram(null);
        //c.getComponent(MeshComponent.class).shader = ECSUtils.setNewShader(Resources.Shaders.Grid.gridShader, meshComponent.shader);(Resources.Shaders.Grid.gridShader);
        //grid.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Icons.object2DFileIcon);
    }

    public static void draw()
    {
        /*
        int newLevel = (int) ((1.0 / Main.getMainCamera2D().getTransform().getScale().x) / 100.0f);
        if(newLevel < 1) newLevel = 1;
        level = newLevel;

        //Systems.out.println("lvl: " + level + ", " + newLevel);

        Vector2f cameraScale = Main.getMainCamera2D().getTransform().getScale();
        grid.getComponent(MeshRendererComponent.class).setUV(new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f / cameraScale.y,
                1.0f / cameraScale.x, 1.0f / cameraScale.y,
                1.0f / cameraScale.x, 0.0f
        });

         */

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

        //Systems.out.println(1.0f / Main.getMainCamera2D().getTransform().getScale().x + ", " + 1.0f / Main.getMainCamera2D().getTransform().getScale().y + ", " + newLevel);
        //Graphics.getMainRenderer().render(grid);
        //grid.getComponent(TransformComponent.class).getTransform().updateModelMatrix();
    }
}
