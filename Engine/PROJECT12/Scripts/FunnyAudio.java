import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.AudioComponent;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class FunnyAudio extends Component
{
    @Override
    public void update()
    {

    }

    @Override
    public void deltaUpdate(float deltaTime)
    {

    }

    // otherEntity - an entity, one of whose colliders entered one of the colliders of this entity.
    @Override
    public void collider2DEnter(Entity otherEntity)
    {
        AudioComponent audioComponent = this.entity.getComponent(AudioComponent.class);
        if(audioComponent != null) {
            audioComponent.audio.play();
        }
    }

    // otherEntity - an entity whose body came out of the colliders of this entity
    @Override
    public void collider2DExit(Entity otherEntity)
    {

    }

    // camera2DComponent - the camera that renders this entity.
    @Override
    public void render(Camera2DComponent camera2DComponent)
    {

    }

    // Use the "shader" parameter to render this entity.
    @Override
    public void render(Camera2DComponent camera2DComponent, Shader shader)
    {

    }
}
