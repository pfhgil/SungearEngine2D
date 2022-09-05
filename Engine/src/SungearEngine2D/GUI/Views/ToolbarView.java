package SungearEngine2D.GUI.Views;

import Core2D.Controllers.PC.Keyboard;
import SungearEngine2D.DebugDraw.Gizmo;
import SungearEngine2D.Main.Resources;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeFlags;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class ToolbarView extends View
{
    public void draw()
    {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

        ImGuiWindowClass windowClass = new ImGuiWindowClass();
        windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoTabBar);
        windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags.NoResize);
        ImGui.setNextWindowClass(windowClass);

        ImGui.begin("Toolbar View", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);
        {
            ImGui.popStyleVar(1);

            int gizmoMode = Gizmo.gizmoMode;
            Vector4f gizmoTranslationButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
            if(gizmoMode == Gizmo.GizmoMode.TRANSLATION || gizmoMode == Gizmo.GizmoMode.TRANSLATION_SCALE || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE) {
                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                gizmoTranslationButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
            }
            ImGui.setCursorPos(0.0f, 2);
            if(ImGui.imageButton(Resources.Textures.Icons.gizmoTranslationIcon.getTextureHandler(), 13, 13, 0, 0, 1, 1, -1, 1, 1, 1, 0, gizmoTranslationButtonColor.x, gizmoTranslationButtonColor.y, gizmoTranslationButtonColor.z, gizmoTranslationButtonColor.w)) {
                if(gizmoMode != Gizmo.GizmoMode.TRANSLATION && !Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.TRANSLATION;
                } else if(gizmoMode == Gizmo.GizmoMode.ROTATION && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.TRANSLATION_ROTATION;
                } else if(gizmoMode == Gizmo.GizmoMode.SCALE && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.TRANSLATION_SCALE;
                } else if(gizmoMode == Gizmo.GizmoMode.ROTATION_SCALE && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE;
                }
            }
            if(gizmoMode == Gizmo.GizmoMode.TRANSLATION || gizmoMode == Gizmo.GizmoMode.TRANSLATION_SCALE || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE) {
                ImGui.popStyleColor(3);
            }

            ImGui.sameLine();
            Vector4f gizmoRotationButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
            if(gizmoMode == Gizmo.GizmoMode.ROTATION || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION || gizmoMode == Gizmo.GizmoMode.ROTATION_SCALE || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE) {
                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                gizmoRotationButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
            }
            if(ImGui.imageButton(Resources.Textures.Icons.gizmoRotationIcon.getTextureHandler(), 13, 13, 0, 0, 1, 1, -1, 1, 1, 1, 0, gizmoRotationButtonColor.x, gizmoRotationButtonColor.y, gizmoRotationButtonColor.z, gizmoRotationButtonColor.w)) {
                if(gizmoMode != Gizmo.GizmoMode.ROTATION && !Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.ROTATION;
                } else if(gizmoMode == Gizmo.GizmoMode.TRANSLATION && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.TRANSLATION_ROTATION;
                } else if(gizmoMode == Gizmo.GizmoMode.SCALE && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.ROTATION_SCALE;
                } else if(gizmoMode == Gizmo.GizmoMode.TRANSLATION_SCALE && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE;
                }
            }
            if(gizmoMode == Gizmo.GizmoMode.ROTATION || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION || gizmoMode == Gizmo.GizmoMode.ROTATION_SCALE || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE) {
                ImGui.popStyleColor(3);
            }

            ImGui.sameLine();
            Vector4f gizmoScaleButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
            if(gizmoMode == Gizmo.GizmoMode.SCALE || gizmoMode == Gizmo.GizmoMode.TRANSLATION_SCALE || gizmoMode == Gizmo.GizmoMode.ROTATION_SCALE || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE) {
                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                gizmoScaleButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
            }
            if(ImGui.imageButton(Resources.Textures.Icons.gizmoScaleIcon.getTextureHandler(), 13, 13, 0, 0, 1, 1, -1, 1, 1, 1, 0, gizmoScaleButtonColor.x, gizmoScaleButtonColor.y, gizmoScaleButtonColor.z, gizmoScaleButtonColor.w)) {
                if(gizmoMode != Gizmo.GizmoMode.SCALE && !Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.SCALE;
                } else if(gizmoMode == Gizmo.GizmoMode.ROTATION && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.ROTATION_SCALE;
                } else if(gizmoMode == Gizmo.GizmoMode.TRANSLATION && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.TRANSLATION_SCALE;
                } else if(gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION && Keyboard.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Gizmo.gizmoMode = Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE;
                }
            }
            if(gizmoMode == Gizmo.GizmoMode.SCALE || gizmoMode == Gizmo.GizmoMode.TRANSLATION_SCALE || gizmoMode == Gizmo.GizmoMode.ROTATION_SCALE || gizmoMode == Gizmo.GizmoMode.TRANSLATION_ROTATION_SCALE) {
                ImGui.popStyleColor(3);
            }

            ImGui.sameLine();
            Vector4f gizmoNoneButtonColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
            if(gizmoMode == Gizmo.GizmoMode.NO_GIZMO) {
                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                gizmoNoneButtonColor.set(0.5f, 0.5f, 0.5f, 1.0f);
            }
            if(ImGui.imageButton(Resources.Textures.Icons.noneIcon.getTextureHandler(), 13, 13, 0, 0, 1, 1, -1, 1, 1, 1, 0, gizmoNoneButtonColor.x, gizmoNoneButtonColor.y, gizmoNoneButtonColor.z, gizmoNoneButtonColor.w)) {
                Gizmo.gizmoMode = Gizmo.GizmoMode.NO_GIZMO ;
            }
            if(gizmoMode == Gizmo.GizmoMode.NO_GIZMO) {
                ImGui.popStyleColor(3);
            }
        }
        ImGui.end();
    }
}
