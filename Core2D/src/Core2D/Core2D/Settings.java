package Core2D.Core2D;

import org.joml.Vector3f;

public class Settings
{
    public enum QualityType
    {
        LOW,
        MEDIUM,
        HIGH
    }

    public static class Debug
    {
        public static boolean ENABLE_DEBUG_PHYSICS_DRAWING = false;
    }

    public static class Graphics
    {
        public static class TexturesQuality
        {
            public static class TexturesFiltrationQuality
            {
                public static QualityType quality = QualityType.LOW;
            }
        }
    }

    public static class System
    {
        public static boolean sleepSystem = false;
    }

    public static class Other
    {
        public static class Picking
        {
            // текущий максимальный цвет для picking
            public static final Vector3f currentPickingColor = new Vector3f(0.0f, 0.0f, 0.0f);
        }
    }
}
