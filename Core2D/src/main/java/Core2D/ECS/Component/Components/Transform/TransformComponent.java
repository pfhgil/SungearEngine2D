package Core2D.ECS.Component.Components.Transform;

import Core2D.ECS.Component.Component;
import Core2D.Common.Interfaces.NonDuplicated;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TransformComponent extends Component implements NonDuplicated
{
    // позиция
    public Vector3f position = new Vector3f();
    // поворот (по оси z)
    public Vector3f rotation = new Vector3f();
    // масштаб
    public Vector3f scale = new Vector3f(1.0f);

    // ------------------------------------------------------ прошлые значения (на прошлом кадре)
    public transient Vector3f lastPosition = new Vector3f();
    public transient Vector3f lastRotation = new Vector3f();
    public transient Vector3f lastScale = new Vector3f(1.0f);
    // ------------------------------------------------------

    // цент объекта (относительно позиции объекта)
    public Vector3f center = new Vector3f();

    // матрица перемещения объекта
    public transient Matrix4f translationMatrix = new Matrix4f();
    // матрица поворота объекта
    public transient Matrix4f rotationMatrix = new Matrix4f();
    // матрица масштаба объекта
    public transient Matrix4f scaleMatrix = new Matrix4f();

    // матрица модели объекта
    public transient Matrix4f modelMatrix = new Matrix4f();

    public transient Matrix4f mvpMatrix = new Matrix4f();

    public transient TransformComponent parentTransformComponent;
}
