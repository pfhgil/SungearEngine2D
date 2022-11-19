package Core2D.DataClasses;

import Core2D.Core2D.Settings;
import Core2D.Log.Log;
import Core2D.Utils.ExceptionsUtils;
import Core2D.Utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Deprecated
public class FontData
{
    private Texture2DData fontTextureData = new Texture2DData();
    private String fontDescription = "";

    public static FontData load(String fontPathWithoutExtension)
    {
        FontData fontData = new FontData();
        try {
            fontData.set(load(
                    new BufferedInputStream(new FileInputStream(fontPathWithoutExtension + ".fnt")),
                    new BufferedInputStream(new FileInputStream(fontPathWithoutExtension + ".png"))));
        } catch (FileNotFoundException e) {
            Log.CurrentSession.println(ExceptionsUtils.toString(new RuntimeException(e)), Log.MessageType.ERROR);
        }
        return fontData;
    }

    public static FontData load(InputStream descriptionInputStream, InputStream fontImageInputStream)
    {
        //descriptionInputStream.
        FontData fontData = new FontData();

        Settings.QualityType lastQuality = Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality;
        Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = Settings.QualityType.HIGH;
        fontData.fontTextureData = new Texture2DData().load(fontImageInputStream);
        Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = lastQuality;

        fontData.fontDescription = FileUtils.readAllFile(descriptionInputStream);

        return fontData;
    }

    public void set(FontData fontData)
    {
        fontTextureData.set(fontData.fontTextureData);
        fontDescription = fontData.fontDescription;
    }

    public Texture2DData getFontTextureData() { return fontTextureData; }

    public String getFontDescription() { return fontDescription; }
}
