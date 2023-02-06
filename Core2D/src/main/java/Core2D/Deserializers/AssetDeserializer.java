package Core2D.Deserializers;

import Core2D.AssetManager.Asset;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import com.google.gson.*;

import java.lang.reflect.Type;

public class AssetDeserializer implements JsonDeserializer<Asset>
{
    @Override
    public Asset deserialize(JsonElement jsonElement, Type t, JsonDeserializationContext context) throws JsonParseException
    {
        Gson gson = new Gson();
        Asset asset = gson.fromJson(jsonElement, Asset.class);

        try {
            if(asset != null) {
                asset.setAssetObject(context.deserialize(gson.toJsonTree(asset.getAssetObject()), Class.forName(asset.getAssetObjectClassName())));
            }
            return asset;
        } catch(ClassNotFoundException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(new JsonParseException("Unknown element type: " + asset.getAssetObjectClassName(), e)), Log.MessageType.ERROR);
            return null;
        }
    }
}
