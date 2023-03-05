package SungearEngine2D.DebugDraw;

import Core2D.ECS.Component.Components.Physics.BoxCollider2DComponent;
import Core2D.ECS.Component.Components.Physics.CircleCollider2DComponent;
import Core2D.ECS.Component.Components.Primitives.BoxComponent;
import Core2D.ECS.Component.Components.Primitives.CircleComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Physics.PhysicsWorld;
import Core2D.Transform.Transform;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Main;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
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

            if(entityTransformComponent != null) {
                for (int i = 0; i < boxCollider2DComponents.size(); i++) {
                    BoxCollider2DComponent boxCollider2DComponent = boxCollider2DComponents.get(i);

                    if (boxes.size() < i + 1) {
                        Entity newBox = Entity.createAsBox();

                        newBox.getComponent(BoxComponent.class).setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

                        boxes.add(newBox);
                    }

                    if(boxCollider2DComponent.getBoxCollider2D().getFixture() == null) continue;

                    Entity box = boxes.get(i);

                    box.active = true;

                    BoxComponent boxComponent = box.getComponent(BoxComponent.class);

                    PolygonShape polygonShape = (PolygonShape) boxCollider2DComponent.getBoxCollider2D().getFixture().getShape();
                    if(polygonShape == null) return;

                    Vec2[] verticesPos = polygonShape.getVertices();
                    Body body = boxCollider2DComponent.getBoxCollider2D().getRigidbody2D().getBody();

                    Vec2 wp0 = body.getWorldPoint(verticesPos[0]);
                    Vec2 wp1 = body.getWorldPoint(verticesPos[1]);
                    Vec2 wp2 = body.getWorldPoint(verticesPos[2]);
                    Vec2 wp3 = body.getWorldPoint(verticesPos[3]);

                    boxComponent.getLinesData()[0].getVertices()[0].set(wp0.x * PhysicsWorld.RATIO, wp0.y * PhysicsWorld.RATIO);
                    boxComponent.getLinesData()[0].getVertices()[1].set(wp1.x * PhysicsWorld.RATIO, wp1.y * PhysicsWorld.RATIO);

                    boxComponent.getLinesData()[1].getVertices()[0].set(wp1.x * PhysicsWorld.RATIO, wp1.y * PhysicsWorld.RATIO);
                    boxComponent.getLinesData()[1].getVertices()[1].set(wp2.x * PhysicsWorld.RATIO, wp2.y * PhysicsWorld.RATIO);

                    boxComponent.getLinesData()[2].getVertices()[0].set(wp2.x * PhysicsWorld.RATIO, wp2.y * PhysicsWorld.RATIO);
                    boxComponent.getLinesData()[2].getVertices()[1].set(wp3.x * PhysicsWorld.RATIO, wp3.y * PhysicsWorld.RATIO);

                    boxComponent.getLinesData()[3].getVertices()[0].set(wp3.x * PhysicsWorld.RATIO, wp3.y * PhysicsWorld.RATIO);
                    boxComponent.getLinesData()[3].getVertices()[1].set(wp0.x * PhysicsWorld.RATIO, wp0.y * PhysicsWorld.RATIO);

                    box.update();

                    Graphics.getMainRenderer().render(box, Main.getMainCamera2DComponent());
                }

                for (int i = 0; i < circleCollider2DComponents.size(); i++) {
                    CircleCollider2DComponent circleCollider2DComponent = circleCollider2DComponents.get(i);

                    if (circles.size() < i + 1) {
                        Entity newCircle = Entity.createAsCircle();

                        newCircle.getComponent(CircleComponent.class).setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

                        circles.add(newCircle);
                    }

                    if(circleCollider2DComponent.getCircleCollider2D().getFixture() == null) continue;

                    Entity circle = circles.get(i);

                    circle.active = true;

                    CircleComponent circleComponent = circle.getComponent(CircleComponent.class);

                    CircleShape circleShape = (CircleShape) circleCollider2DComponent.getCircleCollider2D().getFixture().getShape();
                    if(circleShape == null) return;

                    Vec2 circlePos = circleCollider2DComponent.getCircleCollider2D().getRigidbody2D().getBody().getWorldPoint(circleShape.m_p);
                    float circleRadius = circleShape.m_radius * PhysicsWorld.RATIO;

                    circleComponent.setOffset(new Vector2f(circlePos.x * PhysicsWorld.RATIO, circlePos.y * PhysicsWorld.RATIO));
                    circleComponent.setRadius(circleRadius);

                    circle.update();

                    Graphics.getMainRenderer().render(circle, Main.getMainCamera2DComponent());
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
}
