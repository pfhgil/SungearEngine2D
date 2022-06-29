package SungearEngine2D.GUI.Views;

import Core2D.Layering.LayerObject;
import Core2D.Object2D.Object2D;
import Core2D.Scene2D.SceneManager;
import SungearEngine2D.Main.Resources;
import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
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
                    int s = 0;
                    for(int i = 0; i < SceneManager.getCurrentScene2D().getLayering().getLayers().size(); i++) {
                        for(int k = 0; k < SceneManager.getCurrentScene2D().getLayering().getLayers().get(i).getRenderingObjects().size(); k++) {
                            LayerObject layerObject = SceneManager.getCurrentScene2D().getLayering().getLayers().get(i).getRenderingObjects().get(k);
                            ImGui.pushID("Scene2DLayerObject_" + s);
                            if(layerObject.getObject() instanceof Object2D && ImGui.treeNode(((Object2D) layerObject.getObject()).getName())) {
                                if(ImGui.beginDragDropSource()) {
                                    ImGui.setDragDropPayload("SceneObject2D", layerObject.getObject());
                                    ImGui.image(Resources.Textures.Icons.object2DFileIcon.getTextureHandler(), 25.0f, 25.0f);
                                    ImGui.endDragDropSource();
                                }
                                ImGui.treePop();
                            }
                            ImGui.popID();

                            s++;
                        }
                    }
                    ImGui.treePop();
                }
            }
        }
        ImGui.end();
    }
}
