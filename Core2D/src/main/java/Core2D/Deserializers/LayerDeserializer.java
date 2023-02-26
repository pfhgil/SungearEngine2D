package Core2D.Deserializers;

import Core2D.Drawable.Drawable;
import Core2D.Layering.Layer;
import Core2D.Utils.WrappedObject;
import com.google.gson.*;

import java.lang.reflect.Type;

public class LayerDeserializer implements JsonDeserializer<Layer>
{
    @Override
    public Layer deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        int ID = jsonObject.get("ID").getAsInt();
<<<<<<< Updated upstream
        JsonArray renderingObjects = jsonObject.getAsJsonArray("renderingObjects");

        Layer layer = new Layer(ID, name);

        for(JsonElement element : renderingObjects) {
            WrappedObject object = context.deserialize(element, WrappedObject.class);
            Drawable objParams = ((Drawable) object.getObject());

            objParams.setLayer(layer);

            object.setObject(null);
=======
        JsonArray entities = jsonObject.getAsJsonArray("entities");

        Layer layer = new Layer(ID, name);

        for(JsonElement element : entities) {
            Entity entity = context.deserialize(element, Entity.class);
            entity.setLayer(layer);
>>>>>>> Stashed changes
        }
        return layer;
    }
}
