package Core2D.Physics;

import Core2D.Core2D.Core2D;
import Core2D.Log.Log;
import Core2D.Scene2D.Scene2D;
import org.jbox2d.dynamics.*;

public class Rigidbody2D
{
    //private transient Object2D attachedObject2D;


    private BodyDef bodyDef;
    // само тело
    private transient Body body;

    private boolean noLinearVelocity;

    private transient Scene2D scene2D;

    public Rigidbody2D()
    {
        if(Core2D.getSceneManager2D().getCurrentScene2D() != null) {
            bodyDef = new BodyDef();
            bodyDef.position.set(0.0f, 0.0f);
            bodyDef.type = BodyType.DYNAMIC;
            scene2D = Core2D.getSceneManager2D().getCurrentScene2D();
            body = scene2D.getPhysicsWorld().createBody(bodyDef);
        } else {
            Log.CurrentSession.println("Error while creating Rigidbody2D. Core2D.getSceneManager2D().getCurrentScene2D() == null");
        }
    }

    public void set(Rigidbody2D rigidbody2D)
    {
        if(Core2D.getSceneManager2D().getCurrentScene2D() != null) {
            bodyDef.type = rigidbody2D.getType();
            body.setType(rigidbody2D.getType());
        } else {
            Log.CurrentSession.println("Error while creating Rigidbody2D. Core2D.getSceneManager2D().getCurrentScene2D() == null");
        }
    }

    public void destroy()
    {
        //attachedObject2D = null;

        scene2D.getPhysicsWorld().destroyBody(body);
        bodyDef = null;
    }

    public BodyType getType()
    {
        return bodyDef.type;
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
        bodyDef.type = bodyType;
        body.setType(bodyType);
    }

    //public Object2D getAttachedObject2D() { return attachedObject2D; }
    //public void setAttachedObject2D(Object2D attachedObject2D) { this.attachedObject2D = attachedObject2D; }


    public BodyDef getBodyDef() { return bodyDef; }

    public Body getBody() { return body; }

    public boolean isNoLinearVelocity() { return noLinearVelocity; }
    public void setNoLinearVelocity(boolean noLinearVelocity) { this.noLinearVelocity = noLinearVelocity; }
}
