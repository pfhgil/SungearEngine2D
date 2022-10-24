package SungearEngine2D.Main;

import Core2D.Core2D.Core2D;
import Core2D.Texture2D.Texture2D;
import org.lwjgl.opengl.GL13;

// TODO: в дальнейшем все это перенести в AssetManager
public class Resources
{
    public static void load()
    {
        //Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = Settings.QualityType.MEDIUM;
        Textures.Icons.load();
        Textures.Grid.load();
        Textures.Gizmo.load();
        //Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = Settings.QualityType.LOW;

        System.gc();
    }

    public static class Textures
    {
        public static class Icons
        {
            public static Texture2D directoryIcon = new Texture2D();
            public static Texture2D imageFileIcon = new Texture2D();
            public static Texture2D javaFileIcon = new Texture2D();
            public static Texture2D object2DFileIcon = new Texture2D();
            public static Texture2D textFileIcon = new Texture2D();
            public static Texture2D unknownFileIcon = new Texture2D();

            public static Texture2D layersIcon = new Texture2D();

            public static Texture2D threeDotsIcon = new Texture2D();

            public static Texture2D playButtonIcon = new Texture2D();
            public static Texture2D stopButtonIcon = new Texture2D();
            public static Texture2D pauseButtonIcon = new Texture2D();

            public static Texture2D cameraIcon = new Texture2D();

            public static Texture2D gizmoTranslationIcon = new Texture2D();
            public static Texture2D gizmoRotationIcon = new Texture2D();
            public static Texture2D gizmoScaleIcon = new Texture2D();

            public static Texture2D noneIcon = new Texture2D();

            public static Texture2D checkMarkIcon = new Texture2D();

            public static Texture2D xIcon = new Texture2D();

            public static Texture2D collapseIcon = new Texture2D();

            public static void load() {
                directoryIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/directory.png"));
                imageFileIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/image_file.png"));
                javaFileIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/java_file.png"));
                object2DFileIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/object2d_file.png"));
                textFileIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/text_file.png"));
                unknownFileIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/unknown_file.png"));

                layersIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/layers.png"));

                threeDotsIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/three_dots_icon.png"));

                playButtonIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/play_button.png"));
                stopButtonIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/stop_button.png"));
                pauseButtonIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/pause_button.png"));

                cameraIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/camera_icon.png"));

                gizmoTranslationIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/gizmo_translation.png"));
                gizmoRotationIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/gizmo_rotation.png"));
                gizmoScaleIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/gizmo_scale.png"));

                noneIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/none.png"));

                checkMarkIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/check_mark.png"));
                xIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/x_icon.png"));
                collapseIcon.loadTexture(Core2D.class.getResourceAsStream("/data/icons/collapse_icon.png"));
            }
        }

        public static class Grid
        {
            public static Texture2D gridChunkTexture = new Texture2D();

            public static void load()
            {
                gridChunkTexture.param = GL13.GL_REPEAT;
                gridChunkTexture.loadTexture(Core2D.class.getResourceAsStream("/data/grid/grid.png"));
            }
        }

        public static class Gizmo
        {
            public static Texture2D gizmoArrow = new Texture2D();
            public static Texture2D gizmoPoint = new Texture2D();
            public static Texture2D gizmoCircle = new Texture2D();

            public static void load()
            {
                gizmoArrow.loadTexture(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_arrow.png"));
                gizmoPoint.loadTexture(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_point.png"));
                gizmoCircle.loadTexture(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_circle.png"));
            }
        }
    }
}
