package Core2D.AtlasDrawing;

import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Object2D.Object2D;
import Core2D.Texture2D.Texture2D;

import java.util.ArrayList;
import java.util.List;

public class AtlasDrawing extends CommonDrawableObjectsParameters
{
    private List<Object2D> drawableObjects2D;
    private Texture2D atlasTexture2D;

    public AtlasDrawing(Texture2D atlasTexture2D)
    {
        drawableObjects2D = new ArrayList<>();
        this.atlasTexture2D = atlasTexture2D;
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        for(int i = 0; i < drawableObjects2D.size(); i++) {
            drawableObjects2D.get(i).deltaUpdate(deltaTime);
        }
    }

    @Override
    public void destroy()
    {
        shouldDestroy = true;

        for(Object2D obj : drawableObjects2D) {
            obj.destroy();
            obj = null;
        }

        drawableObjects2D.clear();
        drawableObjects2D = null;

        atlasTexture2D = null;

        destroyParams();
    }

    public Texture2D getAtlasTexture2D() { return atlasTexture2D; }

    public List<Object2D> getDrawableObjects2D() { return drawableObjects2D; }
}
