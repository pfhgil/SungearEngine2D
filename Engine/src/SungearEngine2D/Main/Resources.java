package SungearEngine2D.Main;

import Core2D.Core2D.Core2D;
import Core2D.DataClasses.ShaderData;
import Core2D.DataClasses.Texture2DData;
import Core2D.Graphics.RenderParts.Shader;
import org.lwjgl.glfw.GLFW;

// TODO: в дальнейшем все это перенести в AssetManager
public class Resources
{
    public static void load()
    {
        //Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = Settings.QualityType.MEDIUM;
        Textures.Icons.load();
        Shaders.Grid.load();
        Textures.Gizmo.load();
        Cursors.load();
        //Settings.Graphics.TexturesQuality.TexturesFiltrationQuality.quality = Settings.QualityType.LOW;

        System.gc();
    }

    public static class Cursors
    {
        private static long cursorResizeAll;
        private static long cursorArrow;


        public static void load()
        {
            cursorResizeAll = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR);
            cursorArrow = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        }

        public static long getCursorResizeAll() { return cursorResizeAll; }

        public static long getCursorArrow() { return cursorArrow; }
    }

    public static class Textures
    {
        public static class Icons
        {
            public static Texture2DData directoryIcon = new Texture2DData();
            public static Texture2DData openDirectoryIcon = new Texture2DData();
            public static Texture2DData imageFileIcon = new Texture2DData();
            public static Texture2DData javaFileIcon = new Texture2DData();
            public static Texture2DData javaFileIcon14 = new Texture2DData();
            public static Texture2DData object2DFileIcon = new Texture2DData();
            public static Texture2DData textFileIcon96 = new Texture2DData();
            public static Texture2DData textFileIcon14 = new Texture2DData();
            public static Texture2DData unknownFileIcon = new Texture2DData();

            public static Texture2DData layersIcon = new Texture2DData();

            public static Texture2DData threeDotsIcon = new Texture2DData();

            public static Texture2DData playButtonIcon = new Texture2DData();
            public static Texture2DData stopButtonIcon = new Texture2DData();
            public static Texture2DData pauseButtonIcon = new Texture2DData();

            public static Texture2DData cameraIcon96 = new Texture2DData();
            public static Texture2DData cameraIcon48 = new Texture2DData();

            public static Texture2DData gizmoTranslationIcon = new Texture2DData();
            public static Texture2DData gizmoRotationIcon = new Texture2DData();
            public static Texture2DData gizmoScaleIcon = new Texture2DData();

            public static Texture2DData noneIcon = new Texture2DData();

            public static Texture2DData checkMarkIcon = new Texture2DData();

            public static Texture2DData xIcon = new Texture2DData();

            public static Texture2DData collapseIcon = new Texture2DData();

            public static Texture2DData editIcon24 = new Texture2DData();

            public static void load() {
                directoryIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/directory0.png"), "/data/icons/directory0.png");
                openDirectoryIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/open_directory.png"), "/data/icons/open_directory.png");
                imageFileIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/image_file.png"), "/data/icons/image_file.png");
                javaFileIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/java_file.png"), "/data/icons/java_file.png");
                javaFileIcon14 = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/java_file_icon14.png"), "/data/icons/java_file_icon14.png");
                object2DFileIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/object2d_file.png"), "/data/icons/object2d_file.png");
                textFileIcon96 = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/text_file_icon96.png"), "/data/icons/text_file_icon96.png");
                textFileIcon14 = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/text_file_icon14.png"), "/data/icons/text_file_icon14.png");
                unknownFileIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/unknown_file.png"), "/data/icons/unknown_file.png");

                layersIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/layers.png"), "/data/icons/layers.png");

                threeDotsIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/three_dots_icon.png"), "/data/icons/three_dots_icon.png");

                playButtonIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/play_button.png"), "/data/icons/play_button.png");
                stopButtonIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/stop_button.png"), "/data/icons/stop_button.png");
                pauseButtonIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/pause_button.png"), "/data/icons/pause_button.png");

                cameraIcon96 = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/camera_icon96.png"), "/data/icons/camera_icon96.png");
                cameraIcon48 = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/camera_icon48.png"), "/data/icons/camera_icon48.png");

                gizmoTranslationIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/gizmo_translation.png"), "/data/icons/gizmo_translation.png");
                gizmoRotationIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/gizmo_rotation.png"), "/data/icons/gizmo_rotation.png");
                gizmoScaleIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/gizmo_scale.png"), "/data/icons/gizmo_scale.png");

                noneIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/none.png"), "/data/icons/none.png");

                checkMarkIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/check_mark.png"), "/data/icons/check_mark.png");
                xIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/x_icon.png"), "/data/icons/x_icon.png");

                collapseIcon = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/collapse_icon.png"), "/data/icons/collapse_icon.png");

                editIcon24 = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/icons/edit_icon24.png"), "/data/icons/edit_icon24.png");
            }
        }

        public static class Gizmo
        {
            public static Texture2DData gizmoArrow = new Texture2DData();
            public static Texture2DData gizmoPoint = new Texture2DData();
            public static Texture2DData gizmoCircle = new Texture2DData();

            public static void load()
            {
                gizmoArrow = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_arrow.png"), "/data/gizmo/gizmo_arrow.png");
                gizmoPoint = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_point.png"), "/data/gizmo/gizmo_point.png");
                gizmoCircle = new Texture2DData().load(Core2D.class.getResourceAsStream("/data/gizmo/gizmo_circle.png"), "/data/gizmo/gizmo_circle.png");
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
