package Core2D.Physics.Collider2D;

import Core2D.Component.Components.TransformComponent;
import Core2D.Graphics.Graphics;
import Core2D.Object2D.Object2D;
import Core2D.Physics.PhysicsWorld;
import Core2D.Physics.Rigidbody2D;
import Core2D.Primitives.Line2D;
import Core2D.Utils.MathUtils;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CircleCollider2D
{
    private transient Rigidbody2D rigidbody2D;
    private transient Fixture fixture;

    private Vector2f offset = new Vector2f(0.0f, 0.0f);
    private float radius = 50.0f;

    private transient Line2D[] debugLines = new Line2D[21];
    private int increment = 360 / (debugLines.length - 1);

    public CircleCollider2D()
    {
        for(int i = 0; i < debugLines.length; i++) {
            debugLines[i] = new Line2D(new Vector2f(), new Vector2f());
            debugLines[i].setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
            debugLines[i].setLineWidth(4.0f);
        }
    }

    public void draw(Object2D object2D)
    {
        for (Line2D line2D : debugLines) {
            Graphics.getMainRenderer().render(line2D);
        }

        int currentAngle = 0;

        TransformComponent transformComponent = object2D.getComponent(TransformComponent.class);
        Vector3f rotatedOffset = new Vector3f(offset.x, offset.y, 0.0f);
        rotatedOffset.rotateZ((float) Math.toRadians(transformComponent.getTransform().getRotation()));
        Vector2f center = new Vector2f(transformComponent.getTransform().getPosition())
                .add(transformComponent.getTransform().getCentre())
                .add(new Vector2f(rotatedOffset.x, rotatedOffset.y));

        Vector2f firstPoint = new Vector2f(center);
        Vector2f currentPoint = new Vector2f();
        Vector2f lastPoint = new Vector2f();

        Vector2f tmp0 = new Vector2f(0, radius);
        MathUtils.rotate(tmp0, currentAngle, new Vector2f());
        lastPoint.set(tmp0.add(center));

        currentAngle += increment;

        tmp0 = new Vector2f(0, radius);
        MathUtils.rotate(tmp0, currentAngle, new Vector2f());
        currentPoint.set(tmp0.add(center));

        debugLines[0].setStart(new Vector2f(lastPoint));
        debugLines[0].setEnd(new Vector2f(currentPoint));

        currentAngle += increment;

        for(int i = 1; i < debugLines.length - 1; i++) {
            Vector2f tmp = new Vector2f(0, radius);
            MathUtils.rotate(tmp, currentAngle, new Vector2f());
            lastPoint.set(currentPoint);
            currentPoint.set(tmp.add(center));

            debugLines[i].setStart(new Vector2f(lastPoint));
            debugLines[i].setEnd(new Vector2f(currentPoint));

            currentAngle += increment;
        }

        debugLines[debugLines.length - 1].setStart(currentPoint);
        debugLines[debugLines.length - 1].setEnd(firstPoint);
    }

    public void destroy()
    {
        offset = null;

        for(int i = 0; i < debugLines.length; i++) {
            debugLines[i].destroy();
            debugLines[i] = null;
        }
        debugLines = null;

        rigidbody2D = null;
        fixture = null;
    }

    public void set(CircleCollider2D circleCollider2D)
    {
        setOffset(circleCollider2D.getOffset());
        setRadius(circleCollider2D.getRadius());

        circleCollider2D = null;
    }

    private void updateShape()
    {
        if(fixture != null && rigidbody2D != null) {
            CircleShape shape = (CircleShape) fixture.getShape();
            shape.m_radius = radius / PhysicsWorld.RATIO;
            shape.m_p.set(offset.x / PhysicsWorld.RATIO, offset.y / PhysicsWorld.RATIO);

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