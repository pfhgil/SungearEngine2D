package Core2D.ECS.Component.Components.Audio;

import Core2D.ECS.Component.Component;

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
    public float lastSecond = 0f;

    public boolean cyclic = false;

    // --------------------------------------------

    //public Audio audio = new Audio();

    /*
    public AudioComponent(AudioComponent component)
    {
        set(component);
    }

     */

    @Override
    public void init()
    {
        /*
        if(entity != null && !entity.isShouldDestroy()) {
            audio.getTransform().setParentTransform(entity.getComponent(TransformComponent.class).getTransform());
        }

         */
    }

    /*
    @Override
    public void destroy()
    {
        audio.destroy();
    }

     */
    /*
    @Override
    public void update()
    {
        //audio.update();
        //super.update();
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        //audio.deltaUpdate(deltaTime);
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof AudioComponent) {
            audio.set(((AudioComponent) component).audio);
        }
    }

     */
}
