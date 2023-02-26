package Core2D.Transform;

import Core2D.Physics.PhysicsWorld;
import Core2D.Physics.Rigidbody2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MatrixUtils;
import org.jbox2d.common.Vec2;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

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
    private Vector2f centre = new Vector2f();

    // матрица перемещения объекта
    private transient Matrix4f translationMatrix = new Matrix4f();
    // матрица поворота объекта
    private transient Matrix4f rotationMatrix = new Matrix4f();
    // матрица масштаба объекта
    private transient Matrix4f scaleMatrix = new Matrix4f();

    // матрица модели объекта
    private transient Matrix4f localModelMatrix = new Matrix4f();

    // результативная матрица модели объекта
    private transient Matrix4f globalModelMatrix = new Matrix4f();

    // кастомная матрица
    private transient Matrix4f customMatrix = new Matrix4f();

    // позиция, куда нужно передвинуть плавно объект
    private transient Vector2f destinationPosition = new Vector2f();
    // множитель скорости, с которой будет передвигать объект в позицию moveToPosition
    private transient Vector2f moveToDestinationSpeedCoeff = new Vector2f();
    // нужно ли передвигать объект к позиции
    private transient boolean needToMoveToDestination = false;

    // поворот, на который нужно повернуть плавно объект
    private transient float destinationRotation;
    // скорость, с которой объект будет набирать поворот destinationRotation
    private transient float destinationRotationToAdd;
    // нужно ли повернуть объект
    private transient boolean needToRotateToDestinationRotation = false;

    // погрешность при достижении цели
    private transient Vector2f destinationPositionInfelicity = new Vector2f(0.01f, 0.01f);

    // прикрепленный rigibody2d
    private transient Rigidbody2D rigidbody2D;

    // колбэк
    private transient TransformCallback transformCallback = null;

    private transient Transform parentTransform;

    public Transform()
    {
        init();
    }

    public Transform(Transform transform)
    {
        set(transform);
    }

    public Transform(Rigidbody2D rigidbody2D)
    {
        this.rigidbody2D = rigidbody2D;

        init();
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
            if(Math.abs(dif.x) > destinationPositionInfelicity.x || Math.abs(dif.y) > destinationPositionInfelicity.y) {
                translate(new Vector2f(dif));
            } else {
                needToMoveToDestination = false;
                if(transformCallback != null) {
                    transformCallback.onDestinationReached();
                }
            }
        }

        if(needToRotateToDestinationRotation) {
            int a = (int) rotation / 360;
            float clearRotation = (float) Math.ceil(rotation - 360.0f * a);

            a = (int) destinationRotationToAdd / 360;
            float clearDestinationRotationToAdd = destinationRotationToAdd - 360.0f * a;

            destinationRotation = (float) Math.ceil(destinationRotation);

            float firstVar = destinationRotation - clearRotation;
            firstVar += (firstVar > 180) ? -360.0f : (firstVar < -180.0f) ? 360.0f : 0.0f;

            firstVar = (float) Math.ceil(firstVar);

            if(firstVar < 0.0f) {
                rotate(-clearDestinationRotationToAdd);
            } else if(firstVar > 0.0f) {
                rotate(clearDestinationRotationToAdd);
            }
        }

        boolean canUpdateRigigbody2D = true;
        if(SceneManager.currentSceneManager.getCurrentScene2D() != null &&
                SceneManager.currentSceneManager.getCurrentScene2D().getPhysicsWorld() != null &&
                !SceneManager.currentSceneManager.getCurrentScene2D().getPhysicsWorld().simulatePhysics) {
            canUpdateRigigbody2D = false;
        }
        if(rigidbody2D != null && canUpdateRigigbody2D) {
            setPositionLikeRigidbody2D();
            setRotationLikeRigidbody2D();
        }

<<<<<<< Updated upstream
        if(parentTransform != null) {
            Matrix4f translationMatrix = MatrixUtils.getTranslationMatrix(parentTransform.getResultModelMatrix());
            Matrix4f rotationMatrix = MatrixUtils.getRotationMatrix(parentTransform.getResultModelMatrix());

            Vector2f parentScale = MatrixUtils.getScale(parentTransform.getResultModelMatrix());

            Vector2f lastScale = new Vector2f(scale);
            setScale(new Vector2f(scale).mul(parentScale));
            scale.set(lastScale);

            Vector2f lastPosition = new Vector2f(position);
            setPosition(new Vector2f(position).mul(parentScale));
            position.set(lastPosition);

            Matrix4f result = new Matrix4f(translationMatrix).mul(rotationMatrix);
            resultModelMatrix.set(result);
            resultModelMatrix.mul(modelMatrix);
        } else {
            resultModelMatrix.set(modelMatrix);
        }
=======
        //updateModelMatrix();
>>>>>>> Stashed changes
    }

    public void translate(Vector2f translation)
    {
        position.add(translation);

        //translationMatrix.translate(translation.x, translation.y, 0.0f);

        if(rigidbody2D != null) {
            Vec2 newPosition = new Vec2(rigidbody2D.getBody().getTransform().position).add(new Vec2(translation.x / PhysicsWorld.RATIO, translation.y / PhysicsWorld.RATIO));
            rigidbody2D.getBody().setTransform(newPosition, rigidbody2D.getBody().getAngle());
        }

        updateModelMatrix();
    }

    public void translateInRotationDirection(Vector2f translation)
    {
        Vector3f rotatedTranslation = new Vector3f(translation.x, translation.y, 0.0f);
        rotatedTranslation.rotateZ((float) Math.toRadians(rotation));

        position.add(new Vector2f(rotatedTranslation.x, rotatedTranslation.y));

        /*
        translationMatrix.identity();
        translationMatrix.translate(position.x, position.y, 0.0f);

         */

        if(rigidbody2D != null) {
            Vec2 newPosition = new Vec2(rigidbody2D.getBody().getTransform().position).add(new Vec2(rotatedTranslation.x / PhysicsWorld.RATIO, rotatedTranslation.y / PhysicsWorld.RATIO));
            rigidbody2D.getBody().setTransform(newPosition, rigidbody2D.getBody().getAngle());
        }

        updateModelMatrix();
    }

    public void rotate(float rotation)
    {
        this.rotation += rotation;

<<<<<<< Updated upstream
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateX((float) Math.toRadians(0));
        rotationQ.rotateY((float) Math.toRadians(0));
        rotationQ.rotateZ((float) Math.toRadians(rotation));

        rotationMatrix.rotateAround(rotationQ, centre.x, centre.y, 0.0f);

        if(rigidbody2D != null) {
            float newAngle = (float) (Math.toDegrees(rigidbody2D.getBody().getAngle()) + rotation);
            rigidbody2D.getBody().setTransform(rigidbody2D.getBody().getTransform().position, (float) Math.toRadians(newAngle));
        }

=======
>>>>>>> Stashed changes
        updateModelMatrix();
    }

    public void rotate(float rotation, Matrix4f dest)
    {
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateX((float) Math.toRadians(0));
        rotationQ.rotateY((float) Math.toRadians(0));
        rotationQ.rotateZ((float) Math.toRadians(rotation));

        dest.rotateAround(rotationQ, centre.x, centre.y, 0.0f);
    }

<<<<<<< Updated upstream
    public void rotateAround(float rotation, Vector2f point)
    {
        this.rotation += rotation;

        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateX((float) Math.toRadians(0));
        rotationQ.rotateY((float) Math.toRadians(0));
        rotationQ.rotateZ((float) Math.toRadians(rotation));

        rotationMatrix.rotateAround(rotationQ, point.x, point.y, 0.0f);

        if(rigidbody2D != null) {
            float newAngle = (float) (Math.toDegrees(rigidbody2D.getBody().getAngle()) + rotation);
            rigidbody2D.getBody().setTransform(rigidbody2D.getBody().getTransform().position, (float) Math.toRadians(newAngle));
        }

        updateModelMatrix();
    }

=======
>>>>>>> Stashed changes
    public float getRotationOfLookAt(Vector2f target)
    {
        Vector2f resultPosition = MatrixUtils.getPosition(globalModelMatrix);
        float parentRotation = 0.0f;
        if(parentTransform != null) {
            parentRotation = MatrixUtils.getRotation(parentTransform.getGlobalModelMatrix());
        }

        float dx = resultPosition.x - target.x;
        float dy = resultPosition.y - target.y;
        float angle = (float) (Math.atan2(dy, dx) * 180.0f / Math.PI);

        return angle - parentRotation;
    }

    public void lookAt(Vector2f target)
    {
        setRotation(getRotationOfLookAt(target));
    }

    public void scale(Vector2f scale)
    {
        this.scale.add(scale);

<<<<<<< Updated upstream
        scaleMatrix.identity();
        scaleMatrix.scale(this.scale.x, this.scale.y, 1.0f);

=======
>>>>>>> Stashed changes
        updateModelMatrix();
    }

    public void scaleMul(Vector2f scale)
    {
        this.scale.mul(scale);

<<<<<<< Updated upstream
        scaleMatrix.identity();
        scaleMatrix.scale(this.scale.x, this.scale.y, 1.0f);

=======
>>>>>>> Stashed changes
        updateModelMatrix();
    }

    public void lerpMoveTo(Vector2f toPosition, Vector2f coeff)
    {
        destinationPosition = new Vector2f(toPosition);
        moveToDestinationSpeedCoeff = new Vector2f(coeff);

        needToMoveToDestination = true;
    }

    public void lerpLookAt(Vector2f target, float destinationRotationToAdd)
    {
        this.destinationRotation = getRotationOfLookAt(target);
        this.destinationRotationToAdd = destinationRotationToAdd;

        needToRotateToDestinationRotation = true;
    }

    public void applyLinearImpulse(Vector2f impulse, Vector2f pos)
    {
        if(rigidbody2D != null) {
            rigidbody2D.getBody().applyLinearImpulse(new Vec2(impulse.x / PhysicsWorld.RATIO, impulse.y / PhysicsWorld.RATIO), new Vec2(pos.x / PhysicsWorld.RATIO, pos.y / PhysicsWorld.RATIO));
        }
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
    }

    // обновление матрицы модели
    private void updateModelMatrix()
    {
<<<<<<< Updated upstream
        modelMatrix = new Matrix4f(customMatrix).mul(translationMatrix).mul(rotationMatrix).mul(scaleMatrix);
=======
        // rotation -----------------------------
        rotationMatrix.identity();
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateX((float) Math.toRadians(0));
        rotationQ.rotateY((float) Math.toRadians(0));
        rotationQ.rotateZ((float) Math.toRadians(this.rotation));

        rotationMatrix.rotateAround(rotationQ, centre.x, centre.y, 0.0f);

        // scale --------------------------------

        scaleMatrix.identity();
        scaleMatrix.scale(this.scale.x, this.scale.y, 1.0f);

        if(rigidbody2D != null) {
            Vec2 newPosition = new Vec2(rigidbody2D.getBody().getTransform().position);
            rigidbody2D.getBody().setTransform(newPosition, rigidbody2D.getBody().getAngle());
        }

        // translation --------------------------

        translationMatrix.identity();
        translationMatrix.translate(this.position.x, this.position.y, 0.0f);

        // --------------------------------------

        localModelMatrix.identity().mul(translationMatrix).mul(rotationMatrix).mul(scaleMatrix);

        if(parentTransform != null) {
            parentTransform.updateModelMatrix();
            globalModelMatrix.set(parentTransform.getGlobalModelMatrix()).mul(localModelMatrix);
        } else {
            globalModelMatrix.set(localModelMatrix);
        }
>>>>>>> Stashed changes
    }

    private void updateRigidbody2D()
    {
        if(rigidbody2D != null && rigidbody2D.getBody() != null) {
            Vector2f realPosition = getRealPosition();
            float realRotation = getRealRotation();
            rigidbody2D.getBody().setTransform(new Vec2(realPosition.x / PhysicsWorld.RATIO, realPosition.y / PhysicsWorld.RATIO), (float) Math.toRadians(realRotation));
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

        localModelMatrix = new Matrix4f(transform.getLocalModelMatrix());

        customMatrix = new Matrix4f(transform.getCustomMatrix());

        destinationPosition = new Vector2f();
        moveToDestinationSpeedCoeff = new Vector2f();
        destinationPositionInfelicity = new Vector2f();

        init();
    }

    public Vector2f getPosition() { return position; }
    public void setPosition(Vector2f position)
    {
        this.position.set(position);

        updateRigidbody2D();
        updateModelMatrix();
    }
    private void setPositionLikeRigidbody2D()
    {
        Vec2 bodyPos = rigidbody2D.getBody().getTransform().position.mul(PhysicsWorld.RATIO);
        this.position.set(new Vector2f(bodyPos.x, bodyPos.y));

        /*
        translationMatrix.identity();
        translationMatrix.translate(position.x, position.y, 0.0f);

<<<<<<< Updated upstream
=======
         */

>>>>>>> Stashed changes
        updateModelMatrix();
    }

    public float getRotation() { return rotation; }
    public void setRotation(float angle)
    {
        this.rotation = angle;

        updateModelMatrix();
        updateRigidbody2D();
        updateModelMatrix();
    }
<<<<<<< Updated upstream
    public void setRotationAround(float angle, Vector2f point)
    {
        this.rotation = angle;

        rotationMatrix.identity();
        Quaternionf rotationQ = new Quaternionf();

        rotationQ.rotateX((float) Math.toRadians(0));
        rotationQ.rotateY((float) Math.toRadians(0));
        rotationQ.rotateZ((float) Math.toRadians(this.rotation));

        rotationMatrix.rotateAround(rotationQ, point.x, point.y, 0.0f);

        updateRigidbody2D();
        updateModelMatrix();
    }
=======
>>>>>>> Stashed changes
    private void setRotationLikeRigidbody2D()
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
        this.scale.set(scale);

<<<<<<< Updated upstream
        scaleMatrix.identity();
        scaleMatrix.scale(this.scale.x, this.scale.y, 1.0f);

        if(rigidbody2D != null) {
            Vec2 newPosition = new Vec2(rigidbody2D.getBody().getTransform().position);
            rigidbody2D.getBody().setTransform(newPosition, rigidbody2D.getBody().getAngle());
        }

        updateModelMatrix();
=======
        updateModelMatrix();
    }

    public Vector2f getRealPosition()
    {
        return MatrixUtils.getPosition(globalModelMatrix);
    }

    public Vector2f getRealScale()
    {
        Vector2f scale = MatrixUtils.getScale(globalModelMatrix);

        if(this.scale.x < 0.0f) {
            scale.x *= -1.0f;
        }
        if(this.scale.y < 0.0f) {
            scale.y *= -1.0f;
        }

        return scale;
    }

    public float getRealRotation()
    {
        float rotation = MatrixUtils.getRotation(globalModelMatrix);

        if(scale.x < 0.0f) {
            rotation -= 180.0f;
        }

        return rotation;
>>>>>>> Stashed changes
    }

    public Vector2f getCentre() { return centre; }
    public void setCentre(Vector2f centre) { this.centre.set(centre); }

    public Matrix4f getTranslationMatrix() { return translationMatrix; }

    public Matrix4f getRotationMatrix() { return rotationMatrix; }

    public Matrix4f getScaleMatrix() { return scaleMatrix; }

    public Matrix4f getLocalModelMatrix() { return localModelMatrix; }

    public Matrix4f getGlobalModelMatrix() { return globalModelMatrix; }

    public Matrix4f getCustomMatrix() { return customMatrix; }

    public Rigidbody2D getRigidbody2D()
    {
        return rigidbody2D;
    }
    public void setRigidbody2D(Rigidbody2D rigidbody2D)
    {
        this.rigidbody2D = rigidbody2D;

        updateRigidbody2D();
        setScale(scale);
    }

    public TransformCallback getTransformCallback() {return transformCallback; }
    public void setTransformCallback(TransformCallback transformCallback) { this.transformCallback = transformCallback; }

    public Vector2f getDestinationPositionInfelicity() { return destinationPositionInfelicity; }
    public void setDestinationPositionInfelicity(Vector2f destinationPositionInfelicity) { this.destinationPositionInfelicity = destinationPositionInfelicity; }

    public boolean isNeedToMoveToDestination() { return needToMoveToDestination; }
    public void setNeedToMoveToDestination(boolean needToMoveToDestination) { this.needToMoveToDestination = needToMoveToDestination; }

    public Transform getParentTransform() { return parentTransform; }
    public void setParentTransform(Transform parentTransform) { this.parentTransform = parentTransform; }
}