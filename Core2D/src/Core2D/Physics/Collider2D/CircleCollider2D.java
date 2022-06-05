package Core2D.Physics.Collider2D;

import Core2D.Object2D.Object2D;
import Core2D.Physics.PhysicsWorld;
import org.jbox2d.collision.shapes.CircleShape;

public class CircleCollider2D extends Collider2D
{
    private CircleShape shape;

    private float radius;

    public CircleCollider2D(Object2D attachedObject2D)
    {
        super(attachedObject2D);

        shape = new CircleShape();
        shape.m_radius = 1.0f / PhysicsWorld.RATIO;

        setShape(shape);

        create();

        attachedObject2D = null;
    }

    public CircleCollider2D(Object2D attachedObject2D, Collider2D boxCollider2D)
    {
        super(attachedObject2D);

        shape = new CircleShape();
        shape.m_radius = 1.0f / PhysicsWorld.RATIO;

        setShape(shape);

        create(boxCollider2D);

        attachedObject2D = null;
        boxCollider2D = null;
    }

    public CircleShape getCircleShape() { return shape; }

    public float getRadius() { return radius; }
    public void setRadius(float radius)
    {
        this.radius = radius;

        body.destroyFixture(body.getFixtureList());

        shape.m_radius = radius / PhysicsWorld.RATIO;

        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
    }
}