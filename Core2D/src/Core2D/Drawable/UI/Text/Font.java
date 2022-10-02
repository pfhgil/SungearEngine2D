package Core2D.Drawable.UI.Text;

import Core2D.Core2D.Settings;
import Core2D.Texture2D.Texture2D;
import Core2D.Utils.FileUtils;
import org.joml.Vector2f;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class Font
{
    public static class Glyph
    {
        private char symbol = '?';

        private Vector2f texturePosition = new Vector2f();
        private Vector2f size = new Vector2f();
        private Vector2f offset = new Vector2f();
        private float xAdvance = 0.0f;

        public Glyph() { }

        public char getSymbol() { return symbol; }
        public void setSymbol(char symbol) { this.symbol = symbol; }

        public Vector2f getTexturePosition() { return texturePosition; }
        public void setTexturePosition(Vector2f texturePosition) { this.texturePosition = texturePosition; }

        public Vector2f getSize() { return size; }
        public void setSize(Vector2f size) { this.size = size; }

        public Vector2f getOffset() { return offset; }
        public void setOffset(Vector2f offset) { this.offset = offset; }

        public float getxAdvance() { return xAdvance; }

        public void setxAdvance(float xAdvance) { this.xAdvance = xAdvance; }
    }

    private List<Glyph> glyphs = new ArrayList<>();
    private Map<Character, Glyph> glyphsMap = new HashMap<>();

    private Texture2D fontImage;

    public Font() { }

    public Font(String descriptionPath, String fontImagePath)
    {
        load(descriptionPath, fontImagePath);
    }

    public Font(InputStream descriptionInputStream, InputStream fontImageInputStream)
    {
        load(descriptionInputStream, fontImageInputStream);
    }

    public void load(String descriptionPath, String fontImagePath)
    {
        Settings.QualityType lastQuality = Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality;
        Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = Settings.QualityType.HIGH;
        fontImage = new Texture2D(fontImagePath);
        Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = lastQuality;

        String fileText = FileUtils.readAllFile(new File(descriptionPath));

        read(fileText);
    }

    public void load(InputStream descriptionInputStream, InputStream fontImageInputStream)
    {
        Settings.QualityType lastQuality = Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality;
        Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = Settings.QualityType.HIGH;
        fontImage = new Texture2D(fontImageInputStream);
        Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = lastQuality;

        String fileText = FileUtils.readAllFile(descriptionInputStream);

        read(fileText);
    }

    private void read(String fontTextFile)
    {
        Scanner linesScanner = new Scanner(fontTextFile);

        while(linesScanner.hasNextLine()) {
            String currentLine = linesScanner.nextLine();

            Scanner valuesScanner = new Scanner(currentLine);
            valuesScanner.useDelimiter("=| ");

            Glyph newGlyph = new Glyph();

            while(valuesScanner.hasNext()) {
                String nextWord = valuesScanner.next();

                if(nextWord.equals("id")) {
                    int charID = valuesScanner.nextInt();

                    newGlyph.setSymbol((char) charID);
                }

                if(nextWord.equals("x")) {
                    newGlyph.getTexturePosition().x = valuesScanner.nextInt();
                }
                if(nextWord.equals("y")) {
                    newGlyph.getTexturePosition().y = valuesScanner.nextInt();
                }

                if(nextWord.equals("width")) {
                    newGlyph.getSize().x = valuesScanner.nextInt();
                }
                if(nextWord.equals("height")) {
                    newGlyph.getSize().y = valuesScanner.nextInt();
                }

                if(nextWord.equals("xoffset")) {
                    newGlyph.getOffset().x = valuesScanner.nextInt();
                }
                if(nextWord.equals("yoffset")) {
                    newGlyph.getOffset().y = valuesScanner.nextInt();
                }

                if(nextWord.equals("xadvance")) {
                    newGlyph.xAdvance = valuesScanner.nextInt();
                }
            }

            glyphs.add(newGlyph);
            glyphsMap.put(newGlyph.getSymbol(), newGlyph);
        }
    }

    public List<Glyph> getGlyphs() { return glyphs; }

    public Map<Character, Glyph> getGlyphsMap() { return glyphsMap; }

    public Texture2D getFontImage() { return fontImage; }
}
