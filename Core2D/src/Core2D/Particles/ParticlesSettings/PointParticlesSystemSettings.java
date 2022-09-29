package Core2D.Particles.ParticlesSettings;

import Core2D.Layering.Layer;
import Core2D.Texture2D.Texture2D;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class PointParticlesSystemSettings extends ParticlesSettings
{
    // минимальные импульс при создании партиклов
    private Vector2f minCreateImpulse = new Vector2f();
    // максимальный импульс при создании партиклов
    private Vector2f maxCreateImpulse = new Vector2f();

    public PointParticlesSystemSettings(Texture2D particlesAtlasTexture, Layer particlesLayer)
    {
        this.particlesAtlasTexture = particlesAtlasTexture;
        this.particlesLayer = particlesLayer;
    }

    public PointParticlesSystemSettings(PointParticlesSystemSettings pointParticlesSystemSettings)
    {
        this.minCreateImpulse = new Vector2f(pointParticlesSystemSettings.getMinCreateImpulse());
        this.maxCreateImpulse = new Vector2f(pointParticlesSystemSettings.getMaxCreateImpulse());

        this.particlesCreatePosition = new Vector2f(pointParticlesSystemSettings.getParticlesCreatePosition());
        this.particlesCreateScale = new Vector2f(pointParticlesSystemSettings.getParticlesCreateScale());

        this.maxParticlesNum = pointParticlesSystemSettings.getMaxParticlesNum();
        this.particlesCreateDelay = pointParticlesSystemSettings.getParticlesCreateDelay();
        this.particlesLifetime = pointParticlesSystemSettings.getParticlesLifetime();

        this.particlesCreateColor = new Vector4f(pointParticlesSystemSettings.getParticlesCreateColor());

        this.particlesAtlasTexture = pointParticlesSystemSettings.getParticlesAtlasTexture();

        this.particlesLayer = pointParticlesSystemSettings.getParticlesLayer();

        this.isParticlesSensors = pointParticlesSystemSettings.isParticlesSensors();
        this.isParticlesFixedRotation = pointParticlesSystemSettings.isParticlesFixedRotation();

        this.particlesCollisionFilter.set(pointParticlesSystemSettings.getParticlesCollisionFilter());
    }

    public Vector2f getMinCreateImpulse() { return minCreateImpulse; }
    public void setMinCreateImpulse(Vector2f minCreateImpulse)
    {
        this.minCreateImpulse = minCreateImpulse;
    }

    public Vector2f getMaxCreateImpulse() { return maxCreateImpulse; }
    public void setMaxCreateImpulse(Vector2f maxCreateImpulse)
    {
        this.maxCreateImpulse = maxCreateImpulse;
    }
}