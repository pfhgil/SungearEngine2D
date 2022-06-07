package SungearEngine2D.Main;

import Core2D.Component.Components.BoxCollider2DComponent;
import Core2D.Controllers.PC.Mouse;
import Core2D.Core2D.Core2D;
import Core2D.Core2D.Graphics;
import Core2D.Object2D.Object2D;
import Core2D.Physics.Collider2D.BoxCollider2D;
import Core2D.ShaderUtils.FrameBufferObject;
import SungearEngine2D.Debug.DebugDraw;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.Grid.Grid;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

public class GraphicsRenderer
{
    private static int cellsNum = 501;

    private static FrameBufferObject renderTarget;
    private static FrameBufferObject pickingRenderTarget;

    public static void init()
    {
        Grid.init(new Vector2f(-(cellsNum - 1) * 50 / 2.0f + 50.0f, -(cellsNum - 1) * 50 / 2.0f + 50.0f), cellsNum, 50);
        renderTarget = new FrameBufferObject(1680, 1050, FrameBufferObject.BuffersTypes.COLOR_BUFFER, GL_TEXTURE0);
        pickingRenderTarget = new FrameBufferObject(1680, 1050, FrameBufferObject.BuffersTypes.COLOR_BUFFER, GL_TEXTURE0);
    }

    public static void draw()
    {
        renderTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT);

        Grid.draw();

        Core2D.getSceneManager2D().drawCurrentScene2D();

        Object inspectingObject = MainView.getInspectorView().getCurrentInspectingObject();
        if(inspectingObject instanceof Object2D) {
            Object2D object2D = (Object2D) inspectingObject;
            List<BoxCollider2DComponent> boxCollider2DComponents = object2D.getAllComponents(BoxCollider2DComponent.class);

            for(BoxCollider2DComponent boxCollider2DComponent : boxCollider2DComponents) {
                boxCollider2DComponent.getBoxCollider2D().draw(object2D);
            }
        }

        inspectingObject = null;

        renderTarget.unBind();

        if(Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT) && !MainView.isSomeViewFocusedExceptSceneView && !MainView.getInspectorView().isEditing()) {
            Vector2f mousePosition = MainView.getSceneView().getMouseRelativePosition(Mouse.getMousePosition());

            Object2D pickedObject2D = Graphics.getPickedObject2D(mousePosition);
            System.out.println("pickedObject2D: " + pickedObject2D + ", cur: " + mousePosition.x + ", " + mousePosition.y);

            MainView.getInspectorView().setCurrentInspectingObject(pickedObject2D);
        }

        if(Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT) && MainView.getInspectorView().isEditing()) {
            MainView.getInspectorView().setEditing(false);
        }
    }

    /*
    private static void DrawPicking()
    {
        pickingRenderTarget.Bind();
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glDisable(GL_BLEND);

        Core2D.getSceneManager2D().DrawCurrentScene2D();
        glReadPixels();

        glEnable(GL_BLEND);
        pickingRenderTarget.UnBind();
    }

     */

    public static FrameBufferObject getRenderTarget() { return renderTarget; }
}
