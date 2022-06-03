package Core2D.Particles.ParticlesSettings;

import Core2D.Layering.Layer;
import Core2D.Texture2D.Texture2D;
import org.jbox2d.dynamics.Filter;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class ParticlesSettings
{
    protected Vector2f particlesCreatePosition = new Vector2f();

    protected Vector2f particlesCreateScale = new Vector2f(1.0f, 1.0f);

    // максимальное кол-во партиклов
    protected int maxParticlesNum = 50;
    // задержка при создании партиклов
    protected float particlesCreateDelay = 0.1f;
    // время жизни каждого парктила
    protected float particlesLifetime = 1.0f;

    // цвет партиклов при создании
    protected Vector4f particlesCreateColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // атлас
    protected Texture2D particlesAtlasTexture;

    // слой для партиклов
    protected Layer particlesLayer;

    // физические настройки
    protected boolean isParticlesSensors;
    protected boolean isParticlesFixedRotation;
    protected Filter particlesCollisionFilter = new Filter();

    public void destroy()
    {
        particlesCreatePosition = null;
        particlesCreateScale = null;

        particlesCreateColor = null;

        particlesAtlasTexture = null;

        particlesLayer = null;

        particlesCollisionFilter = null;

        if(this instanceof PointParticlesSystemSettings) {
            ((PointParticlesSystemSettings) this).setMinCreateImpulse(null);
            ((PointParticlesSystemSettings) this).setMaxCreateImpulse(null);
        }
    }


    public Vector2f getParticlesCreatePosition() { return particlesCreatePosition; }
    public void setParticlesCreatePosition(Vector2f particlesCreatePosition)
    {
        this.particlesCreatePosition = particlesCreatePosition;
        particlesCreatePosition = null;
    }

    public Vector2f getParticlesCreateScale() { return particlesCreateScale; }
    public void setParticlesCreateScale(Vector2f particlesCreateScale)
    {
        this.particlesCreateScale = particlesCreateScale;
        particlesCreateScale = null;
    }

    public int getMaxParticlesNum() { return maxParticlesNum; }
    public void setMaxParticlesNum(int maxParticlesNum) { this.maxParticlesNum = maxParticlesNum; }

    public float getParticlesCreateDelay() { return particlesCreateDelay; }
    public void setParticlesCreateDelay(float particlesCreateDelay) { this.particlesCreateDelay = particlesCreateDelay; }

    public float getParticlesLifetime() { return particlesLifetime; }
    public void setParticlesLifetime(float particlesLifetime) { this.particlesLifetime = particlesLifetime; }

    public Vector4f getParticlesCreateColor() { return particlesCreateColor; }
    public void setParticlesCreateColor(Vector4f particlesCreateColor)
    {
        this.particlesCreateColor = particlesCreateColor;
        particlesCreateColor = null;
    }

    public Texture2D getParticlesAtlasTexture() { return particlesAtlasTexture; }
    public void setParticlesAtlasTexture(Texture2D particlesAtlasTexture)
    {
        this.particlesAtlasTexture = particlesAtlasTexture;
        particlesAtlasTexture = null;
    }

    public Layer getParticlesLayer() { return particlesLayer; }
    public void setParticlesLayer(Layer particlesLayer)
    {
        this.particlesLayer = particlesLayer;
        particlesLayer = null;
    }

    public boolean isParticlesSensors() { return isParticlesSensors; }
    public void setParticlesSensors(boolean particlesSensors) { isParticlesSensors = particlesSensors; }

    public boolean isParticlesFixedRotation() { return isParticlesFixedRotation; }
    public void setParticlesFixedRotation(boolean particlesFixedRotation) { isParticlesFixedRotation = particlesFixedRotation; }

    public Filter getParticlesCollisionFilter() { return particlesCollisionFilter; }
    public void setParticlesCollisionFilter(Filter particlesCollisionFilter)
    {
        this.particlesCollisionFilter = particlesCollisionFilter;
        particlesCollisionFilter = null;
    }
}
