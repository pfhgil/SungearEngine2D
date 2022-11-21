package SungearEngine2D.GUI.Views.EditorView;

import Core2D.Component.Components.TransformComponent;
import Core2D.GameObject.GameObject;
import Core2D.Utils.MatrixUtils;
import SungearEngine2D.GUI.Views.ViewsManager;
import SungearEngine2D.GUI.Views.View;
import SungearEngine2D.Main.Main;
import SungearEngine2D.Main.Resources;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static Core2D.Scene2D.SceneManager.currentSceneManager;

public class SceneTreeView extends View
{
    // зависимости, у которых нужно убрать родителя
    private static List<GameObject> childrenNeedFree = new ArrayList<>();
    // новые родители, которых нужно поставить
    private static List<GameObject> parentsToSet = new ArrayList<>();

    private static boolean mouseRightClicked = false;
    private static String action = "";
    private static int objectToActionIterator = -1;
    private static int iterator = 0;
    private static boolean isAnyTreeNodeHovered = false;
    private static Vector2f popupContextWindowRectMin = new Vector2f();
    private static Vector2f popupContextWindowRectMax = new Vector2f();

    public void draw()
    {
        ImGui.begin("Scene2D tree", ImGuiWindowFlags.NoMove);
        {
            iterator = 0;

            update();

            for(GameObject child : childrenNeedFree) {
                child.setParentObject2D(null);
            }

            for(int i = 0; i < parentsToSet.size(); i++) {
                parentsToSet.get(i).addChildObject(childrenNeedFree.get(i));
                childrenNeedFree.get(i).setParentObject2D(parentsToSet.get(i));
            }

            if(childrenNeedFree.size() != 0) {
                childrenNeedFree.clear();
            }
            if(parentsToSet.size() != 0) {
                parentsToSet.clear();
            }

            if(ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                mouseRightClicked = true;
            }

            if((ImGui.isMouseClicked(ImGuiMouseButton.Right) || ImGui.isMouseClicked(ImGuiMouseButton.Left)) &&
                    !ImGui.isMouseHoveringRect(popupContextWindowRectMin.x, popupContextWindowRectMin.y, popupContextWindowRectMax.x, popupContextWindowRectMax.y)) {
                isAnyTreeNodeHovered = false;
            }

            if(currentSceneManager.getCurrentScene2D() != null) {
                if (ImGui.treeNode("Scene2D " + currentSceneManager.getCurrentScene2D().getName())) {
                    if(ImGui.treeNode("Scene cameras")) {
                        int s = 0;
                        if(currentSceneManager.getCurrentScene2D().getCameras2D() != null) {
                            for (int i = 0; i < currentSceneManager.getCurrentScene2D().getCameras2D().size(); i++) {
                                ImGui.pushID("Scene2DCamera2D_" + s);
                                boolean opened = ImGui.treeNodeEx(currentSceneManager.getCurrentScene2D().getCameras2D().get(i).name, ImGuiTreeNodeFlags.Bullet);
                                if (ImGui.isItemHovered()) {
                                    if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                                        ViewsManager.getInspectorView().setCurrentInspectingObject(currentSceneManager.getCurrentScene2D().getCameras2D().get(i));
                                    }
                                    if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                                        Main.getCameraAnchor().getComponent(TransformComponent.class).getTransform().lerpMoveTo(currentSceneManager.getCurrentScene2D().getCameras2D().get(i).getTransform().getPosition(), new Vector2f(10));
                                    }
                                }
                                if (opened) {
                                    ImGui.treePop();
                                }
                                if (ImGui.beginDragDropSource()) {
                                    ImGui.setDragDropPayload("SceneWrappedObject", currentSceneManager.getCurrentScene2D().getCameras2D().get(i));
                                    ImGui.image(Resources.Textures.Icons.cameraIcon.getTextureHandler(), 25.0f, 25.0f);
                                    ImGui.endDragDropSource();
                                }
                                ImGui.popID();
                                s++;
                            }
                        }
                        ImGui.treePop();
                    }
                    if(ImGui.treeNode("Scene objects")) {
                        if (ImGui.beginDragDropTarget()) {
                            Object droppedObject = ImGui.acceptDragDropPayload("SceneGameObject");
                            if (droppedObject != null) {
                                ((GameObject) droppedObject).setParentObject2D(null);
                            }
                            ImGui.endDragDropTarget();
                        }

                        List<Integer> alreadyProcessedObjectsID = new ArrayList<>();
                        for (int i = 0; i < currentSceneManager.getCurrentScene2D().getLayering().getLayers().size(); i++) {
                            for (int k = 0; k < currentSceneManager.getCurrentScene2D().getLayering().getLayers().get(i).getGameObjects().size(); k++) {
                                iterator++;
                                GameObject gameObject = currentSceneManager.getCurrentScene2D().getLayering().getLayers().get(i).getGameObjects().get(k);
                                if(!alreadyProcessedObjectsID.contains(gameObject.ID) && gameObject.getParentObject2D() == null) {
                                    ImGui.pushID("Scene2DWrappedObject_" + gameObject.ID);
                                    boolean opened = false;
                                    opened = ImGui.treeNode(gameObject.name);
                                    if (ImGui.isItemHovered()) {
                                        isAnyTreeNodeHovered = true;
                                    }
                                    if (ImGui.isItemHovered() && ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                                        objectToActionIterator = iterator;
                                    }

                                    if (ImGui.isItemHovered()) {
                                        if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                                            ViewsManager.getInspectorView().setCurrentInspectingObject(gameObject);
                                        }
                                        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                                            Main.getCameraAnchor().getComponent(TransformComponent.class).getTransform().lerpMoveTo(gameObject.getComponent(TransformComponent.class).getTransform().getPosition(), new Vector2f(10));
                                        }
                                    }
                                    if (ImGui.beginDragDropSource()) {
                                        ImGui.setDragDropPayload("SceneGameObject", gameObject);
                                        ImGui.image(Resources.Textures.Icons.object2DFileIcon.getTextureHandler(), 25.0f, 25.0f);
                                        ImGui.endDragDropSource();
                                    }

                                    if(action.equals("DestroyObject2D") && objectToActionIterator == iterator) {
                                        gameObject.destroy();
                                        action = "";
                                        objectToActionIterator = -1;
                                    }

                                    if(ImGui.beginDragDropTarget()) {
                                        Object droppedObject = ImGui.acceptDragDropPayload("SceneGameObject");

                                        if(droppedObject != null) {
                                            GameObject droppedGameObject = (GameObject) droppedObject;
                                            if (droppedGameObject.ID != gameObject.ID) {
                                                childrenNeedFree.add(droppedGameObject);
                                                parentsToSet.add(gameObject);
                                            }
                                        }
                                        ImGui.endDragDropTarget();
                                    }

                                    alreadyProcessedObjectsID.add(gameObject.ID);

                                    showParent(alreadyProcessedObjectsID, gameObject, opened);

                                    if (opened) {
                                        ImGui.treePop();
                                    }
                                    ImGui.popID();
                                }
                            }
                        }
                        ImGui.treePop();
                    }
                    ImGui.treePop();
                }
            }

            if(mouseRightClicked && isAnyTreeNodeHovered) {
                if(ImGui.beginPopupContextWindow("SceneObjectActions")) {
                    if(ImGui.menuItem("Destroy")) {
                        action = "DestroyObject2D";
                    }
                    ImVec2 rectMin = ImGui.getItemRectMin();
                    popupContextWindowRectMin.set(rectMin.x, rectMin.y);
                    ImVec2 rectMax = ImGui.getItemRectMax();
                    popupContextWindowRectMax.set(rectMax.x, rectMax.y);
                    ImGui.endPopup();
                }
            }
        }
        ImGui.end();
    }

    private static void showParent(List<Integer> alreadyProcessedObjectsID, GameObject parentObject2D, boolean parentOpened)
    {
        for(GameObject gameObject : parentObject2D.getChildrenObjects()) {
            iterator++;
            if(parentOpened) {
                boolean opened = ImGui.treeNode(gameObject.name);
                if(ImGui.isItemHovered()) {
                    isAnyTreeNodeHovered = true;
                }
                if(ImGui.isItemHovered() && ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                    objectToActionIterator = iterator;
                }

                ImGui.pushID("Scene2DWrappedObject_" + gameObject.ID);
                if (ImGui.isItemHovered()) {
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                        ViewsManager.getInspectorView().setCurrentInspectingObject(gameObject);
                    }
                    if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                        Main.getCameraAnchor().getComponent(TransformComponent.class).getTransform().lerpMoveTo(
                                MatrixUtils.getPosition(gameObject.getComponent(TransformComponent.class).getTransform().getResultModelMatrix()), new Vector2f(10));
                    }
                }
                if (ImGui.beginDragDropSource()) {
                    ImGui.setDragDropPayload("SceneGameObject", gameObject);
                    ImGui.image(Resources.Textures.Icons.object2DFileIcon.getTextureHandler(), 25.0f, 25.0f);
                    ImGui.endDragDropSource();
                }

                if (ImGui.beginDragDropTarget()) {
                    Object droppedObject = ImGui.acceptDragDropPayload("SceneGameObject");

                    if (droppedObject != null) {
                        GameObject droppedGameObject = (GameObject) droppedObject;
                        if (droppedGameObject.ID != gameObject.ID) {
                            childrenNeedFree.add(droppedGameObject);
                            parentsToSet.add(gameObject);
                        }
                    }
                    ImGui.endDragDropTarget();
                }

                if(action.equals("DestroyObject2D") && objectToActionIterator == iterator) {
                    gameObject.destroy();
                    action = "";
                    objectToActionIterator = -1;
                }

                showParent(alreadyProcessedObjectsID, gameObject, opened);

                if (opened) {
                    ImGui.treePop();
                }
                ImGui.popID();
            } else {
                showParent(alreadyProcessedObjectsID, gameObject, false);
            }
            alreadyProcessedObjectsID.add(gameObject.ID);
        }
    }
}
