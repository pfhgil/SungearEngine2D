package SungearEngine2D.Main;

import Core2D.Component.Components.BoxCollider2DComponent;
import Core2D.Component.Components.CircleCollider2DComponent;
import Core2D.Controllers.PC.Keyboard;
import Core2D.Controllers.PC.Mouse;
import Core2D.Graphics.Graphics;
import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.ShaderUtils.FrameBufferObject;
import SungearEngine2D.DebugDraw.CameraDebugLines;
import SungearEngine2D.GUI.Views.MainView;
import SungearEngine2D.DebugDraw.Grid;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

public class GraphicsRenderer
{
    private static int cellsNum = 501;

    private static FrameBufferObject renderTarget;

    public static void init()
    {
        Grid.init(new Vector2f(10000, 10000));
        CameraDebugLines.init();

        Vector2i targetSize = Graphics.getScreenSize();
        renderTarget = new FrameBufferObject(targetSize.x, targetSize.y, FrameBufferObject.BuffersTypes.COLOR_BUFFER, GL_TEXTURE0);
    }

    public static void draw()
    {
        if(!Keyboard.keyDown(GLFW.GLFW_KEY_F)) renderTarget.bind();
        if(!Keyboard.keyDown(GLFW.GLFW_KEY_F)) glClear(GL_COLOR_BUFFER_BIT);

        Grid.draw();

        SceneManager.drawCurrentScene2D();

        Object inspectingObject = MainView.getInspectorView().getCurrentInspectingObject();
        if(inspectingObject != null) {
            if (inspectingObject instanceof Object2D && !((Object2D) inspectingObject).isShouldDestroy()) {
                Object2D object2D = (Object2D) inspectingObject;
                List<BoxCollider2DComponent> boxCollider2DComponents = object2D.getAllComponents(BoxCollider2DComponent.class);
                List<CircleCollider2DComponent> circleCollider2DComponents = object2D.getAllComponents(CircleCollider2DComponent.class);

                for (BoxCollider2DComponent boxCollider2DComponent : boxCollider2DComponents) {
                    boxCollider2DComponent.getBoxCollider2D().draw(object2D);
                }

                for (CircleCollider2DComponent circleCollider2DComponent : circleCollider2DComponents) {
                    circleCollider2DComponent.getCircleCollider2D().draw(object2D);
                }
            }
        }

        inspectingObject = null;

        CameraDebugLines.draw();

        if(!Keyboard.keyDown(GLFW.GLFW_KEY_F)) renderTarget.unBind();

        if(Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT) && !MainView.isSomeViewFocusedExceptSceneView && !MainView.getInspectorView().isEditing()) {
            Vector2f mousePosition = Mouse.getMousePosition();

            Object2D pickedObject2D = Graphics.getPickedObject2D(mousePosition);
            //System.out.println("pickedObject2D: " + pickedObject2D + ", cur: " + mousePosition.x + ", " + mousePosition.y);

            if(pickedObject2D != null) {
                MainView.getInspectorView().setCurrentInspectingObject(pickedObject2D);
            }
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
