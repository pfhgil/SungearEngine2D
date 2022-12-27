package Core2D.ECS.Component.Components;

import Core2D.Audio.Audio;
import Core2D.ECS.Component.Component;

public class AudioComponent extends Component
{
    public Audio audio = new Audio();

    public AudioComponent() { }

    public AudioComponent(AudioComponent component)
    {
        set(component);
    }

    @Override
    public void init()
    {
        if(entity != null && !entity.isShouldDestroy()) {
            audio.getTransform().setParentTransform(entity.getComponent(TransformComponent.class).getTransform());
        }
    }

    @Override
    public void destroy()
    {
        audio.destroy();
    }

    @Override
    public void update()
    {
        audio.update();
        super.update();
    }

    @Override
    public void deltaUpdate(float deltaTime) { audio.deltaUpdate(deltaTime); }

    @Override
    public void set(Component component)
    {
        if(component instanceof AudioComponent) {
            audio.set(((AudioComponent) component).audio);
        }
    }
}
