package SungearEngine2D.Main;

import Core2D.Component.Components.TransformComponent;
import Core2D.GameObject.GameObject;
import Core2D.Graphics.Graphics;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import Core2D.ShaderUtils.FrameBuffer;
import SungearEngine2D.DebugDraw.CamerasDebugLines;
import SungearEngine2D.DebugDraw.Gizmo;
import SungearEngine2D.DebugDraw.Grid;
import SungearEngine2D.DebugDraw.ObjectsDebugLines;
import SungearEngine2D.GUI.Views.ViewsManager;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.Game;

import static Core2D.Scene2D.SceneManager.currentSceneManager;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

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

        //CamerasDebugLines.draw();

        //ObjectsDebugLines.draw();

        //Gizmo.draw();

        if(Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT) && !ViewsManager.isSomeViewFocusedExceptSceneView && !ViewsManager.getInspectorView().isEditing()) {
            Vector2f mousePosition = Mouse.getMousePosition();

            GameObject pickedObject2D = Graphics.getPickedObject2D(mousePosition);

            if(pickedObject2D != null) {
                ViewsManager.getInspectorView().setCurrentInspectingObject(pickedObject2D);
            }
        }

        if(Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT) && ViewsManager.getInspectorView().isEditing()) {
            ViewsManager.getInspectorView().setEditing(false);
        }
    }
}
