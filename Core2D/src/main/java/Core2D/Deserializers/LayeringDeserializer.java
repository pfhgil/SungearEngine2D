package Core2D.Deserializers;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
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
                for (Camera2DComponent camera2DComponent : entity.getAllComponents(Camera2DComponent.class)) {
                    for (int i = 0; i < camera2DComponent.postprocessingLayers.size(); i++) {
                        PostprocessingLayer ppLayer = camera2DComponent.postprocessingLayers.get(i);

                        ppLayer.setEntitiesLayerToRender(layering.getLayer(ppLayer.getEntitiesLayerToRenderName()));
                        //ppLayer.entitiesLayerToRender = layer;
                    }
                }
            }
        }
        return layering;
    }
}
