package Core2D.Input;

public interface Core2DUserInputCallback
{
    void onInput(int key, String keyName, int mods);
    void onScroll(double xoffset, double yoffset);
}
