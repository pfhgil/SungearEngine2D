package Core2D.ECS.Deprecated;

import Core2D.ECS.Script.ScriptComponent;

@Deprecated
public class ParticlesSystemComponent extends ScriptComponent
{
    /*
    @InspectorView
    public Entity exampleEntity;
    private transient Pool particlesPool = new Pool();
    private transient Timer spawnTimer = new Timer(new TimerCallback() {
        @Override
        public void deltaUpdate(float deltaTime) {

        }

        @Override
        public void update() {
            if(active && SceneManager.currentSceneManager.getCurrentScene2D().isRunning()) {
                if (!particlesPool.hasFree()) {
                    Entity[] particle = new Entity[] { new Entity() };

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
                Entity freeParticle = (Entity) particlesPool.get();
                TimerComponent destroyTimer = freeParticle.getComponent(TimerComponent.class);
                destroyTimer.getTimer().start();

                TransformComponent particleTransform = freeParticle.getComponent(TransformComponent.class);
                TransformComponent systemTransform = entity.getComponent(TransformComponent.class);
                if(particleTransform != null && systemTransform != null) {
                    particleTransform.position.set(systemTransform.position);
                }

                //Systems.out.println(exampleGameObject);
                if(exampleEntity != null) {
                    //freeParticle.set(exampleEntity);
                }

                freeParticle.setLayer(SceneManager.currentSceneManager.getCurrentScene2D().getLayering().getLayer("default"));
            }
        }
    }, 0.25f, true);

    @Override
    public void init()
    {
        script.loadClass(this.getClass(), this);
    }

    @Override
    public void update()
    {
        spawnTimer.startFrame();
    }

     */
}