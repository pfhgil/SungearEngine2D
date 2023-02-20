package Core2D.ECS.Component.Components.Shader;

import Core2D.AssetManager.AssetManager;
import Core2D.ECS.Component.Component;
import Core2D.Graphics.RenderParts.Texture2D;

public class TextureComponent extends Component
{
    private Texture2D texture = new Texture2D(AssetManager.getInstance().getTexture2DData("/data/textures/white_texture.png"));

    @Override
    public void destroy()
    {
        if(this.texture != null) {
            this.texture.destroy();
        }
    }

    public Texture2D getTexture() { return texture; }
    public void setTexture(Texture2D texture)
    {
        if(this.texture != null) {
            this.texture.destroy();
        }
        this.texture = texture;
    }
}
