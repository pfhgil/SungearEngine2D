package Core2D.Deserializers;

import Core2D.Scene2D.SceneObjectType;
import Core2D.Scripting.ScriptSceneObject;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ScriptSceneObjectDeserializer implements JsonDeserializer<ScriptSceneObject>
{
    @Override
    public ScriptSceneObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SceneObjectType objectType = context.deserialize(jsonObject.get("objectType"), SceneObjectType.class);
        int ID = jsonObject.get("ID").getAsInt();
        String name = jsonObject.get("name").getAsString();

        return new ScriptSceneObject(ID, name, objectType);
    }
}
