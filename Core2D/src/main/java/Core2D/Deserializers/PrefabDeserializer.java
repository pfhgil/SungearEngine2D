package Core2D.Deserializers;

import Core2D.Log.Log;
import Core2D.Drawable.Object2D;
import Core2D.Prefab.Prefab;
import Core2D.Utils.ExceptionsUtils;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PrefabDeserializer implements JsonDeserializer<Prefab>, JsonSerializer<Prefab>
{

    @Override
    public Prefab deserialize(JsonElement jsonElement, Type t, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = "";
        JsonElement properties;
        Prefab prefab = new Prefab();
        if(jsonObject.get("type") != null) {
            type = jsonObject.get("type").getAsString();
            properties = jsonObject.get("properties");
        } else {
            return prefab;
        }

        try {
            Object obj =  context.deserialize(properties, Class.forName(type));
            if(obj instanceof Object2D) {
                Object2D object2D = (Object2D) obj;
                object2D.getChildrenObjectsID().clear();
                JsonArray childrenObjectsJArray = jsonObject.getAsJsonArray("childrenObjects");
                if(childrenObjectsJArray != null) {
                    for(JsonElement element : childrenObjectsJArray) {
                        Prefab pref = context.deserialize(element, Prefab.class);
                        if(pref.getPrefabObject() instanceof Object2D) {
                            object2D.addChildObject((Object2D) pref.getPrefabObject());
                        }
                        addPrefabChildren(pref);
                    }
                }
            }
            return new Prefab(obj);
        } catch(ClassNotFoundException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            return null;
        }
    }

    @Override
    public JsonElement serialize(Prefab prefab, Type type, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        if(prefab.getPrefabObject() != null) {
            result.add("type", new JsonPrimitive(prefab.getPrefabObject().getClass().getCanonicalName()));
            result.add("properties", context.serialize(prefab.getPrefabObject(), prefab.getPrefabObject().getClass()));
            result.add("childrenObjects", context.serialize(prefab.getChildrenObjects()));
        }
        return result;
    }

    private void addPrefabChildren(Prefab prefab)
    {
        if(prefab.getPrefabObject() instanceof Object2D) {
            for (Prefab pref : prefab.getChildrenObjects()) {
                if (pref.getPrefabObject() instanceof Object2D) {
                    ((Object2D) prefab.getPrefabObject()).addChildObject((Object2D) pref.getPrefabObject());
                }
            }
        }
    }
}
