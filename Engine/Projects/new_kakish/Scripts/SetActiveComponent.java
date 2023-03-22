import Core2D.AssetManager.AssetManager;
import Core2D.ECS.*;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.*;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.*;
import Core2D.Log.*;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import Core2D.Utils.Utils;
import org.joml.Vector4f;

// Attention! We do not recommend writing logic in components. Try to declare only fields in components.
public class SetActiveComponent extends Component
{
    public Timer setActiveTimer = new Timer(new TimerCallback() {
        @Override
        public void deltaUpdate(float v) {

        }

        @Override
        public void update() {
            TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
            entity.name = "slay";

            //dsfdfdf
            //transformComponent.scale.x = 6.0f;
            transformComponent.scale.y = 0.25f;

            transformComponent.rotation.z = (float) Utils.getRandom(0d, 360d);

            ECSWorld.getCurrentECSWorld().transformationsSystem.updateTransformComponent(transformComponent);

            MeshComponent slayMesh = entity.getComponent(MeshComponent.class);
            slayMesh.setTexture(new Texture2D(AssetManager.getInstance().getTexture2DData("Resources/slay.png")));
            entity.setColor(new Vector4f(66 / 255f, 170 / 255f, 1.0f, 1.0f));

            slayMesh.active = true;

            entity.getComponent(UpScaleComponent.class).upScaleTimer.start();
        }
    }, (float) Utils.getRandom(1.5f, 2.0f));

    @Override
    public void update()
    {
        setActiveTimer.startFrame();
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

