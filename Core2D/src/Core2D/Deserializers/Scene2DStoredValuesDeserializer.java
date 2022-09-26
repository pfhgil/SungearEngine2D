package Core2D.Deserializers;

import Core2D.Scene2D.Scene2DStoredValues;
import com.google.gson.*;

import java.lang.reflect.Type;

public class Scene2DStoredValuesDeserializer implements JsonDeserializer<Scene2DStoredValues> {

    @Override
    public Scene2DStoredValues deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Scene2DStoredValues scene2DStoredValues = new Scene2DStoredValues();

        scene2DStoredValues.path = jsonObject.get("path").getAsString();
        scene2DStoredValues.inBuild = jsonObject.get("inBuild").getAsBoolean();
        scene2DStoredValues.isMainScene2D = jsonObject.get("isMainScene2D").getAsBoolean();

        return scene2DStoredValues;
    }
}
