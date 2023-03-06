package Core2D.DataClasses;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AudioData extends Data
{
    //private int buffer = -1;

    //private Core2D.ECS.Component.Components.Audio.AudioFormat audioFormat = new Core2D.ECS.Component.Components.Audio.AudioFormat();

    private transient ByteBuffer data;

    private transient int format;

    private transient long frameLength;

    public transient long audioLength;

    private transient float sampleRate;

    private transient int sampleSizeInBits;

    private transient int channels;

    private transient int frameSize;

    private transient float frameRate;

    private transient boolean bigEndian;

    public void set(AudioData audioData)
    {
        this.data.clear();
        this.data = audioData.data;

        this.frameLength = audioData.frameLength;
        this.audioLength = audioData.audioLength;

        this.sampleRate = audioData.sampleRate;
        this.sampleSizeInBits = audioData.sampleSizeInBits;
        this.channels = audioData.channels;
        this.frameSize = audioData.frameSize;
        this.frameRate = audioData.frameRate;
        this.bigEndian = audioData.bigEndian;
    }

    @Override
    public AudioData load(String path)
    {
        this.path = path;

        File audioFile = new File(path);
        try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            return createAudioData(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    @Override
    public AudioData load(InputStream inputStream, String path)
    {
        this.path = path;

        try(AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream)); inputStream) {
            return createAudioData(stream);
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }


    private AudioData createAudioData(AudioInputStream stream)
    {
        //int buf = AL10.alGenBuffers();

        final int MONO = 1, STEREO = 2;

        AudioData audioData = new AudioData();

        try(stream) {
            AudioFormat format = stream.getFormat();
            if (format.isBigEndian()) try {
                throw new UnsupportedAudioFileException("Can't handle Big Endian formats yet");
            } catch (UnsupportedAudioFileException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }

            //load stream into byte buffer
            int[] openALFormat = { -1 };
            switch (format.getChannels()) {
                case MONO -> {
                    switch (format.getSampleSizeInBits()) {
                        case 8 -> openALFormat[0] = AL10.AL_FORMAT_MONO8;
                        case 16 -> openALFormat[0] = AL10.AL_FORMAT_MONO16;
                    }
                }
                case STEREO -> openALFormat[0] = switch (format.getSampleSizeInBits()) {
                    case 8 -> AL10.AL_FORMAT_STEREO8;
                    case 16 -> AL10.AL_FORMAT_STEREO16;
                    default -> openALFormat[0];
                };
            }

            byte[] b = new byte[0];
            try {
                b = IOUtils.toByteArray(stream);
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
            audioData.data = BufferUtils.createByteBuffer(b.length).put(b);
            audioData.data.flip();

            //OpenAL.alCall((params) -> AL10.alBufferData(buf, openALFormat[0], data, (int) format.getSampleRate()));

            //audioData.buffer = buf;
            audioData.format = openALFormat[0];
            audioData.sampleRate = format.getSampleRate();
            audioData.sampleSizeInBits = format.getSampleSizeInBits();
            audioData.channels = format.getChannels();
            audioData.frameSize = format.getFrameSize();
            audioData.frameRate = format.getFrameRate();
            audioData.bigEndian = format.isBigEndian();

            audioData.frameLength = stream.getFrameLength();
            //and return the rough notion of length for the audio stream!
            audioData.audioLength = (long) (1000f * stream.getFrameLength() / format.getFrameRate());
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
        return audioData;
    }

    public ByteBuffer getData() { return data; }

    public int getFormat() { return format; }

    public float getSampleRate() { return sampleRate; }

    public long getFrameLength() { return frameLength; }

    public long getAudioLength() { return audioLength; }

    public float getAudioLengthInSeconds() { return audioLength / 1000.0f; }
}
