package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.GameObject.GameObject;
import Core2D.Pooling.Pool;
import Core2D.Scene2D.SceneManager;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import Core2D.Transform.Transform;
import org.joml.Vector2f;

public class ParticlesSystemComponent extends Component
{
    private Pool particlesPool = new Pool();

    // in seconds
    private float spawnInterval = 0.5f;

    private Timer particlesSpawnTimer = new Timer(new TimerCallback() {
        @Override
        public void deltaUpdate(float deltaTime)
        {

        }

        @Override
        public void update()
        {
            if(active && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
                GameObject newParticle = (GameObject) particlesPool.addPoolObject(new GameObject());

                TransformComponent transformComponent = gameObject.getComponent(TransformComponent.class);
                if (transformComponent != null) {
                    Transform transform = transformComponent.getTransform();

                    TransformComponent particleTransformComponent = new TransformComponent();
                    newParticle.addComponent(particleTransformComponent);
                    newParticle.addComponent(new MeshRendererComponent());
                    newParticle.addComponent(new BoxCollider2DComponent());
                    newParticle.addComponent(new Rigidbody2DComponent());

                    newParticle.setLayer(SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default"));

                    particleTransformComponent.getTransform().setPosition(transform.getPosition());
                    particleTransformComponent.getTransform().applyLinearImpulse(new Vector2f(100.0f, 100.0f), new Vector2f());
                }
            }
        }
    }, spawnInterval, true);

    @Override
    public void update()
    {
        particlesSpawnTimer.startFrame();
    }

    public Pool getParticlesPool() { return particlesPool; }

    public float getSpawnInterval() { return spawnInterval; }
    public void setSpawnInterval(float spawnInterval)
    {
        this.spawnInterval = spawnInterval;
        particlesSpawnTimer.destTime = spawnInterval;
    }
}
