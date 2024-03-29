package Core2D.Texture2D;

import Core2D.Core2D.Settings;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import com.google.gson.annotations.SerializedName;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture2D
{
    // id текстуры
    private transient int textureHandler = -1;
    // ширина текстуры
    private transient int width;
    // высота текстуры
    private transient int height;
    // сколько у текстуры каналов
    private transient int channels;
    // информация о пикселях текстуры
    private transient ByteBuffer pixelsData;

    @SerializedName("source")
    // путь до текстуры
    public String path;

    // формат текстуры. сколько каналов и какие каналы будет поддерживать текстура
    private transient int format;
    // сколько бит будет занимать каждый из каналов
    private transient int internalFormat;

    public int param = GL_CLAMP_TO_EDGE;

    private int textureBlock = GL_TEXTURE0;

    public int blendSourceFactor = GL_SRC_ALPHA;
    public int blendDestinationFactor = GL_ONE_MINUS_SRC_ALPHA;

    public Texture2D() { }

    // конструктор
    public Texture2D(InputStream inputStream)
    {
        loadTexture(inputStream);
    }

    // конструктор
    public Texture2D(String path)
    {
        loadTexture(path);
    }

    // конструктор
    public Texture2D(InputStream inputStream, int param)
    {
        this.param = param;

        loadTexture(inputStream);
    }

    // конструктор
    public Texture2D(InputStream inputStream, int param, int textureBlock)
    {
        this.param = param;
        this.textureBlock = textureBlock;

        loadTexture(inputStream);
    }

    // конструктор
    public Texture2D(String path, int param)
    {
        this.param = param;

        loadTexture(path);
    }

    // конструктор
    public Texture2D(String path, int param, int textureBlock)
    {
        this.param = param;
        this.textureBlock = textureBlock;

        loadTexture(path);
    }

    public void loadTexture(String source)
    {
        destroy();

        this.path = source;

        // буфер для ширины текстуры
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        // буфер для высоты текстуры
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        // буфер для каналов текстуры
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

        try {
            pixelsData = stbi_load_from_memory(Core2D.Utils.Utils.resourceToByteBuffer(source), widthBuffer, heightBuffer, channelsBuffer, 0);

            // получаю из буферов размер и каналы загруженной текстуры
            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
            channels = channelsBuffer.get(0);
        } catch (IOException e) {
            String exception = "Error while loading texture by source: " + source +". Error is: " + ExceptionsUtils.toString(e);
            Log.CurrentSession.println(exception, Log.MessageType.ERROR);
        }

        createTexture();
    }

    public void loadTexture(InputStream inputStream)
    {
        destroy();

        // буфер для ширины текстуры
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        // буфер для высоты текстуры
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        // буфер для каналов текстуры
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

        try {
            pixelsData = stbi_load_from_memory(Core2D.Utils.Utils.resourceToByteBuffer(inputStream), widthBuffer, heightBuffer, channelsBuffer, 0);

            inputStream.close();

            // получаю из буферов размер и каналы загруженной текстуры
            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
            channels = channelsBuffer.get(0);

        } catch (IOException e) {
            String exception = "Error while loading texture by source: " + path +". Error is: " + ExceptionsUtils.toString(e);
            Log.CurrentSession.println(exception, Log.MessageType.ERROR);
        }

        createTexture();
    }

    private void createTexture()
    {
        if(Thread.currentThread().getName().equals("main")) {
            // если текстура поддерживает 4 канал (RGBA (Red Green Blue Alpha))
            if (channels == 4) {
                // на каждый из каналов будет выделять 8 бит (поэтому GL_RGBA8)
                internalFormat = GL_RGBA8;
                // текстура будет поддерживать GL_RGBA
                format = GL_RGBA;
            } else if (channels == 3) { // если альфа канал не поддерживается (RGB)
                // на каждый из каналов будет выделять 8 бит (поэтому GL_RGB8)
                internalFormat = GL_RGB8;
                // текстура будет поддерживать GL_RGB
                format = GL_RGB;
            }

            // активирую нулевой текстурный блок
            glActiveTexture(textureBlock);

            // создание текстуры
            textureHandler = glGenTextures();
            bind();

            // сохраняю данные текстуры
            glTexStorage2D(textureHandler, 1, internalFormat, width, height);

            // текстура будет растягиваться под фигуру
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, param);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, param);

            // ставлю режим выравнивания данных текстуры по 1 байту (чтобы цвет текстуры был правильный)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            // устанавливаю параметры текстуры

            // если качество текстур в настройках == low
            if (Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.LOW) {
                // фильтрация текстур, которая выбирает тексель, центр которого находится ближе всего к текстурной координате
                // тексель = пикселю поскольку содержит цвет и альфа компонент
                // текстура будет пискельная
                // более быстрый метод, но текстура выглядит плохо
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            } else if (Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.MEDIUM) { // если качество текстур в настройках == medium
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
            } else if (Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality == Settings.QualityType.HIGH) { // если качество текстур в настройках == high
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

            glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, pixelsData);

            unBind();

            // очищаю буфер с данными текстуры
            if (pixelsData != null) {
                stbi_image_free(pixelsData);
                pixelsData.clear();
                pixelsData = null;
            }
        }
    }

    // удаление текстур
    public void destroy()
    {
        if(Thread.currentThread().getName().equals("main")) {
            if (pixelsData != null) pixelsData.clear();

            glDeleteTextures(textureHandler);
        }
    }

    public void set(Texture2D texture2D)
    {
        textureHandler = texture2D.getTextureHandler();
        textureBlock = texture2D.getGLTextureBlock();
        path = texture2D.path;
        width = texture2D.getWidth();
        height = texture2D.getHeight();
        channels = texture2D.getChannels();
        format = texture2D.getFormat();
        internalFormat = texture2D.getInternalFormat();
        blendSourceFactor = texture2D.blendSourceFactor;
        blendDestinationFactor = texture2D.blendDestinationFactor;
    }

    public void bind()
    {
        if(Thread.currentThread().getName().equals("main")) {
            // активирую нулевой текстурный блок
            glActiveTexture(textureBlock);
            glBindTexture(GL_TEXTURE_2D, textureHandler);
            glBlendFunc(blendSourceFactor, blendDestinationFactor);
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

    // геттеры и сеттеры

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getTextureHandler() { return textureHandler; }

    /**
     * @return OpenGL texture block
     */
    public int getGLTextureBlock() { return textureBlock; }

    /**
     * @return Formatted texture block (textureBlock - GL_TEXTURE0)
     */
    public int getFormattedTextureBlock() { return textureBlock - GL_TEXTURE0; }

    public int getChannels() { return channels; }

    public int getFormat() { return format; }

    public int getInternalFormat() { return internalFormat; }
}
