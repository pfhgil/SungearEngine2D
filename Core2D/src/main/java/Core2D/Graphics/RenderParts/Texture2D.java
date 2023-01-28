package Core2D.Graphics.RenderParts;

import Core2D.DataClasses.Texture2DData;
import Core2D.Graphics.OpenGL;
import Core2D.Log.Log;
import Core2D.Project.ProjectsManager;
import Core2D.Utils.FileUtils;
import Core2D.Utils.Utils;
import com.google.gson.annotations.SerializedName;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class Texture2D
{
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

    public static ByteBuffer cloneByteBuffer(final ByteBuffer original) {
        // Create clone with same capacity as original.
        final ByteBuffer clone = (original.isDirect()) ?
                ByteBuffer.allocateDirect(original.capacity()) :
                ByteBuffer.allocate(original.capacity());

        // Create a read-only copy of the original.
        // This allows reading from the original without modifying it.
        final ByteBuffer readOnlyCopy = original.asReadOnlyBuffer();

        // Flip and read from the original.
        readOnlyCopy.flip();
        clone.put(readOnlyCopy);

        clone.position(original.position());
        clone.limit(original.limit());
        clone.order(original.order());

        return clone;
    }

    public void createTexture(Texture2DData texture2DData)
    {
        this.texture2DData = texture2DData;

        if(ProjectsManager.getCurrentProject() != null && new File(texture2DData.getPath()).exists()) {
            this.path = FileUtils.getRelativePath(
                    new File(texture2DData.getPath()),
                    new File(ProjectsManager.getCurrentProject().getProjectPath())
            );
        } else {
            this.path = texture2DData.getPath();
        }

        // активирую нулевой текстурный блок
        OpenGL.glCall((params) -> glActiveTexture(textureBlock));

        // создание текстуры
        textureHandler = OpenGL.glCall((params) -> glGenTextures(), Integer.class);
        bind();

        // ставлю режим выравнивания данных текстуры по 1 байту (чтобы цвет текстуры был правильный)
        OpenGL.glCall((params) -> glPixelStorei(GL_UNPACK_ALIGNMENT, 1));
        OpenGL.glCall((params) -> glTexImage2D(GL_TEXTURE_2D,
                0,
                texture2DData.getInternalFormat(),
                texture2DData.getWidth(),
                texture2DData.getHeight(),
                0,
                texture2DData.getFormat(),
                GL_UNSIGNED_BYTE,
                texture2DData.getPixelsData()));

        // текстура будет растягиваться под фигуру
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, texture2DData.getWrapParam()));
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, texture2DData.getWrapParam()));

        // устанавливаю параметры текстуры
        OpenGL.glCall((params) -> glGenerateMipmap(GL_TEXTURE_2D));

        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, texture2DData.getFilterParam()));
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, texture2DData.getFilterParam()));

        // использовать сгенерированный мипмап
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE));

        unBind();

        int[] gpuMemAvailable = new int[1];
        glGetIntegerv(0x9049, gpuMemAvailable);
        Log.CurrentSession.println("loaded texture: " + path + ", gpu mem available: " + gpuMemAvailable[0] + " KB (" + gpuMemAvailable[0] / 1024.0f + " MB)", Log.MessageType.INFO);
    }

    // удаление текстур
    public void destroy()
    {
        OpenGL.glCall((params) -> glDeleteTextures(textureHandler));
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
        // активирую нулевой текстурный блок
        OpenGL.glCall((params) -> glActiveTexture(textureBlock));
        OpenGL.glCall((params) -> glBindTexture(GL_TEXTURE_2D, textureHandler));
    }
    public void unBind()
    {
        OpenGL.glCall((params) -> glBindTexture(GL_TEXTURE_2D, 0));
        OpenGL.glCall((params) -> glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA));
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
