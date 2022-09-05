package SungearEngine2D.Main;

import Core2D.Core2D.Core2D;
import Core2D.Texture2D.Texture2D;
import org.lwjgl.opengl.GL13;
import org.w3c.dom.Text;

public class Resources
{
    public static class Textures
    {
        public static class Icons
        {
            public static final Texture2D directoryIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/directory.png"), GL13.GL_REPEAT);
            public static final Texture2D imageFileIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/image_file.png"));
            public static final Texture2D javaFileIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/java_file.png"));
            public static final Texture2D object2DFileIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/object2d_file.png"));
            public static final Texture2D textFileIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/text_file.png"));
            public static final Texture2D unknownFileIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/unknown_file.png"));

            public static final Texture2D layersIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/layers.png"));

            public static final Texture2D threeDotsIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/three_dots_icon.png"));

            public static final Texture2D playButtonIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/play_button.png"));
            public static final Texture2D stopButtonIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/stop_button.png"));
            public static final Texture2D pauseButtonIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/pause_button.png"));

            public static final Texture2D cameraIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/camera_icon.png"));

            public static final Texture2D gizmoTranslationIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/gizmo_translation.png"));
            public static final Texture2D gizmoRotationIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/gizmo_rotation.png"));
            public static final Texture2D gizmoScaleIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/gizmo_scale.png"));

            public static final Texture2D noneIcon = new Texture2D(Core2D.class.getResourceAsStream("/data/icons/none.png"));
        }

        public static class Grid
        {
            public static final Texture2D gridChunkTexture = new Texture2D(Core2D.class.getResourceAsStream("/data/grid/grid.png"), GL13.GL_REPEAT);
        }

        public static class Gizmo
        {
            public static final Texture2D gizmoArrow = new Texture2D(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_arrow.png"));
            public static final Texture2D gizmoPoint = new Texture2D(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_point.png"));
            public static final Texture2D gizmoCircle = new Texture2D(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_circle.png"));
        }
    }
}
