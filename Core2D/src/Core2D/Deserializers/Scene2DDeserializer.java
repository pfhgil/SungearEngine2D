package Core2D.Deserializers;

import Core2D.Layering.Layering;
import Core2D.Scene2D.Scene2D;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.Tag;
import com.google.gson.*;
import org.joml.Vector4f;

import java.lang.reflect.Type;

public class Scene2DDeserializer implements JsonDeserializer<Scene2D>
{
    @Override
    public Scene2D deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        Vector4f screenClearColor = context.deserialize(jsonObject.get("screenClearColor"), Vector4f.class);
        Layering layering = context.deserialize(jsonObject.get("layering"), Layering.class);
        JsonArray tags = jsonObject.getAsJsonArray("tags");
        ScriptSystem scriptSystem = context.deserialize(jsonObject.get("scriptSystem"), ScriptSystem.class);

        Scene2D scene2D = new Scene2D(name);
        scene2D.setScreenClearColor(screenClearColor);
        screenClearColor = null;
        scene2D.setLayering(layering);
        layering = null;
        scene2D.setScriptSystem(scriptSystem);
        scriptSystem = null;

        //this.setName(name);
        //this.setScreenClearColor(screenClearColor);
        //this.filePath = filePath;
        //this.layering = layering;

        for(JsonElement element : tags) {
            Tag tag = context.deserialize(element, Tag.class);
            if(!tag.getName().equals("default")) {
                scene2D.addTag(tag);
            }
        }

        return scene2D;
    }
}