package Core2D.Core2D;

import Core2D.Texture2D.Texture2D;
import Core2D.UI.Text.Font;
import Core2D.Utils.FileUtils;

public class Resources
{
    public static class Textures
    {
        public static final Texture2D WHITE_TEXTURE = new Texture2D(Core2D.class.getResourceAsStream("/data/Textures/white_texture.png"));

        public static class UI
        {
            public static class ProgressBar
            {
                public static final Texture2D DEFAULT_PROGRESS_BAR_TEXTURE = new Texture2D(Core2D.class.getResourceAsStream("/data/Textures/UI/ProgressBar/progress_bar.png"));
            }
        }
    }

    public static class ShadersTexts
    {
        public static class Primitives
        {
            public static class Line2D
            {
                public static final String vertexShaderText  = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Primitives/Line2D/vertexShader.glsl"));
                public static final String fragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Primitives/Line2D/fragmentShader.glsl"));
            }
        }

        public static class Object2D
        {
            public static final String vertexShaderText  = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Object2D/vertexShader.glsl"));
            public static final String fragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Object2D/fragmentShader.glsl"));

            //public static final ShaderProgram shaderProgram = new ShaderProgram(new Shader(vertexShaderText, GL46C.GL_VERTEX_SHADER), new Shader(fragmentShaderText, GL46C.GL_FRAGMENT_SHADER));
        }

        public static class Instancing
        {
            public static class Primitives
            {
                public static class Line2D
                {
                    public static final String vertexShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Primitives/Line2D/Instancing/vertexShader.glsl"));
                    public static final String fragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Primitives/Line2D/Instancing/fragmentShader.glsl"));
                }
            }

            public static class Object2D
            {
                public static final String vertexShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Object2D/Instancing/vertexShader.glsl"));
                public static final String fragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/Object2D/Instancing/fragmentShader.glsl"));
            }

            public static class Text
            {
                public static final String vertexShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/Text/Instancing/vertexShader.glsl"));
                public static final String fragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/Text/Instancing/fragmentShader.glsl"));
            }
        }

        public static class ProgressBar
        {
            public static final String fragmentShaderText = FileUtils.readAllFile(Core2D.class.getResourceAsStream("/data/Shaders/UI/ProgressBar/fragmentShader.glsl"));
        }
    }

    public static class Fonts
    {
        public static final Font COMIC_SANS_MS = new Font(Core2D.class.getResourceAsStream("/data/Fonts/ComicSansSM/cssm.fnt"), Core2D.class.getResourceAsStream("/data/Fonts/ComicSansSM/cssm.png"));
    }
}
