package Core2D.Debug;

import Core2D.ECS.Component.Components.Primitives.BoxComponent;
import Core2D.ECS.Component.Components.Primitives.CircleComponent;
import Core2D.ECS.Component.Components.Primitives.LineComponent;
import Core2D.ECS.Component.Components.Primitives.PrimitiveComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class DebugDraw
{
    private static Map<String, Entity> debugPrimitives = new HashMap<>();

    public static void drawLine(String ID, Vector3f start, Vector3f end, Vector4f color, float width)
    {
        Entity foundEntity = debugPrimitives.get(ID);

        if(foundEntity == null) {
            foundEntity = Entity.createAsLine();
            debugPrimitives.put(ID, foundEntity);
        }

        foundEntity.active = true;

        LineComponent primitiveComponent = foundEntity.getComponent(LineComponent.class);

        if(primitiveComponent != null) {
            primitiveComponent.setLinesWidth(width);
            primitiveComponent.getLinesData()[0].getVertices()[0].set(start);
            primitiveComponent.getLinesData()[0].getVertices()[1].set(end);

            primitiveComponent.setColor(color);
        }

        foundEntity.update();
    }

    public static void drawLine(String ID, Vector3f start, Vector3f end)
    {
        drawLine(ID, start, end, new Vector4f(1f), 5f);
    }

    public static void drawLine(String ID, Vector3f start, Vector3f end, Vector4f color)
    {
        drawLine(ID, start, end, color, 5f);
    }

    public static void drawBox(String ID, Vector3f position, Vector3f rotation, Vector2f size, Vector4f color, float width)
    {
        Entity foundEntity = debugPrimitives.get(ID);

        if(foundEntity == null) {
            foundEntity = Entity.createAsBox();
            debugPrimitives.put(ID, foundEntity);
        }

        foundEntity.active = true;

        BoxComponent primitiveComponent = foundEntity.getComponent(BoxComponent.class);
        TransformComponent transformComponent = foundEntity.getComponent(TransformComponent.class);

        if(primitiveComponent != null) {
            primitiveComponent.setLinesWidth(width);
            primitiveComponent.setSize(size);
            primitiveComponent.setColor(color);

            transformComponent.position.set(position);
            transformComponent.rotation.set(rotation);
        }

        foundEntity.update();
    }

    public static void drawBox(String ID, Vector3f position, Vector3f rotation)
    {
        drawBox(ID, position, rotation, new Vector2f(100f), new Vector4f(1f), 5f);
    }

    public static void drawBox(String ID, Vector3f position, Vector3f rotation, Vector2f size)
    {
        drawBox(ID, position, rotation, size, new Vector4f(1f), 5f);
    }

    public static void drawBox(String ID, Vector3f position, Vector3f rotation, Vector2f size, Vector4f color)
    {
        drawBox(ID, position, rotation, size, color, 5f);
    }

    // TODO: после переноса примитивов на ecs добавить сюда аргумент angleIncrement
    public static void drawCircle(String ID, Vector3f position, Vector3f rotation, float radius, Vector4f color, float width)
    {
        Entity foundEntity = debugPrimitives.get(ID);

        if(foundEntity == null) {
            foundEntity = Entity.createAsCircle();
            foundEntity.getComponent(CircleComponent.class).setAngleIncrement(8);
            debugPrimitives.put(ID, foundEntity);
        }

        foundEntity.active = true;

        CircleComponent primitiveComponent = foundEntity.getComponent(CircleComponent.class);
        TransformComponent transformComponent = foundEntity.getComponent(TransformComponent.class);

        if(primitiveComponent != null) {
            primitiveComponent.setLinesWidth(width);
            primitiveComponent.setRadius(radius);
            primitiveComponent.setColor(color);

            transformComponent.position.set(position);
            transformComponent.rotation.set(rotation);
        }

        foundEntity.update();
    }

    public static void drawCircle(String ID, Vector3f position, Vector3f rotation)
    {
        drawCircle(ID, position, rotation, 100f, new Vector4f(1f), 5f);
    }

    public static void drawCircle(String ID, Vector3f position, Vector3f rotation, float radius)
    {
        drawCircle(ID, position, rotation, radius, new Vector4f(1f), 5f);
    }

    public static void drawCircle(String ID, Vector3f position, Vector3f rotation, float radius, Vector4f color)
    {
        drawCircle(ID, position, rotation, radius, color, 5f);
    }

    public static Map<String, Entity> getDebugPrimitives() { return debugPrimitives; }
}
