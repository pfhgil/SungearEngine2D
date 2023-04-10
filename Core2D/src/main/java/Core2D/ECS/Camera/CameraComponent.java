package Core2D.ECS.Camera;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.DataClasses.MeshData;
import Core2D.ECS.Component;
import Core2D.Graphics.OpenGL.FrameBuffer;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.PostprocessingLayer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CameraComponent extends Component {
    public interface CameraCallback
    {
        void preRender();
        void postRender();
    }

    public enum ViewMode
    {
        VIEW_MODE_2D,
        VIEW_MODE_3D;

        @Override
        public String toString()
        {
            return switch(this) {
                case VIEW_MODE_2D -> "2D";
                case VIEW_MODE_3D -> "3D";
            };
        }
    }

    // transformations ------------------

    public boolean followTransformTranslation = true;
    public boolean followTransformRotation = false;
    public boolean followTransformScale = false;

    public Vector3f position = new Vector3f();
    public Vector3f rotation = new Vector3f();
    public Vector3f scale = new Vector3f(1f);

    public float zoom = 1f;

    public Vector2f viewportSize = new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);

    public transient Matrix4f projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

    public transient Matrix4f viewMatrix = new Matrix4f();

    // ----------------------------------

    // camera settings ------------------
    public ViewMode viewMode = ViewMode.VIEW_MODE_2D;
    public float FOV = 75f;
    public float nearPlane = 1f;
    public float farPlane = 9999f;

    // ----------------------------------

    public boolean sceneMainCamera = false;

    // промежуточный фрейм буфер без пост процессинга
    public transient FrameBuffer frameBuffer;

    // результативный фрейм буфер с пост процессингом
    public transient FrameBuffer resultFrameBuffer;

    public transient List<CameraCallback> cameraCallbacks = new ArrayList<>();


    // render --------------------------------------------------------------
    // будет ли камера рендерить сцену. если выключено, то камера не рендерит, но обновление трансформаций происходит
    public boolean render = true;

    // post processing -----------------------------------------------------
    public transient MeshData quadMeshData = AssetManager.getInstance().getModelData("/data/models/plane_normalized.obj").getMeshData("plane_normalized");
    public Shader postprocessingDefaultShader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/postprocessing/postprocessing_default_shader.glsl"));

    public List<PostprocessingLayer> postprocessingLayers = new ArrayList<>();
}
