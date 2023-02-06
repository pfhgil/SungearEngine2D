package Core2D.Audio;

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

public class AudioInfo
{
    private int buffer = -1;

    private Core2D.Audio.AudioFormat audioFormat = new Core2D.Audio.AudioFormat();

    private long frameLength;

    public long audioLength;

    public void set(AudioInfo audioInfo)
    {
        this.buffer = audioInfo.getBuffer();

        this.frameLength = audioInfo.getFrameLength();
        this.audioLength = audioInfo.getAudioLength();
        this.audioFormat.set(audioInfo.getAudioFormat());
    }

    public static AudioInfo loadAudio(String path)
    {
        File audioFile = new File(path);
        try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            return createAudioInfo(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    public static AudioInfo loadAudio(InputStream inputStream)
    {
        try(AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream)); inputStream) {
            return createAudioInfo(stream);
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return null;
    }

    private static AudioInfo createAudioInfo(AudioInputStream stream)
    {
        int buf = AL10.alGenBuffers();

        final int MONO = 1, STEREO = 2;

        AudioInfo audioInfo = new AudioInfo();

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
                case MONO:
                    switch (format.getSampleSizeInBits()) {
                        case 8 -> openALFormat[0] = AL10.AL_FORMAT_MONO8;
                        case 16 -> openALFormat[0] = AL10.AL_FORMAT_MONO16;
                    }
                    break;
                case STEREO:
                    openALFormat[0] = switch (format.getSampleSizeInBits()) {
                        case 8 -> AL10.AL_FORMAT_STEREO8;
                        case 16 -> AL10.AL_FORMAT_STEREO16;
                        default -> openALFormat[0];
                    };
                    break;
            }

            //load data into a byte buffer
            //I've elected to use IOUtils from Apache Commons here, but the core
            //notion is to load the entire stream into the byte array--you can
            //do this however you would like.
            byte[] b = new byte[0];
            try {
                b = IOUtils.toByteArray(stream);
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
            ByteBuffer data = BufferUtils.createByteBuffer(b.length).put(b);
            data.flip();

            //load audio data into appropriate system space....
            OpenAL.alCall((params) -> AL10.alBufferData(buf, openALFormat[0], data, (int) format.getSampleRate()));

            audioInfo.buffer = buf;
            audioInfo.audioFormat.set(format);
            audioInfo.frameLength = stream.getFrameLength();
            //and return the rough notion of length for the audio stream!
            audioInfo.audioLength = (long) (1000f * stream.getFrameLength() / format.getFrameRate());
        } catch (Exception e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }
        return audioInfo;
    }

    /*
    public float getDurationInSeconds()
    {
        int sizeInBytes = OpenAL.alGet((params) -> AL10.alGetBufferi(buffer, AL10.AL_SIZE), Integer.class);
        int channels = OpenAL.alGet((params) -> AL10.alGetBufferi(buffer, AL10.AL_CHANNELS), Integer.class);
        int bits = OpenAL.alGet((params) -> AL10.alGetBufferi(buffer, AL10.AL_BITS), Integer.class);

        float lengthInSamples = sizeInBytes * 8.0f / (channels * bits);

        int frequency = OpenAL.alGet((params) -> AL10.alGetBufferi(buffer, AL10.AL_FREQUENCY), Integer.class);

        return lengthInSamples / frequency;
    }

     */

    public int getBuffer() { return buffer; }

    public Core2D.Audio.AudioFormat getAudioFormat() { return audioFormat; }

    public long getFrameLength() { return frameLength; }

    public long getAudioLength() { return audioLength; }

    public float getAudioLengthInSeconds() { return audioLength / 1000.0f; }
}
