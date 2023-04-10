package Core2D.ECS.Audio;

import Core2D.ECS.Component;

public class AudioComponent extends Component
{
    // путь аудиозаписи (относительный)
    public String path = "";

    public transient int sourceHandler = -1;

    // --------------------------------------------
    public AudioType type = AudioType.BACKGROUND;

    public float maxDistance = 750f;
    public float referenceDistance = 25f;
    public float rolloffFactor = 0.1f;
    public float volumePercent = 100f;

    public float pitch = 1f;

    public AudioState lastState = AudioState.STOPPED;
    public AudioState state = AudioState.STOPPED;

    // текущая секунда
    public float currentSecond = 0f;

    public float audioLengthInSeconds = 0f;

    // прошлая секунда
    protected float lastSecond = 0f;

    public boolean cyclic = false;

    // --------------------------------------------
}
