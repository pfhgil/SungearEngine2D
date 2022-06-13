package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Core2D.Core2D;
import Core2D.Physics.Rigidbody2D;
import Core2D.Scene2D.SceneManager;

/*
    TODO: добавить класс Rigidbody2D в физику, продумать как все будет работать (Rigidbody2D отвечает за гравитационные явления, тип объекта
    (динамичный, статичный)). коллайдеры отвечают только за столкновения
 */
public class Rigidbody2DComponent extends Component implements NonDuplicated
{
    private Rigidbody2D rigidbody2D;

    public Rigidbody2DComponent()
    {
        rigidbody2D = new Rigidbody2D();
    }

    @Override
    public void destroy()
    {
        rigidbody2D.getScene2D().getPhysicsWorld().destroyRigidbody2D(getObject2D());
        rigidbody2D = null;
        if(getObject2D().getComponent(TransformComponent.class) != null) {
            getObject2D().getComponent(TransformComponent.class).getTransform().setRigidbody2D(null);
        }
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof Rigidbody2DComponent) {
            rigidbody2D.set(((Rigidbody2DComponent) component).getRigidbody2D());
        }
        //super.set(component);
    }

    @Override
    public void init()
    {
        if(SceneManager.getCurrentScene2D() != null) {
            SceneManager.getCurrentScene2D().getPhysicsWorld().addRigidbody2D(getObject2D());
        }
        getObject2D().getComponent(TransformComponent.class).getTransform().setRigidbody2D(this.getRigidbody2D());
    }

    public Rigidbody2D getRigidbody2D() { return rigidbody2D; }
}
