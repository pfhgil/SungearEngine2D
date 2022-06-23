package Core2D.Deserializers;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Layering.Layer;
import Core2D.Layering.LayerObject;
import com.google.gson.*;

import java.lang.reflect.Type;

public class LayerDeserializer implements JsonDeserializer<Layer>
{
    @Override
    public Layer deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        int id = context.deserialize(jsonObject.get("id"), int.class);
        JsonArray renderingObjects = jsonObject.getAsJsonArray("renderingObjects");

        Layer layer = new Layer(id, name);

        for(JsonElement element : renderingObjects) {
            LayerObject object = context.deserialize(element, LayerObject.class);
            CommonDrawableObjectsParameters objParams = ((CommonDrawableObjectsParameters) object.getObject());
            objParams.setLayer(layer);
        }

        return layer;
    }
}
