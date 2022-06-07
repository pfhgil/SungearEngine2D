package Core2D.Component.Components;

import Core2D.Component.Component;
import Core2D.Component.NonDuplicated;
import Core2D.Core2D.Resources;
import Core2D.ShaderUtils.ShaderUtils;
import Core2D.ShaderUtils.VertexBufferObject;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.PositionsQuad;
import org.joml.Vector2f;

public class TextureComponent extends Component implements NonDuplicated
{
    private Texture2D texture2D = null;
    private boolean active = true;

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
        getObject2D().getShaderProgram().bind();

        ShaderUtils.setUniform(
                getObject2D().getShaderProgram().getHandler(),
                "useSampler",
                false
        );

        getObject2D().getShaderProgram().unBind();

        setObject2D(null);

        if(!this.texture2D.equals(Resources.Textures.WHITE_TEXTURE)) {
            this.texture2D.destroy();
        }
        this.texture2D = null;

        UV = null;
    }

    @Override
    public void set(Component component)
    {
        if(component instanceof TextureComponent) {
            if (!this.texture2D.equals(Resources.Textures.WHITE_TEXTURE)) {
                this.texture2D.destroy();
            }
            this.texture2D = null;

            TextureComponent textureComponent = (TextureComponent) component;

            setTexture2D(textureComponent.getTexture2D());
            setActive(textureComponent.isActive());
            setUV(textureComponent.getUV());

            textureComponent = null;
        }

        component = null;
    }

    @Override
    public void init()
    {
        setUV(getUV());
        setTexture2D(Resources.Textures.WHITE_TEXTURE);
    }

    public Texture2D getTexture2D() { return texture2D; }
    public void setTexture2D(Texture2D texture2D)
    {
        this.texture2D = texture2D;
        texture2D = null;

        if(this.texture2D != null) {
            getObject2D().getShaderProgram().bind();

            ShaderUtils.setUniform(
                    getObject2D().getShaderProgram().getHandler(),
                    "sampler",
                    this.texture2D.getFormattedTextureBlock()
            );

            ShaderUtils.setUniform(
                    getObject2D().getShaderProgram().getHandler(),
                    "useSampler",
                    true
            );

            getObject2D().getShaderProgram().unBind();
        }
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active)
    {
        this.active = active;

        getObject2D().getShaderProgram().bind();

        ShaderUtils.setUniform(
                getObject2D().getShaderProgram().getHandler(),
                "useSampler",
                active
        );

        getObject2D().getShaderProgram().unBind();
    }

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
        if(getObject2D() != null) {
            getObject2D().getData()[2] = UV[0];
            getObject2D().getData()[3] = UV[1];

            getObject2D().getData()[6] = UV[2];
            getObject2D().getData()[7] = UV[3];

            getObject2D().getData()[10] = UV[4];
            getObject2D().getData()[11] = UV[5];

            getObject2D().getData()[14] = UV[6];
            getObject2D().getData()[15] = UV[7];

            VertexBufferObject vbo = getObject2D().getVertexArrayObject().getVBOs().get(0);
            getObject2D().getVertexArrayObject().updateVBO(vbo, getObject2D().getData());
            vbo = null;
        }
    }
}
