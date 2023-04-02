package Core2D.DataClasses;

import Core2D.Graphics.OpenGL.OpenGL;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import org.lwjgl.BufferUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture2DData extends Data
{
    // информация о пикселях текстуры
    private transient ByteBuffer pixelsData;

    // ширина текстуры
    private transient int width;
    // высота текстуры
    private transient int height;
    // сколько у текстуры каналов
    private transient int channels;

    // формат текстуры. сколько каналов и какие каналы будет поддерживать текстура
    private transient int format;
    // сколько бит будет занимать каждый из каналов
    private transient int internalFormat;

    private transient int filterParam = GL_LINEAR;

    private transient int wrapParam = GL_CLAMP_TO_EDGE;

    private transient int handler;

    public int textureBlock = GL_TEXTURE0;

    @Override
    public Texture2DData load(String absolutePath)
    {
        this.absolutePath = absolutePath;

        createRelativePath();

        Log.CurrentSession.println("texture data absolute path: " + absolutePath + ", relative: " + relativePath, Log.MessageType.WARNING);

        try (FileInputStream fis = new FileInputStream(absolutePath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            load(bis, absolutePath);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return this;
    }

    @Override
    public Texture2DData load(InputStream inputStream, String absolutePath)
    {
        this.absolutePath = absolutePath;

        createRelativePath();

        // буфер для ширины текстуры
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        // буфер для высоты текстуры
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        // буфер для каналов текстуры
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

        try(inputStream) {
            ByteBuffer resource = Core2D.Utils.Utils.resourceToByteBuffer(inputStream);
            pixelsData = stbi_load_from_memory(resource, widthBuffer, heightBuffer, channelsBuffer, 0);
            resource.clear();
            //MemoryUtil.memFree(resource);

            // получаю из буферов размер и каналы загруженной текстуры
            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
            channels = channelsBuffer.get(0);

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
            } else if (channels == 2) {
                internalFormat = GL_RG8;
                format = GL_RG;
            } else if (channels == 1) {
                internalFormat = GL_LUMINANCE8;
                format = GL_LUMINANCE;
            }

            //Log.CurrentSession.println("Loaded texture: " + path + ", width: " + width + ", height: " + height + ", internalFormat: " + internalFormat + ", format: " + format + ", channels: " + channels, Log.MessageType.SUCCESS);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        widthBuffer.clear();
        heightBuffer.clear();
        channelsBuffer.clear();
        //MemoryUtil.memFree(widthBuffer);
        //MemoryUtil.memFree(heightBuffer);
        //MemoryUtil.memFree(channelsBuffer);

        create();

        return this;
    }

    private void create()
    {
        // активирую нулевой текстурный блок
        OpenGL.glCall((params) -> glActiveTexture(textureBlock));

        // создание текстуры
        handler = OpenGL.glCall((params) -> glGenTextures(), Integer.class);
        OpenGL.glBindTexture(handler, textureBlock);

        // ставлю режим выравнивания данных текстуры по 1 байту (чтобы цвет текстуры был правильный)
        OpenGL.glCall((params) -> glPixelStorei(GL_UNPACK_ALIGNMENT, 1));
        OpenGL.glCall((params) -> glTexImage2D(GL_TEXTURE_2D,
                0,
                internalFormat,
                width,
                height,
                0,
                format,
                GL_UNSIGNED_BYTE,
                pixelsData));

        // текстура будет растягиваться под фигуру
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapParam));
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapParam));

        // устанавливаю параметры текстуры
        OpenGL.glCall((params) -> glGenerateMipmap(GL_TEXTURE_2D));

        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterParam));
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterParam));

        // использовать сгенерированный мипмап
        OpenGL.glCall((params) -> glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE));
    }

    @Override
    public void destroy()
    {
        OpenGL.glCall(func -> glDeleteTextures(handler));
    }

    @Override
    public void set(Data data)
    {
        if(data instanceof Texture2DData texture2DData) {
            pixelsData = ByteBuffer.wrap(texture2DData.pixelsData.array());
            width = texture2DData.width;
            height = texture2DData.height;
            channels = texture2DData.channels;
            wrapParam = texture2DData.getWrapParam();
            filterParam = texture2DData.getFilterParam();
        }
    }

    @Override
    public void setNotTransientFields(Data data)
    {
        if(data instanceof Texture2DData texture2DData) {
            wrapParam = texture2DData.getWrapParam();
            filterParam = texture2DData.getFilterParam();
        }
    }

    public ByteBuffer getPixelsData() { return pixelsData; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getChannels() { return channels; }

    public int getFormat() { return format; }

    public int getInternalFormat() { return internalFormat; }

    public int getFilterParam() { return filterParam; }

    public int getWrapParam() { return wrapParam; }

    public int getHandler() { return handler; }

    public int getFormattedTextureBlock() { return textureBlock - GL_TEXTURE0; }
}
