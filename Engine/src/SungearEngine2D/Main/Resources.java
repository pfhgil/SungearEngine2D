package SungearEngine2D.Main;

import Core2D.Core2D.Core2D;
import Core2D.DataClasses.ShaderData;
import Core2D.DataClasses.Texture2DData;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Graphics.RenderParts.Texture2D;

// TODO: в дальнейшем все это перенести в AssetManager
public class Resources
{
    public static void load()
    {
        //Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = Settings.QualityType.MEDIUM;
        Textures.Icons.load();
        Shaders.Grid.load();
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
                directoryIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/directory.png"), "/data/icons/directory.png"));
                imageFileIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/image_file.png"), "/data/icons/image_file.png"));
                javaFileIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/java_file.png"), "/data/icons/java_file.png"));
                object2DFileIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/object2d_file.png"), "/data/icons/object2d_file.png"));
                textFileIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/text_file.png"), "/data/icons/text_file.png"));
                unknownFileIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/unknown_file.png"), "/data/icons/unknown_file.png"));

                layersIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/layers.png"), "/data/icons/layers.png"));

                threeDotsIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/three_dots_icon.png"), "/data/icons/three_dots_icon.png"));

                playButtonIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/play_button.png"), "/data/icons/play_button.png"));
                stopButtonIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/stop_button.png"), "/data/icons/stop_button.png"));
                pauseButtonIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/pause_button.png"), "/data/icons/pause_button.png"));

                cameraIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/camera_icon.png"), "/data/icons/camera_icon.png"));

                gizmoTranslationIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/gizmo_translation.png"), "/data/icons/gizmo_translation.png"));
                gizmoRotationIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/gizmo_rotation.png"), "/data/icons/gizmo_rotation.png"));
                gizmoScaleIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/gizmo_scale.png"), "/data/icons/gizmo_scale.png"));

                noneIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/none.png"), "/data/icons/none.png"));

                checkMarkIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/check_mark.png"), "/data/icons/check_mark.png"));
                xIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/x_icon.png"), "/data/icons/x_icon.png"));
                collapseIcon.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/collapse_icon.png"), "/data/icons/collapse_icon.png"));
            }
        }

        public static class Gizmo
        {
            public static Texture2D gizmoArrow = new Texture2D();
            public static Texture2D gizmoPoint = new Texture2D();
            public static Texture2D gizmoCircle = new Texture2D();

            public static void load()
            {
                gizmoArrow.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_arrow.png"), "/data/gizmo/gizmo_arrow.png"));
                gizmoPoint.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_point.png"), "/data/gizmo/gizmo_point.png"));
                gizmoCircle.createTexture(new Texture2DData().load(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_circle.png"), "/data/gizmo/gizmo_circle.png"));
            }
        }
    }

    public class Shaders
    {
        public static class Grid
        {
            public static Shader gridShader;

            public static void load()
            {
                gridShader = Shader.create(new ShaderData().load(Core2D.class.getResourceAsStream("/data/shaders/grid/shader.glsl"), "/data/shaders/grid/shader.glsl"));
            }
        }
    }
}
