package Core2D.Deserializers;

import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import com.google.gson.*;

import java.lang.reflect.Type;

public class LayeringDeserializer implements JsonDeserializer<Layering>
{
    @Override
    public Layering deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonArray layers = jsonObject.getAsJsonArray("layers");

        Layering layering = new Layering();

        for(JsonElement element : layers) {
            Layer layer = context.deserialize(element, Layer.class);
            layering.addLayer(layer);

            layer = null;
        }

        layers = null;
        jsonObject = null;

        return layering;
    }
}
