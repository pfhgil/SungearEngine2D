package Core2D.Deserializers;

import Core2D.Component.Component;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Object2D.Object2D;
import Core2D.Texture2D.Texture2D;
import Core2D.UI.Text.Text;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;

import java.lang.reflect.Type;

public class Object2DDeserializer implements JsonDeserializer<Object2D>
{
    @Override
    public Object2D deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        Tag tag = context.deserialize(jsonObject.get("tag"), Tag.class);
        Vector4f color = context.deserialize(jsonObject.get("color"), Vector4f.class);
        int drawingMode = context.deserialize(jsonObject.get("drawingMode"), int.class);
        boolean isUIElement = context.deserialize(jsonObject.get("isUIElement"), boolean.class);
        boolean active = context.deserialize(jsonObject.get("active"), boolean.class);
        JsonArray components = jsonObject.getAsJsonArray("components");

        Object2D object2D = new Object2D();

        object2D.setName(name);
        object2D.setTag(tag.getName());
        object2D.setColor(color);
        object2D.setDrawingMode(drawingMode);
        object2D.setUIElement(isUIElement);
        object2D.setActive(active);

        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);
            if(component instanceof TransformComponent) {
                object2D.getComponent(TransformComponent.class).set(component);
            } else if(component instanceof TextureComponent) {
                TextureComponent textureComponent = (TextureComponent) component;

                Texture2D texture2D = new Texture2D(
                        textureComponent.getTexture2D().getSource(),
                        textureComponent.getTexture2D().getParam(),
                        textureComponent.getTexture2D().getGLTextureBlock()
                );

                object2D.getComponent(TextureComponent.class).set(component);
                object2D.getComponent(TextureComponent.class).setTexture2D(texture2D);

                textureComponent = null;
                texture2D = null;
            } else {
                object2D.addComponent(component);
            }

            component = null;
        }

        return object2D;
    }
}
