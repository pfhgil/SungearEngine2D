package Core2D.UserActions.Executors;

import Core2D.Log.Log;
import Core2D.UserActions.Commands.Command;

import java.util.*;

public class Executor
{
    public static int maxCommandsNum = 32;

    private static List<Command> commands = new ArrayList<>();
    // pointer on current command
    private static Command lastExecutedCommand;

    public static Object bufferedObject;

    @SafeVarargs
    public static <T> void executeCommand(Command command, T... params)
    {
        if(commands.size() >= maxCommandsNum) {
            Command firstCommand = commands.get(0);
            firstCommand.free();
            commands.remove(firstCommand);
        }

        lastExecutedCommand = command;
        commands.add(command);

        command.execute((Object[]) params);
    }

    public static void restoreLastCommand()
    {
        int idx = commands.indexOf(lastExecutedCommand) + 1;

        if(commands.size() > 0 && idx > 0 && idx < commands.size()) {
            lastExecutedCommand = commands.get(idx);
        }

        if (lastExecutedCommand != null) {
            lastExecutedCommand.restore();
        }
    }

    public static void revertLastCommand()
    {
        if (lastExecutedCommand != null) {
            lastExecutedCommand.revert();
        }

        int idx = commands.indexOf(lastExecutedCommand) - 1;

        if(commands.size() > 0 && idx > 0 && idx < commands.size()) {
            lastExecutedCommand = commands.get(idx);
        }
    }
}
