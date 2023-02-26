package Core2D.Physics;

import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.WeldJoint;

public class Rigidbody2D
{
    private BodyType bodyType = BodyType.DYNAMIC;
    private float density = 1.0f;
    private float restitution = 0.0f;
    private float friction = 0.1f;
    private boolean isSensor = false;
    private boolean isFixedRotation = false;

    // само тело
    private transient Body body = new Body(new BodyDef(), new PhysicsWorld());

    private transient Scene2D scene2D;

    public void set(Rigidbody2D rigidbody2D)
    {
        setType(rigidbody2D.getType());
        setDensity(rigidbody2D.getDensity());
        setRestitution(rigidbody2D.getRestitution());
        setFriction(rigidbody2D.getFriction());
        setSensor(rigidbody2D.isSensor());
        setFixedRotation(rigidbody2D.isFixedRotation());
    }

    public void destroy()
    {
        SceneManager.currentSceneManager.getTmpPhysicsWorld().destroyBody(body);
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null && SceneManager.currentSceneManager.getCurrentScene2D().getPhysicsWorld() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().getPhysicsWorld().destroyBody(body);
        }
    }

    public float getDensity() { return density; }
    public void setDensity(float density)
    {
        this.density = density;

        for(Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
            f.setDensity(density);
        }

        body.resetMassData();
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

    public BodyType getType()
    {
        return bodyType;
    }
    public String typeToString()
    {
        return switch(body.getType()) {
            case DYNAMIC -> "Dynamic";
            case STATIC -> "Static";
            case KINEMATIC -> "Kinematic";
        };
    }
    public void setType(BodyType bodyType)
    {
        this.bodyType = bodyType;
        if(this.body != null) {
            body.setType(this.bodyType);
        }
    }

    public Body getBody() { return body; }
    public void setBody(Body body) { this.body = body; }

    public Scene2D getScene2D() { return scene2D; }
    public void setScene2D(Scene2D scene2D) { this.scene2D = scene2D; }
}
