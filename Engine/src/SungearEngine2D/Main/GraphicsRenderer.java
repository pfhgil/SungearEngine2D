package SungearEngine2D.Main;

import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Input.PC.Mouse;
import SungearEngine2D.DebugDraw.CamerasDebugLines;
import SungearEngine2D.DebugDraw.EntitiesDebugDraw;
import SungearEngine2D.DebugDraw.Gizmo;
import SungearEngine2D.DebugDraw.Grid;
import SungearEngine2D.GUI.Views.ViewsManager;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class GraphicsRenderer
{
    private static int cellsNum = 501;

    public static void init()
    {
        Grid.init(new Vector2f(10000, 10000));
        CamerasDebugLines.init();
        //ObjectsDebugLines.init();
        Gizmo.init();
    }

    public static void draw()
    {
        //Grid.draw();

        //currentSceneManager.drawCurrentScene2D();

        CamerasDebugLines.draw();

        EntitiesDebugDraw.draw();

        Gizmo.draw();

        if(Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT) && !ViewsManager.isSomeViewFocusedExceptSceneView) {
            Vector2f mousePosition = Mouse.getMousePosition();

            Entity pickedObject2D = Graphics.getPickedObject2D(mousePosition);

            if(pickedObject2D != null) {
                ViewsManager.getInspectorView().setCurrentInspectingObject(pickedObject2D);
            }
        }
    }

    public static void deltaUpdate(float deltaTime)
    {
        EntitiesDebugDraw.deltaUpdate(deltaTime);
    }
}
