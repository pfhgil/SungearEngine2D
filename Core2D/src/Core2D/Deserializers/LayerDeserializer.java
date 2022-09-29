package Core2D.Deserializers;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
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
        int ID = context.deserialize(jsonObject.get("ID"), int.class);
        JsonArray renderingObjects = jsonObject.getAsJsonArray("renderingObjects");

        Layer layer = new Layer(ID, name);

        for(JsonElement element : renderingObjects) {
            WrappedObject object = context.deserialize(element, WrappedObject.class);
            CommonDrawableObjectsParameters objParams = ((CommonDrawableObjectsParameters) object.getObject());

            objParams.setLayer(layer);

            object.setObject(null);
        }
        return layer;
    }
}
