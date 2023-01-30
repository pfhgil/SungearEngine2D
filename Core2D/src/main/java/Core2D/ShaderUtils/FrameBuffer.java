package Core2D.ShaderUtils;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Settings;
import Core2D.Graphics.OpenGL;
import Core2D.Log.Log;

import java.io.Serializable;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class FrameBuffer implements Serializable
{
    private static final long serialVersionUID = 810L;


    // типы буферов
    public static class BuffersTypes implements Serializable
    {
        private static final long serialVersionUID = 810L;


        // буфер глубины
        public static final int DEPTH_BUFFER = 0;
        // буфер цвета
        public static final int COLOR_BUFFER = 1;
        // буфер для рендеринга
        public static final int RENDERING_BUFFER = 2;
    }

    // id FBO
    private int handler;
    // id render buffer object, прикреленного у этому fbo
    private int RBOHandler;
    // текстура FBO
    private int textureHandler;
    // тип fbo
    private int type;

    // ширина fbo
    private int width;
    // высота fbo
    private int height;

    private int viewportWidth;
    private int viewportHeight;

    // текстурный блок fbo
    private int textureBlock;

    private boolean complete = false;

    public FrameBuffer(int width, int height, int type, int textureBlock)
    {
        this.width = width;
        this.height = height;

        this.viewportWidth = width;
        this.viewportHeight = height;

        this.type = type;

        this.textureBlock = textureBlock;

        handler = OpenGL.glCall((params) -> glGenFramebuffers(), Integer.class);

        OpenGL.glCall((params) -> glBindFramebuffer(GL_FRAMEBUFFER, handler));

        if(type == BuffersTypes.COLOR_BUFFER) {
            createTextureAttachment(width, height);
        } else if(type == BuffersTypes.DEPTH_BUFFER) {
            createDepthTextureAttachment(width, height);
        } else if(type == BuffersTypes.RENDERING_BUFFER) {
            createRBOAttachment(width, height);
            createTextureAttachment(width, height);
        }

        if(OpenGL.glCall((params) -> glCheckFramebufferStatus(GL_FRAMEBUFFER), Integer.class) != GL_FRAMEBUFFER_COMPLETE) {
            Log.CurrentSession.println(new RuntimeException("Error while creating Framebuffer!"), Log.MessageType.ERROR);
        } else {
            complete = true;
        }

        OpenGL.glCall((params) -> glBindFramebuffer(GL_FRAMEBUFFER, 0));
    }

    // активирует FBO
    public void bind()
    {
        OpenGL.glCall((params) -> glBindFramebuffer(GL_FRAMEBUFFER, handler));
        OpenGL.glCall((params) -> glViewport(0, 0, viewportWidth, viewportHeight));
        int toClear = Settings.Graphics.isStencilTestActive() ? GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT : GL_COLOR_BUFFER_BIT;
        OpenGL.glCall((params) -> glClear(toClear));
    }

    // отключает FBO
    public void unBind()
    {
        //if(complete) {
        OpenGL.glCall((params) -> glBindFramebuffer(GL_FRAMEBUFFER, 0));
        OpenGL.glCall((params) -> glViewport(0, 0, Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y));
        //}
    }

    // активирует rbo
    public void bindRBO()
    {
        OpenGL.glCall((params) -> glBindRenderbuffer(GL_RENDERBUFFER, RBOHandler));
    }

    // отключает rbo
    public void unBindRBO()
    {
        OpenGL.glCall((params) -> glBindRenderbuffer(GL_RENDERBUFFER, 0));
    }

    // использовать текстуру
    public void bindTexture()
    {
        OpenGL.glCall((params) -> glActiveTexture(textureBlock));
        OpenGL.glCall((params) -> glBindTexture(GL_TEXTURE_2D, textureHandler));
    }

    // пересоздает fbo
    public void reCreate()
    {
        destroy();

        handler = OpenGL.glCall((params) -> glGenFramebuffers(), Integer.class);

        OpenGL.glCall((params) -> glBindFramebuffer(GL_FRAMEBUFFER, handler));

        if(type == BuffersTypes.COLOR_BUFFER) {
            createTextureAttachment(width, height);
        } else if(type == BuffersTypes.DEPTH_BUFFER) {
            createDepthTextureAttachment(width, height);
        } else if(type == BuffersTypes.RENDERING_BUFFER) {
            createRBOAttachment(width, height);
            createTextureAttachment(width, height);
        }

        OpenGL.glCall((params) -> glBindFramebuffer(GL_FRAMEBUFFER, 0));
    }

    // перестать использовать текстуру
    public void unBindTexture()
    {
        glActiveTexture(GL_TEXTURE0);
        OpenGL.glCall((params) -> glBindTexture(GL_TEXTURE_2D, 0));
    }

    // создает rbo прикрепление к fbo
    public void createRBOAttachment(int width, int height)
    {
        RBOHandler = OpenGL.glCall((params) -> glGenRenderbuffers(), Integer.class);

        bindRBO();

        //OpenGL.glCall((params) -> glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height));

        //OpenGL.glCall((params) -> glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, RBOHandler));

        OpenGL.glCall((params) -> glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height));

        OpenGL.glCall((params) -> glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, RBOHandler));

        unBindRBO();
    }

    // создает текстурное прикрепление к fbo
    public void createTextureAttachment(int width, int height)
    {
        textureHandler = OpenGL.glCall((params) -> glGenTextures(), Integer.class);

        OpenGL.glCall((params) -> glActiveTexture(textureBlock));
        OpenGL.glCall((params) -> glBindTexture(GL_TEXTURE_2D, textureHandler));

        OpenGL.glCall((params) -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null));

        applyTextureParams();

        OpenGL.glCall((params) -> glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureHandler, 0));

        OpenGL.glCall((params) -> glBindTexture(GL_TEXTURE_2D, 0));
    }

    private void applyTextureParams()
    {
        if(Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.LOW) {
            OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST));
            OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST));
        } else if(Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.MEDIUM) {
            OpenGL.glCall((params) -> glGenerateMipmap(GL_TEXTURE_2D));

            OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST));
            OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_NEAREST));

            OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE));
        } else if(Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.HIGH) {
            OpenGL.glCall((params) -> glGenerateMipmap(GL_TEXTURE_2D));

            OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR));
            OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR));

            OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE));
        }

        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT));
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT));
    }

    // создает прикрепление глубины к fbo
    public void createDepthTextureAttachment(int width, int height)
    {
        textureHandler = OpenGL.glCall((params) -> glGenTextures(), Integer.class);

        OpenGL.glCall((params) -> glActiveTexture(textureBlock));
        OpenGL.glCall((params) -> glBindTexture(GL_TEXTURE_2D, textureHandler));

        OpenGL.glCall((params) -> glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null));

        applyTextureParams();

        float borderColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        OpenGL.glCall((params) -> glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor));

        OpenGL.glCall((params) -> glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureHandler, 0));

        OpenGL.glCall((params) -> glBindTexture(GL_TEXTURE_2D, 0));
    }

    // удаляет FBO
    public void destroy()
    {
        OpenGL.glCall((params) -> glDeleteTextures(textureHandler));
        OpenGL.glCall((params) -> glDeleteFramebuffers(handler));
        OpenGL.glCall((params) -> glDeleteRenderbuffers(RBOHandler));
    }

    public int getHandler() { return handler; }

    public int getTextureHandler() { return textureHandler; }

    public int getWidth() { return width; }
    public void setWidth(int width)
    {
        this.width = width;

        reCreate();
    }

    public int getHeight() { return height; }
    public void setHeight(int height)
    {
        this.height = height;

        reCreate();
    }

    public int getViewportWidth() { return viewportWidth; }
    public void setViewportWidth(int viewportWidth) { this.viewportWidth = viewportWidth; }

    public int getViewportHeight() { return viewportHeight; }
    public void setViewportHeight(int viewportHeight) { this.viewportHeight = viewportHeight; }

    public int getTextureBlock() { return textureBlock; }
}
