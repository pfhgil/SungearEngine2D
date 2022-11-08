package Core2D.Deserializers;

import com.google.gson.*;

import javax.sound.sampled.AudioFormat;
import java.lang.reflect.Type;

public class AudioFormatDeserializer implements JsonSerializer<AudioFormat>
{
    @Override
    public JsonElement serialize(AudioFormat audioFormat, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        if(audioFormat != null) {
            result.add("sampleRate", new JsonPrimitive(audioFormat.getSampleRate()));
            result.add("sampleSizeInBits", new JsonPrimitive(audioFormat.getSampleSizeInBits()));
            result.add("channels", new JsonPrimitive(audioFormat.getChannels()));
            result.add("frameSize", new JsonPrimitive(audioFormat.getFrameSize()));
            result.add("frameRate", new JsonPrimitive(audioFormat.getFrameRate()));
            result.add("bigEndian", new JsonPrimitive(audioFormat.isBigEndian()));
            result.add("properties", context.serialize(audioFormat.properties()));
        }

        return result;
    }
}
