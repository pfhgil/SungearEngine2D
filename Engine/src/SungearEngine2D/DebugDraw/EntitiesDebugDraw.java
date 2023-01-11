package SungearEngine2D.DebugDraw;

import Core2D.ECS.Component.Components.BoxCollider2DComponent;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.CircleCollider2DComponent;
import Core2D.ECS.Component.Components.Primitives.BoxComponent;
import Core2D.ECS.Component.Components.Primitives.CircleComponent;
import Core2D.ECS.Component.Components.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.Systems.PrimitivesRendererSystem;
import Core2D.Transform.Transform;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Main;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class EntitiesDebugDraw
{
    private static List<Entity> boxes = new ArrayList<>();
    private static List<Entity> circles = new ArrayList<>();

    public static void draw()
    {
        boxes.forEach(box -> box.active = false);
        circles.forEach(circle -> circle.active = false);

        if(ViewsManager.getInspectorView().getCurrentInspectingObject() instanceof Entity entity) {
            List<BoxCollider2DComponent> boxCollider2DComponents = entity.getAllComponents(BoxCollider2DComponent.class);
            List<CircleCollider2DComponent> circleCollider2DComponents = entity.getAllComponents(CircleCollider2DComponent.class);

            TransformComponent entityTransformComponent = entity.getComponent(TransformComponent.class);
            Transform entityTransform = null;
            if(entityTransformComponent != null) {
                entityTransform = entityTransformComponent.getTransform();
            }

            if(entityTransform != null) {
                for (int i = 0; i < boxCollider2DComponents.size(); i++) {
                    BoxCollider2DComponent boxCollider2DComponent = boxCollider2DComponents.get(i);

                    if (boxes.size() < i + 1) {
                        Entity newBox = new Entity();

                        BoxComponent boxComponent = new BoxComponent();
                        boxComponent.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                        boxComponent.scaleWithEntity = true;

                        newBox.addComponent(new TransformComponent());
                        newBox.addComponent(boxComponent);
                        newBox.addSystem(new PrimitivesRendererSystem());

                        boxes.add(newBox);

                        Main.getMainCamera2D().getComponent(Camera2DComponent.class).getAdditionalEntitiesToRender().add(newBox);
                    }

                    Entity box = boxes.get(i);

                    box.active = true;

                    Transform boxTransform = box.getComponent(TransformComponent.class).getTransform();

                    boxTransform.setPosition(new Vector2f(entityTransform.getPosition()).add(boxCollider2DComponent.getBoxCollider2D().getOffset()));
                    boxTransform.setScale(boxCollider2DComponent.getBoxCollider2D().getScale());
                    boxTransform.setRotation(entityTransform.getRotation());

                    box.update();
                }

                for (int i = 0; i < circleCollider2DComponents.size(); i++) {
                    CircleCollider2DComponent circleCollider2DComponent = circleCollider2DComponents.get(i);

                    if (circles.size() < i + 1) {
                        Entity newCircle = new Entity();

                        CircleComponent circleComponent = new CircleComponent();
                        circleComponent.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                        circleComponent.scaleWithEntity = true;

                        newCircle.addComponent(new TransformComponent());
                        newCircle.addComponent(circleComponent);
                        newCircle.addSystem(new PrimitivesRendererSystem());

                        circles.add(newCircle);

                        Main.getMainCamera2D().getComponent(Camera2DComponent.class).getAdditionalEntitiesToRender().add(newCircle);
                    }

                    Entity circle = circles.get(i);

                    circle.active = true;

                    Transform circleTransform = circle.getComponent(TransformComponent.class).getTransform();

                    circleTransform.setPosition(new Vector2f(entityTransform.getPosition()).add(circleCollider2DComponent.getCircleCollider2D().getOffset()));
                    circle.getComponent(CircleComponent.class).setRadius(circleCollider2DComponent.getCircleCollider2D().getRadius());
                    circleTransform.setRotation(entityTransform.getRotation());

                    circle.update();
                }
            }
        }
    }

    public static void deltaUpdate(float deltaTime)
    {
       for(Entity entity : boxes) {
           entity.deltaUpdate(deltaTime);
       }

        for(Entity entity : circles) {
            entity.deltaUpdate(deltaTime);
        }
    }

    // запрос объектов для отрисовки дебага (занять нужное кол-во объектов для дебага)
    /*
    public static void requestEntities()
    {
        boxesPool.releaseAllUsedPoolObjects();
        circlesPool.releaseAllUsedPoolObjects();

        if(ViewsManager.getInspectorView().getCurrentInspectingObject() instanceof Entity entity) {
            List<BoxCollider2DComponent> boxCollider2DComponents = entity.getAllComponents(BoxCollider2DComponent.class);
            List<CircleCollider2DComponent> circleCollider2DComponents = entity.getAllComponents(CircleCollider2DComponent.class);

            for(BoxCollider2DComponent boxCollider2DComponent : boxCollider2DComponents) {
                if (!boxesPool.hasFree()) {
                    Entity newBox = new Entity();
                    newBox.addComponent(new BoxComponent());
                    boxesPool.addPoolObject(newBox);
                }
                boxesPool.get();
            }

            for(CircleCollider2DComponent circleCollider2DComponent : circleCollider2DComponents) {
                if (!circlesPool.hasFree()) {
                    Entity newCircle = new Entity();
                    newCircle.addComponent(new CircleComponent());
                    circlesPool.addPoolObject(newCircle);
                }
                circlesPool.get();
            }
        }
    }

     */

    /*private static Line2D[] currentPickedObject2DDebugLines = new Line2D[4];

    private static Box2D[] currentPickedObject2DCollidersBoxes2D = new Box2D[100];
    private static Circle2D[] currentPickedObject2DCollidersCircles2D = new Circle2D[100];

    public static void init()
    {
        for(int i = 0; i < currentPickedObject2DCollidersBoxes2D.length; i++) {
            currentPickedObject2DCollidersBoxes2D[i] = new Box2D();
            currentPickedObject2DCollidersBoxes2D[i].setLinesColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
            currentPickedObject2DCollidersCircles2D[i] = new Circle2D(1);
            currentPickedObject2DCollidersCircles2D[i].setLinesColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
            System.out.println("initialized " + i + " colliders");
        }

        for(int i = 0; i < currentPickedObject2DDebugLines.length; i++) {
            currentPickedObject2DDebugLines[i] = new Line2D();
            currentPickedObject2DDebugLines[i].setColor(new Vector4f(0.25f, 0.9f, 0.5f, 1.0f));
            currentPickedObject2DDebugLines[i].setLineWidth(3);
        }
    }

    public static void draw()
    {
        if(ViewsManager.getInspectorView().getCurrentInspectingObject() instanceof Object2D) {
            Object2D object2D = (Object2D) ViewsManager.getInspectorView().getCurrentInspectingObject();
            if(!object2D.isShouldDestroy()) {

                for (Line2D line2D : currentPickedObject2DDebugLines) {
                    Graphics.getMainRenderer().render(line2D);
                }

                Transform transform = object2D.getComponent(TransformComponent.class).getTransform();
                float rotation = MatrixUtils.getRotation(transform.getResultModelMatrix());
                Vector2f center = new Vector2f(MatrixUtils.getPosition(transform.getResultModelMatrix()));
                Vector2f scale = MatrixUtils.getScale(transform.getResultModelMatrix());
                Vector2f halfSize = new Vector2f(100.0f * scale.x, 100.0f * scale.y);

                Vector2f min = new Vector2f(center).sub(new Vector2f(halfSize).mul(0.5f));
                Vector2f max = new Vector2f(center).add(new Vector2f(halfSize).mul(0.5f));

                Vector2f[] vertices = {
                        new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                        new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
                };

                if (rotation != 0.0f) {
                    for (Vector2f vert : vertices) {
                        MathUtils.rotate(vert, rotation, center);
                    }
                }

                currentPickedObject2DDebugLines[0].setStart(new Vector2f(vertices[0].x, vertices[0].y));
                currentPickedObject2DDebugLines[0].setEnd(new Vector2f(vertices[1].x, vertices[1].y));

                currentPickedObject2DDebugLines[1].setStart(new Vector2f(vertices[1].x, vertices[1].y));
                currentPickedObject2DDebugLines[1].setEnd(new Vector2f(vertices[2].x, vertices[2].y));

                currentPickedObject2DDebugLines[2].setStart(new Vector2f(vertices[2].x, vertices[2].y));
                currentPickedObject2DDebugLines[2].setEnd(new Vector2f(vertices[3].x, vertices[3].y));

                currentPickedObject2DDebugLines[3].setStart(new Vector2f(vertices[3].x, vertices[3].y));
                currentPickedObject2DDebugLines[3].setEnd(new Vector2f(vertices[0].x, vertices[0].y));

                List<BoxCollider2DComponent> boxCollider2DComponents = object2D.getAllComponents(BoxCollider2DComponent.class);
                List<CircleCollider2DComponent> circleCollider2DComponents = object2D.getAllComponents(CircleCollider2DComponent.class);

                for(int i = 0; i < boxCollider2DComponents.size(); i++) {
                    if(i < currentPickedObject2DCollidersBoxes2D.length) {
                        BoxCollider2D boxCollider2D = boxCollider2DComponents.get(i).getBoxCollider2D();

                        Vector2f position = new Vector2f(MatrixUtils.getPosition(transform.getResultModelMatrix()));
                        Vector3f rotatedOffset = new Vector3f(boxCollider2D.getOffset().x, boxCollider2D.getOffset().y, 0.0f);
                        rotatedOffset.rotateZ((float) Math.toRadians(MatrixUtils.getRotation(transform.getResultModelMatrix())));
                        position.add(new Vector2f(rotatedOffset.x, rotatedOffset.y));

                        currentPickedObject2DCollidersBoxes2D[i].getTransform().setPosition(position);
                        currentPickedObject2DCollidersBoxes2D[i].getTransform().setRotationAround(MatrixUtils.getRotation(transform.getResultModelMatrix()),
                                new Vector2f(MatrixUtils.getPosition(transform.getResultModelMatrix()).add(new Vector2f(position).add(transform.getCentre()).negate())));
                        currentPickedObject2DCollidersBoxes2D[i].getTransform().setScale(new Vector2f(boxCollider2D.getScale()));

                        currentPickedObject2DCollidersBoxes2D[i].draw();
                    }
                }

                for(int i = 0; i < circleCollider2DComponents.size(); i++) {
                    if(i < currentPickedObject2DCollidersCircles2D.length) {
                        CircleCollider2D circleCollider2D = circleCollider2DComponents.get(i).getCircleCollider2D();

                        Vector2f position = new Vector2f(MatrixUtils.getPosition(transform.getResultModelMatrix()));
                        Vector3f rotatedOffset = new Vector3f(circleCollider2D.getOffset().x, circleCollider2D.getOffset().y, 0.0f);
                        rotatedOffset.rotateZ((float) Math.toRadians(MatrixUtils.getRotation(transform.getResultModelMatrix())));
                        position.add(new Vector2f(rotatedOffset.x, rotatedOffset.y));

                        currentPickedObject2DCollidersCircles2D[i].getTransform().setPosition(position);
                        currentPickedObject2DCollidersCircles2D[i].getTransform().setRotationAround(MatrixUtils.getRotation(transform.getResultModelMatrix()),
                                new Vector2f(MatrixUtils.getPosition(transform.getResultModelMatrix()).add(new Vector2f(position).add(transform.getCentre()).negate())));
                        currentPickedObject2DCollidersCircles2D[i].setRadius(circleCollider2D.getRadius());

                        currentPickedObject2DCollidersCircles2D[i].draw();
                    }
                }
            }
        }
    }*/
}
