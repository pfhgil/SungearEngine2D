package Core2D.Audio;

import Core2D.Transform.Transform;
import Core2D.Utils.MatrixUtils;
import org.joml.Vector2f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

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

    public int source;

    private Transform transform = new Transform();

    public AudioType audioType = AudioType.BACKGROUND;

    private float maxDistance = 750.0f;
    private float referenceDistance = 25.0f;
    private float rolloffFactor = 0.1f;
    public float volumePercent = 100.0f;

    private boolean playing = false;
    private boolean paused = false;

    private int state = AL10.AL_STOPPED;

    private float secOffset = 0.0f;

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
        source = OpenAL.alGet((params) -> AL10.alGenSources(), Integer.class);
        OpenAL.alCall((params) -> AL10.alSourcei(source, AL10.AL_BUFFER, audioInfo.getBuffer()));
        OpenAL.alCall((params) -> AL10.alSource3f(source, AL10.AL_POSITION, 0f, 0f, 0f));
        OpenAL.alCall((params) -> AL10.alSource3f(source, AL10.AL_VELOCITY, 0f, 0f, 0f));

        OpenAL.alCall((params) -> AL10.alSourcef(source, AL10.AL_PITCH, 1f));
        OpenAL.alCall((params) -> AL10.alSourcef(source, AL10.AL_GAIN, 1f));
        OpenAL.alCall((params) -> AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE));

        OpenAL.alCall((params) -> AL10.alSourcei(source, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE));

        setMaxDistance(maxDistance);
        setReferenceDistance(referenceDistance);
        setRolloffFactor(rolloffFactor);
    }

    public void deltaUpdate(float deltaTime)
    {
        transform.update(deltaTime);
    }

    public void update()
    {
        if(audioType == AudioType.WORLDSPACE) {
            Vector2f position = MatrixUtils.getPosition(transform.getResultModelMatrix());

            OpenAL.alCall((params) -> AL10.alSource3f(source, AL10.AL_POSITION, position.x, position.y, 0f));

            float distance = position.distance(new Vector2f(AudioListener.getPosition().x, AudioListener.getPosition().y));

            float[] gain = { Math.max(0.0f, 1.0f - distance / maxDistance) * (volumePercent / 100.0f) };

            if(Float.isNaN(gain[0])) {
                gain[0] = 0.0f;
            }

            OpenAL.alCall((params) -> AL10.alSourcef(source, AL10.AL_GAIN, gain[0]));
        } else if(audioType == AudioType.BACKGROUND) {
            OpenAL.alCall((params) -> AL10.alSource3f(source, AL10.AL_POSITION, 0f, 0f, 0f));
            OpenAL.alCall((params) -> AL10.alSourcef(source, AL10.AL_GAIN, volumePercent / 100.0f));
        }

        boolean lastPlaying = playing;
        state = OpenAL.alGet((params) -> AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE), Integer.class);
        if(state == AL10.AL_PLAYING) {
            playing = true;
            paused = false;
        } else if(state == AL10.AL_STOPPED) {
            playing = false;
            paused = false;
        } else if(state == AL10.AL_PAUSED) {
            playing = true;
            paused = true;
        }

        // аудио перестало играть
        if(lastPlaying && !playing) {
            secOffset = 0.0f;
        }
    }

    public void destroy()
    {
        OpenAL.alCall((params) -> AL10.alDeleteBuffers(audioInfo.getBuffer()));
        OpenAL.alCall((params) -> AL10.alDeleteSources(source));
    }

    public void set(Audio audio)
    {
        this.path = audio.path;
        this.name = audio.name;

        this.audioInfo.set(audio.audioInfo);

        this.transform.set(audio.getTransform());

        this.audioType = audio.audioType;

        setMaxDistance(audio.getMaxDistance());
        setReferenceDistance(audio.getReferenceDistance());
        setRolloffFactor(audio.getRolloffFactor());
        volumePercent = audio.volumePercent;

        setup();
    }

    public void updateBuffer()
    {
        OpenAL.alCall((params) -> AL10.alSourcei(source, AL10.AL_BUFFER, audioInfo.getBuffer()));
    }

    public void play()
    {
        OpenAL.alCall((params) -> AL10.alSourcePlay(source));
    }

    public void stop()
    {
        OpenAL.alCall((params) -> AL10.alSourceStop(source));
    }

    public void pause()
    {
        OpenAL.alCall((params) -> AL10.alSourcePause(source));
    }

    public void setOffsetInSeconds(float offset)
    {
        secOffset = offset;
        OpenAL.alCall((params) -> AL11.alSourcef(source, AL11.AL_SEC_OFFSET, offset));
    }

    public float getCurrentSecond()
    {
        if(playing || paused) {
            return OpenAL.alGet((params) -> AL11.alGetSourcef(source, AL11.AL_SEC_OFFSET), Float.class);
        } else {
            return secOffset;
        }
    }

    public Transform getTransform() { return transform; }

    public float getMaxDistance() { return maxDistance; }
    public void setMaxDistance(float maxDistance)
    {
        this.maxDistance = maxDistance;
        OpenAL.alCall((params) -> AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, maxDistance));
    }

    public float getReferenceDistance() { return referenceDistance; }
    public void setReferenceDistance(float referenceDistance)
    {
        this.referenceDistance = referenceDistance;
        OpenAL.alCall((params) -> AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, referenceDistance));
    }

    public float getRolloffFactor() { return rolloffFactor; }
    public void setRolloffFactor(float rolloffFactor)
    {
        this.rolloffFactor = rolloffFactor;
        OpenAL.alCall((params) -> AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, rolloffFactor));
    }

    public boolean isPlaying() { return playing; }
    public boolean isPaused() { return paused; }

    public int getState() { return state; }
}
