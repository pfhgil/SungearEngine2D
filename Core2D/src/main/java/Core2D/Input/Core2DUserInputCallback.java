package Core2D.Input;

public interface Core2DUserInputCallback
{
    void onKeyboardInput(int key, String keyName, int mods);
    void onScroll(double xOffset, double yOffset);
    void onMousePositionChanged(long window, double posX, double posY);
}
