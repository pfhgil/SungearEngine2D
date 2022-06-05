package Core2D.Physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

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

    public UserContactListener getUserContactListener() { return userContactListener; }
    public void setUserContactListener(UserContactListener userContactListener) { this.userContactListener = userContactListener; }
}
