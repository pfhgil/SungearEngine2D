package Core2D.Physics;

import Core2D.ECS.Component.Components.BoxCollider2DComponent;
import Core2D.ECS.Component.Components.CircleCollider2DComponent;
import Core2D.ECS.Component.Components.Rigidbody2DComponent;
import Core2D.ECS.Component.Components.ScriptComponent;
import Core2D.ECS.Entity;
import Core2D.ECS.System.System;
import Core2D.ECS.System.Systems.ScriptableSystem;
import Core2D.Log.Log;
import Core2D.Physics.Collider2D.BoxCollider2D;
import Core2D.Physics.Collider2D.CircleCollider2D;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import java.util.List;

public class PhysicsWorld extends World
{
    public static final float RATIO = 30.0f;

    public boolean simulatePhysics = false;

    private boolean shouldCollider2DEnter = false;
    private Entity entityAEnter;
    private Entity entityBEnter;

    private boolean shouldCollider2DExit = false;
    private Entity entityAExit;
    private Entity entityBExit;

    public PhysicsWorld()
    {
        super(new Vec2(0.0f, -20.0f), false);
        //this.setAutoClearForces(true);

        Settings.velocityThreshold = 0.0f;

        setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact)
            {
                Body aBody = contact.getFixtureA().getBody();
                Body bBody = contact.getFixtureB().getBody();

                if(aBody != null && bBody != null) {
                    Object userDataA = aBody.getUserData();
                    Object userDataB = bBody.getUserData();

                    if(userDataA instanceof Entity && userDataB instanceof Entity) {
                        shouldCollider2DEnter = true;
                        entityAEnter = (Entity) userDataA;
                        entityBEnter = (Entity) userDataB;
                    }
                }
            }

            @Override
            public void endContact(Contact contact)
            {
                Body aBody = contact.getFixtureA().getBody();
                Body bBody = contact.getFixtureB().getBody();

                if(aBody != null && bBody != null) {
                    Object userDataA = aBody.getUserData();
                    Object userDataB = bBody.getUserData();

                    if(userDataA instanceof Entity && userDataB instanceof Entity) {
                        shouldCollider2DExit = true;
                        entityAExit = (Entity) userDataA;
                        entityBExit = (Entity) userDataB;
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold manifold)
            {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse)
            {
            }
        });
    }

    @Override
    public void step(float deltaTime, int velocityIterations, int positionIterations)
    {
        if(simulatePhysics) {
            super.step(deltaTime, velocityIterations, positionIterations);

            if (shouldCollider2DEnter) {
                if (!entityAEnter.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponentsA = entityAEnter.getAllComponents(ScriptComponent.class);
                    List<ScriptableSystem> scriptableSystemsA = entityAEnter.getAllSystems(ScriptableSystem.class);

                    for (ScriptComponent scriptComponent : scriptComponentsA) {
                        scriptComponent.callMethod((params) -> scriptComponent.collider2DEnter(entityBEnter));
                    }
                    for (ScriptableSystem scriptableSystem : scriptableSystemsA) {
                        scriptableSystem.callMethod((params) -> scriptableSystem.collider2DEnter(entityBEnter));
                    }
                }

                if (!entityBEnter.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponentsB = entityBEnter.getAllComponents(ScriptComponent.class);
                    List<ScriptableSystem> scriptableSystemsB = entityBEnter.getAllSystems(ScriptableSystem.class);

                    for (ScriptComponent scriptComponent : scriptComponentsB) {
                        scriptComponent.callMethod((params) -> scriptComponent.collider2DEnter(entityAEnter));
                    }
                    for (ScriptableSystem scriptableSystem : scriptableSystemsB) {
                        scriptableSystem.callMethod((params) -> scriptableSystem.collider2DEnter(entityAEnter));
                    }
                }

                entityAEnter = null;
                entityBEnter = null;

                shouldCollider2DEnter = false;
            }

            if (shouldCollider2DExit) {
                if (!entityAExit.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponentsA = entityAExit.getAllComponents(ScriptComponent.class);
                    List<ScriptableSystem> scriptableSystemsB = entityAEnter.getAllSystems(ScriptableSystem.class);

                    for (ScriptComponent scriptComponent : scriptComponentsA) {
                        scriptComponent.callMethod((params) -> scriptComponent.collider2DExit(entityBExit));
                    }
                    for (ScriptableSystem scriptableSystem : scriptableSystemsB) {
                        scriptableSystem.callMethod((params) -> scriptableSystem.collider2DEnter(entityBExit));
                    }
                }

                if (!entityBExit.isShouldDestroy()) {
                    List<ScriptComponent> scriptComponentsB = entityBExit.getAllComponents(ScriptComponent.class);
                    List<ScriptableSystem> scriptableSystemsB = entityBEnter.getAllSystems(ScriptableSystem.class);

                    for (ScriptComponent scriptComponent : scriptComponentsB) {
                        scriptComponent.callMethod((params) -> scriptComponent.collider2DExit(entityAExit));
                    }
                    for (ScriptableSystem scriptableSystem : scriptableSystemsB) {
                        scriptableSystem.callMethod((params) -> scriptableSystem.collider2DEnter(entityAEnter));
                    }
                }

                entityAExit = null;
                entityBExit = null;

                shouldCollider2DExit = false;
            }
        }
    }

    public Rigidbody2D addRigidbody2D(Entity entity, Scene2D scene2D)
    {
        Rigidbody2DComponent rigidbody2DComponent = entity.getComponent(Rigidbody2DComponent.class);
        if(rigidbody2DComponent != null) {
            Rigidbody2D rigidbody2D = rigidbody2DComponent.getRigidbody2D();

            BodyDef bodyDef = new BodyDef();

            if (scene2D != null) {
                rigidbody2D.setScene2D(scene2D);
            }
            rigidbody2D.setBody(createBody(bodyDef));
            rigidbody2D.set(rigidbody2D);
            rigidbody2D.getBody().setUserData(entity);

            List<BoxCollider2DComponent> boxCollider2DComponentList = entity.getAllComponents(BoxCollider2DComponent.class);
            List<CircleCollider2DComponent> circleCollider2DComponents = entity.getAllComponents(CircleCollider2DComponent.class);

            for (BoxCollider2DComponent boxCollider2DComponent : boxCollider2DComponentList) {
                addBoxCollider2D(rigidbody2D, boxCollider2DComponent.getBoxCollider2D());
            }
            for (CircleCollider2DComponent circleCollider2DComponent : circleCollider2DComponents) {
                addCircleCollider2D(rigidbody2D, circleCollider2DComponent.getCircleCollider2D());
            }

            return rigidbody2D;
        }

        return null;
    }

    public Rigidbody2D addRigidbody2D(Entity entity)
    {
        return addRigidbody2D(entity, SceneManager.currentSceneManager.getCurrentScene2D());
    }

    public void addBoxCollider2D(Rigidbody2D rigidbody2D, BoxCollider2D boxCollider2D)
    {
        Body body = rigidbody2D.getBody();
        if(body == null) {
            Log.CurrentSession.println("Can not add BoxCollider2D. body == null", Log.MessageType.ERROR);
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

    public void addCircleCollider2D(Rigidbody2D rigidbody2D, CircleCollider2D circleCollider2D)
    {
        Body body = rigidbody2D.getBody();
        if(body == null) {
            Log.CurrentSession.println("Can not add CircleCollider2D. body == null", Log.MessageType.ERROR);
            return;
        }

        CircleShape shape = new CircleShape();
        shape.m_radius = circleCollider2D.getRadius() / PhysicsWorld.RATIO;
        shape.m_p.set(circleCollider2D.getOffset().x / PhysicsWorld.RATIO, circleCollider2D.getOffset().y / PhysicsWorld.RATIO);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = rigidbody2D.getDensity();
        fixtureDef.restitution = rigidbody2D.getRestitution();
        fixtureDef.friction = rigidbody2D.getFriction();
        fixtureDef.isSensor = rigidbody2D.isSensor();
        Fixture fixture = body.createFixture(fixtureDef);
        circleCollider2D.setFixture(fixture);
        circleCollider2D.setRigidbody2D(rigidbody2D);
    }
}
