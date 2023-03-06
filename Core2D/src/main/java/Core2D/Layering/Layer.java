package Core2D.Layering;

import Core2D.AssetManager.AssetManager;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.RenderParts.Shader;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Layer
{
    private List<Entity> entities = new ArrayList<>();

    private int ID;
    private String name;

    private transient boolean shouldDestroy;

    private transient Shader pickingShader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/mesh/picking_shader.glsl"));

    public Layer(int ID, String name)
    {
        this.ID = ID;
        this.name = name;
    }

    // рисует все объекты разными цветами при выборке объектов
    // тут я ставлю цвет объекта для pick и отключаю текстуру
    // TODO: сделать не только для объектов отрисовку
    public void drawPicking(Camera2DComponent camera2DComponent)
    {
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            Vector4f lastColor = new Vector4f(entity.getColor());

            entity.setColor(new Vector4f(entity.getPickColor().x / 255.0f,
                    entity.getPickColor().y / 255.0f,
                    entity.getPickColor().z / 255.0f,
                    1.0f));

            // FIXME
            Graphics.getMainRenderer().render(entity, camera2DComponent, pickingShader);

            entity.setColor(lastColor);
        }
    }

    public Entity getPickedEntity(Vector4f pixelColor)
    {
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity.getPickColor().x == pixelColor.x &&
                    entity.getPickColor().y == pixelColor.y &&
                    entity.getPickColor().z == pixelColor.z &&
                    pixelColor.w != 0.0f) {
                return entity;
            }
        }

        return null;
    }

    // TODO: убрать данный метод, так как не будет update у entity
    public void update()
    {
        Iterator<Entity> layerObjectIterator = entities.iterator();
        while(layerObjectIterator.hasNext()) {
            Entity entity = layerObjectIterator.next();
            if (entity.isShouldDestroy()) {
                if (entity.getParentEntity() != null) {
                    entity.getParentEntity().removeChildEntity(entity);
                    entity.parentEntity = null;
                }

                Iterator<Component> componentsIterator = entity.getComponents().iterator();
                while (componentsIterator.hasNext()) {
                    Component component = componentsIterator.next();
                    component.destroy();
                    componentsIterator.remove();
                }

                Iterator<Entity> childrenIterator = entity.getChildrenEntities().iterator();
                while (childrenIterator.hasNext()) {
                    Entity child = childrenIterator.next();
                    child.destroy();
                    childrenIterator.remove();
                }
                entity.destroy();
                layerObjectIterator.remove();
            } else {
                entity.update();
            }
        }
    }

    // TODO: убрать данный метод, так как не будет deltaUpdate у entity
    public void deltaUpdate(float deltaTime)
    {
        Iterator<Entity> layerObjectIterator = entities.iterator();
        while(layerObjectIterator.hasNext()) {
            Entity entity = layerObjectIterator.next();
            if(!entity.isShouldDestroy()) {
                if (entity.getParentEntity() != null) {
                    entity.deltaUpdate(deltaTime);
                }
            }
        }
    }

    public void destroy()
    {
        shouldDestroy = true;

        Iterator<Entity> layerObjectIterator = entities.iterator();
        while(layerObjectIterator.hasNext()) {
            Entity entity = layerObjectIterator.next();
            entity.destroy();
            layerObjectIterator.remove();
        }
        entities = null;
    }

    public Entity getEntity(int entityID)
    {
        return entities.stream().filter(entity -> entity.ID == entityID).findFirst().orElse(null);
    }

    public List<Entity> getEntities() { return entities; }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isShouldDestroy() { return shouldDestroy; }
}
