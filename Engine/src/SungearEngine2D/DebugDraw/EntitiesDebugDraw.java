package SungearEngine2D.DebugDraw;

import Core2D.ECS.Physics.BoxCollider2DComponent;
import Core2D.ECS.Physics.CircleCollider2DComponent;
import Core2D.ECS.Physics.Rigidbody2DComponent;
import Core2D.ECS.Primitives.BoxComponent;
import Core2D.ECS.Primitives.CircleComponent;
import Core2D.ECS.Transform.TransformComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Settings.PhysicsSettings;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Main.Main;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector3f;
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
            Rigidbody2DComponent rigidbody2DComponent = entity.getComponent(Rigidbody2DComponent.class);

            TransformComponent entityTransformComponent = entity.getComponent(TransformComponent.class);

            if(entityTransformComponent != null && rigidbody2DComponent != null) {
                for (int i = 0; i < boxCollider2DComponents.size(); i++) {
                    BoxCollider2DComponent boxCollider2DComponent = boxCollider2DComponents.get(i);

                    if (boxes.size() < i + 1) {
                        Entity newBox = Entity.createAsBox();

                        newBox.getComponent(BoxComponent.class).color.set(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

                        boxes.add(newBox);
                    }

                    if(boxCollider2DComponent.fixture == null) continue;

                    Entity box = boxes.get(i);

                    box.active = true;

                    BoxComponent boxComponent = box.getComponent(BoxComponent.class);

                    PolygonShape polygonShape = (PolygonShape) boxCollider2DComponent.fixture.getShape();
                    if(polygonShape == null) return;

                    Vec2[] verticesPos = polygonShape.getVertices();
                    Body body = rigidbody2DComponent.body;

                    Vec2 wp0 = body.getWorldPoint(verticesPos[0]);
                    Vec2 wp1 = body.getWorldPoint(verticesPos[1]);
                    Vec2 wp2 = body.getWorldPoint(verticesPos[2]);
                    Vec2 wp3 = body.getWorldPoint(verticesPos[3]);

                    boxComponent.linesData[0].start.set(wp0.x * PhysicsSettings.ratio, wp0.y * PhysicsSettings.ratio, boxComponent.linesData[0].start.z);
                    boxComponent.linesData[0].end.set(wp1.x * PhysicsSettings.ratio, wp1.y * PhysicsSettings.ratio, boxComponent.linesData[0].end.z);

                    boxComponent.linesData[1].start.set(wp1.x * PhysicsSettings.ratio, wp1.y * PhysicsSettings.ratio, boxComponent.linesData[1].start.z);
                    boxComponent.linesData[1].end.set(wp2.x * PhysicsSettings.ratio, wp2.y * PhysicsSettings.ratio, boxComponent.linesData[1].end.z);

                    boxComponent.linesData[2].start.set(wp2.x * PhysicsSettings.ratio, wp2.y * PhysicsSettings.ratio, boxComponent.linesData[2].start.z);
                    boxComponent.linesData[2].end.set(wp3.x * PhysicsSettings.ratio, wp3.y * PhysicsSettings.ratio, boxComponent.linesData[2].end.z);

                    boxComponent.linesData[3].start.set(wp3.x * PhysicsSettings.ratio, wp3.y * PhysicsSettings.ratio, boxComponent.linesData[3].start.z);
                    boxComponent.linesData[3].end.set(wp0.x * PhysicsSettings.ratio, wp0.y * PhysicsSettings.ratio, boxComponent.linesData[3].end.z);

                    box.update();

                    Graphics.getMainRenderer().render(box, Main.getMainCamera2DComponent());
                }

                for (int i = 0; i < circleCollider2DComponents.size(); i++) {
                    CircleCollider2DComponent circleCollider2DComponent = circleCollider2DComponents.get(i);

                    if (circles.size() < i + 1) {
                        Entity newCircle = Entity.createAsCircle();

                        newCircle.getComponent(CircleComponent.class).color.set(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

                        circles.add(newCircle);
                    }

                    if(circleCollider2DComponent.fixture == null) continue;

                    Entity circle = circles.get(i);

                    circle.active = true;

                    CircleComponent circleComponent = circle.getComponent(CircleComponent.class);

                    CircleShape circleShape = (CircleShape) circleCollider2DComponent.fixture.getShape();
                    if(circleShape == null) return;

                    Vec2 circlePos = rigidbody2DComponent.body.getWorldPoint(circleShape.m_p);
                    float circleRadius = circleShape.m_radius * PhysicsSettings.ratio;

                    circleComponent.offset.set(new Vector3f(circlePos.x * PhysicsSettings.ratio, circlePos.y * PhysicsSettings.ratio, circleComponent.offset.z));
                    circleComponent.radius = circleRadius;

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
