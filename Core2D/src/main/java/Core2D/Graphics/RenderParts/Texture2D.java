package Core2D.Graphics.RenderParts;

import Core2D.DataClasses.Texture2DData;
import com.google.gson.annotations.SerializedName;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture2D
{
    // режимы отрисовки текстуры
    public static class TextureDrawModes
    {
        public static final int NO_TEXTURE = 0;
        public static final int DEFAULT = 1;
        public static final int ONLY_ALPHA = 2;
    }

    // reference
    private transient Texture2DData texture2DData;

    // id текстуры
    private transient int textureHandler = -1;


    @SerializedName("source")
    // путь до текстуры
    public String path = "";

    private int textureBlock = GL_TEXTURE0;

    public Texture2D() { }

    public Texture2D(Texture2DData texture2DData, int textureBlock)
    {
        this.textureBlock = textureBlock;

        createTexture(texture2DData);
    }

    public Texture2D(Texture2DData texture2DData)
    {
        createTexture(texture2DData);
    }

    public void createTexture(Texture2DData texture2DData)
    {
        this.texture2DData = texture2DData;

        if(Thread.currentThread().getName().equals("main")) {
            // активирую нулевой текстурный блок
            glActiveTexture(textureBlock);

            // создание текстуры
            textureHandler = glGenTextures();
            bind();

            // сохраняю данные текстуры
            glTexStorage2D(textureHandler, 1, texture2DData.getInternalFormat(), texture2DData.getWidth(), texture2DData.getHeight());

            // текстура будет растягиваться под фигуру
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, texture2DData.getWrapParam());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, texture2DData.getWrapParam());

            // ставлю режим выравнивания данных текстуры по 1 байту (чтобы цвет текстуры был правильный)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            // устанавливаю параметры текстуры
            glGenerateMipmap(GL_TEXTURE_2D);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, texture2DData.getFilterParam());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, texture2DData.getFilterParam());

            // использовать сгенерированный мипмап
            glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE);

            glTexImage2D(GL_TEXTURE_2D,
                    0,
                    texture2DData.getInternalFormat(),
                    texture2DData.getWidth(),
                    texture2DData.getHeight(),
                    0,
                    texture2DData.getFormat(),
                    GL_UNSIGNED_BYTE,
                    texture2DData.getPixelsData());

            unBind();
        }
    }

    // удаление текстур
    public void destroy()
    {
        if(Thread.currentThread().getName().equals("main")) {
            glDeleteTextures(textureHandler);
        }
    }

    public void set(Texture2D texture2D)
    {
        texture2DData = texture2D.texture2DData;
        textureHandler = texture2D.getTextureHandler();
        textureBlock = texture2D.getGLTextureBlock();
        path = texture2D.path;
    }

    public void bind()
    {
        if(Thread.currentThread().getName().equals("main")) {
            // активирую нулевой текстурный блок
            glActiveTexture(textureBlock);
            glBindTexture(GL_TEXTURE_2D, textureHandler);
        }
    }
    public void unBind()
    {
        if(Thread.currentThread().getName().equals("main")) {
            glBindTexture(GL_TEXTURE_2D, 0);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public static String blendFactorToString(int blendFactor)
    {
        return switch(blendFactor) {
            case GL_ZERO -> "GL_ZERO";
            case GL_ONE -> "GL_ONE";
            case GL_SRC_COLOR -> "GL_SRC_COLOR";
            case GL_ONE_MINUS_SRC_COLOR -> "GL_ONE_MINUS_SRC_COLOR";
            case GL_DST_COLOR -> "GL_DST_COLOR";
            case GL_ONE_MINUS_DST_COLOR -> "GL_ONE_MINUS_DST_COLOR";
            case GL_SRC_ALPHA -> "GL_SRC_ALPHA";
            case GL_ONE_MINUS_SRC_ALPHA -> "GL_ONE_MINUS_SRC_ALPHA";
            case GL_DST_ALPHA -> "GL_DST_ALPHA";
            case GL_ONE_MINUS_DST_ALPHA -> "GL_ONE_MINUS_DST_ALPHA";
            case GL_CONSTANT_COLOR -> "GL_CONSTANT_COLOR";
            case GL_ONE_MINUS_CONSTANT_COLOR -> "GL_ONE_MINUS_CONSTANT_COLOR";
            case GL_CONSTANT_ALPHA -> "GL_CONSTANT_ALPHA";
            case GL_ONE_MINUS_CONSTANT_ALPHA -> "GL_ONE_MINUS_CONSTANT_ALPHA";
            default -> "UNKNOWN";
        };
    }

    public static int[] getAllBlendFactors()
    {
        return new int[] {
                GL_ZERO,
                GL_ONE,
                GL_SRC_COLOR,
                GL_ONE_MINUS_SRC_COLOR,
                GL_DST_COLOR,
                GL_ONE_MINUS_DST_COLOR,
                GL_SRC_ALPHA,
                GL_ONE_MINUS_SRC_ALPHA,
                GL_DST_ALPHA,
                GL_ONE_MINUS_DST_ALPHA,
                GL_CONSTANT_COLOR,
                GL_ONE_MINUS_CONSTANT_COLOR,
                GL_CONSTANT_ALPHA,
                GL_ONE_MINUS_CONSTANT_ALPHA
        };
    }

    public Texture2DData getTexture2DData() { return texture2DData; }

    public int getTextureHandler() { return textureHandler; }

    /**
     * @return OpenGL texture block
     */
    public int getGLTextureBlock() { return textureBlock; }

    /**
     * @return Formatted texture block (textureBlock - GL_TEXTURE0)
     */
    public int getFormattedTextureBlock() { return textureBlock - GL_TEXTURE0; }
}
