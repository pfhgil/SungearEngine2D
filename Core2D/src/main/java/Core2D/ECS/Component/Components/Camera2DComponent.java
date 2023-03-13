package Core2D.ECS.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.OpenGL.FrameBuffer;
import Core2D.Graphics.OpenGL.VertexArray;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.PostprocessingLayer;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Camera2DComponent extends Component {
    public interface Camera2DCallback
    {
        void preRender();
        void postRender();
    }

    // transformations ------------------

    public boolean followTranslation = true;
    public boolean followRotation= false;
    public boolean followScale = false;

    public Vector2f position = new Vector2f();
    public float rotation = 0f;
    public Vector2f scale = new Vector2f(0.5f);

    public Vector2f viewportSize = new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);

    public transient Matrix4f projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

    public transient Matrix4f viewMatrix = new Matrix4f();

    // ----------------------------------

    public boolean scene2DMainCamera2D = false;

    // промежуточный фрейм буфер без пост процессинга
    public transient FrameBuffer frameBuffer;

    // результативный фрейм буфер с пост процессингом
    public transient FrameBuffer resultFrameBuffer;

    public transient List<Camera2DCallback> camera2DCallbacks = new ArrayList<>();


    // render --------------------------------------------------------------
    // будет ли камера рендерить сцену. если выключено, то камера не рендерит, но обновление трансформаций происходит
    public boolean render = true;

    // post processing -----------------------------------------------------
    public transient short[] ppQuadIndices = new short[] { 0, 1, 2, 0, 2, 3 };

    // массив данных о вершинах
    // первые строки - позиции вершин, вторые строки - текстурные координаты
    public transient float[] ppQuadData = new float[] {
            -1, -1,
            0, 0,

            -1, 1,
            0, 1,

            1, 1,
            1, 1,

            1, -1,
            1, 0,
    };

    public transient VertexArray ppQuadVertexArray;

    public Shader postprocessingDefaultShader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/postprocessing/postprocessing_default_shader.glsl"));

    public List<PostprocessingLayer> postprocessingLayers = new ArrayList<>();
}
