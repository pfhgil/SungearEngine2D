package Core2D.Deserializers;

import Core2D.Camera2D.Camera2D;
import Core2D.Drawable.Drawable;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Log.Log;
import Core2D.Scene2D.Scene2D;
import Core2D.Systems.ScriptSystem;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.Tag;
import Core2D.Utils.WrappedObject;
import com.google.gson.*;
import org.joml.Vector4f;

import java.lang.reflect.Field;
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
        JsonArray cameras2D = jsonObject.getAsJsonArray("cameras2D");
        Camera2D sceneMainCamera2D = context.deserialize(jsonObject.get("sceneMainCamera2D"), Camera2D.class);
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

        for(Layer layer : layering.getLayers()) {
            for(WrappedObject wrappedObject : layer.getRenderingObjects()) {
                try {
                    Drawable drawable = (Drawable) wrappedObject.getObject();

                    Field layerField = drawable.getClass().getSuperclass().getDeclaredField("layer");
                    layerField.setAccessible(true);
                    layerField.set(drawable, layer);

                    Field layerNameField = drawable.getClass().getSuperclass().getDeclaredField("layerName");
                    layerNameField.setAccessible(true);
                    layerNameField.set(drawable, layer.getName());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
                }
            }
        }

        Scene2D scene2D = new Scene2D(name);
        scene2D.setScreenClearColor(screenClearColor);
        scene2D.setLayering(layering);
        scene2D.setScriptSystem(scriptSystem);
        scene2D.maxObjectID = maxObjectID;
        scene2D.inBuild = inBuild;
        scene2D.isMainScene2D = isMainScene2D;
        scene2D.setScenePath(scenePath);

        if(cameras2D != null) {
            for (JsonElement element : cameras2D) {
                Camera2D camera2D = context.deserialize(element, Camera2D.class);
                scene2D.getCameras2D().add(camera2D);
            }
        }

        if(sceneMainCamera2D != null) {
            Camera2D foundCamera2D = scene2D.findCamera2DByID(sceneMainCamera2D.getID());
            scene2D.setSceneMainCamera2D(foundCamera2D);
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