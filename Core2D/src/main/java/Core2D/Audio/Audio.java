package Core2D.Audio;

import org.lwjgl.openal.AL10;

import java.io.InputStream;

public abstract class Audio
{
    public String path = "";
    public String name = "";

    public AudioInfo audioInfo;

    private int source;

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
    }

    public void deltaUpdate(float deltaTime)
    {

    }

    public void update()
    {

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
}
