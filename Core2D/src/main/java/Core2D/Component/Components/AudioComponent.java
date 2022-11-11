package Core2D.Component.Components;

import Core2D.Audio.Audio;
import Core2D.Component.Component;

public class AudioComponent extends Component
{
    public Audio audio = new Audio();

    @Override
    public void init()
    {
        if(object2D != null && !object2D.isShouldDestroy()) {
            audio.getTransform().setParentTransform(object2D.getComponent(TransformComponent.class).getTransform());
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
