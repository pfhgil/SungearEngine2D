package Core2D.Physics;

import Core2D.Scene2D.Scene2D;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

public class Rigidbody2D
{
    private BodyType bodyType = BodyType.STATIC;
    private float density = 1.0f;
    private float restitution = 0.0f;
    private float friction = 0.1f;
    private boolean isSensor = false;
    // само тело
    private transient Body body;

    private boolean noLinearVelocity;

    private transient Scene2D scene2D;

    public void set(Rigidbody2D rigidbody2D)
    {
        setType(rigidbody2D.getType());
        setDensity(rigidbody2D.getDensity());
        setRestitution(rigidbody2D.getRestitution());
        setFriction(rigidbody2D.getFriction());
        setSensor(rigidbody2D.isSensor());

        rigidbody2D = null;
    }

    public float getDensity() { return density; }
    public void setDensity(float density)
    {
        this.density = density;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setDensity(density);
        }
        /*
        body.destroyFixture(body.getFixtureList());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        body.createFixture(fixtureDef);

         */
    }

    public float getRestitution() { return restitution; }
    public void setRestitution(float restitution)
    {
        this.restitution = restitution;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setRestitution(restitution);
        }
        /*
        body.destroyFixture(body.getFixtureList());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = restitution;
        body.createFixture(fixtureDef);

         */
    }

    public float getFriction() { return friction; }
    public void setFriction(float friction)
    {
        this.friction = friction;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setFriction(friction);
        }
        /*
        body.destroyFixture(body.getFixtureList());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = friction;
        body.createFixture(fixtureDef);

         */
    }

    public boolean isSensor() { return isSensor; }
    public void setSensor(boolean sensor)
    {
        isSensor = sensor;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setSensor(sensor);
        }
    }

    public BodyType getType()
    {
        return bodyType;
    }
    public String typeToString()
    {
        if(body.getType() == BodyType.DYNAMIC) {
            return "Dynamic";
        } else if(body.getType() == BodyType.STATIC) {
            return "Static";
        } else if(body.getType() == BodyType.KINEMATIC) {
            return "Kinematic";
        }

        return "Unknown";
    }
    public void setType(BodyType bodyType)
    {
        this.bodyType = bodyType;
        if(this.body != null) {
            body.setType(this.bodyType);
        }

        bodyType = null;
    }

    public Body getBody() { return body; }
    public void setBody(Body body) { this.body = body; }

    public boolean isNoLinearVelocity() { return noLinearVelocity; }
    public void setNoLinearVelocity(boolean noLinearVelocity) { this.noLinearVelocity = noLinearVelocity; }

    public Scene2D getScene2D() { return scene2D; }
    public void setScene2D(Scene2D scene2D) { this.scene2D = scene2D; }
}
