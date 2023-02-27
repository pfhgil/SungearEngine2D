package Core2D.ECS.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Scene2D.Scene2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.MatrixUtils;
import Core2D.Utils.ShaderUtils;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL46C.*;

public class Camera2DComponent extends Component
{
    public interface Camera2DCallback
    {
        void preRender();
        void postRender();
    }

    public Vector2f viewportSize = new Vector2f(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);

    public transient Matrix4f projectionMatrix = new Matrix4f().ortho2D(-viewportSize.x / 2.0f, viewportSize.x / 2.0f, -viewportSize.y / 2.0f, viewportSize.y / 2.0f);

    public transient Matrix4f viewMatrix = new Matrix4f();

    public boolean isScene2DMainCamera2D = false;

    // промежуточный фрейм буфер без пост процессинга
    public transient FrameBuffer frameBuffer;

    // результативный фрейм буфер с пост процессингом
    public transient FrameBuffer resultFrameBuffer;

    public transient Camera2DCallback camera2DCallback;


    // post processing quad -----------------------------------------------------
    public transient short[] ppQuadIndices = new short[] { 0, 1, 2, 0, 2, 3 };

    // массив данных о вершинах
    // первые строки - позиции вершин, вторые строки - текстурные координаты
    //private transient Vector2f ppQuadSize = new Vector2f(100.0f, 100.0f);
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
