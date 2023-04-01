package arch.DataClasses;

import Core2D.DataClasses.Data;

import java.io.InputStream;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Material2DData extends Data
{
    public transient int param = GL_CLAMP_TO_EDGE;

    public transient int blendSourceFactor = GL_SRC_ALPHA;
    public transient int blendDestinationFactor = GL_ONE_MINUS_SRC_ALPHA;

    @Override
    public Data load(String path) {
        return null;
    }

    @Override
    public Data load(InputStream inputStream, String path) {
        return null;
    }
}
