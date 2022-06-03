package Core2D.Object2D;

import Core2D.Core2D.Settings;
import Core2D.Physics.Collider2D.BoxCollider2D;
import Core2D.Physics.Collider2D.Collider2D;
import Core2D.Physics.Physics;
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

    // прикрепленный коллайдер
    private transient Collider2D collider2D;

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

    public Transform(Collider2D collider2D)
    {
        this.collider2D = collider2D;

        init();

        collider2D = null;
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

        if(collider2D != null) {
            if(Settings.Debug.ENABLE_DEBUG_PHYSICS_DRAWING) collider2D.draw();

            setPositionOfBody();
            setRotationOfBody();
        }
    }

    public void translate(Vector2f translation)
    {
        position = position.add(translation);

        translationMatrix.identity();
        translationMatrix.translate(position.x, position.y, 0.0f);

        if(collider2D != null) {
            Vec2 newPosition = new Vec2(collider2D.getBody().getTransform().position).add(new Vec2(translation.x / Physics.RATIO, translation.y / Physics.RATIO));
            collider2D.getBody().setTransform(newPosition, collider2D.getBody().getAngle());
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

        if(collider2D != null) {
            float newAngle = (float) (Math.toDegrees(collider2D.getBody().getAngle()) + rotation);
            collider2D.getBody().setTransform(collider2D.getBody().getTransform().position, (float) Math.toRadians(newAngle));
        }

        updateModelMatrix();
    }

    public void scale(Vector2f scale)
    {
        this.scale = this.scale.add(scale);

        scaleMatrix.identity();
        scaleMatrix.scale(this.scale.x, this.scale.y, 1.0f);

        centre.x = (100.0f * this.scale.x) / 2.0f;
        centre.y = (100.0f * this.scale.y) / 2.0f;

        if(collider2D != null) {
            if(collider2D instanceof BoxCollider2D) {
                ((BoxCollider2D) collider2D).scale(scale);
            }
        }

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
        if(collider2D != null) {
            collider2D.getBody().applyLinearImpulse(new Vec2(impulse.x / Physics.RATIO, impulse.y / Physics.RATIO), new Vec2(pos.x / Physics.RATIO, pos.y / Physics.RATIO));
        }

        impulse = null;
        pos = null;
    }

    public void applyAngularImpulse(float impulse)
    {
        if(collider2D != null) {
            collider2D.getBody().applyAngularImpulse(impulse / Physics.RATIO);
        }
    }

    public void applyForce(Vector2f force, Vector2f pos)
    {
        if(collider2D != null) {
            collider2D.getBody().applyForce(new Vec2(force.x / Physics.RATIO, force.y / Physics.RATIO), new Vec2(pos.x / Physics.RATIO, pos.y / Physics.RATIO));
        }

        force = null;
        pos = null;
    }

    public void applyTorque(float torque)
    {
        if(collider2D != null) {
            collider2D.getBody().applyTorque(torque / Physics.RATIO);
        }
    }

    public void setLinearVelocity(Vector2f impulse)
    {
        if(collider2D != null) {
            collider2D.getBody().setLinearVelocity(new Vec2(impulse.x / Physics.RATIO, impulse.y / Physics.RATIO));
        }

        impulse = null;
    }

    // обновление матрицы модели (в шейдере тоже)
    private void updateModelMatrix()
    {
        modelMatrix = new Matrix4f(translationMatrix).mul(rotationMatrix).mul(scaleMatrix);
    }

    private void updateCollider2D()
    {
        if(collider2D != null) {
            collider2D.getBody().setTransform(new Vec2((position.x + centre.x) / Physics.RATIO, (position.y + centre.y) / Physics.RATIO), (float) Math.toRadians(rotation));
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
        if(collider2D != null) collider2D.destroy();
        collider2D = null;

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

        updateCollider2D();
        updateModelMatrix();
    }
    private void setPositionOfBody()
    {
        Vec2 bodyPos = collider2D.getBody().getTransform().position.mul(Physics.RATIO);
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

        updateCollider2D();
        updateModelMatrix();
    }
    private void setRotationOfBody()
    {
        this.rotation = (float) Math.toDegrees(collider2D.getBody().getTransform().getAngle());

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

        if(collider2D != null) {
            if(collider2D instanceof BoxCollider2D) {
                ((BoxCollider2D) collider2D).setScale(scale);
            }
        }

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

    public Collider2D getCollider2D()
    {
        return collider2D;
    }
    public void setCollider2D(Collider2D collider2D)
    {
        this.collider2D = collider2D;

        updateCollider2D();
        setScale(scale);

        collider2D = null;
    }

    public TransformCallback getTransformCallback() {return transformCallback; }
    public void setTransformCallback(TransformCallback transformCallback) { this.transformCallback = transformCallback; }

    public Vector2f getDestinationInfelicity() { return destinationInfelicity; }
    public void setDestinationInfelicity(Vector2f destinationInfelicity) { this.destinationInfelicity = destinationInfelicity; }
}
