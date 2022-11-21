package arch.DataClasses;

import com.google.gson.annotations.SerializedName;

import java.nio.ByteBuffer;

public class Texture2DData
{
    private transient ByteBuffer pixelsData;

    private transient int width;
    private transient int height;
    private transient int channels;

    private transient int format;
    private transient int internalFormat;
}
