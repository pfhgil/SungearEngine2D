package Core2D.Particles;

import Core2D.Component.Components.Rigidbody2DComponent;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Object2D.Object2D;
import Core2D.Object2D.Transform;
import Core2D.Particles.ParticlesSettings.ParticlesSettings;
import Core2D.Particles.ParticlesSettings.PointParticlesSystemSettings;
import Core2D.Timer.Timer;
import Core2D.Timer.TimerCallback;
import Core2D.Utils.Utils;
import org.joml.Vector2f;

import java.util.List;

public class ParticlesFactory
{
    public static Timer createParticle(ParticlesSettings particlesSettings, List<Object2D> particlesList)
    {
        Object2D[] particle = { new Object2D() };

        Transform particleTransform = particle[0].getComponent(TransformComponent.class).getTransform();

        particle[0].getComponent(TextureComponent.class).setTexture2D(particlesSettings.getParticlesAtlasTexture());
        particle[0].addComponent(new Rigidbody2DComponent());
        particle[0].setColor(particlesSettings.getParticlesCreateColor());
        particleTransform.setScale(new Vector2f(particlesSettings.getParticlesCreateScale()));
        particleTransform.setPosition(new Vector2f(particlesSettings.getParticlesCreatePosition()));

        //particleTransform.getRigidbody2D().setSensor(particlesSettings.isParticlesSensors());
        particleTransform.getRigidbody2D().getBody().setFixedRotation(particlesSettings.isParticlesFixedRotation());
        particleTransform.getRigidbody2D().getBody().getFixtureList().setFilterData(particlesSettings.getParticlesCollisionFilter());

        applyImpulse(particlesSettings, particle[0]);

        particlesList.add(particle[0]);

        Timer respawnParticleTimer = new Timer(new TimerCallback() {
            @Override
            public void deltaUpdate(float deltaTime) {

            }

            @Override
            public void update() {
                particle[0].destroy();
                particlesList.remove(particle[0]);
                particle[0] = null;
            }
        }, particlesSettings.getParticlesLifetime(), false);

        respawnParticleTimer.start();

        particlesSettings = null;

        particleTransform = null;

        return respawnParticleTimer;
    }

    public static void applyImpulse(ParticlesSettings particlesSettings, Object2D particle)
    {
        if(particlesSettings instanceof PointParticlesSystemSettings) {
            PointParticlesSystemSettings pointParticlesSystemSettings = (PointParticlesSystemSettings) particlesSettings;
            Vector2f randomImpulse = new Vector2f(
                    (float) Utils.getRandom(
                            pointParticlesSystemSettings.getMinCreateImpulse().x,
                            pointParticlesSystemSettings.getMaxCreateImpulse().x
                    ),
                    (float) Utils.getRandom(
                            pointParticlesSystemSettings.getMinCreateImpulse().y,
                            pointParticlesSystemSettings.getMaxCreateImpulse().y
                    )
            );

            Transform particleTransform = particle.getComponent(TransformComponent.class).getTransform();

            particleTransform.applyLinearImpulse(randomImpulse, new Vector2f(particleTransform.getPosition()).add(particleTransform.getCentre()));
        }
    }
}
