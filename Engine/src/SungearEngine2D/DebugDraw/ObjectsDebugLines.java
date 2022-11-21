package SungearEngine2D.DebugDraw;

import Core2D.Component.Components.BoxCollider2DComponent;
import Core2D.Component.Components.CircleCollider2DComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Graphics.Graphics;
import Core2D.Physics.Collider2D.BoxCollider2D;
import Core2D.Physics.Collider2D.CircleCollider2D;
import Core2D.Transform.Transform;
import Core2D.Utils.MathUtils;
import Core2D.Utils.MatrixUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class ObjectsDebugLines
{
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
