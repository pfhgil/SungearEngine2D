package Core2D.Audio;

import Core2D.Transform.Transform;
import Core2D.Utils.MatrixUtils;
import org.joml.Vector2f;
import org.lwjgl.openal.AL10;

import java.io.InputStream;

public class Audio
{
    public enum AudioType
    {
        BACKGROUND,
        WORLDSPACE;

        @Override
        public String toString() {
            return switch(this) {
                case BACKGROUND -> "Background";
                case WORLDSPACE -> "Worldspace";
            };
        }
    }

    public String path = "";
    public String name = "";

    public AudioInfo audioInfo;

    protected int source;

    private Transform transform = new Transform();

    public AudioType audioType = AudioType.BACKGROUND;

    public void loadAndSetup(String path)
    {
        audioInfo = AudioInfo.loadAudio(path);
        setup();
    }

    public void loadAndSetup(InputStream inputStream)
    {
        audioInfo = AudioInfo.loadAudio(inputStream);
        setup();
    }

    public void setup()
    {
        source = AL10.alGenSources();
        AL10.alSourcei(source, AL10.AL_BUFFER, audioInfo.getBuffer());
        AL10.alSource3f(source, AL10.AL_POSITION, 0f, 0f, 0f);
        AL10.alSource3f(source, AL10.AL_VELOCITY, 0f, 0f, 0f);

        AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
        AL10.alSourcef(source, AL10.AL_GAIN, 1.0f);
        AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);

        AL10.alSourcei(source, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);

        AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, 100.0f);
        AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, 25f);
        AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, 0.1f);
    }

    public void deltaUpdate(float deltaTime)
    {
        transform.update(deltaTime);
    }

    public void update()
    {
        if(audioType == AudioType.WORLDSPACE) {
            Vector2f position = MatrixUtils.getPosition(transform.getResultModelMatrix());

            //System.out.println("source pos: " + position.x + ", " + position.y);
            AL10.alSource3f(source, AL10.AL_POSITION, position.x, position.y, 0f);

            float distance = position.distance(new Vector2f(0.0f, 0.0f));
            float referenceDistance = AL10.alGetSourcef(source, AL10.AL_REFERENCE_DISTANCE);
            float maxDistance = AL10.alGetSourcef(source, AL10.AL_MAX_DISTANCE);
            float rolloffFactor = AL10.alGetSourcef(source, AL10.AL_ROLLOFF_FACTOR);

            //System.out.println(String.format("distance: %f, referenceDistance: %f, maxDistance: %f, rolloffFactor: %f", distance, referenceDistance, maxDistance, rolloffFactor));

            distance = Math.max(distance, referenceDistance);
            //distance = Math.min(distance, maxDistance);
            float gain = referenceDistance / (referenceDistance + rolloffFactor * (distance - referenceDistance));

            AL10.alSourcef(source, AL10.AL_GAIN, gain);
        } else if(audioType == AudioType.BACKGROUND) {
            AL10.alSource3f(source, AL10.AL_POSITION, 0f, 0f, 0f);
        }
    }

    public void destroy()
    {
        AL10.alDeleteBuffers(audioInfo.getBuffer());
        AL10.alDeleteSources(source);
    }

    public void updateBuffer()
    {
        AL10.alSourcei(source, AL10.AL_BUFFER, audioInfo.getBuffer());
    }

    public void start()
    {
        AL10.alSourcePlay(source);
    }

    public void stop()
    {
        AL10.alSourceStop(source);
    }

    public void pause()
    {
        AL10.alSourcePause(source);
    }

    public int getSource() { return source; }

    public Transform getTransform() { return transform; }
}
