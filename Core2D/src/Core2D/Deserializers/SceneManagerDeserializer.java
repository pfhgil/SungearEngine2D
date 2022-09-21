package Core2D.Deserializers;

import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import com.google.gson.*;

import java.lang.reflect.Type;

public class SceneManagerDeserializer implements JsonDeserializer<SceneManager> {
    @Override
    public SceneManager deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        SceneManager sceneManager = new SceneManager();

        JsonArray scenes = jsonObject.getAsJsonArray("scenes");
        for(JsonElement element : scenes) {
            Scene2D scene2D = context.deserialize(element, Scene2D.class);
            sceneManager.getScenes().add(scene2D);
            if(scene2D.isMainScene2D()) {
                sceneManager.mainScene2D = scene2D;
            }
        }
        return sceneManager;
    }
}
