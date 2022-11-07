package Core2D.Audio;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AudioInfo
{
    private int buffer = -1;

    public String audioPath = "";

    public String audioName = "";

    private AudioFormat audioFormat;

    private long frameLength;

    public long audioLength;

    public static AudioInfo loadAudio(String path)
    {
        AudioInputStream stream = null;
        File audioFile = new File(path);
        try {
            stream = AudioSystem.getAudioInputStream(audioFile);
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        AudioInfo audioInfo = createAudioInfo(stream);

        if(stream != null) {
            audioInfo.audioPath = audioFile.getPath();
            audioInfo.audioName = FilenameUtils.getBaseName(audioFile.getName());
        }

        return audioInfo;
    }

    public static AudioInfo loadAudio(InputStream inputStream)
    {
        AudioInputStream stream = null;
        try {
            stream = AudioSystem.getAudioInputStream(inputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return createAudioInfo(stream);
    }

    private static AudioInfo createAudioInfo(AudioInputStream stream)
    {
        int buf = AL10.alGenBuffers();

        final int MONO = 1, STEREO = 2;

        AudioInfo audioInfo = new AudioInfo();

        if(stream != null) {
            AudioFormat format = stream.getFormat();
            if (format.isBigEndian()) try {
                throw new UnsupportedAudioFileException("Can't handle Big Endian formats yet");
            } catch (UnsupportedAudioFileException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }

            //load stream into byte buffer
            int openALFormat = -1;
            switch (format.getChannels()) {
                case MONO:
                    switch (format.getSampleSizeInBits()) {
                        case 8 -> openALFormat = AL10.AL_FORMAT_MONO8;
                        case 16 -> openALFormat = AL10.AL_FORMAT_MONO16;
                    }
                    break;
                case STEREO:
                    openALFormat = switch (format.getSampleSizeInBits()) {
                        case 8 -> AL10.AL_FORMAT_STEREO8;
                        case 16 -> AL10.AL_FORMAT_STEREO16;
                        default -> openALFormat;
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
            AL10.alBufferData(buf, openALFormat, data, (int) format.getSampleRate());

            audioInfo.buffer = buf;
            audioInfo.audioFormat = format;
            audioInfo.frameLength = stream.getFrameLength();
            //and return the rough notion of length for the audio stream!
            audioInfo.audioLength = (long) (1000f * stream.getFrameLength() / format.getFrameRate());
        }
        return audioInfo;
    }

    public int getBuffer() { return buffer; }

    public AudioFormat getAudioFormat() { return audioFormat; }

    public long getFrameLength() { return frameLength; }

    public long getAudioLength() { return audioLength; }
}
