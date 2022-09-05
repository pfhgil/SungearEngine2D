package Core2D.UI.InputField;

import Core2D.AssetManager.AssetManager;
import Core2D.CommonParameters.CommonDrawableObjectsParameters;
import Core2D.Component.Components.TransformComponent;
import Core2D.Controllers.PC.Keyboard;
import Core2D.Controllers.PC.Mouse;
import Core2D.Core2D.Core2D;
import Core2D.Input.UserInputCallback;
import Core2D.Object2D.Object2D;
import Core2D.Object2D.Transform;
import Core2D.UI.Text.Text;
import Core2D.Utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

/**
 * ДОДЕЛАТЬ
 */
// TODO: написать destroy метод
public class InputField extends CommonDrawableObjectsParameters
{
    public static class Cursor
    {
        private Object2D cursor;

        // позиция курсора по символам
        private int cursorPosition = 0;

        // время мигания
        private float blinkTime = 0.3f;
        private float currentBlinkTime = 0.0f;

        public Cursor()
        {
            cursor = new Object2D();
            cursor.setUIElement(true);
            cursor.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
            cursor.getComponent(TransformComponent.class).getTransform().setScale(new Vector2f(0.025f, 1.0f));
        }

        public void update(float deltaTime)
        {
            currentBlinkTime += deltaTime;

            if(currentBlinkTime >= blinkTime) {
                cursor.setActive(!cursor.isActive());
                currentBlinkTime = 0.0f;
            }
        }

        public Object2D getCursor() { return cursor; }

        public int getCursorPosition() { return cursorPosition; }
        public void setCursorPosition(int cursorPosition) { this.cursorPosition = cursorPosition; }

        public float getBlinkTime() { return blinkTime; }
        public void setBlinkTime(float blinkTime) { this.blinkTime = blinkTime; }

        public float getCurrentBlinkTime() { return currentBlinkTime; }
        public void setCurrentBlinkTime(float currentBlinkTime) { this.currentBlinkTime = currentBlinkTime; }
    }

    private Object2D inputField;
    private Text text;
    private Text takeGlyphWidthText;
    private Cursor cursor;

    private UserInputCallback userInputCallback;

    private float cursorOffsetX = 5.0f;
    // делитель отступа для курсора
    private float cursorOffsetDivisorY = 10.0f;

    // время зажатия клавиши, при котором будет долго вводиться текст
    private float keyDownDelay = 0.015f;
    private float currentKeyDownDelay = 0.0f;

    private boolean selected = false;

    // delta time
    private float dt = 0.0f;

    public InputField()
    {
        inputField = new Object2D();
        inputField.setUIElement(true);
        inputField.setColor(new Vector4f(0.5f, 0.1f, 0.1f, 1.0f));

        cursor = new Cursor();

        text = new Text(AssetManager.getFont("comicSansSM"), "");
        text.setColor(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));

        takeGlyphWidthText = new Text(AssetManager.getFont("comicSansSM"), "");

        userInputCallback = new UserInputCallback() {
            @Override
            public void onInput(int key, String keyName, int mods) {
                int capsLockMod = mods & GLFW.GLFW_MOD_CAPS_LOCK;
                boolean capsLockActive = (capsLockMod == GLFW.GLFW_MOD_CAPS_LOCK);

                if(Keyboard.keyDown(key)) {
                    currentKeyDownDelay += dt;
                } else {
                    currentKeyDownDelay = 0.0f;
                }

                Transform cursorTransform = cursor.getCursor().getComponent(TransformComponent.class).getTransform();

                if(selected && (Keyboard.keyReleased(key) || currentKeyDownDelay >= keyDownDelay)) {
                    if(key == GLFW.GLFW_KEY_SPACE) {
                        String firstPart = text.getText().substring(0, cursor.getCursorPosition());
                        String secondPart = text.getText().substring(cursor.getCursorPosition());
                        text.setText(firstPart + " " + secondPart);

                        takeGlyphWidthText.setText(" ");

                        cursorTransform.translate(new Vector2f(takeGlyphWidthText.getWidth(), 0.0f));
                        cursor.setCursorPosition(cursor.getCursorPosition() + 1);
                    } else if(key == GLFW.GLFW_KEY_BACKSPACE && cursor.getCursorPosition() > 0) {
                        String firstPart = text.getText().substring(0, cursor.getCursorPosition());
                        String secondPart = text.getText().substring(cursor.getCursorPosition());
                        String lastChar = firstPart.substring(cursor.getCursorPosition() - 1, cursor.getCursorPosition());
                        firstPart = firstPart.substring(0, cursor.getCursorPosition() - 1);
                        text.setText(firstPart + secondPart);

                        takeGlyphWidthText.setText(lastChar);

                        cursorTransform.translate(new Vector2f(-takeGlyphWidthText.getWidth(), 0.0f));
                        cursor.setCursorPosition(cursor.getCursorPosition() - 1);
                    } else if(key == GLFW.GLFW_KEY_LEFT && cursor.getCursorPosition() > 0) {
                        String lastChar = text.getText().substring(cursor.getCursorPosition() - 1, cursor.getCursorPosition());

                        takeGlyphWidthText.setText(lastChar);

                        cursorTransform.translate(new Vector2f(-takeGlyphWidthText.getWidth(), 0.0f));
                        cursor.setCursorPosition(cursor.getCursorPosition() - 1);
                    } else if(key == GLFW.GLFW_KEY_RIGHT && cursor.getCursorPosition() < text.getText().length()) {
                        String nextChar = text.getText().substring(cursor.getCursorPosition(), cursor.getCursorPosition() + 1);

                        takeGlyphWidthText.setText(nextChar);

                        cursorTransform.translate(new Vector2f(takeGlyphWidthText.getWidth(), 0.0f));
                        cursor.setCursorPosition(cursor.getCursorPosition() + 1);
                    } else {
                        if(keyName != null) {
                            String firstPart = text.getText().substring(0, cursor.getCursorPosition());
                            String secondPart = text.getText().substring(cursor.getCursorPosition());

                            if(capsLockActive) keyName = keyName.toUpperCase();

                            text.setText(firstPart + keyName + secondPart);

                            takeGlyphWidthText.setText(keyName);

                            cursorTransform.translate(new Vector2f(takeGlyphWidthText.getWidth(), 0.0f));
                            cursor.setCursorPosition(cursor.getCursorPosition() + 1);
                        }
                    }

                    cursor.setCurrentBlinkTime(0.0f);
                    cursor.getCursor().setActive(true);
                }

                cursorTransform = null;
            }

            @Override
            public void onScroll(double xoffset, double yoffset) {

            }
        };

        Core2D.getCore2DInputCallback().getUserInputCallbacks().add(userInputCallback);
    }

    @Override
    public void update()
    {
        if(Mouse.buttonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            selected = Utils.isPointInNoRotatedObject(Mouse.getMousePosition(), inputField);
        }
    }

    @Override
    public void deltaUpdate(float deltaTime)
    {
        dt = deltaTime;

        cursor.update(deltaTime);
    }

    public void setPosition(Vector2f position)
    {
        Transform inputFieldTransform = inputField.getComponent(TransformComponent.class).getTransform();

        inputFieldTransform.setPosition(position);
        float cursorOffsetY = inputFieldTransform.getScale().y * 100.0f / cursorOffsetDivisorY;
        cursor.getCursor().getComponent(TransformComponent.class).getTransform().setPosition(new Vector2f(position.x + cursorOffsetX, position.y + cursorOffsetY));
        text.setPosition(new Vector2f(position.x + cursorOffsetX, position.y + inputFieldTransform.getScale().y * 100.0f - cursorOffsetY / 2.0f));

        inputFieldTransform = null;
    }

    public void setScale(Vector2f scale)
    {
        Transform inputFieldTransform = inputField.getComponent(TransformComponent.class).getTransform();
        Transform cursorTransform = cursor.getCursor().getComponent(TransformComponent.class).getTransform();

        inputFieldTransform.setScale(scale);
        float cursorOffsetY = inputFieldTransform.getScale().y * 100.0f / cursorOffsetDivisorY;
        float cursorHeight = (inputFieldTransform.getScale().y * 100.0f) - (cursorOffsetY * 2.0f);
        cursorTransform.setPosition(new Vector2f(inputFieldTransform.getPosition().x + cursorOffsetX, inputFieldTransform.getPosition().y + cursorOffsetY));
        cursorTransform.setScale(new Vector2f(cursorTransform.getScale().x, cursorHeight / 100.0f));

        float textHeight = text.getScale().y * 100.0f - cursorOffsetY * 2.0f;
        text.setScale(new Vector2f(1,  textHeight / 100.0f));
        takeGlyphWidthText.setScale(new Vector2f(1,  textHeight / 100.0f));

        inputFieldTransform = null;
        cursorTransform = null;
    }

    public Object2D getInputField() { return inputField; }

    public Text getText() { return text; }

    public Cursor getCursor() { return cursor; }

    public float getCursorOffsetX() { return cursorOffsetX; }
    public void setCursorOffsetX(float cursorOffsetX) { this.cursorOffsetX = cursorOffsetX; }

    public float getCursorOffsetDivisorY() { return cursorOffsetDivisorY; }
    public void setCursorOffsetDivisorY(float cursorOffsetDivisorY) { this.cursorOffsetDivisorY = cursorOffsetDivisorY; }

    public boolean isSelected() { return selected; }
}