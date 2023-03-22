import Core2D.Core2D.Core2D;
import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Scripting.*;
import Core2D.Log.*;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import org.joml.Math;
import org.joml.Vector2f;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class UpScaleComponent extends Component
{
    public Timer upScaleTimer = new Timer(new TimerCallback() {
        @Override
        public void deltaUpdate(float v)
        {
            //Log.CurrentSession.println(v, Log.MessageType.SUCCESS);

            if(entity != null) {
                TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
                if(transformComponent != null) {
                    //transformComponent.scale.lerp(new Vector2f(6.0f, 0.25f), v);
                    //transformComponent.scale.x = Math.lerp(transformComponent.scale.x, 6.0f, v);
                    transformComponent.scale.x += v * 10.0f;
                }
            }
            //
        }

        @Override
        public void update()
        {

        }
    }, 0.1f);

    @Override
    public void update()
    {
        upScaleTimer.startFrame();
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
    public void render(CameraComponent cameraComponent)
    {

    }

    // Use the "shader" parameter to render this entity.
    @Override
    public void render(CameraComponent cameraComponent, Shader shader)
    {
        //f
    }
}

