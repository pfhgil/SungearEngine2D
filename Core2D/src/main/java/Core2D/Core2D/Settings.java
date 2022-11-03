package Core2D.Core2D;

import org.joml.Vector3f;

public class Settings
{
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
