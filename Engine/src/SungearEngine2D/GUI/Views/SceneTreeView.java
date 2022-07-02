package SungearEngine2D.GUI.Views;

import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import Core2D.Utils.WrappedObject;
import SungearEngine2D.Main.Resources;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;

public class SceneTreeView extends View
{
    public void draw()
    {
        ImGui.begin("Scene2D tree", ImGuiWindowFlags.NoMove);
        {
            update();

            if(SceneManager.getCurrentScene2D() != null) {
                if (ImGui.treeNode("Scene2D " + SceneManager.getCurrentScene2D().getName())) {
                    if(ImGui.treeNode("Scene cameras")) {
                        int s = 0;
                        for(int i = 0; i < SceneManager.getCurrentScene2D().getCameras2D().size(); i++) {
                            ImGui.pushID("Scene2DCamera2D_" + s);
                            boolean opened = ImGui.treeNodeEx(SceneManager.getCurrentScene2D().getCameras2D().get(i).getName(), ImGuiTreeNodeFlags.Bullet);
                            if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                                MainView.getInspectorView().setCurrentInspectingObject(SceneManager.getCurrentScene2D().getCameras2D().get(i));
                            }
                            if(opened) {
                                ImGui.treePop();
                            }
                            ImGui.popID();
                            s++;
                        }
                        ImGui.treePop();
                    }
                    if(ImGui.treeNode("Scene objects")) {
                        int s = 0;
                        for (int i = 0; i < SceneManager.getCurrentScene2D().getLayering().getLayers().size(); i++) {
                            for (int k = 0; k < SceneManager.getCurrentScene2D().getLayering().getLayers().get(i).getRenderingObjects().size(); k++) {
                                WrappedObject wrappedObject = SceneManager.getCurrentScene2D().getLayering().getLayers().get(i).getRenderingObjects().get(k);
                                ImGui.pushID("Scene2DWrappedObject_" + s);
                                boolean opened = false;
                                if (wrappedObject.getObject() instanceof Object2D) {
                                    opened = ImGui.treeNode(((Object2D) wrappedObject.getObject()).getName());
                                }
                                if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                                    MainView.getInspectorView().setCurrentInspectingObject(wrappedObject.getObject());
                                }
                                if (ImGui.beginDragDropSource()) {
                                    ImGui.setDragDropPayload("SceneObject2D", wrappedObject);
                                    ImGui.image(Resources.Textures.Icons.object2DFileIcon.getTextureHandler(), 25.0f, 25.0f);
                                    ImGui.endDragDropSource();
                                }

                                if (opened) {
                                    ImGui.treePop();
                                }
                                ImGui.popID();
                                s++;
                            }
                        }
                        ImGui.treePop();
                    }
                    ImGui.treePop();
                }
            }
        }
        ImGui.end();
    }
}
