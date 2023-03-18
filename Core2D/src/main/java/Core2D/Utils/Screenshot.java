package Core2D.Utils;

import Core2D.Core2D.Core2D;
import Core2D.Graphics.OpenGL.OpenGL;
import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11C;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.opengl.GL11C.glReadPixels;

public class Screenshot
{
    // сделать скрин и сохранить по пути toPath
    public static void take(String toDir)
    {
        int[] pixels = new int[Core2D.getWindow().getSize().x * Core2D.getWindow().getSize().y];
        int bindex = 0;
        ByteBuffer bb = ByteBuffer.allocateDirect(Core2D.getWindow().getSize().x * Core2D.getWindow().getSize().y * 3);

        OpenGL.glCall((params) -> glReadPixels(0,0, Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y, GL11C.GL_RGB, GL11C.GL_UNSIGNED_BYTE, bb));

        BufferedImage imageIn = new BufferedImage(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y,BufferedImage.TYPE_INT_RGB);
        // convert RGB data in ByteBuffer to integer array
        for (int i = 0; i < pixels.length; i++) {
            bindex = i * 3;
            pixels[i] = ((bb.get(bindex) & 0x0ff) << 16) | ((bb.get(bindex + 1) & 0x0ff) << 8) | (bb.get(bindex + 2) & 0x0ff);
        }
        //Allocate colored pixel to buffered Image
        imageIn.setRGB(0, 0, Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y, pixels, 0 , Core2D.getWindow().getSize().x);

        //Creating the transformation direction (horizontal)
        AffineTransform at =  AffineTransform.getScaleInstance(1, -1);
        at.translate(0, -imageIn.getHeight(null));

        //Applying transformation
        AffineTransformOp opRotated = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage imageOut = opRotated.filter(imageIn, null);

        try {
            // получаю текущую дату, час, минуту, секунду
            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
            Date date = new Date();

            ImageIO.write(imageOut, "png", new File(toDir + "/" + dateFormat.format(date) + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    // сделать скрин и сохранить по пути toPath
    public static void take(String toDir, boolean applyBlur, int radius, float brightnessCoeff)
    {
        int[] pixels = new int[Core2D.getWindow().getSize().x * Core2D.getWindow().getSize().y];
        int bindex = 0;
        ByteBuffer bb = ByteBuffer.allocateDirect(Core2D.getWindow().getSize().x * Core2D.getWindow().getSize().y * 3);

        OpenGL.glCall((params) -> glReadPixels(0,0, Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y, GL11C.GL_RGB, GL11C.GL_UNSIGNED_BYTE, bb));

        BufferedImage imageIn = new BufferedImage(Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y,BufferedImage.TYPE_INT_RGB);
        // convert RGB data in ByteBuffer to integer array
        for (int i = 0; i < pixels.length; i++) {
            bindex = i * 3;
            pixels[i] = ((bb.get(bindex) & 0x0ff) << 16) | ((bb.get(bindex + 1) & 0x0ff) << 8) | (bb.get(bindex + 2) & 0x0ff);
        }

        if(applyBlur) {
            int iter = 0;

            for(int x = 0; x < Core2D.getWindow().getSize().x; x++) {
                for (int y = 0; y < Core2D.getWindow().getSize().y; y++) {
                    Vector3f resultColor = new Vector3f(0.0f, 0.0f, 0.0f);

                    for(int x1 = x - radius; x1 < x + radius; x1++) {
                        for (int y1 = y - radius; y1 < y + radius; y1++) {
                            if (x1 > 0 && x1 < Core2D.getWindow().getSize().x &&
                                    y1 > 0 && y1 < Core2D.getWindow().getSize().y) {
                                int x0 = x - x1;
                                int y0 = y - y1;

                                if (x0 * x0 + y0 * y0 <= radius * radius) {
                                    resultColor.add(ColorUtils.intToVector3f(pixels[x1 * Core2D.getWindow().getSize().x + y1]).mul(1.0f / (pixels.length)));
                                }
                            }
                        }
                    }

                    resultColor.mul(brightnessCoeff);


                    Color resCol = new Color(
                            Math.max(0.0f, Math.min(1.0f, resultColor.x)),
                            Math.max(0.0f, Math.min(1.0f, resultColor.y)),
                            Math.max(0.0f, Math.min(1.0f, resultColor.z))
                    );

                    pixels[iter] = resCol.getRGB();

                    iter++;
                }
            }
        }

        //Allocate colored pixel to buffered Image
        imageIn.setRGB(0, 0, Core2D.getWindow().getSize().x, Core2D.getWindow().getSize().y, pixels, 0 , Core2D.getWindow().getSize().x);

        //Creating the transformation direction (horizontal)
        AffineTransform at =  AffineTransform.getScaleInstance(1, -1);
        at.translate(0, -imageIn.getHeight(null));

        //Applying transformation
        AffineTransformOp opRotated = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage imageOut = opRotated.filter(imageIn, null);

        try {
            // получаю текущую дату, час, минуту, секунду
            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
            Date date = new Date();

            ImageIO.write(imageOut, "png", new File(toDir + "/" + dateFormat.format(date) + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
