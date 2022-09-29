package Core2D.Camera2D;

import Core2D.Controllers.PC.Mouse;
import Core2D.Core2D.Core2D;
import Core2D.Graphics.Graphics;
import Core2D.Object2D.Transform;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MathUtils;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.io.Serializable;

// TODO: сделать id для камеры
public class Camera2D implements Serializable, AutoCloseable
{
    private Transform transform;

    private String name = "default";

    private Vector2f viewportSize = new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);

    private transient Matrix4f projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

    private int ID;

    public Camera2D()
    {
        transform = new Transform();
        //transform.getCustomMatrix().ortho(0, viewportSize.x, 0, viewportSize.y, 0.0f, 100.0f);

        if(SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;
            ID = SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID;
        } else {
            ID = Utils.getRandom(0, 1000000000);
        }

        System.out.println("camera id: " + ID);
    }

    public void follow(Transform transform, float deltaTime)
    {
        Vector2f pos = MatrixUtils.getPosition(transform.getResultModelMatrix());

        Vector2f objectResPos = new Vector2f(pos).mul(this.transform.getScale());
        Vector2f cameraResultPos = new Vector2f(objectResPos.negate());
        // ставлю объект посередине вида камеры
        this.transform.setPosition(cameraResultPos);
    }

    public void lerpFollow(Transform transform, Vector2f coeff)
    {
        Vector2f pos = MatrixUtils.getPosition(transform.getResultModelMatrix());
        Vector2f cameraPos = MatrixUtils.getPosition(this.transform.getResultModelMatrix());

        Vector2f cameraResultPos = new Vector2f(new Vector2f(cameraPos).negate().add(new Vector2f(Mouse.getViewportSize().x / 2.0f, Mouse.getViewportSize().y / 2.0f)));
        Vector2f objectResPos = new Vector2f(pos).add(transform.getCentre()).mul(this.transform.getScale());

        Vector2f difference = new Vector2f(cameraResultPos.x - objectResPos.x, cameraResultPos.y - objectResPos.y).mul(coeff);
        this.transform.translate(difference);
    }

    public Transform getTransform() { return transform; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Vector2f getViewportSize() { return viewportSize; }
    public void setViewportSize(Vector2f viewportSize)
    {
        this.viewportSize = viewportSize;

        projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);
    }

    public Matrix4f getProjectionMatrix() { return projectionMatrix; }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    @Override
    public void close() throws Exception {

    }
}
