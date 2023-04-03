package Core2D.UserActions.Executors;

import Core2D.UserActions.Commands.Command;

import java.util.*;

public class Executor
{
    public static int maxCommandsNum = 16;

    private static List<Command> commands = new ArrayList<>();
    // pointer on current command
    private static Command lastExecutedCommand;

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
        if (lastExecutedCommand != null) {
            lastExecutedCommand.restore();
        }

        if(commands.size() > 0) {
            lastExecutedCommand = commands.get(commands.size() - 1);
        }

        commands.add(lastExecutedCommand);
    }

    public static void revertLastCommand()
    {
        if (lastExecutedCommand != null) {
            lastExecutedCommand.revert();
        }

        if(commands.size() > 0) {
            lastExecutedCommand = commands.get(commands.size() - 1);
        }

        commands.remove(lastExecutedCommand);
    }
}
