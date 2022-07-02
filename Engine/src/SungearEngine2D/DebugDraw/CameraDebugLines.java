package SungearEngine2D.DebugDraw;

import Core2D.Camera2D.Camera2D;
import Core2D.Camera2D.CamerasManager;
import Core2D.Core2D.Core2D;
import Core2D.Graphics.Graphics;
import Core2D.Primitives.Line2D;
import Core2D.Scene2D.SceneManager;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.Main.Settings;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

// TODO: сделать нормальный scale
public class CameraDebugLines
{
    private static Line2D[] lines = new Line2D[4];

    public static void init()
    {
        for(int i = 0; i < lines.length; i++) {
            lines[i] = new Line2D(new Vector2f(), new Vector2f());
            lines[i].setColor(new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
            lines[i].setLineWidth(4.0f);
        }
    }

    public static void draw()
    {
        if(MainView.getInspectorView().getCurrentInspectingObject() instanceof Camera2D) {
            Camera2D camera2D = (Camera2D) MainView.getInspectorView().getCurrentInspectingObject();

            Vector2i windowSize = Core2D.getWindow().getSize();

            float mul = (windowSize.x / (float) windowSize.y);
            System.out.println("dif: " + mul);

            if(CamerasManager.getMainCamera2D() != null && (!Settings.Playmode.active || camera2D.getID() != CamerasManager.getMainCamera2D().getID())) {

                SceneManager.getCurrentScene2D().getSceneMainCamera2D().getTransform().setScale(new Vector2f(1.0f, 1.0f / mul));

                /*
                lines[0].setStart(new Vector2f(-camera2D.getTransform().getPosition().x / camera2D.getTransform().getScale().x, -camera2D.getTransform().getPosition().y / camera2D.getTransform().getScale().y));
                lines[0].setEnd(new Vector2f(-camera2D.getTransform().getPosition().x / camera2D.getTransform().getScale().x, (-camera2D.getTransform().getPosition().y + windowSize.y) / camera2D.getTransform().getScale().y));

                lines[1].setStart(new Vector2f(-camera2D.getTransform().getPosition().x / camera2D.getTransform().getScale().x, (-camera2D.getTransform().getPosition().y + windowSize.y) / camera2D.getTransform().getScale().y));
                lines[1].setEnd(new Vector2f((-camera2D.getTransform().getPosition().x + windowSize.x) / camera2D.getTransform().getScale().x, (-camera2D.getTransform().getPosition().y + windowSize.y) / camera2D.getTransform().getScale().y));

                lines[2].setStart(new Vector2f((-camera2D.getTransform().getPosition().x + windowSize.x) / camera2D.getTransform().getScale().x, (-camera2D.getTransform().getPosition().y + windowSize.y) / camera2D.getTransform().getScale().y));
                lines[2].setEnd(new Vector2f((-camera2D.getTransform().getPosition().x + windowSize.x) / camera2D.getTransform().getScale().x, -camera2D.getTransform().getPosition().y / camera2D.getTransform().getScale().y));

                lines[3].setStart(new Vector2f((-camera2D.getTransform().getPosition().x + windowSize.x) / camera2D.getTransform().getScale().x, -camera2D.getTransform().getPosition().y / camera2D.getTransform().getScale().y));
                lines[3].setEnd(new Vector2f(-camera2D.getTransform().getPosition().x / camera2D.getTransform().getScale().x, -camera2D.getTransform().getPosition().y / camera2D.getTransform().getScale().y));


                 */

                lines[0].setStart(new Vector2f(-camera2D.getTransform().getPosition().x / camera2D.getTransform().getScale().x, -camera2D.getTransform().getPosition().y / camera2D.getTransform().getScale().y / mul));
                lines[0].setEnd(new Vector2f(-camera2D.getTransform().getPosition().x / camera2D.getTransform().getScale().x, (-camera2D.getTransform().getPosition().y + 1000) / camera2D.getTransform().getScale().y / mul));

                lines[1].setStart(new Vector2f(-camera2D.getTransform().getPosition().x / camera2D.getTransform().getScale().x, (-camera2D.getTransform().getPosition().y + 1000) / camera2D.getTransform().getScale().y / mul));
                lines[1].setEnd(new Vector2f((-camera2D.getTransform().getPosition().x + 1000) / camera2D.getTransform().getScale().x, (-camera2D.getTransform().getPosition().y + 1000) / camera2D.getTransform().getScale().y / mul));

                lines[2].setStart(new Vector2f((-camera2D.getTransform().getPosition().x + 1000) / camera2D.getTransform().getScale().x, (-camera2D.getTransform().getPosition().y + 1000) / camera2D.getTransform().getScale().y / mul));
                lines[2].setEnd(new Vector2f((-camera2D.getTransform().getPosition().x + 1000) / camera2D.getTransform().getScale().x, -camera2D.getTransform().getPosition().y / camera2D.getTransform().getScale().y / mul));

                lines[3].setStart(new Vector2f((-camera2D.getTransform().getPosition().x + 1000) / camera2D.getTransform().getScale().x, -camera2D.getTransform().getPosition().y / camera2D.getTransform().getScale().y / mul));
                lines[3].setEnd(new Vector2f(-camera2D.getTransform().getPosition().x / camera2D.getTransform().getScale().x, -camera2D.getTransform().getPosition().y / camera2D.getTransform().getScale().y / mul));

                Graphics.getMainRenderer().render(lines[0]);
                Graphics.getMainRenderer().render(lines[1]);
                Graphics.getMainRenderer().render(lines[2]);
                Graphics.getMainRenderer().render(lines[3]);
            }
        }
    }
}
