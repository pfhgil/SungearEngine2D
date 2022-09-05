package Core2D.Physics.Collider2D;

import Core2D.Physics.PhysicsWorld;
import Core2D.Physics.Rigidbody2D;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class CircleCollider2D
{
    private transient Rigidbody2D rigidbody2D;
    private transient Fixture fixture;

    private Vector2f offset = new Vector2f(0.0f, 0.0f);
    private float radius = 50.0f;

    public void destroy()
    {
        rigidbody2D = null;
        fixture = null;
    }

    public void set(CircleCollider2D circleCollider2D)
    {
        this.offset = null;

        setOffset(circleCollider2D.getOffset());
        setRadius(circleCollider2D.getRadius());

        circleCollider2D = null;
    }

    private void updateShape()
    {
        if(fixture != null && rigidbody2D != null) {
            CircleShape shape = (CircleShape) fixture.getShape();
            shape.m_radius = radius / PhysicsWorld.RATIO;
            shape.m_p.set(0.0f, 0.0f);

            Fixture newFixture = rigidbody2D.getBody().createFixture(shape, fixture.getDensity());
            newFixture.setFriction(rigidbody2D.getFriction());
            newFixture.setRestitution(rigidbody2D.getRestitution());
            newFixture.setSensor(rigidbody2D.isSensor());
            rigidbody2D.getBody().destroyFixture(fixture);

            fixture = newFixture;
        }
    }

    public Rigidbody2D getRigidbody2D() { return rigidbody2D; }
    public void setRigidbody2D(Rigidbody2D rigidbody2D) { this.rigidbody2D = rigidbody2D; }

    public Fixture getFixture() { return fixture; }
    public void setFixture(Fixture fixture) { this.fixture = fixture; }

    public Vector2f getOffset() { return offset; }
    public void setOffset(Vector2f offset)
    {
        this.offset = offset;

        updateShape();
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius)
    {
        this.radius = radius;

        updateShape();
    }
}