package SungearEngine2D.GUI.Views.EditorView;

import Core2D.ECS.Component.Component;
import Core2D.ECS.Component.Components.Camera2DComponent;
import Core2D.ECS.Component.Components.MeshComponent;
import Core2D.ECS.Entity;
import Core2D.Graphics.RenderParts.Shader;
import Core2D.Layering.Layer;
import Core2D.Layering.PostprocessingLayer;
import SungearEngine2D.GUI.Views.View;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class ShadersEditorView extends View
{
    public static class ShaderEditorWindow
    {
        public boolean active = true;

        private Shader shader;

        private int layerID = -1;
        private int entityID = -1;
        private int componentID = -1;

        public ShaderEditorWindow(Shader shader, int layerID, int entityID, int componentID)
        {
            this.shader = shader;

            this.layerID = layerID;
            this.entityID = entityID;
            this.componentID = componentID;
        }

        public void draw()
        {
            if(active) {
                ImBoolean opened = new ImBoolean(true);

                ImGui.begin(new File(shader.path).getName(), opened);

                if(!opened.get()) {
                    active = false;
                }

                for (Shader.ShaderUniform shaderUniform : shader.getShaderUniforms()) {
                    if (shaderUniform.value instanceof Integer) {
                        int[] val = new int[]{(int) shaderUniform.value};
                        if (ImGui.dragInt(shaderUniform.getName(), val)) {
                            shaderUniform.value = val[0];
                        }
                    } else if (shaderUniform.value instanceof Float) {
                        float[] val = new float[]{(float) shaderUniform.value};
                        if (ImGui.dragFloat(shaderUniform.getName(), val, 0.01f)) {
                            shaderUniform.value = val[0];
                        }
                    }
                }

                ImGui.end();
            }
        }

        // удержание шейдера, чтобы после десериализации иметь реальный handler. вынести в отдельный поток
        public void handleShader()
        {
            if (currentSceneManager != null && currentSceneManager.getCurrentScene2D() != null && shader != null) {
                Layer layer = currentSceneManager.getCurrentScene2D().getLayering().getLayer(layerID);
                Entity foundEntity = layer.getEntity(entityID);
                if (foundEntity == null) return;
                Component foundComponent = foundEntity.findComponentByID(componentID);
                if (foundComponent == null) return;
                Shader foundShader = null;
                if (foundComponent instanceof MeshComponent meshComponent) {
                    foundShader = meshComponent.getShader();
                }
                if (foundShader == null) return;
                shader = foundShader;
            }
        }
    }

    public boolean active = false;

    private int dockspaceID;

    private List<ShaderEditorWindow> editingShaders = new ArrayList<>();

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

            int size = editingShaders.size();
            for(int i = 0; i < size; i++) {
                editingShaders.get(i).handleShader();
                editingShaders.get(i).draw();
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


    public List<ShaderEditorWindow> getEditingShaders() { return editingShaders; }
}
