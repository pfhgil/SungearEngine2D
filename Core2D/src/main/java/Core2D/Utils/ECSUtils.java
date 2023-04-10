package Core2D.Utils;

import Core2D.ECS.Component;
import Core2D.ECS.Camera.CameraComponent;
import Core2D.ECS.Mesh.MeshComponent;
import Core2D.ECS.Physics.BoxCollider2DComponent;
import Core2D.ECS.Physics.CircleCollider2DComponent;
import Core2D.ECS.Physics.Collider2DComponent;
import Core2D.ECS.Physics.Rigidbody2DComponent;
import Core2D.ECS.Primitives.BoxComponent;
import Core2D.ECS.Primitives.CircleComponent;
import Core2D.ECS.Primitives.LineComponent;
import Core2D.ECS.Primitives.PrimitiveComponent;
import Core2D.ECS.Transform.MoveToComponent;
import Core2D.ECS.Transform.TransformComponent;
import Core2D.Graphics.RenderParts.Shader;

public class ECSUtils
{
    // TODO: сделать копирование для всех компонентов
    public static <T extends Component> T copyComponent(T component)
    {
        if(component instanceof TransformComponent transformComponent) {
            TransformComponent copy = new TransformComponent();

            copy.position.set(transformComponent.position);
            copy.rotation.set(transformComponent.rotation);
            copy.scale.set(transformComponent.scale);

            copy.center.set(transformComponent.center);

            copy.active = transformComponent.active;

            return (T) copy;
        } else if(component instanceof MoveToComponent moveToComponent) {
            MoveToComponent copy = new MoveToComponent();

            copy.needMoveTo = moveToComponent.needMoveTo;
            copy.destinationPosition = moveToComponent.destinationPosition;
            copy.ration = moveToComponent.ration;

            copy.errorRate = moveToComponent.errorRate;

            copy.active = moveToComponent.active;

            return (T) copy;
        } else if(component instanceof CameraComponent cameraComponent) {
            CameraComponent copy = new CameraComponent();

            copy.followTransformTranslation = cameraComponent.followTransformTranslation;;
            copy.followTransformRotation = cameraComponent.followTransformRotation;
            copy.followTransformScale = cameraComponent.followTransformScale;

            copy.position.set(cameraComponent.position);
            copy.rotation.set(cameraComponent.rotation);
            copy.scale.set(cameraComponent.scale);

            copy.zoom = cameraComponent.zoom;

            copy.viewportSize.set(cameraComponent.viewportSize);

            copy.viewMode = cameraComponent.viewMode;
            copy.FOV = cameraComponent.FOV;
            copy.nearPlane = cameraComponent.nearPlane;
            copy.farPlane = cameraComponent.farPlane;

            copy.sceneMainCamera = cameraComponent.sceneMainCamera;

            copy.render = cameraComponent.render;

            copy.active = cameraComponent.active;

            return (T) copy;
        } else if(component instanceof MeshComponent meshComponent) {
            MeshComponent copy = new MeshComponent();

            copy.texture2DData = meshComponent.texture2DData;
            copy.modelData = meshComponent.modelData;
            copy.material2D = meshComponent.material2D;

            copy.active = meshComponent.active;

            return (T) copy;
        } else if(component instanceof BoxCollider2DComponent boxCollider2DComponent) {
            BoxCollider2DComponent copy = new BoxCollider2DComponent();

            fillCollider2DDefaultFields(copy, boxCollider2DComponent);

            copy.scale.set(boxCollider2DComponent.scale);

            return (T) copy;
        } else if(component instanceof CircleCollider2DComponent circleCollider2DComponent) {
            CircleCollider2DComponent copy = new CircleCollider2DComponent();

            fillCollider2DDefaultFields(copy, circleCollider2DComponent);

            copy.radius = circleCollider2DComponent.radius;

            return (T) copy;
        } else if(component instanceof Rigidbody2DComponent rigidbody2DComponent) {
            Rigidbody2DComponent copy = new Rigidbody2DComponent();

            copy.bodyType = rigidbody2DComponent.bodyType;

            copy.density = rigidbody2DComponent.density;
            copy.restitution = rigidbody2DComponent.restitution;
            copy.friction = rigidbody2DComponent.friction;

            copy.sensor = rigidbody2DComponent.sensor;

            copy.fixedRotation = rigidbody2DComponent.fixedRotation;

            return (T) copy;
        } else if(component instanceof LineComponent lineComponent) {
            LineComponent copy = new LineComponent();

            fillPrimitiveDefaultFields(copy, lineComponent);

            return (T) copy;
        } else if(component instanceof CircleComponent circleComponent) {
            CircleComponent copy = new CircleComponent();

            fillPrimitiveDefaultFields(copy, circleComponent);

            copy.radius = circleComponent.radius;
            copy.angleIncrement = circleComponent.angleIncrement;

            return (T) copy;
        } else if(component instanceof BoxComponent boxComponent) {
            BoxComponent copy = new BoxComponent();

            fillPrimitiveDefaultFields(copy, boxComponent);

            copy.size.set(boxComponent.size);

            return (T) copy;
        }

        return (T) new Component();
    }

    private static void fillCollider2DDefaultFields(Collider2DComponent copy, Collider2DComponent original)
    {
        copy.offset.set(original.offset);
        copy.angle = original.angle;

        copy.density = original.density;
        copy.followRigidbody2DDensity = original.followRigidbody2DDensity;

        copy.restitution = original.restitution;
        copy.followRigidbody2DRestitution = original.followRigidbody2DRestitution;

        copy.friction = original.friction;
        copy.followRigidbody2DFriction = original.followRigidbody2DFriction;

        copy.sensor = original.sensor;
        copy.followRigidbody2DSensor = original.followRigidbody2DSensor;

        copy.followTransformScale = original.followTransformScale;
    }

    private static void fillPrimitiveDefaultFields(PrimitiveComponent copy, PrimitiveComponent original)
    {
        copy.shader = original.shader;

        copy.offset.set(original.offset);
        copy.color.set(original.color);
        copy.linesWidth = original.linesWidth;
    }

    public static Shader setNewShader(Shader newShader, Shader oldShader)
    {
        oldShader.destroy();
        return newShader;
    }
}
