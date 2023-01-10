package Core2D.Deserializers;

import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CommonDeserializer<T> implements JsonDeserializer<T>, JsonSerializer<T>
{

    @Override
    public T deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement properties = jsonObject.get("properties");

        try {
            return context.deserialize(properties, Class.forName(type));
        } catch(ClassNotFoundException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(new JsonParseException("Unknown element type: " + type, e)), Log.MessageType.ERROR);
            return null;
        }
    }

    @Override
    public JsonElement serialize(T t, Type type, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(t.getClass().getCanonicalName()));
        result.add("properties", context.serialize(t, t.getClass()));
        return result;
    }
}
