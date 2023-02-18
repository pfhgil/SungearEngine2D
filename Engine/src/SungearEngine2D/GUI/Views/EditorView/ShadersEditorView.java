package SungearEngine2D.GUI.Views.EditorView;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Component.Components.ProgramTimeComponent;
import Core2D.ECS.Component.Components.Shader.ShaderUniformFloatComponent;
import Core2D.ECS.Component.Components.Shader.TextureComponent;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Utils.ComponentHandler;
import SungearEngine2D.GUI.ImGuiUtils;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.GUI.Views.ViewsManager;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.lwjgl.opengl.GL46C;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShadersEditorView extends View
{
    public static class ShaderEditorWindow
    {
        public boolean active = true;

        private Shader shader;

        private ComponentHandler componentHandler;

        public ShaderEditorWindow(Shader shader, ComponentHandler componentHandler)
        {
            this.shader = shader;
            this.componentHandler = componentHandler;
        }

        public void draw()
        {
            if(active) {
                ImBoolean opened = new ImBoolean(true);

                ImGui.setNextWindowDockID(ViewsManager.getShadersEditorView().dockspaceID);
                ImGui.begin(new File(shader.path).getName(), opened);

                if(!opened.get()) {
                    active = false;
                    ViewsManager.getShadersEditorView().shaderEditorWindows.remove(this);
                }

                for (Shader.ShaderUniform shaderUniform : shader.getShaderUniforms()) {
                    boolean isSampler = shaderUniform.getType() == GL46C.GL_SAMPLER_1D ||
                            shaderUniform.getType() == GL46C.GL_SAMPLER_2D ||
                            shaderUniform.getType() == GL46C.GL_SAMPLER_3D;
                    if (shaderUniform.value instanceof Integer) {
                        if(!isSampler || shaderUniform.getAttachedComponent() == null) {
                            int[] val = new int[]{(int) shaderUniform.value};
                            if (ImGui.dragInt(shaderUniform.getName(), val)) {
                                shaderUniform.value = val[0];
                            }

                            Object droppedObject = acceptDragDrop();

                            ImGui.separator();

                            if(droppedObject instanceof Component component) {
                                shaderUniform.attachToComponent(component);
                            }
                        } else {
                            String[] text = new String[1];
                            if(shaderUniform.getAttachedComponent() instanceof TextureComponent textureComponent) {
                                text[0] = new File(textureComponent.getTexture().path).getName();
                            }

                            ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText(shaderUniform.getName(), new ImString(text[0]), ImGuiInputTextFlags.ReadOnly));

                            Object droppedObject = acceptDragDrop();

                            if(droppedObject instanceof Component component) {
                                shaderUniform.attachToComponent(component);
                            }

                            //ImGui.sameLine();

                            ImGui.pushID("DetachComponentButton_" + shaderUniform.getName());
                            if(ImGui.button("Detach component")) {
                                shaderUniform.resetAttachedComponent();
                            }
                            ImGui.popID();

                            ImGui.separator();
                        }
                    } else if (shaderUniform.value instanceof Float) {
                        if(shaderUniform.getAttachedComponent() == null) {
                            float[] val = new float[]{(float) shaderUniform.value};
                            if (ImGui.dragFloat(shaderUniform.getName(), val, 0.01f)) {
                                shaderUniform.value = val[0];
                            }

                            Object droppedObject = acceptDragDrop();

                            ImGui.separator();

                            if(droppedObject instanceof Component component) {
                                shaderUniform.attachToComponent(component);
                            }
                        } else {
                            String[] text = new String[1];
                            if(shaderUniform.getAttachedComponent() instanceof ProgramTimeComponent programTimeComponent) {
                                text[0] = "" + programTimeComponent.uniformValue;
                            }

                            ImGuiUtils.imCallWBorder(func -> ImGuiUtils.defaultInputText(shaderUniform.getName(), new ImString(text[0]), ImGuiInputTextFlags.ReadOnly));

                            Object droppedObject = acceptDragDrop();

                            if(droppedObject instanceof Component component) {
                                shaderUniform.attachToComponent(component);
                            }

                            //ImGui.sameLine();

                            ImGui.pushID("DetachComponentButton_" + shaderUniform.getName());
                            if(ImGui.button("Detach component")) {
                                shaderUniform.resetAttachedComponent();
                            }
                            ImGui.popID();

                            ImGui.separator();
                        }
                    }
                }

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
                foundShader = meshComponent.getShader();
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
