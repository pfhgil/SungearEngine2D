package Core2D.Layering;

import Core2D.AssetManager.AssetManager;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.FrameBuffer;
import Core2D.Graphics.RenderParts.Shader;

import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class PostprocessingLayer
{
    // ссылка на слой, который нужно отрендерить
    private transient Layer entitiesLayerToRender;

    // имя слоя, который нужно отрендерить
    private String entitiesLayerToRenderName = "";

    // render -----------------------------------------------
    // будет ли рендериться слой
    public boolean render = true;
    // будет ли накладываться слой
    public boolean overlay = true;

    private FrameBuffer frameBuffer;

    private Shader shader = new Shader(AssetManager.getInstance().getShaderData("/data/shaders/postprocessing/postprocessing_default_shader.glsl"));
    // ------------------------------------------------------

    // TODO: сделать flip y для pp слоев и для результативнго fbo камеры
    public PostprocessingLayer(Layer entitiesLayerToRender)
    {
        this.entitiesLayerToRender = entitiesLayerToRender;
        entitiesLayerToRenderName = entitiesLayerToRender.getName();
        init();
    }

    public void init()
    {
        frameBuffer = new FrameBuffer(Graphics.getScreenSize().x, Graphics.getScreenSize().y, FrameBuffer.BuffersTypes.RENDERING_BUFFER, GL_TEXTURE0);
    }

    public void updateName()
    {
        entitiesLayerToRenderName = entitiesLayerToRender.getName();
    }

    public void destroy()
    {
        entitiesLayerToRender = null;

        if(frameBuffer != null) {
            frameBuffer.destroy();
        }

        if(shader != null) {
            shader.destroy();
        }
    }

    public Shader getShader() { return shader; }
    public void setShader(Shader shader)
    {
        if(this.shader != null) {
            this.shader.destroy();
        }

        this.shader = shader;
    }

    public FrameBuffer getFrameBuffer() { return frameBuffer; }

    public Layer getEntitiesLayerToRender() { return entitiesLayerToRender; }

    public void setEntitiesLayerToRender(Layer entitiesLayerToRender)
    {
        this.entitiesLayerToRender = entitiesLayerToRender;
        if(entitiesLayerToRender != null) {
            entitiesLayerToRenderName = entitiesLayerToRender.getName();
        }
    }

    public String getEntitiesLayerToRenderName() { return entitiesLayerToRenderName; }
}
