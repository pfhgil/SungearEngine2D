package SungearEngine2D.GUI.Views.EditorView;

import Core2D.Core2D.Core2D;
import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.ECSWorld;
import Core2D.Graphics.Graphics;
import Core2D.Graphics.OpenGL.*;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Input.PC.Mouse;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Utils.ComponentHandler;
import Core2D.Utils.ShaderUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.Main.Resources;
import SungearEngine2D.Main.EngineSettings;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46C;

import static Core2D.Scene2D.SceneManager.currentSceneManager;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class GameView extends View
{
    // позиция окна сцены относительно окна
    private Vector2f sceneViewWindowScreenPosition = new Vector2f();
    private Vector2f sceneViewWindowSize = new Vector2f();

    private String windowName = "";
    private final String windowID;

    private int viewTextureHandler;

    private final boolean isClosable;

    private final ImBoolean opened  = new ImBoolean(true);

    private static FrameBuffer debugRenderFB;

    // слой постпроцесса
    private PostprocessingLayer postprocessingLayer;
    private ComponentHandler camera2DComponentHandler = new ComponentHandler();
    private VertexArray ppQuadVertexArray;

    public GameView(String windowName, String windowID, int viewTextureHandler, boolean isClosable)
    {
        this(windowName, windowID, viewTextureHandler, isClosable, null, null);
    }

    public GameView(String windowName, String windowID, int viewTextureHandler, boolean isClosable, PostprocessingLayer postprocessingLayer, ComponentHandler camera2DComponentHandler)
    {
        this.windowName = windowName;
        this.windowID = windowID;
        this.viewTextureHandler = viewTextureHandler;
        this.isClosable = isClosable;

        if(debugRenderFB == null) {
            debugRenderFB = new FrameBuffer(Graphics.getScreenSize().x, Graphics.getScreenSize().y, FrameBuffer.BuffersTypes.COLOR_BUFFER, GL_TEXTURE0);
        }

        if(ppQuadVertexArray == null) {
            short[] ppQuadIndices = new short[] { 0, 1, 2, 0, 2, 3 };

            float[] ppQuadData = new float[] {
                    -1, -1,
                    0, 0,

                    -1, 1,
                    0, 1,

                    1, 1,
                    1, 1,

                    1, -1,
                    1, 0,
            };

            ppQuadVertexArray = new VertexArray();
            // VBO вершин (VBO - Vertex Buffer Object. Может хранить в себе цвета, позиции вершин и т.д.)
            VertexBuffer vertexBuffer = new VertexBuffer(ppQuadData);
            // IBO вершин (IBO - Index Buffer Object. IBO хранит в себе индексы вершин, по которым будут соединяться вершины)
            IndexBuffer indexBuffer = new IndexBuffer(ppQuadIndices);

            // создаю описание аттрибутов в шейдерной программе
            BufferLayout attributesLayout = new BufferLayout(
                    new VertexAttribute(0, "positionAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2),
                    new VertexAttribute(1, "textureCoordsAttribute", VertexAttribute.ShaderDataType.SHADER_DATA_TYPE_T_FLOAT2)
            );

            vertexBuffer.setLayout(attributesLayout);
            ppQuadVertexArray.putVBO(vertexBuffer, false);
            ppQuadVertexArray.putIBO(indexBuffer);

            ppQuadIndices = null;

            // отвязываю vao
            ppQuadVertexArray.unBind();
        }

        if(camera2DComponentHandler != null) {
            this.postprocessingLayer = postprocessingLayer;

            this.camera2DComponentHandler = camera2DComponentHandler;
        }
    }

    public void draw()
    {
        if(!opened.get() && isClosable) {
            ViewsManager.getFBOViews().remove(this);
            return;
        }

        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.65f, 0.65f, 0.65f, 1.0f);
        ImGui.pushID(windowID);
        ImGui.setNextWindowDockID(ViewsManager.getMainDockspaceID());

        boolean windowFocused = isClosable ? ImGui.begin(windowName, opened, ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar) :
                ImGui.begin(windowName, ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);

        if(windowFocused) {
            ImGui.beginMenuBar();
            {
                Vector4f playButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                boolean active = EngineSettings.Playmode.active;
                if(active) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                    playButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                }
                if(ImGui.imageButton(Resources.Textures.Icons.playButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, playButtonColor.x, playButtonColor.y, playButtonColor.z, playButtonColor.w)) {
                    if(currentSceneManager.getCurrentScene2D() != null) {
                        if(!EngineSettings.Playmode.active && !EngineSettings.Playmode.paused) {
                            ViewsManager.getSceneView().startPlayMode();
                        } else {
                            ViewsManager.getSceneView().stopPlayMode();
                        }
                    }
                }
                if(active) {
                    ImGui.popStyleColor(3);
                }

                Vector4f pauseButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                boolean paused = EngineSettings.Playmode.paused;
                if(paused) {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                    pauseButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
                }
                if(ImGui.imageButton(Resources.Textures.Icons.pauseButtonIcon.getTextureHandler(), 8, 10, 0, 0, 1, 1, -1, 1, 1, 1, 0, pauseButtonColor.x, pauseButtonColor.y, pauseButtonColor.z, pauseButtonColor.w)) {
                    if(currentSceneManager.getCurrentScene2D() != null) {
                        ViewsManager.getSceneView().pausePlayMode();
                    }
                }
                if(paused) {
                    ImGui.popStyleColor(3);
                }

                if(ImGui.imageButton(Resources.Textures.Icons.stopButtonIcon.getTextureHandler(), 8, 10)) {
                    if(currentSceneManager.getCurrentScene2D() != null) {
                        ViewsManager.getSceneView().stopPlayMode();
                    }
                }
                //if(ImGui.menuItem())
            }
            ImGui.endMenuBar();

            ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());

            Vector2i engineWindowSize = Core2D.getWindow().getSize();
            ImVec2 windowSize = getLargestSizeForViewport(engineWindowSize.x / (float) engineWindowSize.y);
            ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
            ImGui.setCursorPos(windowPos.x, windowPos.y);

            // нахожу позицию окна
            ImVec2 windowScreenPos = ImGui.getCursorScreenPos();
            windowScreenPos.x -= ImGui.getScrollX();
            windowScreenPos.y -= ImGui.getScrollY();

            sceneViewWindowScreenPosition.x = windowScreenPos.x;
            sceneViewWindowScreenPosition.y = (Core2D.getWindow().getSize().y - windowScreenPos.y) - windowSize.y;

            sceneViewWindowSize.x = windowSize.x;
            sceneViewWindowSize.y = windowSize.y;

            Mouse.setViewportPosition(sceneViewWindowScreenPosition);
            Mouse.setViewportSize(new Vector2f(sceneViewWindowSize.x, sceneViewWindowSize.y));

            debugRenderFB.bind();
            debugRenderFB.clear();
            ppQuadVertexArray.bind();
            if(postprocessingLayer != null) {
                Shader shader = postprocessingLayer.getShader();
                FrameBuffer frameBufferToBind = postprocessingLayer.getFrameBuffer();

                frameBufferToBind.bindTexture();

                shader.bind();

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "color",
                        new Vector4f(1.0f)
                );

                ShaderUtils.setUniform(
                        shader.getProgramHandler(),
                        "sampler",
                        frameBufferToBind.getTextureBlock() - GL_TEXTURE0
                );

                // нарисовать два треугольника
                OpenGL.glCall((params) -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0));

                frameBufferToBind.unBindTexture();
            }
            ppQuadVertexArray.unBind();
            debugRenderFB.unBind();

            if(OpenGL.glCall((params) -> GL46C.glIsTexture(viewTextureHandler), Boolean.class) && postprocessingLayer == null) {
                ImGui.image(viewTextureHandler, sceneViewWindowSize.x, sceneViewWindowSize.y);
            } else if(postprocessingLayer != null) {
                ImGui.image(debugRenderFB.getTextureHandler(), sceneViewWindowSize.x, sceneViewWindowSize.y);
            } else {
                opened.set(false);
            }

            ImGui.popStyleColor(1);
        } else {
            ImGui.popStyleColor(1);
        }
        ImGui.end();
        ImGui.popID();
    }

    // просто удерживание пп леера, так как он может слететь после релоада сцены
    // поместить handle в отдельный поток.
    public void handlePostprocessingLayer()
    {
        Component foundComponent = camera2DComponentHandler.getComponent();
        if(foundComponent == null) return;
        if(foundComponent instanceof Camera2DComponent camera2DComponent) {
            postprocessingLayer = ECSWorld.getCurrentECSWorld().camerasUpdater.getPostprocessingLayerByName(camera2DComponent, postprocessingLayer.getEntitiesLayerToRenderName());
        }
    }

    private ImVec2 getLargestSizeForViewport(float targetAspect)
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        //windowSize.x -= ImGui.getScrollX();
        //windowSize.y -= ImGui.getScrollY();

        //Vector2i engineWindowSize = Core2D.getWindow().getSize();
        //float targetAspect = engineWindowSize.x / (float) engineWindowSize.y;

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / targetAspect;

        if(aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * targetAspect;
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize)
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    public int getViewTextureHandler() { return viewTextureHandler; }
    public void setViewTextureHandler(int viewTextureHandler) { this.viewTextureHandler = viewTextureHandler; }

    public void setPostprocessingLayer(PostprocessingLayer postprocessingLayer, ComponentHandler camera2DComponentHandler)
    {
        if(camera2DComponentHandler != null) {
            this.postprocessingLayer = postprocessingLayer;

            this.camera2DComponentHandler = camera2DComponentHandler;
        }
    }

    public String getWindowID() { return windowID; }
}
