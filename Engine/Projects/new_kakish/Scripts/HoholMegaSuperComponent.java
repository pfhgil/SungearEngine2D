import Core2D.AssetManager.AssetManager;
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
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Input.PC.Keyboard;
import Core2D.Input.PC.Mouse;
import Core2D.Layering.Layer;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.*;
import Core2D.Log.*;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import Core2D.Utils.Utils;
import org.jbox2d.common.Vec2;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.*;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class HoholMegaSuperComponent extends Component
{
    private boolean controlsActive = true;

    private Timer slayActiveTimer = new Timer(new TimerCallback() {
        @Override
        public void deltaUpdate(float v) {

        }

        @Override
        public void update() {
            List<AudioComponent> audioComponents = entity.getAllComponents(AudioComponent.class);

            if(audioComponents.size() > 0) {
                audioComponents.get(1).state = AudioState.PLAYING;
                audioComponents.get(0).state = AudioState.PLAYING;
            }

            controlsActive = true;
        }
    }, 1.5f);

    @Override
    public void update()
    {
        TransformComponent entityTransform = entity.getComponent(TransformComponent.class);

        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().getPhysicsWorld().setGravity(new Vec2(0f, 0f));
        }

        if(controlsActive) {
            entityTransform.rotation = lookAt(entityTransform.position, Mouse.getMouseOGLPosition(Mouse.getMousePosition()));
        }

        if(Keyboard.keyReleased(GLFW.GLFW_KEY_G) && controlsActive) {
            controlsActive = false;

            Entity nearestDamagableEntity = getNearestDamagableEntity(entityTransform.position);

            if(nearestDamagableEntity != null) {
                entityTransform.rotation = lookAt(entityTransform.position, nearestDamagableEntity.getComponent(TransformComponent.class).position);
            }
            entityTransform.position.add(translateInRotationDirection(entityTransform.rotation, -100f));

            if(nearestDamagableEntity != null) {
                int slays = Utils.getRandom(5, 8);

                TransformComponent nearestTransformComponent = nearestDamagableEntity.getComponent(TransformComponent.class);
                for(int i = 0; i < slays; i++) {
                    createSlayEntity(nearestTransformComponent.position);
                }
            }

            slayActiveTimer.start();
        }

        slayActiveTimer.startFrame();
    }

    private Entity createSlayEntity(Vector2f onPosition)
    {
        Entity slayEntity = Entity.createAsObject2D();

        TransformComponent slayTransform = slayEntity.getComponent(TransformComponent.class);
        slayTransform.scale.x = -20f;

        //dfdfdfsfsdfdf
        slayEntity.addComponent(new SetActiveComponent());
        slayEntity.addComponent(new UpScaleComponent());
        slayEntity.addComponent(new DestroyEntityComponent());

        slayTransform.position.x = onPosition.x;
        slayTransform.position.y = onPosition.y;

        slayEntity.setLayer(SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("slaysLayer"));

        slayEntity.getComponent(MeshComponent.class).active = false;

        return slayEntity;
    }

    private Entity getNearestDamagableEntity(Vector2f fromPosition)
    {
        //
        Vector2f nearest = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
        Entity nearestEntity = null;

        for(Layer layer : SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayers()) {
            for(Entity e : layer.getEntities()) {
                PickableComponent pickableComponent = e.getComponent(PickableComponent.class);

                if(pickableComponent != null) {
                    TransformComponent transformComponent = e.getComponent(TransformComponent.class);

                    Vector2f dif = new Vector2f(fromPosition).add(new Vector2f(transformComponent.position).negate());

                    if(dif.x < nearest.x || dif.y < nearest.y) {
                        nearestEntity = e;
                    }
                }
            }
        }

        return nearestEntity;
    }

    private float lookAt(Vector2f position, Vector2f mousePos)
    {
        float dx = position.x - mousePos.x;
        float dy = position.y - mousePos.y;

        return (float) (Math.atan2(dy, dx) * 180.0f / Math.PI);
    }

    private Vector2f translateInRotationDirection(float rotation, float translation)
    {
        Vector3f rotatedTranslation = new Vector3f(translation, 0f, 0f);
        rotatedTranslation.rotateZ(Math.toRadians(rotation));

        return new Vector2f(-rotatedTranslation.x, -rotatedTranslation.y);
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        if(Keyboard.keyDown(GLFW.GLFW_KEY_W) && controlsActive) {
            transformComponent.position.add(translateInRotationDirection(transformComponent.rotation, 1000f * deltaTime));
        }
    }

    // otherEntity - an entity, one of whose colliders entered one of the colliders of this entity.
    @Override
    public void collider2DEnter(Entity otherEntity)
    {
        PickableComponent pickableComponent = otherEntity.getComponent(PickableComponent.class);
        if(pickableComponent != null) {
            otherEntity.destroy();
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

