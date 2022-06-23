package SungearEngine2D.GUI.Views;

import Core2D.Controllers.PC.Mouse;
import Core2D.Core2D.Core2D;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

public abstract class View
{
    private Vector2f windowScreenPosition = new Vector2f();
    private Vector2f windowScreenSize = new Vector2f();

    private boolean hovered;

    protected void update()
    {
        ImVec2 size = ImGui.getWindowSize();
        windowScreenSize.x = size.x;
        windowScreenSize.y = size.y;

        ImVec2 position = ImGui.getWindowPos();
        windowScreenPosition.x = position.x;
        windowScreenPosition.y = position.y;

        hovered = ImGui.isWindowHovered();
    }

    public boolean isHovered()
    {
        Vector2f mousePosition = new Vector2f(Mouse.getMousePosition());
        mousePosition.y = Core2D.getWindow().getSize().y - mousePosition.y;
        return hovered ||
                (mousePosition.x > windowScreenPosition.x && mousePosition.x < windowScreenPosition.x + windowScreenSize.x &&
                        mousePosition.y > windowScreenPosition.y && mousePosition.y < windowScreenPosition.y + windowScreenSize.y);
    }
}
