package Core2D.Object2D;

import Core2D.Physics.PhysicsWorld;
import Core2D.Physics.Rigidbody2D;
import org.jbox2d.common.Vec2;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import java.io.Serializable;

// трансформации объекта
public class Transform implements Serializable
{
    // позиция
    private Vector2f position = new Vector2f();
    // поворот (по оси z)
    private float rotation = 0.0f;
    // масштаб
    private Vector2f scale = new Vector2f(1.0f, 1.0f);

    // цент объекта (относительно позиции объекта)
    private transient Vector2f centre = new Vector2f();

    // матрица перемещения объекта
    private transient Matrix4f translationMatrix = new Matrix4f();
    // матрица поворота объекта
    private transient Matrix4f rotationMatrix = new Matrix4f();
    // матрица масштаба объекта
    private transient Matrix4f scaleMatrix = new Matrix4f();

    // матрица модели объекта
    private transient Matrix4f modelMatrix = new Matrix4f();

    // позиция куда нужно передвинуть плавно объект
    private transient Vector2f destinationPosition = new Vector2f();
    // множитель скорости, с которой будет передвигать объект в позицию moveToPosition
    private transient Vector2f moveToDestinationSpeedCoeff = new Vector2f();
    // нужно ли передвигать объект к позиции
    private transient boolean needToMoveToDestination = false;

    // погрешность при достижении цели
    private transient Vector2f destinationInfelicity = new Vector2f(0.01f, 0.01f);

    // прикрепленный rigibody2d
    private transient Rigidbody2D rigidbody2D;

    // колбэк
    private transient TransformCallback transformCallback = null;

    public Transform()
    {
        init();
    }

    public Transform(Transform transform)
    {
        position = new Vector2f(transform.getPosition());
        rotation = transform.getRotation();
        scale = new Vector2f(transform.getScale());

        centre = new Vector2f(transform.getCentre());

        translationMatrix = new Matrix4f(transform.getTranslationMatrix());
        rotationMatrix = new Matrix4f(transform.getRotationMatrix());
        scaleMatrix = new Matrix4f(transform.getScaleMatrix());

        modelMatrix = new Matrix4f(transform.getModelMatrix());

        init();

        transform = null;
    }

    public Transform(Rigidbody2D rigidbody2D)
    {
        this.rigidbody2D = rigidbody2D;

        init();

        rigidbody2D = null;
    }

    public void init()
    {
        setScale(scale);
        setRotation(rotation);
        setPosition(position);
    }

    public void update(float deltaTime)
    {
        if(needToMoveToDestination) {
            Vector2f dif = new Vector2f(destinationPosition.x - position.x, destinationPosition.y - position.y).mul(moveToDestinationSpeedCoeff).mul(deltaTime);

            // 0.1f - погрешность, чтобы объект всегда достигал цели
            if(Math.abs(dif.x) > destinationInfelicity.x || Math.abs(dif.y) > destinationInfelicity.y) {
                translate(new Vector2f(dif));
            } else {
                needToMoveToDestination = false;
                if(transformCallback != null) {
                    transformCallback.onDestinationReached();
                }
            }
        }

        if(rigidbody2D != null) {
            //if(Settings.Debug.ENABLE_DEBUG_PHYSICS_DRAWING) rigidbody2D.draw();

            setPositionOfBody();
            setRotationOfBody();
        }
    }

    public void translate(Vector2f translation)
    {
        position = position.add(translation);

        translationMatrix.identity();
        translationMatrix.translate(position.x, position.y, 0.0f);

        if(rigidbody2D != null) {
            Vec2 newPosition = new Vec2(rigidbody2D.getBody().getTransform().position).add(new Vec2(translation.x / PhysicsWorld.RATIO, translation.y / PhysicsWorld.RATIO));
            rigidbody2D.getBody().setTransform(newPosition, rigidbody2D.getBody().getAngle());
        }

        translation = null;

        updateModelMatrix();
    }

    public void rotate(float rotation)
    {
        this.rotation += rotation;

        rotationMatrix.identity();
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateX((float) Math.toRadians(0));
        rotationQ.rotateY((float) Math.toRadians(0));
        rotationQ.rotateZ((float) Math.toRadians(this.rotation));

        rotationMatrix.rotateAround(rotationQ, centre.x, centre.y, 0.0f);

        if(rigidbody2D != null) {
            float newAngle = (float) (Math.toDegrees(rigidbody2D.getBody().getAngle()) + rotation);
            rigidbody2D.getBody().setTransform(rigidbody2D.getBody().getTransform().position, (float) Math.toRadians(newAngle));
        }

        updateModelMatrix();
    }

    public void lookAt(Vector2f target)
    {
        float dx = target.x - position.x;
        float dy = target.y - position.y;
        float angle = (float) (Math.atan2(dy, dx) * 180.0f / Math.PI);

        setRotation(angle);
    }

    public void scale(Vector2f scale)
    {
        this.scale = this.scale.add(scale);

        scaleMatrix.identity();
        scaleMatrix.scale(this.scale.x, this.scale.y, 1.0f);

        centre.x = (100.0f * this.scale.x) / 2.0f;
        centre.y = (100.0f * this.scale.y) / 2.0f;

        /*
        if(rigidbody2D != null) {
            if(rigidbody2D instanceof BoxCollider2D) {
                ((BoxCollider2D) rigidbody2D).scale(scale);
            }
        }

         */

        scale = null;

        updateModelMatrix();
    }

    public void moveTo(Vector2f toPosition, Vector2f coeff)
    {
        destinationPosition = new Vector2f(toPosition);
        moveToDestinationSpeedCoeff = new Vector2f(coeff);

        toPosition = null;
        coeff = null;

        needToMoveToDestination = true;
    }

    public void applyLinearImpulse(Vector2f impulse, Vector2f pos)
    {
        if(rigidbody2D != null) {
            rigidbody2D.getBody().applyLinearImpulse(new Vec2(impulse.x / PhysicsWorld.RATIO, impulse.y / PhysicsWorld.RATIO), new Vec2(pos.x / PhysicsWorld.RATIO, pos.y / PhysicsWorld.RATIO));
        }

        impulse = null;
        pos = null;
    }

    public void applyAngularImpulse(float impulse)
    {
        if(rigidbody2D != null) {
            rigidbody2D.getBody().applyAngularImpulse(impulse / PhysicsWorld.RATIO);
        }
    }

    public void applyForce(Vector2f force, Vector2f pos)
    {
        if(rigidbody2D != null) {
            rigidbody2D.getBody().applyForce(new Vec2(force.x / PhysicsWorld.RATIO, force.y / PhysicsWorld.RATIO), new Vec2(pos.x / PhysicsWorld.RATIO, pos.y / PhysicsWorld.RATIO));
        }

        force = null;
        pos = null;
    }

    public void applyTorque(float torque)
    {
        if(rigidbody2D != null) {
            rigidbody2D.getBody().applyTorque(torque / PhysicsWorld.RATIO);
        }
    }

    public void setLinearVelocity(Vector2f impulse)
    {
        if(rigidbody2D != null) {
            rigidbody2D.getBody().setLinearVelocity(new Vec2(impulse.x / PhysicsWorld.RATIO, impulse.y / PhysicsWorld.RATIO));
        }

        impulse = null;
    }

    // обновление матрицы модели (в шейдере тоже)
    private void updateModelMatrix()
    {
        modelMatrix = new Matrix4f(translationMatrix).mul(rotationMatrix).mul(scaleMatrix);
    }

    private void updateRigidbody2D()
    {
        if(rigidbody2D != null) {
            rigidbody2D.getBody().setTransform(new Vec2((position.x + centre.x) / PhysicsWorld.RATIO, (position.y + centre.y) / PhysicsWorld.RATIO), (float) Math.toRadians(rotation));
        }
    }

    public void set(Transform transform)
    {
        position = new Vector2f(transform.getPosition());
        rotation = transform.getRotation();
        scale = new Vector2f(transform.getScale());

        centre = new Vector2f(transform.getCentre());

        translationMatrix = new Matrix4f(transform.getTranslationMatrix());
        rotationMatrix = new Matrix4f(transform.getRotationMatrix());
        scaleMatrix = new Matrix4f(transform.getScaleMatrix());

        modelMatrix = new Matrix4f(transform.getModelMatrix());

        init();

        transform = null;
    }

    public void destroy()
    {
        rigidbody2D = null;

        position = null;
        scale = null;

        centre = null;

        translationMatrix = null;
        rotationMatrix = null;
        scaleMatrix = null;

        modelMatrix = null;

        destinationPosition = null;
        moveToDestinationSpeedCoeff = null;
        destinationInfelicity = null;

        transformCallback = null;
    }

    public Vector2f getPosition() { return position; }
    public void setPosition(Vector2f position)
    {
        this.position = new Vector2f(position);

        translationMatrix.identity();
        translationMatrix.translate(position.x, position.y, 0.0f);

        position = null;

        updateRigidbody2D();
        updateModelMatrix();
    }
    private void setPositionOfBody()
    {
        Vec2 bodyPos = rigidbody2D.getBody().getTransform().position.mul(PhysicsWorld.RATIO);
        this.position = new Vector2f(bodyPos.x - centre.x, bodyPos.y - centre.y);

        translationMatrix.identity();
        translationMatrix.translate(position.x, position.y, 0.0f);

        updateModelMatrix();
    }

    public float getRotation() { return rotation; }
    public void setRotation(float angle)
    {
        this.rotation = angle;

        rotationMatrix.identity();
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateLocalX((float) Math.toRadians(0));
        rotationQ.rotateLocalY((float) Math.toRadians(0));
        rotationQ.rotateLocalZ((float) Math.toRadians(this.rotation));

        rotationMatrix.rotateAroundLocal(rotationQ, centre.x, centre.y, 0.0f);

        updateRigidbody2D();
        updateModelMatrix();
    }
    private void setRotationOfBody()
    {
        this.rotation = (float) Math.toDegrees(rigidbody2D.getBody().getTransform().getAngle());

        rotationMatrix.identity();
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateLocalX((float) Math.toRadians(0));
        rotationQ.rotateLocalY((float) Math.toRadians(0));
        rotationQ.rotateLocalZ((float) Math.toRadians(this.rotation));

        rotationMatrix.rotateAroundLocal(rotationQ, centre.x, centre.y, 0.0f);

        updateModelMatrix();
    }

    public Vector2f getScale() { return scale; }
    public void setScale(Vector2f scale)
    {
        this.scale = new Vector2f(scale);

        scaleMatrix.identity();
        scaleMatrix.scale(this.scale.x, this.scale.y, 1.0f);

        centre.x = (100.0f * this.scale.x) / 2.0f;
        centre.y = (100.0f * this.scale.y) / 2.0f;

        /*
        if(rigidbody2D != null) {
            if(collider2D instanceof BoxCollider2D) {
                ((BoxCollider2D) collider2D).setScale(scale);
            }
        }

         */

        scale = null;

        updateModelMatrix();
    }

    public Vector2f getCentre() { return centre; }
    public void setCentre(Vector2f centre)
    {
        this.centre = new Vector2f(centre);

        centre = null;
    }

    public Matrix4f getTranslationMatrix() { return translationMatrix; }

    public Matrix4f getRotationMatrix() { return rotationMatrix; }

    public Matrix4f getScaleMatrix() { return scaleMatrix; }

    public Matrix4f getModelMatrix() { return modelMatrix; }

    public Rigidbody2D getRigidbody2D()
    {
        return rigidbody2D;
    }
    public void setRigidbody2D(Rigidbody2D rigidbody2D)
    {
        this.rigidbody2D = rigidbody2D;

        updateRigidbody2D();
        setScale(scale);

        rigidbody2D = null;
    }

    public TransformCallback getTransformCallback() {return transformCallback; }
    public void setTransformCallback(TransformCallback transformCallback) { this.transformCallback = transformCallback; }

    public Vector2f getDestinationInfelicity() { return destinationInfelicity; }
    public void setDestinationInfelicity(Vector2f destinationInfelicity) { this.destinationInfelicity = destinationInfelicity; }
}
