package Core2D.Particles;

import Core2D.Core2D.Core2D;
import Core2D.Instancing.ObjectsInstancing;
import Core2D.Object2D.Object2D;
import Core2D.Particles.ParticlesSettings.ParticlesSettings;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;

public class ParticlesSystem
{
    private ParticlesSettings particlesSettings;

    private ObjectsInstancing particlesObjectsInstancing;

    private Timer createParticlesTimer;

    private boolean active = false;

    // циклично спавнит партиклы
    private boolean cyclic = false;

    private int allCreatedParticles = 0;

    public ParticlesSystem(ParticlesSettings particlesSettings)
    {
        this.particlesSettings = particlesSettings;

        particlesObjectsInstancing = new ObjectsInstancing(null, this.particlesSettings.getParticlesAtlasTexture(), false);
        particlesObjectsInstancing.setLayer(particlesSettings.getParticlesLayer());

        this.createParticlesTimer = new Timer(new TimerCallback() {
            @Override
            public void DeltaUpdate(float deltaTime) {

            }

            @Override
            public void Update() {
                if(particlesObjectsInstancing.getDrawableObjects2D().size() < particlesSettings.getMaxParticlesNum()) {
                    ParticlesFactory.createParticle(particlesSettings, particlesObjectsInstancing.getDrawableObjects2D());
                    allCreatedParticles++;
                }
                if(allCreatedParticles >= particlesSettings.getMaxParticlesNum() && !cyclic) {
                    allCreatedParticles = 0;
                    createParticlesTimer.stop();
                }
            }
        }, particlesSettings.getParticlesCreateDelay(), true);
    }

    public void startSystem()
    {
        if(!active) {
            active = true;

            createParticlesTimer.start();
        }
    }

    public void stopSystem()
    {
        if(active) {
            active = false;

            createParticlesTimer.stop();
        }
    }

    public void clearParticlesList()
    {
        for(int i = 0; i < particlesObjectsInstancing.getDrawableObjects2D().size(); i++) {
            Object2D particle = particlesObjectsInstancing.getDrawableObjects2D().get(i);

            particle.destroy();
        }

        particlesObjectsInstancing.getDrawableObjects2D().clear();
    }

    public void destroy()
    {
        particlesSettings.destroy();
        particlesSettings = null;
        particlesObjectsInstancing.destroy();
        particlesObjectsInstancing = null;

        Core2D.getTimersManager().getAllTimers().remove(createParticlesTimer);
        createParticlesTimer.setName(null);
        createParticlesTimer = null;
    }

    public ParticlesSettings getParticlesSettings() { return particlesSettings; }
    public void setParticlesSettings(ParticlesSettings particlesSettings)
    {
        this.particlesSettings = particlesSettings;
        particlesSettings = null;
    }

    public Timer getCreateParticlesTimer() { return createParticlesTimer; }

    public boolean isActive() { return active; }

    public boolean isCyclic() { return cyclic; }
    public void setCyclic(boolean cyclic) { this.cyclic = cyclic; }
}
