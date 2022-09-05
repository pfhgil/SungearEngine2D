package Core2D.UI.Text;

import Core2D.AssetManager.AssetManager;
import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Components.TextureComponent;
import Core2D.Component.Components.TransformComponent;
import Core2D.Instancing.ObjectsInstancing;
import Core2D.Object2D.Object2D;
import Core2D.Object2D.Transform;
import Core2D.Shader.ShaderProgram;
import Core2D.Utils.PositionsQuad;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

public class Text extends CommonDrawableObjectsParameters
{
    private String text;
    private ObjectsInstancing textObjectsInstancing;
    private Font font;

    private Vector2f position = new Vector2f();
    private Vector2f scale = new Vector2f(1.0f, 1.0f);
    private Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // длина текста
    private float width = 0.0f;

    // максимальная высота текста
    private float maxHeight = 0.0f;

    // минимальная высота
    private float minHeight = 0.0f;

    // средняя высота
    private float averageHeight = 0.0f;

    private Object2D[] chars;

    // дефолтный отступ вправо при scale.x = 1.0f (при пробеле)
    private float defaultRightStep = 10.0f;
    // дефолтный отступ вниз при scale.y = 1.0f (при использовании символа переноса строки)
    private float defaultDownStep = 28.0f;

    // текущие отступы
    private float currentRightStep = defaultRightStep;
    private float currentDownStep = defaultDownStep;

    private ShaderProgram shaderProgram;

    private boolean isUIElement = true;

    public Text(Font font)
    {
        this.font = font;

        createShaderProgram();

        setText("hello world!");
    }

    public Text(Font font, String text)
    {
        this.font = font;

        createShaderProgram();

        setText(text);
    }

    private void createShaderProgram()
    {
        shaderProgram = AssetManager.getShaderProgram("textInstancingProgram");
    }

    private void calculateMinHeight()
    {
        minHeight = maxHeight;
        for(int i = 0; i < textObjectsInstancing.getDrawableObjects2D().size(); i++) {
            float curHeight = Math.abs(textObjectsInstancing.getDrawableObjects2D().get(i).getComponent(TransformComponent.class).getTransform().getScale().y) * 100.0f;
            if(curHeight < minHeight) minHeight = curHeight;
        }
    }

    @Override
    public void destroy()
    {
        shouldDestroy = true;

        text = null;
        textObjectsInstancing.destroy();
        textObjectsInstancing = null;

        font = null;

        position = null;
        scale = null;
        color = null;

        Arrays.fill(chars, null);

        chars = null;

        shaderProgram = null;
    }

    public String getText() { return text; }
    public void setText(String text)
    {
        if(chars != null) {
            Arrays.fill(chars, null);
        }

        chars = null;

        if(textObjectsInstancing != null) {
            textObjectsInstancing.destroy();
            textObjectsInstancing = null;
        }

        this.text = text;

        String noSpacesText = text.replaceAll(" ", "");
        noSpacesText = noSpacesText.replaceAll("\n", "");

        chars = new Object2D[noSpacesText.length()];

        char[] textChars = noSpacesText.toCharArray();

        Vector2f fontImageSize = new Vector2f(font.getFontImage().getWidth(), font.getFontImage().getHeight());

        width = 0.0f;
        maxHeight = 0.0f;

        float allHeight = 0.0f;

        for(int i = 0; i < chars.length; i++) {
            //System.out.println(textChars[i]);
            Font.Glyph currentGlyph = font.getGlyphsMap().get(textChars[i]);

            Vector2f charScale = new Vector2f(currentGlyph.getSize().x / 100.0f * scale.x, -currentGlyph.getSize().y / 100.0f * scale.y);
            float curCharHeight = Math.abs(charScale.y) * 100.0f;

            chars[i] = new Object2D();
            chars[i].setUIElement(isUIElement);
            chars[i].setColor(new Vector4f(color));
            chars[i].getComponent(TransformComponent.class).getTransform().setScale(charScale);

            allHeight += curCharHeight;

            if(curCharHeight > maxHeight) maxHeight = curCharHeight;

            chars[i].getComponent(TextureComponent.class).setUV(new PositionsQuad(
                    new Vector2f(currentGlyph.getTexturePosition().x / fontImageSize.x, 1.0f - (currentGlyph.getTexturePosition().y / fontImageSize.y)),
                    new Vector2f(currentGlyph.getTexturePosition().x / fontImageSize.x, 1.0f - ((currentGlyph.getTexturePosition().y + currentGlyph.getSize().y) / fontImageSize.y)),
                    new Vector2f((currentGlyph.getTexturePosition().x + currentGlyph.getSize().x) / fontImageSize.x, 1.0f - ((currentGlyph.getTexturePosition().y + currentGlyph.getSize().y) / fontImageSize.y)),
                    new Vector2f((currentGlyph.getTexturePosition().x + currentGlyph.getSize().x) / fontImageSize.x, 1.0f - (currentGlyph.getTexturePosition().y / fontImageSize.y))
            ));

            width += charScale.x * 100.0f;

            charScale = null;
        }

        averageHeight = allHeight / chars.length;

        char[] withSpacesText = text.toCharArray();

        for(int i = 0; i < withSpacesText.length; i++) {
            if(withSpacesText[i] == ' ') {
                width += currentRightStep;
            }
        }

        createShaderProgram();
        textObjectsInstancing = new ObjectsInstancing(chars, font.getFontImage(), isUIElement);
        textObjectsInstancing.setShaderProgram(shaderProgram);

        calculateMinHeight();

        noSpacesText = null;
        textChars = null;
        fontImageSize = null;

        setPosition(position);
    }

    public Font getFont() { return font; }
    public void setFont(Font font) { this.font = font; }

    public Vector2f getPosition() { return position; }
    public void setPosition(Vector2f position)
    {
        this.position = position;

        Vector2f currentCharPosition = new Vector2f(position);

        char[] textChars = text.toCharArray();

        int iter = 0;

        for(int i = 0; i < textChars.length; i++) {
            Font.Glyph currentGlyph = font.getGlyphsMap().get(textChars[i]);

            if(textChars[i] == ' ') {
                currentCharPosition.x += currentRightStep;
            } else if(textChars[i] == (char) 10) {
                // (char) 10 - символ переноса строки
                currentCharPosition.y -= currentDownStep;
                currentCharPosition.x = position.x;
            } else {
                Transform charTransform = textObjectsInstancing.getDrawableObjects2D().get(i).getComponent(TransformComponent.class).getTransform();

                charTransform.setPosition(new Vector2f(currentCharPosition).add(new Vector2f(currentGlyph.getOffset()).negate().mul(scale)));
                currentCharPosition.x += charTransform.getScale().x * 100.0f;

                iter++;

                charTransform = null;
            }
        }

        textChars = null;
        currentCharPosition = null;
    }

    public Vector2f getScale() { return scale; }
    public void setScale(Vector2f scale)
    {
        this.scale = scale;

        currentRightStep = defaultRightStep * scale.x;
        currentDownStep = defaultDownStep * scale.y;

        String noSpacesText = text.replaceAll(" ", "");
        noSpacesText = noSpacesText.replaceAll("\n", "");
        char[] textChars = noSpacesText.toCharArray();

        width = 0.0f;
        maxHeight = 0.0f;

        float allHeight = 0.0f;

        for(int i = 0; i < textObjectsInstancing.getDrawableObjects2D().size(); i++) {
            Transform charTransform = textObjectsInstancing.getDrawableObjects2D().get(i).getComponent(TransformComponent.class).getTransform();

            Font.Glyph currentGlyph = font.getGlyphsMap().get(textChars[i]);

            charTransform.setScale(new Vector2f(currentGlyph.getSize().x / 100.0f * scale.x, currentGlyph.getSize().y / 100.0f * -scale.y));

            width += charTransform.getScale().x * 100.0f;

            float curHeight = Math.abs(charTransform.getScale().y) * 100.0f;

            allHeight += curHeight;

            if(curHeight > maxHeight) maxHeight = curHeight;

            charTransform = null;
        }

        averageHeight = allHeight / textObjectsInstancing.getDrawableObjects2D().size();

        calculateMinHeight();

        char[] withSpacesText = text.toCharArray();

        for(int i = 0; i < withSpacesText.length; i++) {
            if(withSpacesText[i] == ' ') {
                width += currentRightStep;
            }
        }

        noSpacesText = null;
        textChars = null;

        setPosition(position);
    }

    public Vector4f getColor() { return color; }
    public void setColor(Vector4f color)
    {
        this.color = color;

        for(int i = 0; i < textObjectsInstancing.getDrawableObjects2D().size(); i++) {
            textObjectsInstancing.getDrawableObjects2D().get(i).setColor(new Vector4f(color));
        }
    }

    public float getWidth() { return width; }

    public float getMaxHeight() { return maxHeight; }

    public float getMinHeight() { return minHeight; }

    public float getAverageHeight() { return averageHeight; }

    public boolean isUIElement() { return isUIElement; }
    public void setUIElement(boolean isUIElement)
    {
        this.isUIElement = isUIElement;

        textObjectsInstancing.setUIInstancing(this.isUIElement);
        for(Object2D glyph : textObjectsInstancing.getDrawableObjects2D()) {
            glyph.setUIElement(this.isUIElement);
        }
    }

    public ObjectsInstancing getTextObjectsInstancing() { return textObjectsInstancing; }
}
