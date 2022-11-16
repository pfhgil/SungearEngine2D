package Core2D.Drawable;

import Core2D.Core2D.Core2D;
import Core2D.Texture2D.Texture2D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// deprecated потому, что движок не поддерживает данный тип рендеринга
@Deprecated
public class AtlasDrawing extends Drawable
{
    private List<Object2D> drawableObjects2D;
    private Texture2D atlasTexture2D;

    //private final Consumer<AtlasDrawing> render = Core2D.getMainRenderer()::render;

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
        }

        drawableObjects2D.clear();
        drawableObjects2D = null;

        atlasTexture2D = null;

        //destroyParams();
    }

    @Override
    public void render()
    {
        //render.accept(this);
    }

    public Texture2D getAtlasTexture2D() { return atlasTexture2D; }

    public List<Object2D> getDrawableObjects2D() { return drawableObjects2D; }
}
