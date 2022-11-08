package Core2D.Drawable.UI.Button;

import Core2D.AssetManager.AssetManager;
import Core2D.Component.Components.TransformComponent;
import Core2D.Core2D.Core2D;
import Core2D.Drawable.Drawable;
import Core2D.Drawable.Object2D;
import Core2D.Drawable.UI.Text.Text;
import Core2D.Drawable.UI.UIElementCallback;
import Core2D.Input.PC.Mouse;
import Core2D.Transform.Transform;
import Core2D.Utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

// TODO: написать destroy метод
public class Button extends Drawable
{
    private Object2D button;

    private UIElementCallback uiElementCallback;

    private boolean picked = false;
    private boolean hovered = false;

    private String name = "button";

    private Vector2f originalClickPosition = new Vector2f();

    private Text text;

    private float textOffsetX = 10.0f;

    // обратный множитель
    private Vector4f backMultiplier = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private final Consumer<Button> render = Core2D.getMainRenderer()::render;
    public Button(String name, String text, UIElementCallback uiElementCallback)
    {
        this.name = name;
        this.text = new Text(AssetManager.getFont("comicSansSM"), text);

        create(uiElementCallback);
    }

    public Button(String name, UIElementCallback uiElementCallback)
    {
        this.name = name;
        this.text = new Text(AssetManager.getFont("comicSansSM"));

        create(uiElementCallback);
    }

    public Button(UIElementCallback uiElementCallback)
    {
        create(uiElementCallback);
    }

    private void create(UIElementCallback uiElementCallback)
    {
        button = new Object2D();

        button.setUIElement(true);

        this.uiElementCallback = uiElementCallback;

        if(text != null) updateTextPosition();
    }

    @Override
    public void destroy()
    {
        shouldDestroy = true;

        button.destroy();
        button = null;

        uiElementCallback = null;

        name = null;

        originalClickPosition = null;

        text.destroy();
        text = null;

        backMultiplier = null;
    }

    @Override
    public void update()
    {
        Vector2f mousePosition = Mouse.getMousePosition();
        if (Utils.isPointInNoRotatedObject(mousePosition, button) && !hovered) {
            applyMultiplier(new Vector4f(0.7f, 0.7f, 0.7f, 1.0f));
            uiElementCallback.onHovered();
            hovered = true;
        } else if (!Utils.isPointInNoRotatedObject(mousePosition, button) && hovered) {
            applyBackMultiplier();
            hovered = false;
            picked = false;
        }

        if (Mouse.buttonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT) && Utils.isPointInNoRotatedObject(mousePosition, button) && Utils.isPointInNoRotatedObject(mousePosition, button) && !picked) {
            applyBackMultiplier();
            applyMultiplier(new Vector4f(0.4f, 0.4f, 0.4f, 1.0f));
            picked = true;
        }
        if (Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT) && Utils.isPointInNoRotatedObject(mousePosition, button)) {
            applyBackMultiplier();
            uiElementCallback.onClicked();
            picked = false;
            hovered = false;
        }

        if (Mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            originalClickPosition = new Vector2f(mousePosition);
        }
    }

    @Override
    public void render()
    {
        render.accept(this);
    }

    private void updateTextPosition()
    {
        Transform buttonTransform = button.getComponent(TransformComponent.class).getTransform();

        text.setScale(buttonTransform.getScale());

        float needSize = (buttonTransform.getScale().x * 100.0f) - (textOffsetX * 2);

        Vector2f resultTextScale = new Vector2f(needSize / (buttonTransform.getScale().x * 100.0f),needSize / (buttonTransform.getScale().x * 100.0f));

        text.setScale(resultTextScale);

        Vector2f resultTextPosition = new Vector2f(buttonTransform.getCentre().x - text.getWidth() / 2.0f, buttonTransform.getCentre().y + text.getAverageHeight());

        text.setPosition(new Vector2f(buttonTransform.getPosition()).add(resultTextPosition));
    }

    private void applyMultiplier(Vector4f multiplier)
    {
        button.setColor(new Vector4f(button.getColor()).mul(multiplier));
        if(text != null) text.setColor(new Vector4f(text.getColor()).mul(multiplier));

        backMultiplier = new Vector4f(1.0f / multiplier.x, 1.0f / multiplier.y, 1.0f / multiplier.z, 1.0f / multiplier.w);
    }

    private void applyBackMultiplier()
    {
        button.setColor(new Vector4f(button.getColor()).mul(backMultiplier));
        if(text != null) text.setColor(new Vector4f(text.getColor()).mul(backMultiplier));
    }

    public Object2D getButton() { return button; }

    public UIElementCallback getUiElementCallback() { return uiElementCallback; }
    public void setUiElementCallback(UIElementCallback uiElementCallback) { this.uiElementCallback = uiElementCallback; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Text getText() { return text; }
    public void setText(Text text)
    {
        this.text = text;

        updateTextPosition();
    }

    /**
     * устанавливает scale как для кнопки, так и для текста
     */
    public void setScale(Vector2f scale)
    {
        this.button.getComponent(TransformComponent.class).getTransform().setScale(scale);

        if(text != null) updateTextPosition();
    }

    /**
     * устанавливает позицию как для кнопки, так и для текста
     */
    public void setPosition(Vector2f position)
    {
        this.button.getComponent(TransformComponent.class).getTransform().setPosition(position);

        if(text != null) updateTextPosition();
    }

    /**
     * устанавливает цвет как для кнопки, так и для текста
     */
    public void setColor(Vector4f color)
    {
        button.setColor(color);
        if(text != null) text.setColor(color);
    }

    public float getTextOffsetX() { return textOffsetX; }
    public void setTextOffsetX(float textOffsetX)
    {
        this.textOffsetX = textOffsetX;

        if(text != null) updateTextPosition();
    }

    @Override
    public void setActive(boolean active)
    {
        this.active = active;
        button.setActive(active);
        text.setActive(active);
    }
}