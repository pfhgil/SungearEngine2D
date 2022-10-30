package Core2D.Physics.Collider2D;

import Core2D.Physics.PhysicsWorld;
import Core2D.Physics.Rigidbody2D;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

/**
 * A square physical object required to detect intersections with other objects in the physical world. Use only with Rigidbody2D!
 * @see PhysicsWorld
 */
public class BoxCollider2D
{
    private transient Rigidbody2D rigidbody2D;
    private transient Fixture fixture;

    private Vector2f scale = new Vector2f(1.0f, 1.0f);
    private Vector2f offset = new Vector2f();

    public void set(BoxCollider2D boxCollider2D)
    {
        this.offset = null;
        this.scale = null;

        setOffset(boxCollider2D.getOffset());
        setScale(boxCollider2D.getScale());
    }

    private void updateShape()
    {
        if(fixture != null && rigidbody2D != null) {
            PolygonShape shape = (PolygonShape) fixture.getShape();
            shape.setAsBox((100.0f / PhysicsWorld.RATIO / 2.0f) * scale.x, (100.0f / PhysicsWorld.RATIO / 2.0f) * scale.y, new Vec2(0.0f, 0.0f), rigidbody2D.getBody().getAngle());

            Fixture newFixture = rigidbody2D.getBody().createFixture(shape, fixture.getDensity());
            newFixture.setFriction(rigidbody2D.getFriction());
            newFixture.setRestitution(rigidbody2D.getRestitution());
            newFixture.setSensor(rigidbody2D.isSensor());
            rigidbody2D.getBody().destroyFixture(fixture);

            fixture = newFixture;
        }
    }

    public void destroy()
    {
        if(rigidbody2D != null) {
            rigidbody2D.getBody().destroyFixture(fixture);
        }
    }

    public Vector2f getOffset() { return offset; }
    public void setOffset(Vector2f offset)
    {
        this.offset = offset;

        updateShape();
    }

    public Vector2f getScale() { return scale; }
    public void setScale(Vector2f scale)
    {
        this.scale = scale;

        updateShape();
    }

    public Fixture getFixture() { return fixture; }
    public void setFixture(Fixture fixture) { this.fixture = fixture; }

    public Rigidbody2D getRigidbody2D() { return rigidbody2D; }
    public void setRigidbody2D(Rigidbody2D rigidbody2D) { this.rigidbody2D = rigidbody2D; }
}