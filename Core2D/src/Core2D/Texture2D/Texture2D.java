package Core2D.Texture2D;

import Core2D.Core2D.Settings;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture2D
{
    // id текстуры
    private transient int textureHandler;
    // ширина текстуры
    private transient int width;
    // высота текстуры
    private transient int height;
    // сколько у текстуры каналов
    private transient int channels;
    // информация о пикселях текстуры
    private transient ByteBuffer pixelsData;
    // путь до текстуры
    private String source;
    // формат текстуры. сколько каналов и какие каналы будет поддерживать текстура
    private transient int format;
    // сколько бит будет занимать каждый из каналов
    private transient int internalFormat;

    private int param = GL_CLAMP_TO_EDGE;

    private int textureBlock = GL_TEXTURE0;

    public Texture2D() { }

    // конструктор
    public Texture2D(InputStream inputStream)
    {
        loadTexture(inputStream);
    }

    // конструктор
    public Texture2D(String source)
    {
        loadTexture(source);
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
    public Texture2D(String source, int param)
    {
        this.param = param;

        loadTexture(source);
    }

    // конструктор
    public Texture2D(String source, int param, int textureBlock)
    {
        this.param = param;
        this.textureBlock = textureBlock;

        loadTexture(source);
    }

    private void loadTexture(String source)
    {
        this.source = source;

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
            exception = null;
        }

        // если текстура поддерживает 4 канал (RGBA (Red Green Blue Alpha))
        if(channels == 4) {
            // на каждый из каналов будет выделять 8 бит (поэтому GL_RGBA8)
            internalFormat = GL_RGBA8;
            // текстура будет поддерживать GL_RGBA
            format = GL_RGBA;
        } else if(channels == 3) { // если альфа канал не поддерживается (RGB)
            // на каждый из каналов будет выделять 8 бит (поэтому GL_RGB8)
            internalFormat = GL_RGB8;
            // текстура будет поддерживать GL_RGB
            format = GL_RGB;
        }

        createTexture();
    }

    private void loadTexture(InputStream inputStream)
    {
        // буфер для ширины текстуры
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        // буфер для высоты текстуры
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        // буфер для каналов текстуры
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

        try {
            pixelsData = stbi_load_from_memory(Core2D.Utils.Utils.resourceToByteBuffer(inputStream), widthBuffer, heightBuffer, channelsBuffer, 0);

            inputStream.close();
            inputStream = null;

            // получаю из буферов размер и каналы загруженной текстуры
            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
            channels = channelsBuffer.get(0);

        } catch (IOException e) {
            String exception = "Error while loading texture by source: " + source +". Error is: " + ExceptionsUtils.toString(e);
            Log.CurrentSession.println(exception, Log.MessageType.ERROR);
            exception = null;
        }

        createTexture();
    }

    private void createTexture()
    {
        // если текстура поддерживает 4 канал (RGBA (Red Green Blue Alpha))
        if(channels == 4) {
            // на каждый из каналов будет выделять 8 бит (поэтому GL_RGBA8)
            internalFormat = GL_RGBA8;
            // текстура будет поддерживать GL_RGBA
            format = GL_RGBA;
        } else if(channels == 3) { // если альфа канал не поддерживается (RGB)
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

        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, pixelsData);

        unBind();

        // очищаю буфер с данными текстуры
        if(pixelsData != null) {
            stbi_image_free(pixelsData);
            pixelsData.clear();
            pixelsData = null;
        }
    }

    // удаление текстур
    public void destroy()
    {
        if(pixelsData != null) pixelsData.clear();
        pixelsData = null;

        source = null;

        glDeleteTextures(textureHandler);
    }

    public void set(Texture2D texture2D)
    {
        textureHandler = texture2D.getTextureHandler();
        textureBlock = texture2D.getGLTextureBlock();
        source = texture2D.getSource();
        width = texture2D.getWidth();
        height = texture2D.getHeight();
        channels = texture2D.getChannels();
        format = texture2D.getFormat();
        internalFormat = texture2D.getInternalFormat();
    }

    public void bind()
    {
        // активирую нулевой текстурный блок
        glActiveTexture(textureBlock);
        glBindTexture(GL_TEXTURE_2D, textureHandler);
    }
    public void unBind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    // геттеры и сеттеры

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getTextureHandler() { return textureHandler; }

    /**
     * @return textureBlock
     */
    public int getGLTextureBlock() { return textureBlock; }

    /**
     * @return textureBlock - GL_TEXTURE0
     */
    public int getFormattedTextureBlock() { return textureBlock - GL_TEXTURE0; }

    public String getSource() { return source; }

    public int getParam() { return param; }

    public int getChannels() { return channels; }

    public int getFormat() { return format; }

    public int getInternalFormat() { return internalFormat; }

    /*
    public void setTextureHandler(int textureHandler) { this.textureHandler = textureHandler; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getChannels() { return channels; }
    public void setChannels(int channels) { this.channels = channels; }

    public ByteBuffer getPixelsData() { return pixelsData; }
    public void setPixelsData(ByteBuffer pixelsData) { this.pixelsData = pixelsData; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
     */
}
