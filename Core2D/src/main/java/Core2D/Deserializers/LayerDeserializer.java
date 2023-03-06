package Core2D.Deserializers;

import Core2D.ECS.Entity;
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
        JsonArray gameObjects = jsonObject.getAsJsonArray("entities");

        Layer layer = new Layer(ID, name);

        for(JsonElement element : gameObjects) {
            Entity entity = context.deserialize(element, Entity.class);
            entity.setLayer(layer);
        }
        return layer;
    }
}
