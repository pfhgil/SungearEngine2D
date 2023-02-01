package Core2D.Deserializers;

import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
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
        JsonArray gameObjects = jsonObject.getAsJsonArray("entities");

        Layer layer = new Layer(ID, name);

        for(JsonElement element : gameObjects) {
            Entity entity = context.deserialize(element, Entity.class);
            entity.setLayer(layer);

            for(Camera2DComponent camera2DComponent : entity.getAllComponents(Camera2DComponent.class)) {
                for(int i = 0; i < camera2DComponent.getPostprocessingLayersNum(); i++) {
                    PostprocessingLayer ppLayer = camera2DComponent.getPostprocessingLayer(i);

                    if(ppLayer.getEntitiesLayerToRenderName().equals(layer.getName())) {
                        ppLayer.setEntitiesLayerToRender(layer);
                    }
                    //ppLayer.entitiesLayerToRender = layer;
                }
            }
        }
        return layer;
    }
}
