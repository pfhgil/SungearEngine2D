package Core2D.DataClasses;

import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture2DData extends Data
{
    // ширина текстуры
    private transient int width;
    // высота текстуры
    private transient int height;
    // сколько у текстуры каналов
    private transient int channels;
    // информация о пикселях текстуры
    private transient ByteBuffer pixelsData;

    @Override
    public Texture2DData load(String path)
    {
        try {
            load(new BufferedInputStream(new FileInputStream(path)));
        } catch (FileNotFoundException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException(e)), Log.MessageType.ERROR);
        }

        return this;
    }

    @Override
    public Texture2DData load(InputStream inputStream)
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

            // получаю из буферов размер и каналы загруженной текстуры
            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
            channels = channelsBuffer.get(0);

        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return this;
    }

    public void set(Texture2DData texture2DData)
    {
        pixelsData = ByteBuffer.wrap(texture2DData.pixelsData.array());
        width = texture2DData.width;
        height = texture2DData.height;
        channels = texture2DData.channels;
    }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getChannels() { return channels; }

    public ByteBuffer getPixelsData() { return pixelsData; }
}
