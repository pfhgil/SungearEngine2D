package Core2D.Camera2D;

import Core2D.Core2D.Core2D;
import Core2D.Input.PC.Mouse;
import Core2D.Transform.Transform;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.io.Serializable;

/**
 * Camera2D class. The camera has no components yet
 */
public class Camera2D implements Serializable
{
    private Transform transform = new Transform();

    /**
     * Camera name. Default name is "default"
     */
    public String name = "default";

    private Vector2f viewportSize = new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);

    private transient Matrix4f projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

    /**
     * Camera ID on scene
     */
    private int ID;

    /**
     * Camera constructor. Initializes transform and ID.
     * Шf the current scene is null,
     * then ID is equal to a random number from 0 to 1000000000,
     * in another case ID is equal to the ID of the last object on the scene + 1.
     */
    public Camera2D()
    {
        if(SceneManager.currentSceneManager != null && SceneManager.currentSceneManager.getCurrentScene2D() != null) {
            SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID++;
            ID = SceneManager.currentSceneManager.getCurrentScene2D().maxObjectID;
        } else {
            ID = Utils.getRandom(0, 1000000000);
        }
    }

    /**
     * The camera follows the transform position.
     * New camera positio
     * //@param transform Someone`s transform.
     */


    /*
    public void follow(Transform transform)
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

     */




    public Transform getTransform() { return transform; }

    public Vector2f getViewportSize() { return viewportSize; }
    public void setViewportSize(Vector2f viewportSize)
    {
        this.viewportSize = viewportSize;

        projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);
    }

    public Matrix4f getProjectionMatrix() { return projectionMatrix; }

    public int getID() { return ID; }
}
