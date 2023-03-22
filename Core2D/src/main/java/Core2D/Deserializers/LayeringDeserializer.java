package Core2D.Deserializers;

import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Entity;
import Core2D.Layering.Layer;
import Core2D.Layering.Layering;
import Core2D.Layering.PostprocessingLayer;
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
            layering.getLayers().add(layer);
        }

        for(Layer layer : layering.getLayers()) {
            for(Entity entity : layer.getEntities()) {
                for (CameraComponent cameraComponent : entity.getAllComponents(CameraComponent.class)) {
                    for (int i = 0; i < cameraComponent.postprocessingLayers.size(); i++) {
                        PostprocessingLayer ppLayer = cameraComponent.postprocessingLayers.get(i);

                        ppLayer.setEntitiesLayerToRender(layering.getLayer(ppLayer.getEntitiesLayerToRenderName()));
                        //ppLayer.entitiesLayerToRender = layer;
                    }
                }
            }
        }
        return layering;
    }
}
