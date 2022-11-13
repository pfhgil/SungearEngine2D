package Core2D.Audio;

public class AudioFormat
{
    private float sampleRate;

    private int sampleSizeInBits;

    private int channels;

    private int frameSize;

    private float frameRate;

    private boolean bigEndian;

    public void set(AudioFormat audioFormat)
    {
        this.sampleRate = audioFormat.sampleRate;
        this.sampleSizeInBits = audioFormat.sampleSizeInBits;
        this.channels = audioFormat.channels;
        this.frameSize = audioFormat.frameSize;
        this.frameRate = audioFormat.frameRate;
        this.bigEndian = audioFormat.bigEndian;
    }

    public void set(javax.sound.sampled.AudioFormat audioFormat)
    {
        this.sampleRate = audioFormat.getSampleRate();
        this.sampleSizeInBits = audioFormat.getSampleSizeInBits();
        this.channels = audioFormat.getChannels();
        this.frameSize = audioFormat.getFrameSize();
        this.frameRate = audioFormat.getFrameRate();
        this.bigEndian = audioFormat.isBigEndian();
    }

    public float getSampleRate() { return sampleRate; }

    public int getSampleSizeInBits() { return sampleSizeInBits; }

    public int getChannels() { return channels; }

    public int getFrameSize() { return frameSize; }

    public float getFrameRate() { return frameRate; }

    public boolean isBigEndian() { return bigEndian; }
}
