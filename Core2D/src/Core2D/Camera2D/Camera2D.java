package Core2D.Camera2D;

import Core2D.Core2D.Core2D;
import Core2D.Object2D.Transform;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.Utils;
import org.joml.Vector2f;

import java.io.Serializable;

// TODO: сделать id для камеры
public class Camera2D implements Serializable, AutoCloseable
{
    private Transform transform;

    private String name = "default";

    private int ID;

    public Camera2D()
    {
        transform = new Transform();

        if(SceneManager.getCurrentScene2D() != null) {
            SceneManager.getCurrentScene2D().maxObjectID++;
            ID = SceneManager.getCurrentScene2D().maxObjectID;
        } else {
            ID = Utils.getRandom(0, 1000000000);
        }

        System.out.println("camera id: " + ID);
    }

    /**
     * Исправить. Умножать на deltaTime, чтобы камера не "лагала"
     **/
    public void follow(Transform transform)
    {
        Vector2f objectResPos = new Vector2f(transform.getPosition()).add(transform.getCentre()).mul(this.transform.getScale());
        Vector2f cameraResultPos = new Vector2f(objectResPos.negate().add(new Vector2f(Core2D.getWindow().getSize().x / 2.0f, Core2D.getWindow().getSize().y / 2.0f)));
        Vector2f resultCentre = new Vector2f(transform.getCentre()).add(new Vector2f(Core2D.getWindow().getSize().x / 2.0f, Core2D.getWindow().getSize().y / 2.0f)).mul(new Vector2f(this.transform.getScale()));
        // ставлю объект посередине вида камеры
        this.transform.setPosition(cameraResultPos);
        this.transform.setCentre(resultCentre);
    }

    public void lerpFollow(Transform transform, Vector2f coeff)
    {
        Vector2f cameraResultPos = new Vector2f(new Vector2f(this.transform.getPosition()).negate().add(new Vector2f(Core2D.getWindow().getSize().x / 2.0f, Core2D.getWindow().getSize().y / 2.0f)));
        Vector2f objectResPos = new Vector2f(transform.getPosition()).add(transform.getCentre()).mul(this.transform.getScale());

        Vector2f difference = new Vector2f(cameraResultPos.x - objectResPos.x, cameraResultPos.y - objectResPos.y).mul(coeff);
        this.transform.translate(difference);
    }

    public Transform getTransform() { return transform; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    @Override
    public void close() throws Exception {

    }
}
