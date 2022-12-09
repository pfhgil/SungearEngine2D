package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.GameObject.GameObject;
import Core2D.Pooling.Pool;
import Core2D.Scene2D.SceneManager;
import Core2D.Scripting.InspectorView;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;

public class ParticlesSystemComponent extends ScriptComponent
{
    @InspectorView
    public GameObject exampleGameObject;
    private transient Pool particlesPool = new Pool();
    private transient Timer spawnTimer = new Timer(new TimerCallback() {
        @Override
        public void deltaUpdate(float deltaTime) {

        }

        @Override
        public void update() {
            if(active && SceneManager.currentSceneManager.getCurrentScene2D().isRunning()) {
                if (!particlesPool.hasFree()) {
                    GameObject[] particle = new GameObject[] { new GameObject() };

                    TimerComponent destroyTimer = new TimerComponent();
                    destroyTimer.getTimer().destTime = 0.5f;
                    destroyTimer.getTimer().getTimerCallbacks().add(new TimerCallback() {
                        @Override
                        public void deltaUpdate(float deltaTime) {

                        }

                        @Override
                        public void update() {
                            particlesPool.releaseUsedPoolObject(particle[0]);
                        }
                    });
                    particle[0].addComponent(destroyTimer);

                    particlesPool.addPoolObject(particle[0]);
                }
                GameObject freeParticle = (GameObject) particlesPool.get();
                TimerComponent destroyTimer = freeParticle.getComponent(TimerComponent.class);
                destroyTimer.getTimer().start();

                TransformComponent particleTransform = freeParticle.getComponent(TransformComponent.class);
                TransformComponent systemTransform = gameObject.getComponent(TransformComponent.class);
                if(particleTransform != null && systemTransform != null) {
                    particleTransform.getTransform().setPosition(systemTransform.getTransform().getPosition());
                }

                //System.out.println(exampleGameObject);
                if(exampleGameObject != null) {
                    freeParticle.set(exampleGameObject);
                }

                freeParticle.setLayer(SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default"));
            }
        }
    }, 0.25f, true);

    @Override
    public void init()
    {
        getScript().loadClass(this.getClass(), this);
    }

    @Override
    public void update()
    {
        spawnTimer.startFrame();
    }
}
