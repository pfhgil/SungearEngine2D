package Core2D.Physics.Collider2D;

import Core2D.Component.Components.TransformComponent;
import Core2D.Object2D.Object2D;
import Core2D.Physics.PhysicsWorld;
import Core2D.Physics.Rigidbody2D;
import Core2D.Primitives.Line2D;
import Core2D.Utils.MathUtils;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.*;

import java.lang.Math;

public class BoxCollider2D
{
    private transient Rigidbody2D rigidbody2D;
    private transient Fixture fixture;

    private Vector2f scale = new Vector2f(1.0f, 1.0f);
    private Vector2f offset = new Vector2f();

    private transient Line2D[] debugLines = new Line2D[4];

    public BoxCollider2D()
    {
        for(int i = 0; i < debugLines.length; i++) {
            debugLines[i] = new Line2D(new Vector2f(), new Vector2f());
            debugLines[i].setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
            debugLines[i].setLineWidth(4.0f);
        }
    }

    public void draw(Object2D object2D)
    {
        for(Line2D line2D : debugLines) {
            line2D.draw();
        }

        TransformComponent transformComponent = object2D.getComponent(TransformComponent.class);
        Vector3f rotatedOffset = new Vector3f(offset.x, offset.y, 0.0f);
        rotatedOffset.rotateZ((float) Math.toRadians(transformComponent.getTransform().getRotation()));
        Vector2f center = new Vector2f(transformComponent.getTransform().getPosition())
                .add(transformComponent.getTransform().getCentre())
                .add(new Vector2f(rotatedOffset.x, rotatedOffset.y));
        Vector2f halfSize = new Vector2f(100.0f * scale.x, 100.0f * scale.y);

        Vector2f min = new Vector2f(center).sub(new Vector2f(halfSize).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(halfSize).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if(transformComponent.getTransform().getRotation() != 0.0f) {
            for (Vector2f vert : vertices) {
                MathUtils.rotate(vert, transformComponent.getTransform().getRotation(), center);
            }
        }

        debugLines[0].setStart(new Vector2f(vertices[0].x, vertices[0].y));
        debugLines[0].setEnd(new Vector2f(vertices[1].x, vertices[1].y));

        debugLines[1].setStart(new Vector2f(vertices[1].x, vertices[1].y));
        debugLines[1].setEnd(new Vector2f(vertices[2].x, vertices[2].y));

        debugLines[2].setStart(new Vector2f(vertices[2].x, vertices[2].y));
        debugLines[2].setEnd(new Vector2f(vertices[3].x, vertices[3].y));

        debugLines[3].setStart(new Vector2f(vertices[3].x, vertices[3].y));
        debugLines[3].setEnd(new Vector2f(vertices[0].x, vertices[0].y));
    }

    public void destroy()
    {
        offset = null;
        scale = null;

        for(int i = 0; i < debugLines.length; i++) {
            debugLines[i].destroy();
            debugLines[i] = null;
        }
        debugLines = null;

        rigidbody2D = null;
        fixture = null;
    }

    public void set(BoxCollider2D boxCollider2D)
    {
        setOffset(boxCollider2D.getOffset());
        setScale(boxCollider2D.getScale());

        boxCollider2D = null;
    }

    private void updateShape()
    {
        PolygonShape shape = (PolygonShape) fixture.getShape();
        shape.setAsBox((100.0f / PhysicsWorld.RATIO / 2.0f) * scale.x, (100.0f / PhysicsWorld.RATIO / 2.0f) * scale.y, new Vec2(offset.x / PhysicsWorld.RATIO, offset.y / PhysicsWorld.RATIO), rigidbody2D.getBody().getAngle());

        Fixture newFixture = rigidbody2D.getBody().createFixture(shape, fixture.getDensity());
        rigidbody2D.getBody().destroyFixture(fixture);

        fixture = newFixture;
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