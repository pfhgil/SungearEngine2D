package Core2D.Physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public interface UserContactListener
{
    void beginContact(Contact contact);
    void endContact(Contact contact);
    void preSolve(Contact contact, Manifold manifold);
    void postSolve(Contact contact, ContactImpulse contactImpulse);
}
