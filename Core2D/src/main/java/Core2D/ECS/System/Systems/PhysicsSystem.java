package Core2D.ECS.System.Systems;

import Core2D.Common.Interfaces.NonDuplicated;
import Core2D.Common.Interfaces.NonRemovable;
import Core2D.ECS.Component.Components.Physics.BoxCollider2DComponent;
import Core2D.ECS.Component.Components.Physics.CircleCollider2DComponent;
import Core2D.ECS.Component.Components.Physics.Collider2DComponent;
import Core2D.ECS.Component.Components.Physics.Rigidbody2DComponent;
import Core2D.ECS.Component.Components.Transform.TransformComponent;
import Core2D.ECS.ECSWorld;
import Core2D.ECS.Entity;
import Core2D.ECS.System.ComponentsQuery;
import Core2D.ECS.System.System;
import Core2D.Settings.PhysicsSettings;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.List;

public class PhysicsSystem extends System implements NonRemovable, NonDuplicated
{
    private final transient World world = new World(new Vec2(), false);

    public boolean simulatePhysics = true;

    public PhysicsSystem()
    {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact)
            {
                if(!simulatePhysics || !active) return;
                int size = ECSWorld.getCurrentECSWorld().getSystems().size();
                for(int i = 0; i < size; i++) {
                    System system = ECSWorld.getCurrentECSWorld().getSystems().get(i);
                    if(!system.active) continue;

                    Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
                    Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

                    if(entityA != null && entityB != null && entityA.active && entityB.active) {
                        system.beginContact(contact, entityA, entityB);
                    }
                }
            }

            @Override
            public void endContact(Contact contact)
            {
                if(!simulatePhysics || !active) return;
                int size = ECSWorld.getCurrentECSWorld().getSystems().size();
                for(int i = 0; i < size; i++) {
                    System system = ECSWorld.getCurrentECSWorld().getSystems().get(i);
                    if(!system.active) continue;

                    Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
                    Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

                    if(entityA != null && entityB != null && entityA.active && entityB.active) {
                        system.endContact(contact, entityA, entityB);
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold manifold)
            {
                if(!simulatePhysics || !active) return;
                int size = ECSWorld.getCurrentECSWorld().getSystems().size();
                for(int i = 0; i < size; i++) {
                    System system = ECSWorld.getCurrentECSWorld().getSystems().get(i);
                    if(!system.active) continue;

                    Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
                    Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

                    if(entityA != null && entityB != null && entityA.active && entityB.active) {
                        system.preSolve(contact, entityA, entityB, manifold);
                    }
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse)
            {
                if(!simulatePhysics || !active) return;
                int size = ECSWorld.getCurrentECSWorld().getSystems().size();
                for(int i = 0; i < size; i++) {
                    System system = ECSWorld.getCurrentECSWorld().getSystems().get(i);
                    if(!system.active) continue;

                    Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
                    Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

                    if(entityA != null && entityB != null && entityA.active && entityB.active) {
                        system.postSolve(contact, entityA, entityB, contactImpulse);
                    }
                }
            }
        });
    }

    @Override
    public void update(ComponentsQuery componentsQuery)
    {
        if(!active) return;

        Rigidbody2DComponent rigidbody2DComponent = componentsQuery.getComponent(Rigidbody2DComponent.class);

        updateRigidbody2DComponent(rigidbody2DComponent);

        List<BoxCollider2DComponent> boxCollider2DComponents = componentsQuery.getAllComponents(BoxCollider2DComponent.class);
        for(BoxCollider2DComponent boxCollider2DComponent : boxCollider2DComponents) {
            updateBoxCollider2DComponent(rigidbody2DComponent, boxCollider2DComponent);
        }

        List<CircleCollider2DComponent> circleCollider2DComponents = componentsQuery.getAllComponents(CircleCollider2DComponent.class);
        for(CircleCollider2DComponent circleCollider2DComponent : circleCollider2DComponents) {
            updateCircleCollider2DComponent(rigidbody2DComponent, circleCollider2DComponent);
        }
    }

    @Override
    public void deltaUpdate(ComponentsQuery componentsQuery, float deltaTime) {

    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        if(!simulatePhysics || !active) return;

        // setting settings
        world.setGravity(new Vec2(PhysicsSettings.gravity.x, PhysicsSettings.gravity.y));
        Settings.velocityThreshold = PhysicsSettings.velocityThreshold;

        // updating physics world
        world.step(deltaTime, PhysicsSettings.velocityIterations, PhysicsSettings.positionIterations);
    }

    public void addRigidbody2D(Rigidbody2DComponent rigidbody2DComponent)
    {
        if(rigidbody2DComponent == null || !active || !rigidbody2DComponent.active) return;

        BodyDef bodyDef = new BodyDef();

        TransformComponent transformComponent = rigidbody2DComponent.entity.getComponent(TransformComponent.class);
        if (transformComponent != null) {
            bodyDef.angle = Math.toRadians(transformComponent.rotation.z);
            bodyDef.position.set(new Vec2(transformComponent.position.x / PhysicsSettings.ratio, transformComponent.position.y / PhysicsSettings.ratio));
        }

        rigidbody2DComponent.body = world.createBody(bodyDef);
        rigidbody2DComponent.body.setUserData(rigidbody2DComponent.entity);

        rigidbody2DComponent.body.setFixedRotation(false);
    }

    public void updateRigidbody2DComponent(Rigidbody2DComponent rigidbody2DComponent)
    {
        if(rigidbody2DComponent == null || rigidbody2DComponent.body == null || !active || !rigidbody2DComponent.active) return;

        if (rigidbody2DComponent.bodyType != rigidbody2DComponent.lastBodyType) {
            rigidbody2DComponent.body.setType(rigidbody2DComponent.bodyType);
            rigidbody2DComponent.lastBodyType = rigidbody2DComponent.bodyType;
        }

        /*
        if (rigidbody2DComponent.density != rigidbody2DComponent.lastDensity) {
            rigidbody2DComponent.lastDensity = rigidbody2DComponent.density;
        }

        if (rigidbody2DComponent.restitution != rigidbody2DComponent.lastRestitution) {
            rigidbody2DComponent.lastRestitution = rigidbody2DComponent.restitution;
        }

        if (rigidbody2DComponent.friction != rigidbody2DComponent.lastFriction) {
            rigidbody2DComponent.lastFriction = rigidbody2DComponent.friction;
        }

        if (rigidbody2DComponent.sensor != rigidbody2DComponent.lastSensor) {
            rigidbody2DComponent.lastSensor = rigidbody2DComponent.sensor;
        }

         */

        if (rigidbody2DComponent.fixedRotation != rigidbody2DComponent.lastFixedRotation) {
            rigidbody2DComponent.body.setFixedRotation(rigidbody2DComponent.fixedRotation);

            rigidbody2DComponent.lastFixedRotation = rigidbody2DComponent.fixedRotation;
        }
    }

    public void updateBoxCollider2DComponent(Rigidbody2DComponent rigidbody2DComponent, BoxCollider2DComponent boxCollider2DComponent)
    {
        if(!active || !rigidbody2DComponent.active || !boxCollider2DComponent.active) return;

        updateFixture(rigidbody2DComponent, boxCollider2DComponent);
    }

    public void updateCircleCollider2DComponent(Rigidbody2DComponent rigidbody2DComponent, CircleCollider2DComponent circleCollider2DComponent)
    {
        if(!active || !rigidbody2DComponent.active || !circleCollider2DComponent.active) return;

        updateFixture(rigidbody2DComponent, circleCollider2DComponent);
    }

    public void updateFixture(Rigidbody2DComponent rigidbody2DComponent, Collider2DComponent collider2DComponent)
    {
        if(!active || !rigidbody2DComponent.active || !collider2DComponent.active) return;

        Body body = rigidbody2DComponent.body;
        if(body == null) return;

        FixtureDef fixtureDef = new FixtureDef();

        boolean baseColliderChanged = !collider2DComponent.lastOffset.equals(collider2DComponent.offset) || collider2DComponent.lastAngle != collider2DComponent.angle || collider2DComponent.fixture == null;
        boolean updated = false;

        if(collider2DComponent instanceof CircleCollider2DComponent circleCollider2DComponent) {
            if(circleCollider2DComponent.lastRadius != circleCollider2DComponent.radius || baseColliderChanged) {
                CircleShape shape = new CircleShape();
                shape.m_radius = circleCollider2DComponent.radius / PhysicsSettings.ratio;
                shape.m_p.set(circleCollider2DComponent.offset.x / PhysicsSettings.ratio, circleCollider2DComponent.offset.y / PhysicsSettings.ratio);

                circleCollider2DComponent.lastRadius = circleCollider2DComponent.radius;

                fixtureDef.shape = shape;

                updated = true;
            }
        } else if(collider2DComponent instanceof BoxCollider2DComponent boxCollider2DComponent) {
            if(!boxCollider2DComponent.lastScale.equals(boxCollider2DComponent.scale) || baseColliderChanged) {
                PolygonShape shape = new PolygonShape();
                Vector2f halfSize = new Vector2f((100.0f / PhysicsSettings.ratio / 2.0f) * boxCollider2DComponent.scale.x, (100.0f / PhysicsSettings.ratio / 2.0f) * boxCollider2DComponent.scale.y);
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(boxCollider2DComponent.offset.x / PhysicsSettings.ratio, boxCollider2DComponent.offset.y / PhysicsSettings.ratio),
                        Math.toRadians(boxCollider2DComponent.angle));

                boxCollider2DComponent.lastScale.set(boxCollider2DComponent.scale);

                fixtureDef.shape = shape;

                updated = true;
            }

            collider2DComponent.lastOffset.set(collider2DComponent.offset);
            collider2DComponent.lastAngle = collider2DComponent.angle;
        }

        if(updated) {
            if(collider2DComponent.fixture != null) {
                body.destroyFixture(collider2DComponent.fixture);
            }
            collider2DComponent.fixture = body.createFixture(fixtureDef);
        }

        if(collider2DComponent.fixture == null) return;

        float actualDensity = collider2DComponent.followRigidbody2DDensity ? rigidbody2DComponent.density : collider2DComponent.density;
        float actualRestitution = collider2DComponent.followRigidbody2DRestitution ? rigidbody2DComponent.restitution : collider2DComponent.restitution;
        float actualFriction = collider2DComponent.followRigidbody2DFriction ? rigidbody2DComponent.friction : collider2DComponent.friction;
        boolean actualSensor = collider2DComponent.followRigidbody2DSensor ? rigidbody2DComponent.sensor : collider2DComponent.sensor;

        if(collider2DComponent.lastDensity != actualDensity) {
            collider2DComponent.fixture.setDensity(actualDensity);

            rigidbody2DComponent.body.resetMassData();

            collider2DComponent.lastDensity = actualDensity;
        }

        if(collider2DComponent.lastRestitution != actualRestitution) {
            collider2DComponent.fixture.setRestitution(actualRestitution);

            collider2DComponent.lastRestitution = actualRestitution;
        }

        if(collider2DComponent.lastFriction != actualFriction) {
            collider2DComponent.fixture.setFriction(actualFriction);

            collider2DComponent.lastFriction = actualFriction;
        }

        if(collider2DComponent.lastSensor != actualSensor) {
            collider2DComponent.fixture.setSensor(actualSensor);

            collider2DComponent.lastSensor = actualSensor;
        }
    }

    public World getWorld() { return world; }
}
