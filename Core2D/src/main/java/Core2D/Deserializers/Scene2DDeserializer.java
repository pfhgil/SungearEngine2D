package Core2D.Deserializers;

import Core2D.Component.Components.Camera2DComponent;
import Core2D.GameObject.GameObject;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Scene2D.Scene2D;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;
import org.newdawn.slick.Game;

import java.lang.reflect.Type;

public class Scene2DDeserializer implements JsonDeserializer<Scene2D>
{
    @Override
    public Scene2D deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String scenePath = jsonObject.get("scenePath").getAsString();
        Vector4f screenClearColor = context.deserialize(jsonObject.get("screenClearColor"), Vector4f.class);
        Layering layering = context.deserialize(jsonObject.get("layering"), Layering.class);
        JsonArray tags = jsonObject.getAsJsonArray("tags");
        ScriptSystem scriptSystem = context.deserialize(jsonObject.get("scriptSystem"), ScriptSystem.class);
        int maxObjectID = jsonObject.get("maxObjectID").getAsInt();
        JsonElement inBuildElem = jsonObject.get("inBuild");
        boolean inBuild = false;
        if(inBuildElem != null) {
            inBuild = jsonObject.get("inBuild").getAsBoolean();
        }

        JsonElement isMainScene2DElem = jsonObject.get("isMainScene2D");
        boolean isMainScene2D = false;
        if(isMainScene2DElem != null) {
            isMainScene2D = jsonObject.get("isMainScene2D").getAsBoolean();
        }

        Scene2D scene2D = new Scene2D(name);
        scene2D.setScreenClearColor(screenClearColor);
        scene2D.setLayering(layering);
        scene2D.setScriptSystem(scriptSystem);
        scene2D.maxObjectID = maxObjectID;
        scene2D.inBuild = inBuild;
        scene2D.isMainScene2D = isMainScene2D;
        scene2D.setScenePath(scenePath);

        for(Layer layer : layering.getLayers()) {
            for(GameObject gameObject : layer.getGameObjects()) {
                Camera2DComponent camera2DComponent = gameObject.getComponent(Camera2DComponent.class);

                if(camera2DComponent != null) {

                }
            }
        }

        if(tags != null) {
            for (JsonElement element : tags) {
                Tag tag = context.deserialize(element, Tag.class);
                if (!tag.getName().equals("default")) {
                    scene2D.addTag(tag);
                }
            }
        }

        return scene2D;
    }
}