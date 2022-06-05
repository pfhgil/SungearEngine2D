package Core2D.Physics.Collider2D;

import Core2D.Component.Components.TransformComponent;
import Core2D.Object2D.Object2D;
import Core2D.Object2D.Transform;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL32;

public abstract class Collider2D
{
    protected Object2D attachedObject2D;

    protected Shape shape;

    protected FixtureDef fixtureDef;
    protected Fixture fixture;

    // само тело
    protected Body body;

    protected boolean noLinearVelocity;

    // коробка из линий, которая рисуется дл наглядности физики
    protected Object2D drawingBoundingBox;

    public Collider2D(Object2D attachedObject2D)
    {
        this.attachedObject2D = attachedObject2D;
        attachedObject2D = null;
    }

    protected void create()
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(0.0f, 0.0f);
        bodyDef.type = BodyType.DYNAMIC;
        //body = Core2D.getPhysics().getPhysicsWorld().createBody(bodyDef);

        fixtureDef = new FixtureDef();
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.shape = shape;

        if(body != null) fixture = body.createFixture(fixtureDef);

        drawingBoundingBox = new Object2D();

        //drawingBoundingBox.getTransform().SetPosition(new Vector2f(0.0f, 0.0f));
        drawingBoundingBox.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        drawingBoundingBox.setDrawingMode(GL32.GL_LINE_LOOP);
    }

    protected void create(Collider2D collider2D)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(0.0f, 0.0f);
        bodyDef.type = collider2D.getBody().getType();

        //body = Core2D.getPhysics().getPhysicsWorld().createBody(bodyDef);

        body.setTransform(collider2D.getBody().getPosition(), collider2D.getBody().getAngle());
        body.setLinearVelocity(collider2D.getBody().getLinearVelocity());

        MassData massData = new MassData();
        collider2D.getBody().getMassData(massData);
        body.setMassData(massData);

        body.setAngularDamping(collider2D.getBody().getAngularDamping());
        body.setAngularVelocity(collider2D.getBody().getAngularVelocity());

        body.setActive(collider2D.getBody().isActive());
        body.setFixedRotation(collider2D.getBody().isFixedRotation());
        body.setAwake(collider2D.getBody().isAwake());
        body.setBullet(collider2D.getBody().isBullet());
        body.setSleepingAllowed(collider2D.getBody().isSleepingAllowed());

        fixtureDef = collider2D.getFixtureDef();
        fixtureDef.shape = shape;

        if(body != null) fixture = body.createFixture(fixtureDef);

        drawingBoundingBox = new Object2D(collider2D.getDrawingBoundingBox());

        collider2D = null;
    }

    public void draw()
    {
        Transform boundingBoxTransform = drawingBoundingBox.getComponent(TransformComponent.class).getTransform();
        Transform attachedObjectTransform = attachedObject2D.getComponent(TransformComponent.class).getTransform();

        boundingBoxTransform.setPosition(new Vector2f(attachedObjectTransform.getPosition()));
        boundingBoxTransform.setRotation(attachedObjectTransform.getRotation());
        boundingBoxTransform.setScale(new Vector2f(attachedObjectTransform.getScale()));

        drawingBoundingBox.draw();
    }

    public void destroy()
    {
        this.attachedObject2D = null;

        this.shape = null;
        this.fixtureDef = null;
        this.fixture.destroy();
        this.fixture = null;

        this.drawingBoundingBox.destroy();
        this.drawingBoundingBox = null;

        //Core2D.getPhysics().getPhysicsWorld().destroyBody(body);

        this.body = null;
    }

    public Object2D getAttachedObject2D() { return attachedObject2D; }
    public void setAttachedObject2D(Object2D attachedObject2D)
    {
        this.attachedObject2D = attachedObject2D;
        attachedObject2D = null;
    }

    public Body getBody() { return body; }

    public void setShape(Shape shape)
    {
        this.shape = shape;
        shape = null;
    }

    public FixtureDef getFixtureDef() { return fixtureDef; }
    public void setRestitution(float restitution)
    {
        body.destroyFixture(body.getFixtureList());
        fixtureDef.restitution = restitution;
        body.createFixture(fixtureDef);
    }
    public void setDensity(float density)
    {
        body.destroyFixture(body.getFixtureList());
        fixtureDef.density = density;
        body.createFixture(fixtureDef);
    }
    public void setFriction(float friction)
    {
        body.destroyFixture(body.getFixtureList());
        fixtureDef.friction = friction;
        body.createFixture(fixtureDef);
    }
    public void setSensor(boolean sensor)
    {
        body.destroyFixture(body.getFixtureList());
        fixtureDef.isSensor = sensor;
        body.createFixture(fixtureDef);
    }

    public boolean isNoLinearVelocity() { return noLinearVelocity; }
    public void setNoLinearVelocity(boolean noLinearVelocity) { this.noLinearVelocity = noLinearVelocity; }

    public Object2D getDrawingBoundingBox() { return drawingBoundingBox; }
}
