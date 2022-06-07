package Core2D.Physics;

import Core2D.Component.Components.BoxCollider2DComponent;
import Core2D.Component.Components.Rigidbody2DComponent;
import Core2D.Core2D.Core2D;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Physics.Collider2D.BoxCollider2D;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import java.util.List;

public class PhysicsWorld extends World
{
    private UserContactListener userContactListener;

    public static final float RATIO = 30.0f;

    public PhysicsWorld()
    {
        super(new Vec2(0.0f, -20.0f), false);

        Settings.velocityThreshold = 0.0f;

        setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact)
            {
                if(userContactListener != null) userContactListener.beginContact(contact);
            }

            @Override
            public void endContact(Contact contact)
            {
                if(userContactListener != null) userContactListener.endContact(contact);
            }

            @Override
            public void preSolve(Contact contact, Manifold manifold)
            {
                if(userContactListener != null) userContactListener.preSolve(contact, manifold);
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse)
            {
                if(userContactListener != null) userContactListener.postSolve(contact, contactImpulse);
            }
        });
    }

    public void addRigidbody2D(Object2D object2D)
    {
        Rigidbody2DComponent rigidbody2DComponent = object2D.getComponent(Rigidbody2DComponent.class);
        if(rigidbody2DComponent != null) {
            Rigidbody2D rigidbody2D = rigidbody2DComponent.getRigidbody2D();

            if(Core2D.getSceneManager2D().getCurrentScene2D() != null) {
                BodyDef bodyDef = new BodyDef();
                bodyDef.position.set(0.0f, 0.0f);
                bodyDef.type = rigidbody2D.getType();
                rigidbody2D.setScene2D(Core2D.getSceneManager2D().getCurrentScene2D());
                rigidbody2D.setBody(Core2D.getSceneManager2D().getCurrentScene2D().getPhysicsWorld().createBody(bodyDef));

                List<BoxCollider2DComponent> boxCollider2DComponentList = object2D.getAllComponents(BoxCollider2DComponent.class);

                for(BoxCollider2DComponent boxCollider2DComponent : boxCollider2DComponentList) {
                    addBoxCollider2D(rigidbody2D, boxCollider2DComponent.getBoxCollider2D());
                }
            } else {
                Log.CurrentSession.println("Error while creating Rigidbody2D. Core2D.getSceneManager2D().getCurrentScene2D() == null");
            }
        }
    }

    public void addBoxCollider2D(Rigidbody2D rigidbody2D, BoxCollider2D boxCollider2D)
    {
        Body body = rigidbody2D.getBody();
        if(body == null) {
            Log.CurrentSession.println("Can not add BoxCollider2D. body == null");

            return;
        }

        PolygonShape shape = new PolygonShape();
        Vector2f halfSize = new Vector2f((100.0f / RATIO / 2.0f) * boxCollider2D.getScale().x, (100.0f / RATIO / 2.0f) * boxCollider2D.getScale().y);
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(boxCollider2D.getOffset().x / PhysicsWorld.RATIO, boxCollider2D.getOffset().y / PhysicsWorld.RATIO), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = rigidbody2D.getDensity();
        fixtureDef.restitution = rigidbody2D.getRestitution();
        fixtureDef.friction = rigidbody2D.getFriction();
        fixtureDef.isSensor = rigidbody2D.isSensor();
        Fixture fixture = body.createFixture(fixtureDef);
        boxCollider2D.setFixture(fixture);
        boxCollider2D.setRigidbody2D(rigidbody2D);
    }

    public void destroyRigidbody2D(Object2D object2D)
    {
        Rigidbody2DComponent rigidbody2DComponent = object2D.getComponent(Rigidbody2DComponent.class);
        if(rigidbody2DComponent != null) {
            Rigidbody2D rigidbody2D = rigidbody2DComponent.getRigidbody2D();

            rigidbody2D.getScene2D().getPhysicsWorld().destroyBody(rigidbody2D.getBody());
            rigidbody2D = null;
        }
    }

    public UserContactListener getUserContactListener() { return userContactListener; }
    public void setUserContactListener(UserContactListener userContactListener) { this.userContactListener = userContactListener; }
}
