package Core2D.Utils;

import Core2D.Component.Components.TransformComponent;
import Core2D.Log.Log;
import Core2D.Object2D.Object2D;
import Core2D.Object2D.Transform;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

public class Utils
{
    private static Random random = new Random();

    // создает FloatBuffer, помещает туда data и возвращает получившийся буфер
    public static FloatBuffer createFloatBuffer(float[] data)
    {
        // создание float buffer для сортированных координат вершин
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(data.length);
        // помещаю данные в буфер
        floatBuffer.put(data);
        // переключение в ввод/вывод
        floatBuffer.flip();

        return floatBuffer;
    }
    // создает ShortBuffer, помещает туда data и возвращает получившийся буфер
    public static ShortBuffer createShortBuffer(short[] data)
    {
        // создание short buffer для сортированных координат вершин
        ShortBuffer floatBuffer = MemoryUtil.memAllocShort(data.length);
        // помещаю данные в буфер
        floatBuffer.put(data);
        // переключение в ввод/вывод
        floatBuffer.flip();

        return floatBuffer;
    }

    // создает ByteBuffer, помещает туда data и возвращает получившийся буфер
    public static ByteBuffer createByteBuffer(byte[] data)
    {
        // создание float buffer для сортированных координат вершин
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(data.length);
        // помещаю данные в буфер
        byteBuffer.put(data);
        // переключение в ввод/вывод
        byteBuffer.flip();

        return byteBuffer;
    }

    public static ByteBuffer resourceToByteBuffer(InputStream inputStream) throws IOException
    {
        BufferedInputStream bfi = new BufferedInputStream(inputStream);

        // создаю buffer с размером fileChannel.size() + 1 (+1 для символа конца строки)
        ByteBuffer buffer = BufferUtils.createByteBuffer(bfi.available() + 1);

        byte[] b = new byte[bfi.available() + 1];

        bfi.read(b);
        buffer.put(b);

        // переключение в ввод/вывод
        buffer.flip();

        b = null;

        return buffer;
    }

    public static String inputStreamToString(InputStream inputStream)
    {
        StringBuilder s = new StringBuilder();
        String newLine = "";

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "CP866"));
        } catch (UnsupportedEncodingException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            return "";
        }

        while(true) {
            try {
                if (((newLine = bufferedReader.readLine()) == null)) break;
            } catch (IOException e) {
                Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
            }
            s.append(newLine).append("\n");

        }

        return s.toString();
    }

    public static String outputStreamToString(OutputStream outputStream)
    {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            baos.writeTo(outputStream);

            return baos.toString();
        } catch (IOException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);
        }

        return "no string";
    }

    // считывает из файла в ByteBuffer
    public static ByteBuffer resourceToByteBuffer(String resource) throws IOException
    {
        // создаю файл с путем resource
        File file = new File(resource);

        // создаю FileInputStream для чтения из файла
        FileInputStream fileInputStream = new FileInputStream(file);
        // создаю FileChannel для чтения
        FileChannel fileChannel = fileInputStream.getChannel();

        // создаю buffer с размером fileChannel.size() + 1 (+1 для символа конца строки)
        ByteBuffer buffer = BufferUtils.createByteBuffer((int) fileChannel.size() + 1);

        // считываю последовательность байтов из fileChannel в buffer
        while(fileChannel.read(buffer) != -1) {
            ;
        }
        // закрываю поток fileInputStream
        fileInputStream.close();
        // закрываю поток fileChannel
        fileChannel.close();
        // переключение в ввод/вывод
        buffer.flip();

        return buffer;
    }

    public static byte[] getByteBufferBytes(ByteBuffer buffer)
    {
        int length = buffer.slice().remaining();
        Log.CurrentSession.println("buffer length: " + length, Log.MessageType.ERROR);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    public static int getRandom(int min, int max)
    {
        int dif = max - min;
        return random.nextInt(dif + 1) + min;
    }

    public static double getRandom(double min, double max)
    {
        return min + Math.random() * (max - min);
    }

    public static boolean isPointInNoRotatedObject(Vector2f mousePosition, Object2D object2D)
    {
        Transform objectTransform = object2D.getComponent(TransformComponent.class).getTransform();

        return mousePosition.x >= objectTransform.getPosition().x &&
                mousePosition.x <= objectTransform.getPosition().x + 100.0f * objectTransform.getScale().x &&
                mousePosition.y >= objectTransform.getPosition().y &&
                mousePosition.y <= objectTransform.getPosition().y + 100.0f * objectTransform.getScale().y;
    }
}
