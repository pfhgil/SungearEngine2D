package Core2D.UserActions.Commands;

import Core2D.Common.Interfaces.Unregistered;
import Core2D.UserActions.Commands.Command;
import Core2D.UserActions.Executors.Executor;

public class ObjectCopy extends Command implements Unregistered
{
    @Override
    public void execute(Object... params)
    {
        Executor.bufferedObject = params;
    }

    @Override
    public void restore()
    {

    }

    @Override
    public void revert()
    {

    }

    @Override
    public void free()
    {

    }
}
