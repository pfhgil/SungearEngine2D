package SungearEngine2D.DebugDraw;

//import Core2D.Component.Components.TextureComponent;
/*import Core2D.Drawable.Primitives.Circle2D;
import Core2D.Drawable.Primitives.Line2D;*/
import Core2D.AssetManager.AssetManager;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.Primitives.CircleComponent;
import Core2D.ECS.Component.Components.Primitives.LineComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Input.PC.Mouse;
import Core2D.Graphics.OpenGL.FrameBuffer;
import Core2D.Transform.Transform;
import Core2D.Utils.MathUtils;
import Core2D.Utils.MatrixUtils;
import SungearEngine2D.CameraController.CameraController;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.Resources;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL13;

import java.lang.Math;

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

    public static final Entity yArrow = Entity.createAsObject2D();
    public static final Entity xArrow = Entity.createAsObject2D();

    public static final Entity centrePoint = Entity.createAsObject2D();

    public static final Entity centrePointToEditCentre = Entity.createAsObject2D();

    public static final Entity rotationCircle = Entity.createAsCircle(); //new Circle2D(300.0f, 1, new Vector4f(0.0f, 1.0f, 0.0f, 0.65f));
    public static final Entity rotationHandler = Entity.createAsObject2D();

    public static final Entity yScaleHandler = Entity.createAsObject2D();
    public static final Entity yScaleLine = Entity.createAsLine();
    public static final Entity xScaleHandler = Entity.createAsObject2D();
    public static final Entity xScaleLine = Entity.createAsLine();

    private static Entity selectedGizmoTool;

    public static boolean active = true;

    private static Vector2f lastMousePosition = new Vector2f();

    private static FrameBuffer gizmoPickingTarget;

    private static Shader pickingShader;

    public static void init()
    {
        Vector2i size = Graphics.getScreenSize();
        gizmoPickingTarget = new FrameBuffer(size.x, size.y, FrameBuffer.BuffersTypes.COLOR_BUFFER, GL13.GL_TEXTURE0);

        yArrow.name = "gizmo.yArrow";
        xArrow.name = "gizmo.xArrow";
        centrePoint.name = "gizmo.centrePoint";
        centrePointToEditCentre.name = "gizmo.centrePointToEditCentre";
        rotationHandler.name = "gizmo.rotationHandler";
        yScaleHandler.name = "gizmo.yScaleHandler";
        xScaleHandler.name = "gizmo.xScaleHandler";

        yArrow.getComponent(MeshComponent.class).setTexture(Resources.Textures.Gizmo.gizmoArrow);
        xArrow.getComponent(MeshComponent.class).setTexture(Resources.Textures.Gizmo.gizmoArrow);
        centrePoint.getComponent(MeshComponent.class).setTexture(Resources.Textures.Gizmo.gizmoPoint);
        centrePointToEditCentre.getComponent(MeshComponent.class).setTexture(Resources.Textures.Gizmo.gizmoPoint);
        //rotationCircle.getComponent(TextureComponent.class).setTexture2D(Resources.Textures.Gizmo.gizmoCircle);
        rotationHandler.getComponent(MeshComponent.class).setTexture(Resources.Textures.Gizmo.gizmoPoint);
        yScaleHandler.getComponent(MeshComponent.class).setTexture(Resources.Textures.Gizmo.gizmoPoint);
        xScaleHandler.getComponent(MeshComponent.class).setTexture(Resources.Textures.Gizmo.gizmoPoint);

        yArrow.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 0.65f));
        xArrow.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 0.65f));
        centrePoint.setColor(new Vector4f(0.5f, 0.5f, 0.5f, 1));
        centrePointToEditCentre.setColor(new Vector4f(0.25f, 0.9f, 0.5f, 1.0f));
        rotationCircle.getComponent(CircleComponent.class).setColor(new Vector4f(0.0f, 1.0f, 0.0f, 0.65f));
        rotationHandler.setColor(new Vector4f(0.5f, 0.5f, 0.5f, 1));
        yScaleHandler.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1));
        yScaleLine.getComponent(LineComponent.class).setColor(new Vector4f(1.0f, 0.0f, 0.0f, 0.65f));
        xScaleHandler.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1));
        xScaleLine.getComponent(LineComponent.class).setColor(new Vector4f(0.0f, 1.0f, 0.0f, 0.65f));

        //xArrow.getComponent(TransformComponent.class).getTransform().setRotation(-90.0f);

        Vector2f arrowScale = new Vector2f(Resources.Textures.Gizmo.gizmoArrow.getTexture2DData().getWidth() / 100.0f, Resources.Textures.Gizmo.gizmoArrow.getTexture2DData().getHeight() / 100.0f).mul(1.5f);
        Vector2f pointScale = new Vector2f(Resources.Textures.Gizmo.gizmoPoint.getTexture2DData().getWidth() / 100.0f, Resources.Textures.Gizmo.gizmoPoint.getTexture2DData().getHeight() / 100.0f);
        //Vector2f rotationCircleScale = new Vector2f(Resources.Textures.Gizmo.gizmoCircle.getWidth() / 100.0f, Resources.Textures.Gizmo.gizmoCircle.getWidth() / 100.0f).mul(5.5f);
        yArrow.getComponent(TransformComponent.class).scale.set(arrowScale);
        xArrow.getComponent(TransformComponent.class).scale.set(arrowScale);
        centrePoint.getComponent(TransformComponent.class).scale.set(pointScale);
        centrePointToEditCentre.getComponent(TransformComponent.class).scale.set(new Vector2f(pointScale).div(2.5f));
        rotationCircle.getComponent(CircleComponent.class).setRadius(300.0f);
        rotationCircle.getComponent(CircleComponent.class).setAngleIncrement(5);
        //rotationCircle.getComponent(TransformComponent.class).getTransform().setScale(rotationCircleScale);
        rotationHandler.getComponent(TransformComponent.class).scale.set(pointScale);
        yScaleHandler.getComponent(TransformComponent.class).scale.set(pointScale);
        xScaleHandler.getComponent(TransformComponent.class).scale.set(pointScale);

        yScaleLine.getComponent(LineComponent.class).setLinesWidth(6.0f);
        xScaleLine.getComponent(LineComponent.class).setLinesWidth(6.0f);
        //Transform rotationCircleTransform = rotationCircle.getComponent(TransformComponent.class).getTransform();
        //rotationHandler.getComponent(TransformComponent.class).getTransform().setPosition(new Vector2f(rotationCircleTransform.getPosition()).add(new Vector2f(0.0f, rotationCircleTransform.getScale().x * 100.0f / 2.0f)));

        pickingShader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/mesh/picking_shader.glsl"));
    }

    public static void draw()
    {
        yArrow.active = false;
        xArrow.active = false;

        centrePoint.active = false;

        centrePointToEditCentre.active = false;

        rotationCircle.active = false;
        rotationHandler.active = false;

        yScaleHandler.active = false;
        yScaleLine.active = false;
        xScaleHandler.active = false;
        xScaleLine.active = false;

        if(ViewsManager.getInspectorView().getCurrentInspectingObject() instanceof Entity entity && active) {
            if(!entity.isShouldDestroy()) {
                TransformComponent entityTransformComponent = entity.getComponent(TransformComponent.class);
                // Draw custom GIZMO
                if(entityTransformComponent == null) return;

                Vector2f entityPosition = MatrixUtils.getPosition(entityTransformComponent.modelMatrix);
                float entityRotation = MatrixUtils.getRotation(entityTransformComponent.modelMatrix);

                Vector2f object2DCentrePosition = new Vector2f();
                if(entityTransformComponent.parentTransformComponent != null) {
                    Vector2f parentPosition = MatrixUtils.getPosition(entityTransformComponent.parentTransformComponent.modelMatrix);
                    object2DCentrePosition.set(new Vector2f(entityTransformComponent.position)
                                    .mul(MatrixUtils.getScale(entityTransformComponent.parentTransformComponent.modelMatrix)))
                            .add(entityTransformComponent.centre);
                    object2DCentrePosition.add(parentPosition);
                    MathUtils.rotate(object2DCentrePosition, MatrixUtils.getRotation(entityTransformComponent.parentTransformComponent.modelMatrix), parentPosition);
                } else {
                    object2DCentrePosition.set(entityTransformComponent.position).add(entityTransformComponent.centre);
                }

                TransformComponent yArrowTransform = yArrow.getComponent(TransformComponent.class);
                TransformComponent xArrowTransform = xArrow.getComponent(TransformComponent.class);
                TransformComponent rotationHandlerTransform = rotationHandler.getComponent(TransformComponent.class);
                TransformComponent rotationCircleTransform = rotationCircle.getComponent(TransformComponent.class);
                TransformComponent yScaleHandlerTransform = yScaleHandler.getComponent(TransformComponent.class);
                TransformComponent xScaleHandlerTransform = xScaleHandler.getComponent(TransformComponent.class);

                yArrowTransform.position.set(new Vector2f(entityPosition).add(new Vector2f(0.0f, yArrowTransform.scale.y * 100.0f / 2.0f)));
                xArrowTransform.position.set(new Vector2f(entityPosition));

                centrePoint.getComponent(TransformComponent.class).position.set(entityPosition);
                centrePointToEditCentre.getComponent(TransformComponent.class).position.set(new Vector2f(object2DCentrePosition));

                rotationCircleTransform.position.set(object2DCentrePosition);
                rotationHandlerTransform.position.set(new Vector2f(rotationCircleTransform.position)
                        .add(new Vector2f(0.0f, rotationCircle.getComponent(CircleComponent.class).getRadius())));
                Vector2f rotationOffset = new Vector2f(object2DCentrePosition).add(new Vector2f(rotationHandlerTransform.position).negate());
                rotationHandlerTransform.rotation = entityRotation;
                rotationHandlerTransform.centre.set(rotationOffset);

                float parentRotation = 0.0f;
                Vector2f yScaleLineEnd = new Vector2f(0.0f, 350.0f);
                Vector2f xScaleLineEnd = new Vector2f(350.0f, 0.0f);
                Vector2f xArrowOffset = new Vector2f(xArrowTransform.scale.y * 100.0f / 2.0f, 0.0f);
                if (entityTransformComponent.parentTransformComponent != null) {
                    parentRotation = MatrixUtils.getRotation(entityTransformComponent.parentTransformComponent.modelMatrix);
                    MathUtils.rotate(yScaleLineEnd, parentRotation, new Vector2f(0.0f));
                    MathUtils.rotate(xScaleLineEnd, parentRotation, new Vector2f(0.0f));
                    MathUtils.rotate(xArrowOffset, 90.0f, new Vector2f(0.0f));
                    xArrowTransform.position.add(xArrowOffset);

                    yArrowTransform.rotation = parentRotation;
                    xArrowTransform.rotation = parentRotation - 90.0f;

                    yArrowTransform.centre.set(new Vector2f(entityPosition).add(new Vector2f(yArrowTransform.position).negate()));
                    xArrowTransform.centre.set(new Vector2f(entityPosition).add(new Vector2f(xArrowTransform.position).negate()));
                } else {
                    xArrowTransform.position.add(xArrowOffset);
                    yArrowTransform.rotation = 0.0f;
                    xArrowTransform.rotation = -90.0f;
                }

                yScaleLine.getComponent(LineComponent.class).getLinesData()[0].getVertices()[0].set(entityPosition);
                yScaleLine.getComponent(LineComponent.class).getLinesData()[0].getVertices()[1].set(new Vector2f(entityPosition).add(yScaleLineEnd));
                xScaleLine.getComponent(LineComponent.class).getLinesData()[0].getVertices()[0].set(entityPosition);
                xScaleLine.getComponent(LineComponent.class).getLinesData()[0].getVertices()[1].set(new Vector2f(entityPosition).add(xScaleLineEnd));
                yScaleHandlerTransform.position.set(new Vector2f(entityPosition).add(yScaleLineEnd));
                xScaleHandlerTransform.position.set(new Vector2f(entityPosition).add(xScaleLineEnd));

                if (gizmoMode == GizmoMode.SCALE || gizmoMode == GizmoMode.TRANSLATION_SCALE || gizmoMode == GizmoMode.ROTATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                    yScaleLine.active = true;
                    yScaleHandler.active = true;
                    xScaleLine.active = true;
                    xScaleHandler.active = true;
                }
                if (gizmoMode == GizmoMode.TRANSLATION || gizmoMode == GizmoMode.TRANSLATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                    yArrow.active = true;
                    xArrow.active = true;
                    centrePoint.active = true;
                }
                if (gizmoMode == GizmoMode.ROTATION || gizmoMode == GizmoMode.TRANSLATION_ROTATION || gizmoMode == GizmoMode.ROTATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                    rotationCircle.active = true;
                    rotationHandler.active = true;
                }

                centrePointToEditCentre.active = true;
                //Graphics.getMainRenderer().render(centrePointToEditCentre);

                yArrow.update();
                xArrow.update();
                centrePoint.update();
                centrePointToEditCentre.update();
                rotationCircle.update();
                rotationHandler.update();
                yScaleHandler.update();
                yScaleLine.update();
                xScaleHandler.update();
                xScaleLine.update();

                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(yArrow.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(xArrow.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(centrePoint.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(centrePointToEditCentre.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(rotationCircle.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(rotationHandler.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(yScaleHandler.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(yScaleLine.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(xScaleHandler.getComponent(TransformComponent.class));
                ECSWorld.getCurrentECSWorld().transformationsSystem.updateAllMatrices(xScaleLine.getComponent(TransformComponent.class));

                if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
                    gizmoPickingTarget.bind();
                    gizmoPickingTarget.clear();

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

                    if (gizmoMode == GizmoMode.SCALE || gizmoMode == GizmoMode.TRANSLATION_SCALE || gizmoMode == GizmoMode.ROTATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                        Graphics.getMainRenderer().render(yScaleLine, Main.getMainCamera2DComponent(), pickingShader);
                        Graphics.getMainRenderer().render(yScaleHandler, Main.getMainCamera2DComponent(), pickingShader);
                        Graphics.getMainRenderer().render(xScaleLine, Main.getMainCamera2DComponent(), pickingShader);
                        Graphics.getMainRenderer().render(xScaleHandler, Main.getMainCamera2DComponent(), pickingShader);
                    }
                    if (gizmoMode == GizmoMode.TRANSLATION || gizmoMode == GizmoMode.TRANSLATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                        Graphics.getMainRenderer().render(yArrow, Main.getMainCamera2DComponent(), pickingShader);
                        Graphics.getMainRenderer().render(xArrow, Main.getMainCamera2DComponent(), pickingShader);
                        Graphics.getMainRenderer().render(centrePoint, Main.getMainCamera2DComponent(), pickingShader);
                    }
                    if (gizmoMode == GizmoMode.ROTATION || gizmoMode == GizmoMode.TRANSLATION_ROTATION || gizmoMode == GizmoMode.ROTATION_SCALE || gizmoMode == GizmoMode.TRANSLATION_ROTATION_SCALE) {
                        Graphics.getMainRenderer().render(rotationCircle, Main.getMainCamera2DComponent(), pickingShader);
                        Graphics.getMainRenderer().render(rotationHandler, Main.getMainCamera2DComponent(), pickingShader);
                    }

                    Graphics.getMainRenderer().render(centrePointToEditCentre, Main.getMainCamera2DComponent());

                    Vector4f pickedColor = Graphics.getPixelColor(Mouse.getMousePosition());

                    yArrow.setColor(yArrowLastColor);
                    xArrow.setColor(xArrowLastColor);
                    centrePoint.setColor(centrePointLastColor);
                    centrePointToEditCentre.setColor(centrePointToEditCentreLastColor);
                    rotationHandler.setColor(rotationHandlerLastColor);
                    yScaleHandler.setColor(yScaleHandlerLastColor);
                    xScaleHandler.setColor(xScaleHandlerLastColor);

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
                    if (entityTransformComponent.parentTransformComponent != null) {
                        MathUtils.rotate(offset, -MatrixUtils.getRotation(entityTransformComponent.parentTransformComponent.modelMatrix),
                                new Vector2f(0.0f, 0.0f));
                    }
                    switch (selectedGizmoTool.name) {
                        case "gizmo.yArrow" -> entityTransformComponent.position.add(new Vector2f(0.0f, -offset.y));
                        case "gizmo.xArrow" -> entityTransformComponent.position.add(new Vector2f(-offset.x, 0.0f));
                        case "gizmo.centrePoint" -> entityTransformComponent.position.add(new Vector2f(-offset.x, -offset.y));
                        case "gizmo.rotationHandler" -> {
                            Vector2f p = new Vector2f(object2DCentrePosition);
                            if(entityTransformComponent.parentTransformComponent != null) {
                                Vector2f parentPosition = MatrixUtils.getPosition(entityTransformComponent.parentTransformComponent.modelMatrix);
                                MathUtils.rotate(mouseOGLPosition, -MatrixUtils.getRotation(entityTransformComponent.parentTransformComponent.modelMatrix), parentPosition);
                            }
                            entityTransformComponent.rotation = (float) (Math.atan2(mouseOGLPosition.y - p.y, mouseOGLPosition.x - p.x) / Math.PI / 2f) * 360f - (360f / 4f);
                        }
                        case "gizmo.yScaleHandler" ->
                                entityTransformComponent.scale.add(new Vector2f(0.0f, -offset.y * scaleSensitivity.y));
                        case "gizmo.xScaleHandler" ->
                                entityTransformComponent.scale.add(new Vector2f(-offset.x * scaleSensitivity.x, 0.0f));
                        case "gizmo.centrePointToEditCentre" ->
                                entityTransformComponent.centre.add(new Vector2f(offset).negate());
                    }
                }

                Graphics.getMainRenderer().render(yArrow, Main.getMainCamera2DComponent());
                Graphics.getMainRenderer().render(xArrow, Main.getMainCamera2DComponent());

                Graphics.getMainRenderer().render(centrePoint, Main.getMainCamera2DComponent());

                Graphics.getMainRenderer().render(centrePointToEditCentre, Main.getMainCamera2DComponent());

                Graphics.getMainRenderer().render(rotationCircle, Main.getMainCamera2DComponent());
                Graphics.getMainRenderer().render(rotationHandler, Main.getMainCamera2DComponent());

                Graphics.getMainRenderer().render(yScaleHandler, Main.getMainCamera2DComponent());
                Graphics.getMainRenderer().render(yScaleLine, Main.getMainCamera2DComponent());
                Graphics.getMainRenderer().render(xScaleHandler, Main.getMainCamera2DComponent());
                Graphics.getMainRenderer().render(xScaleLine, Main.getMainCamera2DComponent());
            }
        }
    }
}