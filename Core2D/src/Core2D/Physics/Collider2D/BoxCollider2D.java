package Core2D.Physics.Collider2D;

import Core2D.Object2D.Object2D;
import Core2D.Physics.PhysicsWorld;
import org.jbox2d.collision.shapes.PolygonShape;
import org.joml.Vector2f;

public class BoxCollider2D extends Collider2D
{
    private PolygonShape shape;

    private Vector2f scale;

    public BoxCollider2D(Object2D attachedObject2D)
    {
        super(attachedObject2D);

        shape = new PolygonShape();
        shape.setAsBox(100.0f / PhysicsWorld.RATIO / 2, 100.0f / PhysicsWorld.RATIO / 2);

        setShape(shape);

        create();

        attachedObject2D = null;
    }

    public BoxCollider2D(Object2D attachedObject2D, Collider2D boxCollider2D)
    {
        super(attachedObject2D);

        shape = new PolygonShape();
        shape.setAsBox(100.0f / PhysicsWorld.RATIO / 2, 100.0f / PhysicsWorld.RATIO / 2);

        setShape(shape);

        create(boxCollider2D);

        attachedObject2D = null;
        boxCollider2D = null;
    }

    public void scale(Vector2f scale)
    {
        //Vector2f dif = new Vector2f(this.scale.x - scale.x, this.scale.y - scale.y);
        this.scale.add(new Vector2f(scale));

        body.destroyFixture(body.getFixtureList());

        shape.setAsBox(Math.abs((100.0f / PhysicsWorld.RATIO / 2.0f) * this.scale.x), Math.abs((100.0f / PhysicsWorld.RATIO / 2.0f) * this.scale.y));

        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);

        scale = null;
    }

    public void setScale(Vector2f scale)
    {
        this.scale = scale;

        body.destroyFixture(body.getFixtureList());

        shape.setAsBox(Math.abs((100.0f / PhysicsWorld.RATIO / 2.0f) * this.scale.x), Math.abs((100.0f / PhysicsWorld.RATIO / 2.0f) * this.scale.y));

        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);

        scale = null;
    }

    public PolygonShape getShape() { return shape; }
}