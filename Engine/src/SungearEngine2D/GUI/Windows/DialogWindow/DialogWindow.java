package SungearEngine2D.GUI.Windows.DialogWindow;

import Core2D.Core2D.Core2D;
import SungearEngine2D.GUI.Views.MainView;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class DialogWindow
{
    private String windowName;

    private DialogWindowCallback dialogWindowCallback;

    private int buttonsNum = 0;

    private String middleButtonText;

    private String leftButtonText;
    private String rightButtonText;

    private Vector2f windowSize = new Vector2f(350.0f, 350.0f);
    private Vector2f currentWindowSize = new Vector2f(windowSize);

    private boolean active = true;

    public DialogWindow(String windowName, String middleButtonText)
    {
        this.windowName = windowName;

        buttonsNum = 1;

        this.middleButtonText = middleButtonText;
    }

    public DialogWindow(String windowName,String leftButtonText, String rightButtonText)
    {
        this.windowName = windowName;

        buttonsNum = 2;

        this.leftButtonText = leftButtonText;
        this.rightButtonText = rightButtonText;
    }

    public void draw()
    {
        if(active) {
            ImGui.setNextWindowPos(Core2D.getWindow().getSize().x / 2.0f - windowSize.x / 2.0f, Core2D.getWindow().getSize().y / 2.0f - windowSize.y / 2.0f, ImGuiCond.Appearing);
            ImGui.setNextWindowSize(windowSize.x, windowSize.y, ImGuiCond.Appearing);
            ImGui.begin(windowName, ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse);
            ImGui.setWindowFocus();

            ImVec2 currentWindowSize = ImGui.getWindowSize();
            this.currentWindowSize.x = currentWindowSize.x;
            this.currentWindowSize.y = currentWindowSize.y;

            if (dialogWindowCallback != null) {
                dialogWindowCallback.onDraw();
            }

            if(buttonsNum == 1) {
                ImVec2 middleButtonTextSize = new ImVec2();
                ImGui.calcTextSize(middleButtonTextSize, middleButtonText);
                ImGui.setCursorPos(currentWindowSize.x / 2.0f - (middleButtonTextSize.x + 10.0f) / 2.0f, currentWindowSize.y - 25.0f);
                if(ImGui.button(middleButtonText)) {
                    if(dialogWindowCallback != null) {
                        dialogWindowCallback.onMiddleButtonClicked();
                    }
                }
                middleButtonTextSize = null;
            } else if(buttonsNum == 2) {
                ImVec2 leftButtonTextSize = new ImVec2();
                ImGui.calcTextSize(leftButtonTextSize, leftButtonText);
                ImGui.setCursorPos(10.0f, currentWindowSize.y - 25.0f);
                if(ImGui.button(leftButtonText)) {
                    if(dialogWindowCallback != null) {
                        dialogWindowCallback.onLeftButtonClicked();
                    }
                }

                ImVec2 rightButtonTextSize = new ImVec2();
                ImGui.calcTextSize(rightButtonTextSize, rightButtonText);
                ImGui.setCursorPos(currentWindowSize.x - rightButtonTextSize.x - 10.0f - 10.0f, currentWindowSize.y - 25.0f);
                if(ImGui.button(rightButtonText)) {
                    if(dialogWindowCallback != null) {
                        dialogWindowCallback.onRightButtonClicked();
                    }
                }
            }

            ImGui.end();
        }
    }

    public void setDialogWindowCallback(DialogWindowCallback dialogWindowCallback) { this.dialogWindowCallback = dialogWindowCallback; }

    public int getButtonsNum() { return buttonsNum; }
    public void setButtonsNum(int buttonsNum) { this.buttonsNum = buttonsNum; }

    public String getWindowName() { return windowName; }
    public void setWindowName(String windowName) { this.windowName = windowName; }

    public String getMiddleButtonText() { return middleButtonText; }
    public void setMiddleButtonText(String middleButtonText) { this.middleButtonText = middleButtonText; }

    public String getLeftButtonText() { return leftButtonText; }
    public void setLeftButtonText(String leftButtonText) { this.leftButtonText = leftButtonText; }

    public String getRightButtonText() { return rightButtonText; }
    public void setRightButtonText(String rightButtonText) { this.rightButtonText = rightButtonText; }

    public Vector2f getWindowSize() { return windowSize; }
    public void setWindowSize(Vector2f windowSize) { this.windowSize = windowSize; }

    public Vector2f getCurrentWindowSize() { return currentWindowSize; }

    public boolean isActive() { return active; }
    public void setActive(boolean active)
    {
        this.active = active;
    }
}