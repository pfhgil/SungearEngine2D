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
        /*
        JsonArray scenes2DPaths = jsonObject.getAsJsonArray("scenes2DPaths");
        for(JsonElement element : scenes2DPaths) {
            String path = element.getAsString();
            sceneManager.getScenes2DPaths().add(path);
        }

        JsonArray scenes2DInBuild = jsonObject.getAsJsonArray("scenes2DInBuild");
        for(JsonElement element : scenes2DInBuild) {
            boolean inBuild = element.getAsBoolean();
            sceneManager.getScenes2DInBuild().add(inBuild);
        }

         */
        JsonArray scene2DStoredValuesArr = jsonObject.getAsJsonArray("scene2DStoredValues");
        for(JsonElement element : scene2DStoredValuesArr) {
            Scene2DStoredValues scene2DStoredValues = context.deserialize(element, Scene2DStoredValues.class);
            sceneManager.getScene2DStoredValues().add(scene2DStoredValues);
        }

        sceneManager.mainScene2DPath = jsonObject.get("mainScene2DPath").getAsString();

        /*
        JsonArray scenes = jsonObject.getAsJsonArray("scenes");
        for(JsonElement element : scenes) {
            Scene2D scene2D = context.deserialize(element, Scene2D.class);
            sceneManager.getScenes().add(scene2D);
            if(scene2D.isMainScene2D()) {
                sceneManager.mainScene2D = scene2D;
            }
        }

         */
        return sceneManager;
    }
}
