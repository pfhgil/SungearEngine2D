package Core2D.Deserializers;

import Core2D.GameObject.GameObject;
import Core2D.Layering.Layer;
import com.google.gson.*;

import java.lang.reflect.Type;

public class LayerDeserializer implements JsonDeserializer<Layer>
{
    @Override
    public Layer deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        int ID = jsonObject.get("ID").getAsInt();
        JsonArray gameObjects = jsonObject.getAsJsonArray("gameObjects");

        Layer layer = new Layer(ID, name);

        for(JsonElement element : gameObjects) {
            GameObject gameObject = context.deserialize(element, GameObject.class);
            gameObject.setLayer(layer);
        }
        return layer;
    }
}
