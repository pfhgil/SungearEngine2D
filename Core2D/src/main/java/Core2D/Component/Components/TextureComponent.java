package Core2D.Component.Components;

import Core2D.AssetManager.AssetManager;
import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.GameObject.GameObject;
import Core2D.ShaderUtils.VertexBuffer;
import Core2D.GameObject.RenderParts.Texture2D;
import Core2D.Utils.PositionsQuad;
import org.joml.Vector2f;

/**
 * The TextureComponent. This component is NonDuplicated.
 * @see Texture2D
 * @see NonDuplicated
 */
@Deprecated
public class TextureComponent extends Component implements NonDuplicated
{
    private Texture2D texture2D = new Texture2D();

    private int textureDrawMode = Texture2D.TextureDrawModes.DEFAULT;

    private float[] UV = new float[] {
            0.0f, 0.0f,

            0.0f, 1.0f,

            1.0f, 1.0f,

            1.0f, 0.0f,
    };

    /**
     * Removes the texture if it does not equal the white texture. Sets the texture draw mode to NO_TEXTURE.
     * @see Component#destroy()
     */
    @Override
    public void destroy()
    {
        MeshRendererComponent meshRendererComponent = gameObject.getComponent(MeshRendererComponent.class);
        if(meshRendererComponent != null) {
            meshRendererComponent.shader.bind();

            textureDrawMode = Texture2D.TextureDrawModes.NO_TEXTURE;

            meshRendererComponent.shader.unBind();
        }
    }

    /**
     * Applies component parameters to this component.
     * Removes the past texture if it does not equal the white texture.
     * Sets a new texture from the passed TextureComponent, UV for it, and whether it is active.
     * @see Component#set(Component)
     * @see TextureComponent#setTexture2D(Texture2D)
     * @see TextureComponent#setActive(boolean)
     * @see TextureComponent#setUV(float[])
     * @param component TextureComponent.
     */
    @Override
    public void set(Component component)
    {
        if(component instanceof TextureComponent textureComponent) {
            this.texture2D = new Texture2D();

            setTexture2D(textureComponent.getTexture2D());
            setActive(textureComponent.isActive());
            setUV(textureComponent.getUV());
        }
    }

    /**
     * Initializes the component.
     * Sets default UV (0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0), and also sets the white texture.
     * @see Component#init()
     */
    @Override
    public void init()
    {
        setUV(getUV());
        setTexture2D(new Texture2D(AssetManager.getInstance().getTexture2DData("/data/textures/white_texture.png")));
    }

    public Texture2D getTexture2D() { return texture2D; }

    /**
     * Sets a new texture and changes the texture rendering mode to DEFAULT.
     * @param texture2D Texture2D.
     */
    public void setTexture2D(Texture2D texture2D)
    {
        this.texture2D.set(texture2D);

        if(this.texture2D != null) {
            textureDrawMode = Texture2D.TextureDrawModes.DEFAULT;
        }
    }

    /**
     * @see Component#isActive()
     * @return active.
     */
    @Override
    public boolean isActive() { return active; }

    /**
     * Sets whether the texture is active. If active, the texture rendering mode changes to DEFAULT, if not, then to NO_TEXTURE.
     * @see Component#setActive(boolean)
     * @param active Is component active.
     */
    @Override
    public void setActive(boolean active)
    {
        this.active = active;

        if(active) {
            textureDrawMode = Texture2D.TextureDrawModes.DEFAULT;
        } else {
            textureDrawMode = Texture2D.TextureDrawModes.NO_TEXTURE;
        }
    }

    public int getTextureDrawMode() { return textureDrawMode; }
    public void setTextureDrawMode(int textureDrawMode) { this.textureDrawMode = textureDrawMode; }

    public float[] getUV() { return UV; }

    /**
     * Sets the UV for the texture. You should describe UV as follows:
     * {
     *     point0.x, point0.y (lower left point),
     *     point1.x, point1.y (upper left point),
     *     point2.x, point2.y (upper right point),
     *     point3.x, point3.y (lower right point)
     * }
     * @param UV UV.
     */
    public void setUV(float[] UV)
    {
        this.UV = UV;

        updateUV();
    }

    /**
     * Sets the UV for the texture.
     * @see TextureComponent#setUV(float[])
     * @param bottomLeft bottom left point.
     * @param upLeft upper left point.
     * @param upRight upper right point.
     * @param bottomRight bottom right point.
     */
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
    }

    /**
     * Sets the UV for the texture.
     * @see PositionsQuad
     */
    public void setUV(PositionsQuad positionsQuad)
    {
        Vector2f resP0 = new Vector2f(positionsQuad.getLeftBottom().x / positionsQuad.getAtlasSize().x, positionsQuad.getLeftBottom().y / positionsQuad.getAtlasSize().y);
        Vector2f resP1 = new Vector2f(positionsQuad.getLeftTop().x / positionsQuad.getAtlasSize().x, positionsQuad.getLeftTop().y / positionsQuad.getAtlasSize().y);
        Vector2f resP2 = new Vector2f(positionsQuad.getRightTop().x / positionsQuad.getAtlasSize().x, positionsQuad.getRightTop().y / positionsQuad.getAtlasSize().y);
        Vector2f resP3 = new Vector2f(positionsQuad.getRightBottom().x / positionsQuad.getAtlasSize().x, positionsQuad.getRightBottom().y / positionsQuad.getAtlasSize().y);

        UV[0] = resP0.x;
        UV[1] = resP0.y;

        UV[2] = resP1.x;
        UV[3] = resP1.y;

        UV[4] = resP2.x;
        UV[5] = resP2.y;

        UV[6] = resP3.x;
        UV[7] = resP3.y;

        updateUV();
    }

    /**
     * Updates the UV of the bound object in the data array, and also updates the VBO of the bound object.
     * @see GameObject
     */
    public void updateUV()
    {
        if(gameObject != null) {
            MeshRendererComponent meshRendererComponent = gameObject.getComponent(MeshRendererComponent.class);
            meshRendererComponent.getData()[2] = UV[0];
            meshRendererComponent.getData()[3] = UV[1];

            meshRendererComponent.getData()[6] = UV[2];
            meshRendererComponent.getData()[7] = UV[3];

            meshRendererComponent.getData()[10] = UV[4];
            meshRendererComponent.getData()[11] = UV[5];

            meshRendererComponent.getData()[14] = UV[6];
            meshRendererComponent.getData()[15] = UV[7];

            if(meshRendererComponent.getVertexArrayObject() != null) {
                VertexBuffer vbo = meshRendererComponent.getVertexArrayObject().getVBOs().get(0);
                meshRendererComponent.getVertexArrayObject().updateVBO(vbo, meshRendererComponent.getData());
            }
        }
    }
}
