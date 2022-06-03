package Core2D.Camera2D;

import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Object2D.Object2D;
import Core2D.Object2D.Transform;
import org.joml.Vector2f;

import java.io.Serializable;

public class Camera2D implements Serializable
{
    private Transform transform;
    // прикрепленный объект, за котором следует камера
    private Object2D attachedObject2D;

    public Camera2D()
    {
        transform = new Transform();

        Core2D.currentCamera2D = this;
    }

    public Camera2D(Object2D attachedObject2D)
    {
        transform = new Transform();
        setAttachedObject2D(attachedObject2D);

        Core2D.currentCamera2D = this;
    }

    /**
     * Исправить. Умножать на deltaTime, чтобы камера не "лагала"
     **/
    public void follow()
    {
        if(attachedObject2D != null) {
            Transform attachedObjectTransform = attachedObject2D.getComponent(TransformComponent.class).getTransform();

            Vector2f objectResPos = new Vector2f(attachedObjectTransform.getPosition()).add(attachedObjectTransform.getCentre()).mul(transform.getScale());
            Vector2f cameraResultPos = new Vector2f(objectResPos.negate().add(new Vector2f(Core2D.getWindow().getSize().x / 2.0f, Core2D.getWindow().getSize().y / 2.0f)));
            Vector2f resultCentre = new Vector2f(attachedObjectTransform.getCentre()).add(new Vector2f(Core2D.getWindow().getSize().x / 2.0f, Core2D.getWindow().getSize().y / 2.0f)).mul(new Vector2f(transform.getScale()));
            // ставлю объект посередине вида камеры
            transform.setPosition(cameraResultPos);
            transform.setCentre(resultCentre);

            attachedObjectTransform = null;
        }
    }

    public void lerpFollow(Vector2f coeff)
    {
        if(attachedObject2D != null) {
            Transform attachedObjectTransform = attachedObject2D.getComponent(TransformComponent.class).getTransform();

            Vector2f cameraResultPos = new Vector2f(new Vector2f(transform.getPosition()).negate().add(new Vector2f(Core2D.getWindow().getSize().x / 2.0f, Core2D.getWindow().getSize().y / 2.0f)));
            Vector2f objectResPos = new Vector2f(attachedObjectTransform.getPosition()).add(attachedObjectTransform.getCentre()).mul(transform.getScale());

            Vector2f difference = new Vector2f(cameraResultPos.x - objectResPos.x, cameraResultPos.y - objectResPos.y).mul(coeff);
            transform.translate(difference);

            attachedObjectTransform = null;
        }
    }

    public Transform getTransform() { return transform; }

    public Object2D getAttachedObject2D() { return attachedObject2D; }
    public void setAttachedObject2D(Object2D attachedObject2D)
    {
        this.attachedObject2D = attachedObject2D;
        this.attachedObject2D.setAttachedCamera2D(this);
    }
}
