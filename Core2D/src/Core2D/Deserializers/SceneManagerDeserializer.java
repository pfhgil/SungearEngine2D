package Core2D.Deserializers;

import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.Scene2DStoredValues;
import Core2D.Scene2D.SceneManager;
import com.google.gson.*;

import java.lang.reflect.Type;

public class SceneManagerDeserializer implements JsonDeserializer<SceneManager> {
    @Override
    public SceneManager deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        SceneManager sceneManager = new SceneManager();

        JsonArray scene2DStoredValuesArr = jsonObject.getAsJsonArray("scene2DStoredValues");
        for(JsonElement element : scene2DStoredValuesArr) {
            Scene2DStoredValues scene2DStoredValues = context.deserialize(element, Scene2DStoredValues.class);
            sceneManager.getScene2DStoredValues().add(scene2DStoredValues);
        }

        sceneManager.mainScene2DPath = jsonObject.get("mainScene2DPath").getAsString();

        return sceneManager;
    }
}
