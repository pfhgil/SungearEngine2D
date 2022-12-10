package Core2D.Layering;

import Core2D.Component.Component;
import Core2D.Component.Components.MeshRendererComponent;
import Core2D.GameObject.GameObject;
import Core2D.GameObject.RenderParts.Texture2D;
import Core2D.Graphics.Graphics;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Layer
{
    private List<GameObject> gameObjects = new ArrayList<>();

    private int ID;
    private String name;

    private transient boolean shouldDestroy;

    public Layer(int ID, String name)
    {
        this.ID = ID;
        this.name = name;
    }

    // рисует все объекты разными цветами при выборке объектов
    // тут я ставлю цвет объекта для pick и отключаю текстуру
    // TODO: сделать не только для объектов отрисовку
    public void drawPicking()
    {
        for(int i = 0; i < gameObjects.size(); i++) {
            Core2D.GameObject.GameObject gameObject = gameObjects.get(i);

            Vector4f lastColor = new Vector4f(gameObject.getColor());

            gameObject.setColor(new Vector4f(gameObject.getPickColor().x / 255.0f,
                    gameObject.getPickColor().y / 255.0f,
                    gameObject.getPickColor().z / 255.0f,
                    1.0f));

            MeshRendererComponent meshRendererComponent = gameObject.getComponent(MeshRendererComponent.class);
            if(meshRendererComponent != null) {
                meshRendererComponent.textureDrawMode = Texture2D.TextureDrawModes.ONLY_ALPHA;
                Graphics.getMainRenderer().render(gameObject);
                gameObject.setColor(lastColor);
                meshRendererComponent.textureDrawMode = Texture2D.TextureDrawModes.DEFAULT;
            }
        }
    }

    public Core2D.GameObject.GameObject getPickedObject2D(Vector4f pixelColor)
    {
        for(int i = 0; i < gameObjects.size(); i++) {
            Core2D.GameObject.GameObject gameObject = gameObjects.get(i);
            if (gameObject.getPickColor().x == pixelColor.x &&
                    gameObject.getPickColor().y == pixelColor.y &&
                    gameObject.getPickColor().z == pixelColor.z &&
                    pixelColor.w != 0.0f) {
                return gameObject;
            }
        }

        return null;
    }

    public void deltaUpdate(float deltaTime)
    {
        Iterator<Core2D.GameObject.GameObject> layerObjectIterator = gameObjects.iterator();
        while(layerObjectIterator.hasNext()) {
            Core2D.GameObject.GameObject gameObject = layerObjectIterator.next();
            if(gameObject.isShouldDestroy()) {
                if (gameObject.getParentObject2D() != null) {
                    gameObject.getParentObject2D().removeChild(gameObject);
                    gameObject.parentGameObject = null;
                }

                Iterator<Component> componentsIterator = gameObject.getComponents().iterator();
                while (componentsIterator.hasNext()) {
                    Component component = componentsIterator.next();
                    component.destroy();
                    componentsIterator.remove();
                }

                Iterator<Core2D.GameObject.GameObject> childrenIterator = gameObject.getChildrenObjects().iterator();
                while (childrenIterator.hasNext()) {
                    Core2D.GameObject.GameObject child = childrenIterator.next();
                    child.destroy();
                    childrenIterator.remove();
                }
                gameObject.destroy();
                layerObjectIterator.remove();
            } else {
                // ВНИМАНИЕ! ЕСЛИ ДЕЛЬТА АПДЕЙТ БУДЕТ ИСПОЛЬЗОВАН КАК ТО ПО ДРУГОМУ (НАПРИМЕР: БУДЕТ ВЫЗЫВАТЬСЯ В ЦИКЛЕ),
                // ТО gameObject.update() нужно перенести в отдельный метод update у слоя (его нужно реализовать)
                gameObject.update();
                gameObject.deltaUpdate(deltaTime);
            }
        }
    }

    public void destroy()
    {
        shouldDestroy = true;

        Iterator<Core2D.GameObject.GameObject> layerObjectIterator = gameObjects.iterator();
        while(layerObjectIterator.hasNext()) {
            Core2D.GameObject.GameObject gameObject = layerObjectIterator.next();
            gameObject.destroy();
            layerObjectIterator.remove();
        }

        gameObjects = null;
    }

    public List<Core2D.GameObject.GameObject> getGameObjects() { return gameObjects; }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isShouldDestroy() { return shouldDestroy; }
}
