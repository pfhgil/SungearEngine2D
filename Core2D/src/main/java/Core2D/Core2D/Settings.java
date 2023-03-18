package Core2D.Core2D;

import Core2D.Graphics.OpenGL.OpenGL;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11C;

public class Settings
{
    public static void init()
    {
        Graphics.setStencilTestActive(Graphics.isStencilTestActive());
    }

    /**
     * Types of quality .
     */
    public enum QualityType
    {
        LOW,
        MEDIUM,
        HIGH
    }

    public static class Graphics
    {
        public static class TexturesQuality
        {
            /**
             * Texture filtering quality. By default, it is at the low settings.
             */
            public static class TexturesFiltrationQuality
            {
                public static QualityType quality = QualityType.LOW;
            }
        }

        private static boolean stencilTestActive = true;

        public static boolean isStencilTestActive() { return stencilTestActive; }

        public static void setStencilTestActive(boolean stencilTestActive)
        {
            Graphics.stencilTestActive = stencilTestActive;

            if(stencilTestActive) {
                OpenGL.glCall((params) -> GL11C.glEnable(GL11C.GL_STENCIL_TEST));
            } else {
                OpenGL.glCall((params) -> GL11C.glDisable(GL11C.GL_STENCIL_TEST));
            }
        }
    }

    /**
     * Settings of the Core2D itself.
     */
    public static class Core2D
    {
        /**
         * If true then the Core2D needs to fall asleep.
         */
        public static boolean sleepCore2D = false;

        public static int destinationFPS = 60;
    }

    public static class Other
    {
        /**
         * Objects picking settings.
         */
        public static class Picking
        {
            /**
             * Current maximum color for the object.
             */
            // текущий максимальный цвет для picking
            public static final Vector3f currentPickingColor = new Vector3f(0.0f, 0.0f, 0.0f);
        }
    }
}
