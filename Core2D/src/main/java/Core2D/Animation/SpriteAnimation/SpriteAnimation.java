package Core2D.Animation.SpriteAnimation;

import Core2D.ECS.Mesh.MeshComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Texture2D;
import Core2D.Utils.PositionsQuad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class SpriteAnimation
{
    private List<PositionsQuad> atlasTexturesPositions = new ArrayList<>();

    private List<Texture2D> sprites = new ArrayList<>();

    private boolean useAtlas;

    private float currentTime;
    private float changeSpriteTime;

    private int currentSprite = 0;

    private transient Entity attachedEntity;

    private boolean active = true;

    public SpriteAnimation(Entity attachedEntity, float changeSpriteTime, PositionsQuad... positionsQuads)
    {
        this.attachedEntity = attachedEntity;
        this.changeSpriteTime = changeSpriteTime;

        useAtlas = true;

        atlasTexturesPositions.addAll(Arrays.asList(positionsQuads));
    }

    public SpriteAnimation(Entity attachedEntity, float changeSpriteTime, List<Texture2D> sprites)
    {
        this.attachedEntity = attachedEntity;
        this.changeSpriteTime = changeSpriteTime;

        useAtlas = false;

        this.sprites.addAll(sprites);
    }

    public SpriteAnimation(SpriteAnimation spriteAnimation)
    {
        this.attachedEntity = spriteAnimation.getAttachedObject2D();
        this.changeSpriteTime = spriteAnimation.getChangeSpriteTime();

        for(PositionsQuad positionsQuad : spriteAnimation.getAtlasTexturesPositions()) {
            this.atlasTexturesPositions.add(new PositionsQuad(positionsQuad));
        }

        this.useAtlas = spriteAnimation.isUseAtlas();

        this.sprites.addAll(spriteAnimation.getSprites());
    }

    public void update(float deltaTime)
    {
        if(active) {
            currentTime += deltaTime;

            if (currentTime >= changeSpriteTime) {
                currentTime = 0.0f;

                setCurrentSprite(currentSprite);

                currentSprite++;
            }
        }
    }

    public void destroy()
    {
        Iterator<PositionsQuad> positionsQuadIterator = atlasTexturesPositions.iterator();
        while(positionsQuadIterator.hasNext()) {
            PositionsQuad positionsQuad = positionsQuadIterator.next();
            positionsQuad.destroy();
            positionsQuad = null;
            positionsQuadIterator.remove();
        }
        atlasTexturesPositions = null;

        Iterator<Texture2D> textures2DIterator = sprites.iterator();
        while(textures2DIterator.hasNext()) {
            Texture2D texture2D = textures2DIterator.next();
            texture2D = null;
            textures2DIterator.remove();
        }
        sprites = null;

        attachedEntity = null;
    }


    public Entity getAttachedObject2D() { return attachedEntity; }

    public float getChangeSpriteTime() { return changeSpriteTime; }
    public void setChangeSpriteTime(float changeSpriteTime) { this.changeSpriteTime = changeSpriteTime; }

    public List<PositionsQuad> getAtlasTexturesPositions() { return atlasTexturesPositions; }

    public boolean isUseAtlas() { return useAtlas; }

    public List<Texture2D> getSprites() { return sprites; }

    public void setCurrentTime(float currentTime) { this.currentTime = currentTime; }

    public void setCurrentSprite(int currentSprite)
    {
        this.currentSprite = currentSprite;

        if (useAtlas) {
            if (this.currentSprite > atlasTexturesPositions.size() - 1) {
                this.currentSprite = 0;
            }

            //attachedEntity.getComponent(MeshComponent.class).setUV(atlasTexturesPositions.get(this.currentSprite));
        } else {
            if (this.currentSprite > atlasTexturesPositions.size() - 1) {
                this.currentSprite = 0;
            }

            MeshComponent textureComponent = attachedEntity.getComponent(MeshComponent.class);
            if(textureComponent != null) {
                //textureComponent.setTexture(sprites.get(this.currentSprite));
            }
        }
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
