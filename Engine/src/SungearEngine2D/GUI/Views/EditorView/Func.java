package SungearEngine2D.GUI.Views.EditorView;

import java.util.function.Consumer;

public interface Func<T> extends Consumer<T>
{
    default Object[] getParams() { return null; }
    @Override
    void accept(T t);
}
