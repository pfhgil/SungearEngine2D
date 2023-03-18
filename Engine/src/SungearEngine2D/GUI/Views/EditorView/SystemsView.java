package SungearEngine2D.GUI.Views.EditorView;

import SungearEngine2D.GUI.Views.View;

public class SystemsView extends View
{
    // FIXME
    /*
    private boolean showPopupWindow = false;

    private boolean someButtonInPopupWindowHovered = false;

    private System currentEditingSystem;

    private String action = "";

    public void draw() {
        Entity inspectingEntity = (Entity) ViewsManager.getInspectorView().getCurrentInspectingObject();
        if(inspectingEntity == null) return;

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

        ImGui.begin("Systems", ImGuiWindowFlags.NoMove);
        ImGui.popStyleVar(3);

        if (showPopupWindow) {
            ImGui.openPopup("Component actions");
            if (ImGui.beginPopupContextWindow("Component actions", ImGuiMouseButton.Left)) {
                if (!(currentEditingSystem instanceof NonRemovable)) {
                    boolean deleteClicked = ImGui.menuItem("Remove");
                    someButtonInPopupWindowHovered = ImGui.isItemHovered();
                    if (deleteClicked) {
                        try {
                            inspectingEntity.removeSystem(currentEditingSystem);
                        } catch (Exception e) {
                            ImGui.endPopup();
                        }
                        showPopupWindow = false;
                    }
                }

                ImGui.endPopup();
            }
        }

        if ((ImGui.isMouseClicked(ImGuiMouseButton.Left) || ImGui.isMouseClicked(ImGuiMouseButton.Right)) &&
                !someButtonInPopupWindowHovered) {
            showPopupWindow = false;
        }

        for (int i = 0; i < inspectingEntity.getSystems().size(); i++) {
            System currentSystem = inspectingEntity.getSystems().get(i);
            String systemName = currentSystem.getClass().getSimpleName();

            if(i == 0) {
                ImVec2 cursorPos = ImGui.getCursorPos();
                ImGui.setCursorPos(cursorPos.x, cursorPos.y + 5.0f);
            }

            ImGui.pushID(systemName + i);
            if (!systemName.equals("ScriptableSystem")) {
                ImGui.collapsingHeader(systemName);
            } else {
                ImGui.collapsingHeader(((ScriptableSystem) currentSystem).script.getName() + " (" + systemName + ")");
            }
            ImVec2 minRect = ImGui.getItemRectMin();
            ImVec2 maxRect = ImGui.getItemRectMax();

            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("System", currentSystem);
                ImGui.text(systemName);
                ImGui.endDragDropSource();
            }
            ImGui.popID();

            if (ImGui.isMouseHoveringRect(minRect.x, minRect.y, maxRect.x, maxRect.y) &&
                    ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                showPopupWindow = true;
                currentEditingSystem = currentSystem;
            }

            ImGui.separator();
        }

        ImVec2 textSize = new ImVec2();
        ImGui.calcTextSize(textSize, "Add system");
        ImVec2 windowSize = ImGui.getWindowSize();
        ImGui.setCursorPos(windowSize.x / 2.0f - textSize.x / 2.0f, ImGui.getCursorPosY());
        if (ImGui.button("Add system")) {
            action = "addEntitySystem";
        }

        if (ViewsManager.getResourcesView().getCurrentMovingFile() != null) {
            if (ImGui.beginDragDropTarget()) {
                String extension = FilenameUtils.getExtension(ViewsManager.getResourcesView().getCurrentMovingFile().getName());
                if (extension.equals("java")) {
                    Object droppedFile = ImGui.acceptDragDropPayload("File");
                    if (droppedFile instanceof File javaFile) {
                        ViewsManager.getInspectorView().compileAndAddScript(javaFile, inspectingEntity);
                    }

                }
                ImGui.endDragDropTarget();
            }
        }

        drawAction(inspectingEntity);
        ImGui.end();
    }

    private void drawAction(Entity inspectingEntity) {
        if (action.equals("addEntitySystem")) {
            ImGui.newLine();

            ImVec2 windowSize = ImGui.getWindowSize();
            ImGui.setCursorPos(windowSize.x / 2.0f - 105.0f / 2.0f, ImGui.getCursorPosY());
            ImGui.setNextItemWidth(120.0f);

            ImGui.pushID("Systems");
            {
                if (ImGui.beginListBox("")) {

                    try {
                        if (ImGui.selectable("MeshRendererSystem")) {
                            inspectingEntity.addSystem(new MeshRendererSystem());
                            action = "";
                        }
                        if (ImGui.selectable("PrimitivesRendererSystem")) {
                            inspectingEntity.addSystem(new PrimitivesRendererSystem());
                            action = "";
                        }
                    } catch (Exception e) {
                        Log.CurrentSession.println(ExceptionsUtils.toString(e), Log.MessageType.ERROR);

                        action = "";

                        ImGui.endListBox();
                        ImGui.popID();

                        return;
                    }

                    if (ImGui.isMouseClicked(ImGuiMouseButton.Left) && !ImGui.isAnyItemHovered()) {
                        action = "";
                    }

                    ImGui.endListBox();
                }
            }
            ImGui.popID();
        }
    }

     */
}
