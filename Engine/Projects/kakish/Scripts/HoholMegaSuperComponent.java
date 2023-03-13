import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
//import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.Component.Components.Audio.AudioComponent;
import Core2D.ECS.Component.Components.Audio.AudioState;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Input.PC.Keyboard;
import Core2D.Scripting.*;
import Core2D.Log.*;
import org.lwjgl.glfw.GLFW;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class HoholMegaSuperComponent extends Component
{
    @Override
    public void update()
    {
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        //Log.CurrentSession.println("mmmega kaks", Log.MessageType.ERROR);
        if(Keyboard.keyDown(GLFW.GLFW_KEY_D)) {
            transformComponent.position.x += 1.0f;
            //Camera2DComponent camera2DComponent = entity.getComponent(Camera2DComponent.class);
        } else if(Keyboard.keyDown(GLFW.GLFW_KEY_A)) {
            transformComponent.position.x -= 1f;
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {

    }

    // otherEntity - an entity, one of whose colliders entered one of the colliders of this entity.
    @Override
    public void collider2DEnter(Entity otherEntity)
    {
        AudioComponent nighcallKakishComponent = entity.getComponent(AudioComponent.class);
        nighcallKakishComponent.state = AudioState.PLAYING;
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

