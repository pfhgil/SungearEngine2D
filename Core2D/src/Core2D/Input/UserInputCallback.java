package Core2D.Input;

public interface UserInputCallback
{
    void onInput(int key, String keyName, int mods);
    void onScroll(double xoffset, double yoffset);
}
