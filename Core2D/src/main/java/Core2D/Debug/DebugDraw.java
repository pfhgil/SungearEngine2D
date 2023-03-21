package Core2D.Debug;

import Core2D.ECS.Component.Components.Primitives.PrimitiveComponent;
import Core2D.ECS.Entity;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class DebugDraw
{
    private static Map<String, Entity> debugPrimitives = new HashMap<>();

    /*
    private static Camera2DComponent.Camera2DCallback debugDrawCallback = new Camera2DComponent.Camera2DCallback() {
        @Override
        public void preRender() {

        }

        @Override
        public void postRender(Camera2DComponent camera2DComponent) {
            for(Entity entity : debugPrimitives.values()) {
                ECSWorld.getCurrentECSWorld().meshesRendererSystem.renderEntity(entity, camera2DComponent);
            }
        }
    };

     */

    public static void drawLine2D(String ID, Vector2f start, Vector2f end)
    {
        Entity foundEntity = debugPrimitives.get(ID);

        if(foundEntity == null) {
            foundEntity = Entity.createAsLine();
            debugPrimitives.put(ID, foundEntity);
            //Log.CurrentSession.println("created line!", Log.MessageType.WARNING);
        }

        PrimitiveComponent primitiveComponent = foundEntity.getComponent(PrimitiveComponent.class);

        if(primitiveComponent != null) {
            primitiveComponent.setLinesWidth(10f);
            primitiveComponent.getLinesData()[0].getVertices()[0].set(start);
            primitiveComponent.getLinesData()[0].getVertices()[1].set(end);
        }

        foundEntity.update();
    }

    public static Map<String, Entity> getDebugPrimitives() { return debugPrimitives; }
}
