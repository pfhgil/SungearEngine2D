package Core2D.ECS.Component.Components.Audio;

import Core2D.Audio.Audio;
import Core2D.ECS.Component.Component;
import org.lwjgl.openal.AL10;

public class AudioComponent extends Component
{
    public int handler = -1;

    // --------------------------------------------
    public AudioType type = AudioType.BACKGROUND;

    public float maxDistance = 750.0f;
    public float referenceDistance = 25.0f;
    public float rolloffFactor = 0.1f;
    public float volumePercent = 100.0f;

    public boolean playing = false;
    public boolean paused = false;

    public int state = AL10.AL_STOPPED;

    // текущая секунда
    public float currentSecond = 0.0f;
    // прошлая секунда
    public float lastSecond = 0.0f;

    public boolean cyclic = false;

    // --------------------------------------------

    public Audio audio = new Audio();

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
