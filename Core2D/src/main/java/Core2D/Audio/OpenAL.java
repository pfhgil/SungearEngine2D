package Core2D.Audio;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;
import java.util.function.Function;

public class OpenAL
{
    public static void init()
    {
        long device = ALC10.alcOpenDevice((ByteBuffer) null);
        ALCCapabilities deviceCapabilities = ALC.createCapabilities(device);
        IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);

        contextAttribList.put(ALC10.ALC_REFRESH);
        contextAttribList.put(60);

        contextAttribList.put(ALC10.ALC_SYNC);
        contextAttribList.put(ALC10.ALC_FALSE);

        contextAttribList.put(EXTEfx.ALC_MAX_AUXILIARY_SENDS);
        contextAttribList.put(2);

        contextAttribList.put(0);
        contextAttribList.flip();

        long newContext = ALC10.alcCreateContext(device, contextAttribList);

        if(!ALC10.alcMakeContextCurrent(newContext)) {
            throw new RuntimeException("Failed to make context current");
        }

        AL.createCapabilities(deviceCapabilities);

        // настройки listener
        OpenAL.alCall((params) -> AL10.alListener3f(AL10.AL_VELOCITY, 0f, 0f, 0f));
        OpenAL.alCall((params) -> AL10.alListener3f(AL10.AL_POSITION, 0f, 0f, -1f));
        OpenAL.alCall((params) -> AL10.alDistanceModel(AL11.AL_INVERSE_DISTANCE_CLAMPED));
        AL11.alDistanceModel(AL11.AL_INVERSE_DISTANCE_CLAMPED);
    }

    public static void alCall(Consumer<Object[]> func, int idToCheck)
    {
        if (idToCheck != -1) {
            if (!AL10.alIsSource(idToCheck) || !AL10.alIsBuffer(idToCheck)) {
                return;
            }
        }
        func.accept(null);
        checkForALErrors();
    }

    public static void alCall(Consumer<Object[]> func)
    {
        alCall(func, -1);
    }

    public static <T> T alCall(Function<Object[], ?> func, Class<T> toClass, int idToCheck)
    {
        if (idToCheck != -1) {
            if (!AL10.alIsSource(idToCheck) || !AL10.alIsBuffer(idToCheck)) {
                return toClass.cast(-1);
            }
        }

        Object obj = func.apply(null);
        checkForALErrors();
        return toClass.cast(obj);
    }

    public static <T> T alCall(Function<Object[], ?> func, Class<T> toClass)
    {
        return alCall(func, toClass, -1);
    }

    public static void checkForALErrors()
    {
        int errCode = AL10.alGetError();
        if(errCode != AL10.AL_NO_ERROR) {
            String stringErr = switch(errCode) {
                case AL10.AL_INVALID_NAME -> "AL_INVALID_NAME: a bad ID was passed.";
                case AL10.AL_INVALID_ENUM -> "AL_INVALID_ENUM: an invalid enum value was passed.";
                case AL10.AL_INVALID_VALUE -> "AL_INVALID_VALUE: an invalid value was passed.";
                case AL10.AL_INVALID_OPERATION -> "AL_INVALID_OPERATION: the requested operation is not valid.";
                case AL10.AL_OUT_OF_MEMORY -> "AL_OUT_OF_MEMORY: the requested operation resulted in OpenAL running out of memory.";
                default -> "Unknown error.";
            };

            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException("OpenAL error: " + stringErr + " (code: " + errCode + "). ")), Log.MessageType.ERROR);
        }
    }
}
