package Core2D.ECS.Component.Components.Transform;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.NonDuplicated;
import Core2D.ECS.NonRemovable;
import Core2D.Transform.Transform;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * The TextureComponent. This component is NonDuplicated and NonDuplicated.
 * @see Transform
 * @see NonDuplicated
 * @see NonRemovable
 */
public class TransformComponent extends Component implements NonDuplicated
{
    // позиция
    public Vector2f position = new Vector2f();
    // поворот (по оси z)
    public float rotation = 0.0f;
    // масштаб
    public Vector2f scale = new Vector2f(1.0f, 1.0f);

    // ------------------------------------------------------ прошлые значения (на прошлом кадре)
    public Vector2f lastPosition = new Vector2f();
    public float lastRotation = 0.0f;
    public Vector2f lastScale = new Vector2f(1.0f, 1.0f);
    // ------------------------------------------------------

    // цент объекта (относительно позиции объекта)
    public Vector2f centre = new Vector2f();

    // матрица перемещения объекта
    public transient Matrix4f translationMatrix = new Matrix4f();
    // матрица поворота объекта
    public transient Matrix4f rotationMatrix = new Matrix4f();
    // матрица масштаба объекта
    public transient Matrix4f scaleMatrix = new Matrix4f();

    // матрица модели объекта
    public transient Matrix4f modelMatrix = new Matrix4f();

    public transient Matrix4f mvpMatrix = new Matrix4f();

    // прикрепленный rigibody2d
    //public transient Rigidbody2D rigidbody2D;

    // колбэк
    //public transient TransformCallback transformCallback = null;

    public transient TransformComponent parentTransformComponent;



    // ---------------------------------------------------------------------------------------------------------------------------------

    public TransformComponent() { }

    public TransformComponent(TransformComponent component)
    {
        //set(component);
    }

    public TransformComponent(Transform transform)
    {
        //this.transform = new Transform(transform);
    }

    @Override
    public void init()
    {
        //transform.init();
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof TransformComponent transformComponent) {
            //this.transform.set(transformComponent.getTransform());
        }
    }


    @Override
    public void deltaUpdate(float deltaTime)
    {
        //transform.update(deltaTime);
    }

    // ---------------------------------------------------------------------
}
