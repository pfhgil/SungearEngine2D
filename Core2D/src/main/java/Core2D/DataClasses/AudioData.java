package Core2D.DataClasses;

import Core2D.Audio.OpenAL;
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

    private transient int bufferHandler = -1;

    private transient ByteBuffer bufferData;

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
        this.bufferData.clear();
        this.bufferData = audioData.bufferData;

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
    public AudioData load(String absolutePath)
    {
        this.canonicalPath = absolutePath;

        createRelativePath();

        File audioFile = new File(absolutePath);
        try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            create(audioInputStream);
            return this;
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    @Override
    public AudioData load(InputStream inputStream, String absolutePath)
    {
        this.canonicalPath = absolutePath;

        createRelativePath();

        try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream)); inputStream) {
            create(audioInputStream);
            return this;
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }


    private void create(AudioInputStream stream)
    {
        bufferHandler = AL10.alGenBuffers();

        final int MONO = 1, STEREO = 2;

        try(stream) {
            AudioFormat audioFormat = stream.getFormat();
            if (audioFormat.isBigEndian()) try {
                throw new UnsupportedAudioFileException("Can't handle Big Endian formats yet");
            } catch (UnsupportedAudioFileException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }

            //load stream into byte buffer
            int[] openALFormat = { -1 };
            switch (audioFormat.getChannels()) {
                case MONO -> {
                    switch (audioFormat.getSampleSizeInBits()) {
                        case 8 -> openALFormat[0] = AL10.AL_FORMAT_MONO8;
                        case 16 -> openALFormat[0] = AL10.AL_FORMAT_MONO16;
                    }
                }
                case STEREO -> openALFormat[0] = switch (audioFormat.getSampleSizeInBits()) {
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
            bufferData = BufferUtils.createByteBuffer(b.length).put(b);
            bufferData.flip();

            OpenAL.alCall((params) -> AL10.alBufferData(bufferHandler, openALFormat[0], bufferData, (int) audioFormat.getSampleRate()));

            //audioData.buffer = buf;
            this.format = openALFormat[0];
            this.sampleRate = audioFormat.getSampleRate();
            this.sampleSizeInBits = audioFormat.getSampleSizeInBits();
            this.channels = audioFormat.getChannels();
            this.frameSize = audioFormat.getFrameSize();
            this.frameRate = audioFormat.getFrameRate();
            this.bigEndian = audioFormat.isBigEndian();

            this.frameLength = stream.getFrameLength();
            //and return the rough notion of length for the audio stream!
            this.audioLength = (long) (1000f * stream.getFrameLength() / audioFormat.getFrameRate());
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
    }

    public int getBufferHandler() { return bufferHandler; }

    public ByteBuffer getBufferData() { return bufferData; }

    public int getFormat() { return format; }

    public float getSampleRate() { return sampleRate; }

    public long getFrameLength() { return frameLength; }

    public long getAudioLength() { return audioLength; }

    public float getAudioLengthInSeconds() { return audioLength / 1000.0f; }
}
