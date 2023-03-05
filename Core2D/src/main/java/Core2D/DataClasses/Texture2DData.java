package Core2D.DataClasses;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import sun.misc.Unsafe;

import java.io.*;
import java.nio.Buffer;
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

    private int filterParam = GL_LINEAR;

    private int wrapParam = GL_CLAMP_TO_EDGE;

    @Override
    public Texture2DData load(String path)
    {
        this.path = path;

        Log.CurrentSession.println("texture data path: " + path, Log.MessageType.WARNING);

        try (FileInputStream fis = new FileInputStream(path);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            load(bis, path);
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return this;
    }

    @Override
    public Texture2DData load(InputStream inputStream, String path)
    {
        this.path = path;

        // буфер для ширины текстуры
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        // буфер для высоты текстуры
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        // буфер для каналов текстуры
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

        try(inputStream) {
            pixelsData = stbi_load_from_memory(Core2D.Utils.Utils.resourceToByteBuffer(inputStream), widthBuffer, heightBuffer, channelsBuffer, 0);

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

        return this;
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
}
