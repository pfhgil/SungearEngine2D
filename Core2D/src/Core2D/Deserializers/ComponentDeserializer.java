package Core2D.Deserializers;

import Core2D.Component.Component;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentDeserializer implements JsonDeserializer<Component>, JsonSerializer<Component>
{
    @Override
    public Component deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement properties = jsonObject.get("properties");

        try {
            return context.deserialize(properties, Class.forName(type));
        } catch(ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }

    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(component.getClass().getCanonicalName()));
        result.add("properties", context.serialize(component, component.getClass()));
        return result;
    }
}
