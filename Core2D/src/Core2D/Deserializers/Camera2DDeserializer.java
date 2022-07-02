package Core2D.Deserializers;

import Core2D.Camera2D.Camera2D;
import Core2D.Object2D.Transform;
import com.google.gson.*;

import java.lang.reflect.Type;

public class Camera2DDeserializer implements JsonDeserializer<Camera2D>
{
    @Override
    public Camera2D deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
        String name = jsonObject.get("name").getAsString();
        int ID = jsonObject.get("ID").getAsInt();

        Camera2D camera2D = new Camera2D();
        camera2D.getTransform().set(transform);
        camera2D.setName(name);
        camera2D.setID(ID);

        return camera2D;
    }
}
