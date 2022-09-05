package Core2D.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Log.Log;
import Core2D.ShaderUtils.VertexBufferObject;
import Core2D.Texture2D.Texture2D;
import Core2D.Texture2D.TextureDrawModes;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.PositionsQuad;
import org.joml.Vector2f;

public class TextureComponent extends Component implements NonDuplicated
{
    private Texture2D texture2D = new Texture2D();
    private boolean active = true;
    private int textureDrawMode = TextureDrawModes.DEFAULT;

    private float[] UV = new float[] {
            0.0f, 0.0f,

            0.0f, 1.0f,

            1.0f, 1.0f,

            1.0f, 0.0f,
    };

    public TextureComponent()
    {

    }

    @Override
    public void destroy()
    {
        object2D.getShaderProgram().bind();

        textureDrawMode = TextureDrawModes.NO_TEXTURE;

        object2D.getShaderProgram().unBind();

        object2D = null;

        if(this.texture2D.getTextureHandler() != ((Texture2D) AssetManager.getAsset("whiteTexture").getAsset()).getTextureHandler()) {
            this.texture2D.destroy();
        }
        this.texture2D = null;

        UV = null;
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof TextureComponent) {
            if (this.texture2D.getTextureHandler() != ((Texture2D) AssetManager.getAsset("whiteTexture").getAsset()).getTextureHandler()) {
                this.texture2D.destroy();
            }
            this.texture2D = null;
            this.texture2D = new Texture2D();

            TextureComponent textureComponent = (TextureComponent) component;

            setTexture2D(textureComponent.getTexture2D());
            setActive(textureComponent.isActive());
            setUV(textureComponent.getUV());
        }

        component = null;
    }

    @Override
    public void init()
    {
        setUV(getUV());
        setTexture2D(AssetManager.getTexture2D("whiteTexture"));
    }

    public Texture2D getTexture2D() { return texture2D; }
    public void setTexture2D(Texture2D texture2D)
    {
        this.texture2D.set(texture2D);

        if(this.texture2D != null) {
            textureDrawMode = TextureDrawModes.DEFAULT;
        }
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active)
    {
        this.active = active;

        if(active) {
            textureDrawMode = TextureDrawModes.DEFAULT;
        } else {
            textureDrawMode = TextureDrawModes.NO_TEXTURE;
        }
    }

    public int getTextureDrawMode() { return textureDrawMode; }
    public void setTextureDrawMode(int textureDrawMode) { this.textureDrawMode = textureDrawMode; }

    public float[] getUV() { return UV; }
    public void setUV(float[] UV)
    {
        this.UV = UV;

        updateUV();

        UV = null;
    }
    public void setUV(Vector2f bottomLeft, Vector2f upLeft, Vector2f upRight, Vector2f bottomRight)
    {
        UV[0] = bottomLeft.x;
        UV[1] = bottomLeft.y;

        UV[2] = upLeft.x;
        UV[3] = upLeft.y;

        UV[4] = upRight.x;
        UV[5] = upRight.y;

        UV[6] = bottomRight.x;
        UV[7] = bottomRight.y;

        updateUV();

        bottomLeft = null;
        upLeft = null;
        upRight = null;
        bottomRight = null;
    }
    public void setUV(PositionsQuad positionsQuad)
    {
        Vector2f offset = new Vector2f(1.0f / positionsQuad.getAtlasSize().x / 2.0f, 1.0f / positionsQuad.getAtlasSize().y / 2.0f);

        Vector2f resP0 = new Vector2f(positionsQuad.getLeftBottom().x / positionsQuad.getAtlasSize().x + offset.x, positionsQuad.getLeftBottom().y / positionsQuad.getAtlasSize().y + offset.y);
        Vector2f resP1 = new Vector2f(positionsQuad.getLeftTop().x / positionsQuad.getAtlasSize().x + offset.x, positionsQuad.getLeftTop().y / positionsQuad.getAtlasSize().y);
        Vector2f resP2 = new Vector2f(positionsQuad.getRightTop().x / positionsQuad.getAtlasSize().x, positionsQuad.getRightTop().y / positionsQuad.getAtlasSize().y);
        Vector2f resP3 = new Vector2f(positionsQuad.getRightBottom().x / positionsQuad.getAtlasSize().x, positionsQuad.getRightBottom().y / positionsQuad.getAtlasSize().y + offset.y);

        UV[0] = resP0.x;
        UV[1] = resP0.y;

        UV[2] = resP1.x;
        UV[3] = resP1.y;

        UV[4] = resP2.x;
        UV[5] = resP2.y;

        UV[6] = resP3.x;
        UV[7] = resP3.y;

        updateUV();

        positionsQuad = null;
    }

    private void updateUV()
    {
        if(object2D != null) {
            object2D.getData()[2] = UV[0];
            object2D.getData()[3] = UV[1];

            object2D.getData()[6] = UV[2];
            object2D.getData()[7] = UV[3];

            object2D.getData()[10] = UV[4];
            object2D.getData()[11] = UV[5];

            object2D.getData()[14] = UV[6];
            object2D.getData()[15] = UV[7];

            VertexBufferObject vbo = object2D.getVertexArrayObject().getVBOs().get(0);
            object2D.getVertexArrayObject().updateVBO(vbo, object2D.getData());
            vbo = null;
        }
    }
}
