package SungearEngine2D.DebugDraw;

import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Drawable.Object2D;
import Core2D.Drawable.Primitives.Circle2D;
import Core2D.Drawable.Primitives.Line2D;
import Core2D.Graphics.Graphics;
import Core2D.Input.PC.Mouse;
import Core2D.ShaderUtils.FrameBufferObject;
import Core2D.Texture2D.TextureDrawModes;
import Core2D.Transform.Transform;
import Core2D.Utils.MathUtils;
import Core2D.Utils.MatrixUtils;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Resources;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL13;

public class Gizmo
{
    public static class GizmoMode
    {
        public static final int TRANSLATION = 0;
        public static final int ROTATION = 1;
        public static final int SCALE = 2;
        public static final int TRANSLATION_ROTATION = 3;
        public static final int TRANSLATION_SCALE = 4;
        public static final int TRANSLATION_ROTATION_SCALE = 5;
        public static final int ROTATION_SCALE = 6;
        public static final int ALL = 7;
        public static final int NO_GIZMO = 8;
    }

    public static int gizmoMode = GizmoMode.TRANSLATION;

    public static Vector2f scaleSensitivity = new Vector2f(0.1f, 0.1f);

    public static final Object2D yArrow = new Object2D();
    public static final Object2D xArrow = new Object2D();

    public static final Object2D centrePoint = new Object2D();

    public static final Object2D centrePointToEditCentre = new Object2D();

    public static final Circle2D rotationCircle = new Circle2D(300.0f, 1, new Vector4f(0.0f, 1.0f, 0.0f, 0.65f));
    public static final Object2D rotationHandler = new Object2D();

    public static final Object2D yScaleHandler = new Object2D();
    public static final Line2D yScaleLine = new Line2D();
    public static final Object2D xScaleHandler = new Object2D();
    public static final Line2D xScaleLine = new Line2D();

    private static Object2D selectedGizmoTool;

    public static boolean active = true;

    private static Vector2f lastMousePosition = new Vector2f();

    private static FrameBufferObject gizmoPickingTarget;

    public static void init()
    {
        Vector2i size = Graphics.getScreenSize();
        gizmoPickingTarget = new FrameBufferObject(size.x, size.y, FrameBufferObject.BuffersTypes.COLOR_BUFFER, GL13.GL_TEXTURE0);

        yArrow.setName("gizmo.yArrow");
        xArrow.setName("gizmo.xArrow");
        centrePoint.setName("gizmo.centrePoint");
        centrePointToEditCentre.setName("gizmo.centrePointToEditCentre");
        rotationHandler.setName("gizmo.rotationHandler");
        yScaleHandler.setName("gizmo.yScaleHandler");
        xScaleHandler.setName("gizmo.xScaleHandler");

        yArrow.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoArrow);
        xArrow.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoArrow);
        centrePoint.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoPoint);
        centrePointToEditCentre.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoPoint);
        //rotationCircle.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoCircle);
        rotationHandler.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoPoint);
        yScaleHandler.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoPoint);
        xScaleHandler.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoPoint);

        yArrow.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 0.65f));
        xArrow.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 0.65f));
        centrePoint.setColor(new Vector4f(0.5f, 0.5f, 0.5f, 1));
        centrePointToEditCentre.setColor(new Vector4f(0.25f, 0.9f, 0.5f, 1.0f));
        //rotationCircle.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 0.65f));
        rotationHandler.setColor(new Vector4f(0.5f, 0.5f, 0.5f, 1));
        yScaleHandler.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1));
        yScaleLine.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 0.65f));
        xScaleHandler.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1));
        xScaleLine.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 0.65f));

        //xArrow.getComponent(TransformComponent.class).getTransform().setRotation(-90.0f);

        Vector2f arrowScale = new Vector2f(Resources.Textures.Gizmo.gizmoArrow.getWidth() / 100.0f, Resources.Textures.Gizmo.gizmoArrow.getHeight() / 100.0f).mul(1.5f);
        Vector2f pointScale = new Vector2f(Resources.Textures.Gizmo.gizmoPoint.getWidth() / 100.0f, Resources.Textures.Gizmo.gizmoPoint.getHeight() / 100.0f);
        //Vector2f rotationCircleScale = new Vector2f(Resources.Textures.Gizmo.gizmoCircle.getWidth() / 100.0f, Resources.Textures.Gizmo.gizmoCircle.getWidth() / 100.0f).mul(5.5f);
        yArrow.getComponent(TransformComponent.class).getTransform().setScale(arrowScale);
        xArrow.getComponent(TransformComponent.class).getTransform().setScale(arrowScale);
        centrePoint.getComponent(TransformComponent.class).getTransform().setScale(pointScale);
        centrePointToEditCentre.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(pointScale).div(2.5f));
        //rotationCircle.getComponent(TransformComponent.class).getTransform().setScale(rotationCircleScale);
        rotationHandler.getComponent(TransformComponent.class).getTransform().setScale(pointScale);
        yScaleHandler.getComponent(TransformComponent.class).getTransform().setScale(pointScale);
        xScaleHandler.getComponent(TransformComponent.class).getTransform().setScale(pointScale);

        yScaleLine.setLineWidth(6.0f);
        xScaleLine.setLineWidth(6.0f);
        //Transform rotationCircleTransform = rotationCircle.getComponent(TransformComponent.class).getTransform();
        //rotationHandler.getComponent(TransformComponent.class).getTransform().setPosition(new Vector2f(rotationCircleTransform.getPosition()).add(new Vector2f(0.0f, rotationCircleTransform.getScale().x * 100.0f / 2.0f)));
    }

    public static void draw()
    {
        if(ViewsManager.getInspectorView().getCurrentInspectingObject() instanceof Object2D && active) {
            Object2D object2D = (Object2D) ViewsManager.getInspectorView().getCurrentInspectingObject();
            if(!object2D.isShouldDestroy()) {
                Transform object2DTransform = object2D.getComponent(TransformComponent.class).getTransform();
                Vector2f object2DPosition = new Vector2f(MatrixUtils.getPosition(object2DTransform.getResultModelMatrix()));
                float object2DRotation = MatrixUtils.getRotation(object2DTransform.getResultModelMatrix());

                Vector2f object2DCentrePosition = new Vector2f();
                if(object2DTransform.getParentTransform() != null) {
                    Vector2f parentPosition = MatrixUtils.getPosition(object2DTransform.getParentTransform().getResultModelMatrix());
                    object2DCentrePosition.set(new Vector2f(object2DTransform.getPosition()).mul(MatrixUtils.getScale(object2DTransform.getParentTransform().getResultModelMatrix()))).add(object2DTransform.getCentre());
                    object2DCentrePosition.add(parentPosition);
                    MathUtils.rotate(object2DCentrePosition, MatrixUtils.getRotation(object2DTransform.getParentTransform().getResultModelMatrix()), parentPosition);
                } else {
                    object2DCentrePosition.set(object2DTransform.getPosition()).add(object2DTransform.getCentre());
                }

                Transform yArrowTransform = yArrow.getComponent(TransformComponent.class).getTransform();
                Transform xArrowTransform = xArrow.getComponent(TransformComponent.class).getTransform();
                Transform rotationHandlerTransform = rotationHandler.getComponent(TransformComponent.class).getTransform();
                Transform yScaleHandlerTransform = yScaleHandler.getComponent(TransformComponent.class).getTransform();
                Transform xScaleHandlerTransform = xScaleHandler.getComponent(TransformComponent.class).getTransform();

                yArrowTransform.setPosition(new Vector2f(object2DPosition).add(new Vector2f(0.0f, yArrowTransform.getScale().y * 100.0f / 2.0f)));
                xArrowTransform.setPosition(new Vector2f(object2DPosition));
                centrePoint.getComponent(TransformComponent.class).getTransform().setPosition(object2DPosition);
                centrePointToEditCentre.getComponent(TransformComponent.class).getTransform().setPosition(new Vector2f(object2DCentrePosition));
                //Transform rotationCircleTransform = rotationCircle.getComponent(TransformComponent.class).getTransform();
                rotationCircle.getTransform().setPosition(object2DCentrePosition);
                rotationHandlerTransform.setPosition(new Vector2f(rotationCircle.getTransform().getPosition()).add(new Vector2f(0.0f, rotationCircle.getRadius())));
                Vector2f rotationOffset = new Vector2f(object2DCentrePosition).add(new Vector2f(rotationHandlerTransform.getPosition()).negate());
                rotationHandlerTransform.setRotationAround(object2DRotation, rotationOffset);
                float parentRotation = 0.0f;
                Vector2f yScaleLineEnd = new Vector2f(0.0f, 350.0f);
                Vector2f xScaleLineEnd = new Vector2f(350.0f, 0.0f);
                Vector2f xArrowOffset = new Vector2f(xArrowTransform.getScale().y * 100.0f / 2.0f, 0.0f);
                if (object2DTransform.getParentTransform() != null) {
                    parentRotation = MatrixUtils.getRotation(object2DTransform.getParentTransform().getResultModelMatrix());
                    MathUtils.rotate(yScaleLineEnd, parentRotation, new Vector2f(0.0f));
                    MathUtils.rotate(xScaleLineEnd, parentRotation, new Vector2f(0.0f));
                    MathUtils.rotate(xArrowOffset, 90.0f, new Vector2f(0.0f));
                    xArrowTransform.translate(xArrowOffset);

                    yArrowTransform.setRotationAround(parentRotation, new Vector2f(object2DPosition).add(new Vector2f(yArrowTransform.getPosition()).negate()));
                    xArrowTransform.setRotationAround(parentRotation - 90.0f, new Vector2f(object2DPosition).add(new Vector2f(xArrowTransform.getPosition()).negate()));
                } else {
                    xArrowTransform.translate(xArrowOffset);
                    yArrowTransform.setRotation(0.0f);
                    xArrowTransform.setRotation(-90.0f);
                }
                yScaleLine.setStart(object2DPosition);
                yScaleLine.setEnd(new Vector2f(object2DPosition).add(yScaleLineEnd));
                xScaleLine.setStart(object2DPosition);
                xScaleLine.setEnd(new Vector2f(object2DPosition).add(xScaleLineEnd));
                yScaleHandlerTransform.setPosition(new Vector2f(object2DPosition).add(yScaleLineEnd));
                xScaleHandlerTransform.setPosition(new Vector2f(object2DPosition).add(xScaleLineEnd));

                if (gizmoMode == GizmoMode.SCALE || gizmoMode == GizmoMode.TRANSLATION_SCALE || gizmoMode == GizmoMode.ROTATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                    Graphics.getMainRenderer().render(yScaleLine);
                    Graphics.getMainRenderer().render(yScaleHandler);
                    Graphics.getMainRenderer().render(xScaleLine);
                    Graphics.getMainRenderer().render(xScaleHandler);
                }
                if (gizmoMode == GizmoMode.TRANSLATION || gizmoMode == GizmoMode.TRANSLATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                    Graphics.getMainRenderer().render(yArrow);
                    Graphics.getMainRenderer().render(xArrow);
                    Graphics.getMainRenderer().render(centrePoint);
                }
                if (gizmoMode == GizmoMode.ROTATION || gizmoMode == GizmoMode.TRANSLATION_ROTATION || gizmoMode == GizmoMode.ROTATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                    rotationCircle.draw();
                    Graphics.getMainRenderer().render(rotationHandler);
                }

                Graphics.getMainRenderer().render(centrePointToEditCentre);

                yArrow.getComponent(TransformComponent.class).getTransform().update(0.0f);
                xArrow.getComponent(TransformComponent.class).getTransform().update(0.0f);
                centrePoint.getComponent(TransformComponent.class).getTransform().update(0.0f);
                centrePointToEditCentre.getComponent(TransformComponent.class).getTransform().update(0.0f);
                rotationCircle.getTransform().update(0.0f);
                rotationHandler.getComponent(TransformComponent.class).getTransform().update(0.0f);
                yScaleHandler.getComponent(TransformComponent.class).getTransform().update(0.0f);
                yScaleLine.getTransform().update(0.0f);
                xScaleHandler.getComponent(TransformComponent.class).getTransform().update(0.0f);
                xScaleLine.getTransform().update(0.0f);

                if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
                    gizmoPickingTarget.bind();

                    GL13.glClear(GL13.GL_COLOR_BUFFER_BIT);

                    GL13.glDisable(GL13.GL_BLEND);

                    Vector4f yArrowLastColor = new Vector4f(yArrow.getColor());
                    yArrow.setColor(new Vector4f(yArrow.getPickColor().x / 255.0f, yArrow.getPickColor().y / 255.0f, yArrow.getPickColor().z / 255.0f, 1.0f));
                    Vector4f xArrowLastColor = new Vector4f(xArrow.getColor());
                    xArrow.setColor(new Vector4f(xArrow.getPickColor().x / 255.0f, xArrow.getPickColor().y / 255.0f, xArrow.getPickColor().z / 255.0f, 1.0f));
                    Vector4f centrePointLastColor = new Vector4f(centrePoint.getColor());
                    centrePoint.setColor(new Vector4f(centrePoint.getPickColor().x / 255.0f, centrePoint.getPickColor().y / 255.0f, centrePoint.getPickColor().z / 255.0f, 1.0f));
                    Vector4f centrePointToEditCentreLastColor = new Vector4f(centrePointToEditCentre.getColor());
                    centrePointToEditCentre.setColor(new Vector4f(centrePointToEditCentre.getPickColor().x / 255.0f, centrePointToEditCentre.getPickColor().y / 255.0f, centrePointToEditCentre.getPickColor().z / 255.0f, 1.0f));
                    Vector4f rotationHandlerLastColor = new Vector4f(rotationHandler.getColor());
                    rotationHandler.setColor(new Vector4f(rotationHandler.getPickColor().x / 255.0f, rotationHandler.getPickColor().y / 255.0f, rotationHandler.getPickColor().z / 255.0f, 1.0f));
                    Vector4f yScaleHandlerLastColor = new Vector4f(yScaleHandler.getColor());
                    yScaleHandler.setColor(new Vector4f(yScaleHandler.getPickColor().x / 255.0f, yScaleHandler.getPickColor().y / 255.0f, yScaleHandler.getPickColor().z / 255.0f, 1.0f));
                    Vector4f xScaleHandlerLastColor = new Vector4f(xScaleHandler.getColor());
                    xScaleHandler.setColor(new Vector4f(xScaleHandler.getPickColor().x / 255.0f, xScaleHandler.getPickColor().y / 255.0f, xScaleHandler.getPickColor().z / 255.0f, 1.0f));

                    TextureComponent yArrowTextureComponent = yArrow.getComponent(TextureComponent.class);
                    TextureComponent xArrowTextureComponent = xArrow.getComponent(TextureComponent.class);
                    TextureComponent centrePointTextureComponent = centrePoint.getComponent(TextureComponent.class);
                    TextureComponent centrePointToEditCentreTextureComponent = centrePointToEditCentre.getComponent(TextureComponent.class);
                    TextureComponent rotationHandlerTextureComponent = rotationHandler.getComponent(TextureComponent.class);
                    TextureComponent yScaleHandlerTextureComponent = yScaleHandler.getComponent(TextureComponent.class);
                    TextureComponent xScaleHandlerTextureComponent = xScaleHandler.getComponent(TextureComponent.class);

                    yArrowTextureComponent.setTextureDrawMode(TextureDrawModes.ONLY_ALPHA);
                    xArrowTextureComponent.setTextureDrawMode(TextureDrawModes.ONLY_ALPHA);
                    centrePointTextureComponent.setTextureDrawMode(TextureDrawModes.ONLY_ALPHA);
                    centrePointToEditCentreTextureComponent.setTextureDrawMode(TextureDrawModes.ONLY_ALPHA);
                    rotationHandlerTextureComponent.setTextureDrawMode(TextureDrawModes.ONLY_ALPHA);
                    yScaleHandlerTextureComponent.setTextureDrawMode(TextureDrawModes.ONLY_ALPHA);
                    xScaleHandlerTextureComponent.setTextureDrawMode(TextureDrawModes.ONLY_ALPHA);

                    if (gizmoMode == GizmoMode.SCALE || gizmoMode == GizmoMode.TRANSLATION_SCALE || gizmoMode == GizmoMode.ROTATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                        Graphics.getMainRenderer().render(yScaleHandler);
                        Graphics.getMainRenderer().render(xScaleHandler);
                    }
                    if (gizmoMode == GizmoMode.TRANSLATION || gizmoMode == GizmoMode.TRANSLATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                        Graphics.getMainRenderer().render(yArrow);
                        Graphics.getMainRenderer().render(xArrow);
                        Graphics.getMainRenderer().render(centrePoint);
                    }
                    if (gizmoMode == GizmoMode.ROTATION || gizmoMode == GizmoMode.TRANSLATION_ROTATION || gizmoMode == GizmoMode.ROTATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                        Graphics.getMainRenderer().render(rotationHandler);
                    }

                    Graphics.getMainRenderer().render(centrePointToEditCentre);

                    Vector4f pickedColor = Graphics.getPixelColor(Mouse.getMousePosition());

                    yArrow.setColor(yArrowLastColor);
                    xArrow.setColor(xArrowLastColor);
                    centrePoint.setColor(centrePointLastColor);
                    centrePointToEditCentre.setColor(centrePointToEditCentreLastColor);
                    rotationHandler.setColor(rotationHandlerLastColor);
                    yScaleHandler.setColor(yScaleHandlerLastColor);
                    xScaleHandler.setColor(xScaleHandlerLastColor);
                    yArrowTextureComponent.setTextureDrawMode(TextureDrawModes.DEFAULT);
                    xArrowTextureComponent.setTextureDrawMode(TextureDrawModes.DEFAULT);
                    centrePointTextureComponent.setTextureDrawMode(TextureDrawModes.DEFAULT);
                    centrePointToEditCentreTextureComponent.setTextureDrawMode(TextureDrawModes.DEFAULT);
                    rotationHandlerTextureComponent.setTextureDrawMode(TextureDrawModes.DEFAULT);
                    yScaleHandlerTextureComponent.setTextureDrawMode(TextureDrawModes.DEFAULT);
                    xScaleHandlerTextureComponent.setTextureDrawMode(TextureDrawModes.DEFAULT);

                    GL13.glEnable(GL13.GL_BLEND);

                    gizmoPickingTarget.unBind();

                    if (pickedColor.x == yArrow.getPickColor().x &&
                            pickedColor.y == yArrow.getPickColor().y &&
                            pickedColor.z == yArrow.getPickColor().z &&
                            pickedColor.w != 0.0f) {
                        selectedGizmoTool = yArrow;
                    } else if (pickedColor.x == xArrow.getPickColor().x &&
                            pickedColor.y == xArrow.getPickColor().y &&
                            pickedColor.z == xArrow.getPickColor().z &&
                            pickedColor.w != 0.0f) {
                        selectedGizmoTool = xArrow;
                    } else if (pickedColor.x == centrePoint.getPickColor().x &&
                            pickedColor.y == centrePoint.getPickColor().y &&
                            pickedColor.z == centrePoint.getPickColor().z &&
                            pickedColor.w != 0.0f) {
                        selectedGizmoTool = centrePoint;
                    } else if (pickedColor.x == rotationHandler.getPickColor().x &&
                            pickedColor.y == rotationHandler.getPickColor().y &&
                            pickedColor.z == rotationHandler.getPickColor().z &&
                            pickedColor.w != 0.0f) {
                        selectedGizmoTool = rotationHandler;
                    } else if (pickedColor.x == yScaleHandler.getPickColor().x &&
                            pickedColor.y == yScaleHandler.getPickColor().y &&
                            pickedColor.z == yScaleHandler.getPickColor().z &&
                            pickedColor.w != 0.0f) {
                        selectedGizmoTool = yScaleHandler;
                    } else if (pickedColor.x == xScaleHandler.getPickColor().x &&
                            pickedColor.y == xScaleHandler.getPickColor().y &&
                            pickedColor.z == xScaleHandler.getPickColor().z &&
                            pickedColor.w != 0.0f) {
                        selectedGizmoTool = xScaleHandler;
                    } else if (pickedColor.x == centrePointToEditCentre.getPickColor().x &&
                            pickedColor.y == centrePointToEditCentre.getPickColor().y &&
                            pickedColor.z == centrePointToEditCentre.getPickColor().z &&
                            pickedColor.w != 0.0f) {
                        selectedGizmoTool = centrePointToEditCentre;
                    }

                    if (selectedGizmoTool != null) {
                        selectedGizmoTool.setColor(new Vector4f(selectedGizmoTool.getColor().x * 0.75f,
                                selectedGizmoTool.getColor().y * 0.75f,
                                selectedGizmoTool.getColor().z * 0.75f,
                                selectedGizmoTool.getColor().w));
                        CameraController.allowMove = false;
                        lastMousePosition.set(ViewsManager.getSceneView().getMouseOGLPosition(Mouse.getMousePosition()));
                    }
                }

                if (Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_1) && selectedGizmoTool != null) {
                    selectedGizmoTool.setColor(new Vector4f(selectedGizmoTool.getColor().x / 0.75f,
                            selectedGizmoTool.getColor().y / 0.75f,
                            selectedGizmoTool.getColor().z / 0.75f,
                            selectedGizmoTool.getColor().w));
                    selectedGizmoTool = null;
                    CameraController.allowMove = true;
                }

                if (Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_1) && selectedGizmoTool != null) {
                    Vector2f mouseOGLPosition = new Vector2f(ViewsManager.getSceneView().getMouseOGLPosition(Mouse.getMousePosition()));

                    Vector2f offset = new Vector2f(lastMousePosition).add(new Vector2f(mouseOGLPosition).negate());
                    Vector2f notRotatedOffset = new Vector2f(offset);
                    lastMousePosition.set(mouseOGLPosition);
                    if (object2DTransform.getParentTransform() != null) {
                        MathUtils.rotate(offset, -MatrixUtils.getRotation(object2DTransform.getParentTransform().getResultModelMatrix()),
                                new Vector2f(0.0f, 0.0f));
                    }
                    switch (selectedGizmoTool.getName()) {
                        case "gizmo.yArrow" -> object2DTransform.translate(new Vector2f(0.0f, -offset.y));
                        case "gizmo.xArrow" -> object2DTransform.translate(new Vector2f(-offset.x, 0.0f));
                        case "gizmo.centrePoint" -> object2DTransform.translate(new Vector2f(-offset.x, -offset.y));
                        case "gizmo.rotationHandler" -> {
                            Vector2f p = new Vector2f(object2DCentrePosition);
                            if(object2DTransform.getParentTransform() != null) {
                                Vector2f parentPosition = MatrixUtils.getPosition(object2DTransform.getParentTransform().getResultModelMatrix());
                                MathUtils.rotate(mouseOGLPosition, -MatrixUtils.getRotation(object2DTransform.getParentTransform().getResultModelMatrix()), parentPosition);
                            }
                            object2DTransform.setRotation((float) (Math.atan2(mouseOGLPosition.y - p.y, mouseOGLPosition.x - p.x) / Math.PI / 2f) * 360f - (360f / 4f));
                        }
                        case "gizmo.yScaleHandler" ->
                                object2DTransform.scale(new Vector2f(0.0f, -offset.y * scaleSensitivity.y));
                        case "gizmo.xScaleHandler" ->
                                object2DTransform.scale(new Vector2f(-offset.x * scaleSensitivity.x, 0.0f));
                        case "gizmo.centrePointToEditCentre" ->
                                object2DTransform.getCentre().add(new Vector2f(offset).negate());
                    }
                }
            }
        }
    }
}