package Core2D.ShaderUtils;

import Core2D.Core2D.Core2D;
import Core2D.Core2D.Settings;

import java.io.Serializable;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

public class FrameBufferObject implements Serializable
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

    public FrameBufferObject(int width, int height, int type, int textureBlock)
    {
        this.width = width;
        this.height = height;

        this.viewportWidth = width;
        this.viewportHeight = height;

        this.type = type;

        this.textureBlock = textureBlock;

        handler = glGenFramebuffers();

        bind();

        if(type == BuffersTypes.COLOR_BUFFER) {
            createTextureAttachment(width, height);
        } else if(type == BuffersTypes.DEPTH_BUFFER) {
            createDepthTextureAttachment(width, height);
        } else if(type == BuffersTypes.RENDERING_BUFFER) {
            createRBOAttachment(width, height);
            createTextureAttachment(width, height);
        }

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            // вывести ошибку
        }

        // получаю код ошибки opengl
        int errorCode = glGetError();
        // проверка на ошибки
        if(errorCode != 0) {
            // вывести ошибку
        }

        unBind();
    }

    // активирует FBO
    public void bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, handler);
        glViewport(0, 0, viewportWidth, viewportHeight);
    }

    // отключает FBO
    public void unBind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y);
    }

    // активирует rbo
    public void bindRBO()
    {
        glBindRenderbuffer(GL_RENDERBUFFER, RBOHandler);
    }

    // отключает rbo
    public void unBindRBO()
    {
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    // использовать текстуру
    public void bindTexture()
    {
        glActiveTexture(textureBlock);
        glBindTexture(GL_TEXTURE_2D, textureHandler);

        //System.out.println("textureBlock: " + textureBlock + ", textureHandler: " + textureHandler);
    }

    // пересоздает fbo
    public void reCreate()
    {
        destroy();

        handler = glGenFramebuffers();

        bind();

        if(type == BuffersTypes.COLOR_BUFFER) {
            createTextureAttachment(width, height);
        } else if(type == BuffersTypes.DEPTH_BUFFER) {
            createDepthTextureAttachment(width, height);
        } else if(type == BuffersTypes.RENDERING_BUFFER) {
            createRBOAttachment(width, height);
            createTextureAttachment(width, height);
        }

        // получаю код ошибки opengl
        int errorCode = glGetError();
        // проверка на ошибки
        if(errorCode != 0) {
            // вывести ошибку
        }

        unBind();
    }

    // перестать использовать текстуру
    public void unBindTexture()
    {
        //glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    // создает rbo прикрепление к fbo
    public void createRBOAttachment(int width, int height)
    {
        RBOHandler = glGenRenderbuffers();

        bindRBO();

        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);

        /*
        if(glCheckFramebufferStatus(GL_RENDERBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("RBO of FBO was not created! FBO type: " + type);
        }

         */

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, RBOHandler);

        // получаю код ошибки opengl
        int errorCode = glGetError();
        // проверка на ошибки
        if(errorCode != 0) {
            // вывести ошибку
        }

        unBindRBO();
    }

    // создает текстурное прикрепление к fbo
    public void createTextureAttachment(int width, int height)
    {
        textureHandler = glGenTextures();

        glActiveTexture(textureBlock);
        glBindTexture(GL_TEXTURE_2D, textureHandler);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);

        applyTextureParams();

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureHandler, 0);

        // получаю код ошибки opengl
        int errorCode = glGetError();
        // проверка на ошибки
        if(errorCode != 0) {
            // вывести ошибку
        }

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void applyTextureParams()
    {
        // если качество текстур в настройках == low
        if(Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.LOW) {
            // фильтрация текстур, которая выбирает тексель, центр которого находится ближе всего к текстурной координате
            // тексель = пикселю поскольку содержит цвет и альфа компонент
            // текстура будет пискельная
            // более быстрый метод, но текстура выглядит плохо
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        } else if(Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.MEDIUM) { // если качество текстур в настройках == medium
            // использует ближайший мипмап-уровень и отбирает его с помощью метода линейной интерполяции
            // линейная интерполяция — это определение коэффициентов прямой линии, проходящей через две заданные точки. значения в точке определяются по формуле прямой линии
            // мипмап - уровень детализации
            // текстура будет с эффектом сглаживания
            // средний по быстроте метод, но текстура выглядит уже лучше

            // генерирую мипмап. без него не будет работать (черная текстура)
            glGenerateMipmap(GL_TEXTURE_2D);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_NEAREST);

            // использовать сгенерированный мипмап
            glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE);
        } else if(Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.HIGH) { // если качество текстур в настройках == high
            // линейная интерполяция между двумя мипмап-текстурами, которые наиболее точно соответствуют размеру пикселя, и затем выбор интерполированного уровня при помощи «метода ближайших соседей».
            // метод ближайших соседей - метод, выбирающий тексель, центр которого находится ближе всего к текстурной координате
            // линейная интерполяция — это определение коэффициентов прямой линии, проходящей через две заданные точки. значения в точке определяются по формуле прямой линии
            // мипмап - уровень детализации
            // текстура будет с лучшим эффектом сглаживания
            // самый медленный метод, но текстура выглядит лучшим образом

            // генерирую мипмап. без него не будет работать (черная текстура)
            glGenerateMipmap(GL_TEXTURE_2D);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);

            // использовать сгенерированный мипмап
            glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE);
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
    }

    // создает прикрепление глубины к fbo
    public void createDepthTextureAttachment(int width, int height)
    {
        textureHandler = glGenTextures();

        glActiveTexture(textureBlock);
        glBindTexture(GL_TEXTURE_2D, textureHandler);


        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);

        applyTextureParams();

        float borderColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

        //float borderColor[] = { 0.1f, 0.1f, 0.1f, 1.0f };
        //glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureHandler, 0);

        // получаю код ошибки opengl
        int errorCode = glGetError();
        // проверка на ошибки
        if(errorCode != 0) {
            // вывести ошибку
        }

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    // удаляет FBO
    public void destroy()
    {
        unBind();

        glDeleteTextures(textureHandler);
        glDeleteFramebuffers(handler);
    }

    // геттеры и сеттеры


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
