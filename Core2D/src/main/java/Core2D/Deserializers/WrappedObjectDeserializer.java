package Core2D.Deserializers;

import Core2D.Utils.WrappedObject;
import com.google.gson.*;

import java.lang.reflect.Type;

public class WrappedObjectDeserializer implements JsonDeserializer<WrappedObject>, JsonSerializer<WrappedObject> {
    @Override
    public WrappedObject deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = "";
        JsonElement properties;
        if(jsonObject.get("type") != null) {
            type = jsonObject.get("type").getAsString();
            properties = jsonObject.get("properties");
        } else {
            return new WrappedObject(null);
        }

        try {
            return new WrappedObject(context.deserialize(properties, Class.forName(type)));
        } catch(ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }

    @Override
    public JsonElement serialize(WrappedObject object, Type type, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        if(object.getObject() != null) {
            result.add("type", new JsonPrimitive(object.getObject().getClass().getCanonicalName()));
            result.add("properties", context.serialize(object.getObject(), object.getObject().getClass()));
        }
        return result;
    }
}
