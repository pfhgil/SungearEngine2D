package Core2D.DataClasses;

import java.io.InputStream;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class Material2DData extends Data
{
    public int blendSourceFactor = GL_SRC_ALPHA;
    public int blendDestinationFactor = GL_ONE_MINUS_SRC_ALPHA;

    @Override
    public Data load(String path)
    {

        return this;
    }

    @Override
    public Data load(InputStream inputStream, String path)
    {

        return this;
    }
}
