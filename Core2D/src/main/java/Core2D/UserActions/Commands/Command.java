package Core2D.UserActions.Commands;

import java.util.ArrayList;
import java.util.List;

public abstract class Command
{
    public List<Object> executedObjects = new ArrayList<>();
    protected boolean executed = false;

    public abstract void execute(Object... params);
    public abstract void restore();
    public abstract void revert();
    // memory free
    public abstract void free();

    public boolean isExecuted() { return executed; }
}
