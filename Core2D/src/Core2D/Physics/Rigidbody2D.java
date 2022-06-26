package Core2D.Physics;

import Core2D.Scene2D.Scene2D;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

public class Rigidbody2D
{
    private BodyType bodyType = BodyType.DYNAMIC;
    private float density = 1.0f;
    private float restitution = 0.0f;
    private float friction = 0.1f;
    private boolean isSensor = false;
    private boolean isFixedRotation = false;

    private float mass;
    // само тело
    private transient Body body;

    private transient Scene2D scene2D;

    public void set(Rigidbody2D rigidbody2D)
    {
        this.bodyType = null;

        setType(rigidbody2D.getType());
        setDensity(rigidbody2D.getDensity());
        setRestitution(rigidbody2D.getRestitution());
        setFriction(rigidbody2D.getFriction());
        setSensor(rigidbody2D.isSensor());
        setMass(rigidbody2D.getMass());
        setFixedRotation(rigidbody2D.isFixedRotation());

        rigidbody2D = null;
    }

    public void destroy()
    {
        body = null;
        scene2D = null;
    }

    public float getDensity() { return density; }
    public void setDensity(float density)
    {
        this.density = density;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setDensity(density);
        }
    }

    public float getRestitution() { return restitution; }
    public void setRestitution(float restitution)
    {
        this.restitution = restitution;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setRestitution(restitution);
        }
    }

    public float getFriction() { return friction; }
    public void setFriction(float friction)
    {
        this.friction = friction;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setFriction(friction);
        }
    }

    public boolean isSensor() { return isSensor; }
    public void setSensor(boolean sensor)
    {
        isSensor = sensor;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setSensor(sensor);
        }
    }

    public boolean isFixedRotation() { return isFixedRotation; }
    public void setFixedRotation(boolean isFixedRotation)
    {
        this.isFixedRotation = isFixedRotation;

        if(body != null) {
            body.setFixedRotation(isFixedRotation);
        }
    }

    public float getMass() { return mass; }
    public void setMass(float mass)
    {
        this.mass = mass;

        if(body != null) {
            MassData massData = new MassData();
            body.getMassData(massData);
            massData.mass = mass;
            body.setMassData(massData);
            massData = null;
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

    public Scene2D getScene2D() { return scene2D; }
    public void setScene2D(Scene2D scene2D) { this.scene2D = scene2D; }
}
