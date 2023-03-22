import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Scripting.*;
import Core2D.Log.*;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class DestroyEntityComponent extends Component
{
    public Timer destroyTimer = new Timer(new TimerCallback() {
        @Override
        public void deltaUpdate(float v) {

        }

        @Override
        public void update() {
            entity.destroy();
        }
    }, 1.9f);

    @Override
    public void update()
    {
        destroyTimer.startFrame();
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {

    }

    // otherEntity - an entity, one of whose colliders entered one of the colliders of this entity.
    @Override
    public void collider2DEnter(Entity otherEntity)
    {

    }

    // otherEntity - an entity whose body came out of the colliders of this entity
    @Override
    public void collider2DExit(Entity otherEntity)
    {

    }

    // camera2DComponent - the camera that renders this entity.
    @Override
    public void render(CameraComponent camera2DComponent)
    {

    }

    // Use the "shader" parameter to render this entity.
    @Override
    public void render(CameraComponent camera2DComponent, Shader shader)
    {

    }
}

