package Core2D.Deserializers;

import Core2D.Layering.LayerObject;
import com.google.gson.*;

import java.lang.reflect.Type;

public class LayerObjectDeserializer implements JsonDeserializer<LayerObject>, JsonSerializer<LayerObject> {
    @Override
    public LayerObject deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement properties = jsonObject.get("properties");

        try {
            return new LayerObject(context.deserialize(properties, Class.forName(type)));
        } catch(ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }

    @Override
    public JsonElement serialize(LayerObject object, Type type, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(object.getObject().getClass().getCanonicalName()));
        result.add("properties", context.serialize(object.getObject(), object.getObject().getClass()));
        return result;
    }
}
