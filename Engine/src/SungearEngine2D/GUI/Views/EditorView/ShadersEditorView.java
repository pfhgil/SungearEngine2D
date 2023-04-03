package SungearEngine2D.GUI.Views.EditorView;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera.CameraComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.ProgramTimeComponent;
import Core2D.ECS.Component.Components.Shader.ShaderUniformFloatComponent;
import Core2D.ECS.Component.Components.Shader.TextureComponent;
import Core2D.ECS.ECSWorld;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.PostprocessingLayer;
import Core2D.Utils.ComponentHandler;
import SungearEngine2D.GUI.ImGuiUtils;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.Scripting.Compiler;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;
import org.lwjgl.opengl.GL46C;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShadersEditorView extends View
{
    public static class ShaderEditorWindow
    {
        public boolean active = true;

        private Shader shader;

        private ComponentHandler componentHandler;

        // если шейдер изменяется у слоя постпроцессинга
        public String ppLayerName = "";

        private int dockspaceID;

        public ShaderEditorWindow(Shader shader, ComponentHandler componentHandler)
        {
            this.shader = shader;
            this.componentHandler = componentHandler;

            dockspaceID = ImGui.getID("ShaderEditorWindowDockspace_" + shader.path);
        }

        private void editUniformValue(Shader.ShaderUniform shaderUniform, boolean cond, Runnable ifNotAttachedComponentRunnable, Runnable ifAttachedComponentRunnable, Class<?>... componentClassToDrop)
        {
            if(cond) {
                ifNotAttachedComponentRunnable.run();

                Object droppedObject = acceptDragDrop();
                boolean instanceOfNeededComponent = false;

                if(droppedObject != null) {
                    for (Class<?> cls : componentClassToDrop) {
                        if (cls.isAssignableFrom(droppedObject.getClass())) {
                            instanceOfNeededComponent = true;
                            break;
                        }
                    }
                }

                ImGui.separator();

                if (instanceOfNeededComponent && droppedObject instanceof Component component) {
                    shaderUniform.attachToComponent(component);
                }
            } else {
                ifAttachedComponentRunnable.run();

                Object droppedObject = acceptDragDrop();
                boolean instanceOfNeededComponent = false;

                if(droppedObject != null) {
                    for (Class<?> cls : componentClassToDrop) {
                        if (cls.isAssignableFrom(droppedObject.getClass())) {
                            instanceOfNeededComponent = true;
                            break;
                        }
                    }
                }

                System.out.println("uniform name: " + shaderUniform.getName());

                if (instanceOfNeededComponent && droppedObject instanceof Component component) {
                    shaderUniform.attachToComponent(component);
                }

                ImGui.pushID("DetachComponentButton_" + shaderUniform.getName());
                if(ImGui.button("Detach component")) {
                    shaderUniform.resetAttachedComponent();
                }
                ImGui.popID();

                ImGui.separator();
            }
        }

        public void draw()
        {
            if(active) {
                ImBoolean opened = new ImBoolean(true);

                ImGui.setNextWindowDockID(ViewsManager.getShadersEditorView().dockspaceID);

                ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f);
                ImGui.begin(new File(shader.path).getName(), opened);

                ImGui.popStyleVar();

                ImGui.dockSpace(dockspaceID);

                if(!opened.get()) {
                    active = false;
                    ViewsManager.getShadersEditorView().shaderEditorWindows.remove(this);
                }

                ImGui.setNextWindowDockID(dockspaceID);
                if(ImGui.begin("Uniforms")) {
                    ImVec2 uniformsWindowSizeAvail = ImGui.getContentRegionAvail();

                    if(ImGui.button("Compile")) {
                        Compiler.addShaderToCompile(shader);
                    }

                    ImGui.beginChild("UniformsChild", uniformsWindowSizeAvail.x, uniformsWindowSizeAvail.y - 23f, true);

                    for (Shader.ShaderUniform shaderUniform : shader.getShaderUniforms()) {
                        boolean isSampler = shaderUniform.getType() == GL46C.GL_SAMPLER_1D ||
                                shaderUniform.getType() == GL46C.GL_SAMPLER_2D ||
                                shaderUniform.getType() == GL46C.GL_SAMPLER_3D;
                        if (shaderUniform.value instanceof Integer) {
                            editUniformValue(shaderUniform,
                                    !isSampler || shaderUniform.getAttachedComponent() == null,
                                    () -> {
                                        int[] val = new int[]{(int) shaderUniform.value};
                                        if (ImGui.dragInt(shaderUniform.getName(), val)) {
                                            shaderUniform.value = val[0];
                                        }
                                    },
                                    () -> {
                                        String[] text = new String[1];
                                        if (shaderUniform.getAttachedComponent() instanceof TextureComponent textureComponent) {
                                            text[0] = new File(textureComponent.getTexture().path).getName();
                                        }
                                        ImGuiUtils.inputText(shaderUniform.getName(), text[0], ImGuiInputTextFlags.ReadOnly);
                                    },
                                    TextureComponent.class);
                        } else if (shaderUniform.value instanceof Float) {
                            editUniformValue(shaderUniform,
                                    shaderUniform.getAttachedComponent() == null,
                                    () -> {
                                        float[] val = new float[]{(float) shaderUniform.value};
                                        if (ImGui.dragFloat(shaderUniform.getName(), val, 0.01f)) {
                                            shaderUniform.value = val[0];
                                        }
                                    },
                                    () -> {
                                        String[] text = new String[1];
                                        if (shaderUniform.getAttachedComponent() instanceof ProgramTimeComponent programTimeComponent) {
                                            text[0] = "" + programTimeComponent.uniformValue;
                                        }
                                        ImGuiUtils.inputText(shaderUniform.getName(), text[0], ImGuiInputTextFlags.ReadOnly);
                                    },
                                    ShaderUniformFloatComponent.class);
                        }
                    }

                    ImGui.endChild();
                }

                ImGui.end();

                ImGui.setNextWindowDockID(dockspaceID);
                if(ImGui.begin("Defines")) {
                    ImVec2 definesWindowSizeAvail = ImGui.getContentRegionAvail();

                    if(ImGui.button("Compile")) {
                        Compiler.addShaderToCompile(shader);
                    }

                    ImGui.beginChild("DefinesChild", definesWindowSizeAvail.x, definesWindowSizeAvail.y - 23f, true);

                    ImVec2 definesWindowChildSizeAvail = ImGui.getContentRegionAvail();

                    Iterator<Shader.ShaderDefine> shaderDefinesIterator = shader.getShaderDefines().iterator();
                    while (shaderDefinesIterator.hasNext()) {
                        Shader.ShaderDefine shaderDefine = shaderDefinesIterator.next();

                        // 45%
                        ImGui.setNextItemWidth(definesWindowChildSizeAvail.x * 45f / 100f);

                        shaderDefine.name = ImGuiUtils.inputText("", shaderDefine.name, "ShaderDefine_" + shaderDefine);

                        ImGui.sameLine();

                        // 45%
                        ImGui.setNextItemWidth(definesWindowChildSizeAvail.x * 45f / 100f);

                        shaderDefine.value = ImGuiUtils.dragInt("", shaderDefine.value, "ShaderDefine_" + shaderDefine + "_Value");
                    }

                    ImGui.endChild();
                }

                ImGui.end();

                ImGui.end();
            }
        }

        private Object acceptDragDrop()
        {
            Object accepted = null;
            if(ImGui.beginDragDropTarget()) {
                accepted = ImGui.acceptDragDropPayload("Component");
                ImGui.endDragDropTarget();
            }
            return accepted;
        }

        // удержание шейдера, чтобы после десериализации иметь реальный handler. вынести в отдельный поток
        public void handleShader()
        {
            Component foundComponent = componentHandler.getComponent();
            if (foundComponent == null) return;
            Shader foundShader = null;
            if (foundComponent instanceof MeshComponent meshComponent) {
                foundShader = meshComponent.shader;
            } else if(foundComponent instanceof CameraComponent cameraComponent) {
                PostprocessingLayer ppLayer = ECSWorld.getCurrentECSWorld().camerasManagerSystem.getPostprocessingLayerByName(cameraComponent, ppLayerName);
                if(ppLayer != null) {
                    foundShader = ppLayer.getShader();
                }
            }
            if (foundShader == null) return;
            shader = foundShader;
        }
    }

    public boolean active = false;

    private int dockspaceID;

    private List<ShaderEditorWindow> shaderEditorWindows = new ArrayList<>();

    public ShadersEditorView()
    {
        init();
    }

    @Override
    public void init()
    {
        dockspaceID = ImGui.getID("ShaderEditorViewDockspace");
    }

    public void draw()
    {
        if (active) {
            ImBoolean opened = new ImBoolean(true);

            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

            ImGui.begin("Shaders Editor", opened);

            ImGui.popStyleVar(3);

            ImGui.dockSpace(dockspaceID);

            int size = shaderEditorWindows.size();
            for(int i = 0; i < size; i++) {
                shaderEditorWindows.get(i).handleShader();
                shaderEditorWindows.get(i).draw();
            }

            /*

            ImGui.setCursorPos(5.0f, 22.0f);
            ImVec2 regionAvail = ImGui.getContentRegionAvail();
            ImGui.beginChild("DragNDropChild", regionAvail.x - 5.0f, regionAvail.y - 8.0f, false);

            ImGui.endChild();

             */


            if(!opened.get()) {
                active = false;
            }

            ImGui.end();
        }
    }


    public void addEditingShader(ShaderEditorWindow editorWindow)
    {
        if(shaderEditorWindows.stream().noneMatch(shaderEditorWindow -> shaderEditorWindow.shader.path.equals(editorWindow.shader.path))) {
            shaderEditorWindows.add(editorWindow);
        }
    }

    public int getDockspaceID() { return dockspaceID; }

    //public List<ShaderEditorWindow> getShaderEditorWindows() { return shaderEditorWindows; }
}
